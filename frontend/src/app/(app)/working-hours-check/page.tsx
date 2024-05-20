'use client';
import React, {useEffect, useState} from 'react';
import {
    DaySheetDto,
    type UpdateConfirmedRequest,
} from "@/openapi/compassClient";
import Table from "@/components/table";
import {Checkmark24Regular, Edit24Regular, NoteAddRegular, Save24Regular} from "@fluentui/react-icons";
import {toast} from "react-hot-toast";
import {useRouter} from "next/navigation";
import Title1 from "@/components/title1";
import {getDaySheetControllerApi} from "@/openapi/connector";
import toastMessages from "@/constants/toastMessages";
import Modal from '@/components/modal';
import Button from '@/components/button';
import TextArea from '@/components/textarea';
import { convertMilisecondsToTimeString } from '@/utils/time';

enum formFields {
  DAY_NOTES = "dayNotes"
}

function DayNotesModal({ close, onSave, daySheetDto }: Readonly<{
  close: () => void;
  onSave: () => void;
  daySheetDto?: DaySheetDto;
}>) {
  const [notes, setNotes] = useState(daySheetDto?.dayNotes || '');

  const onSubmit = () => {
    const updateAction = () => getDaySheetControllerApi().updateDayNotes({ 
      updateDaySheetDayNotesDto: {
        id: daySheetDto?.id,
        dayNotes: notes
      }
    }).then(() => {
      close();
      onSave();
    });

    toast.promise(updateAction(), {
      loading: toastMessages.UPDATING,
      success: toastMessages.DAYNOTES_UPDATED,
      error: toastMessages.DAYNOTES_NOT_UPDATED,
    });
  }

  return (
    <Modal
      title="Notizen bearbeiten"
      footerActions={
        <Button Icon={Save24Regular} type="submit">Speichern</Button>
      }
      close={close}
      onSubmit={onSubmit}
    >
      <TextArea 
        name={formFields.DAY_NOTES}
        placeholder='Notizen' 
        value={notes} 
        onChange={(e) => setNotes(e.target.value)}
        className='w-full min-h-32' />
    </Modal>
  );
}

export default function WorkingHoursCheckPage() {
    const [loading, setLoading] = useState(true);
    const [showDayNotesModal, setShowDayNotesModal] = useState(false);
    const [daySheets, setDaySheets] = useState<DaySheetDto[]>([]);
    const [selectedDaySheetDto, setSelectedDaySheetDto] = useState<DaySheetDto>();
    const router = useRouter();

    function loadDaySheets() {
        setLoading(true);
        getDaySheetControllerApi().getAllDaySheetNotConfirmed().then(daySheets => {
            close();
            daySheets = daySheets.sort((a, b) => {
                if (a.date && b.date) {
                    return new Date(a.date).getTime() - new Date(b.date).getTime();
                }
                return 0;
            });
            setDaySheets(daySheets);
            setLoading(false);
        }).catch(() => {
            toast.error(toastMessages.DAYSHEETS_NOT_LOADED);
        });
    }

    const confirmDaySheet = async (id: number) => {
        const updateDayRequest: UpdateConfirmedRequest = {
          id: id
        };

        const confirmAction = () => getDaySheetControllerApi().updateConfirmed(updateDayRequest).then(() => {
            close();
            loadDaySheets();
        })

        toast.promise(confirmAction(), {
            loading: toastMessages.CONFIRMING,
            success: toastMessages.DAYSHEET_CONFIRMED,
            error: toastMessages.DAYSHEET_NOT_CONFIRMED,
        });
    };

    const navigateToSingleDay = () => {
        if (selectedDaySheetDto != undefined && selectedDaySheetDto.id != undefined) {
            router.push(`/working-hours-single-day?day-sheet=${selectedDaySheetDto.id}`);
        }
    };

    useEffect(() => {
        loadDaySheets();
    }, []);

    return (
      <>
        {showDayNotesModal && (
          <DayNotesModal 
			  		close={() => setShowDayNotesModal(false)}
            onSave={() => loadDaySheets()}
            daySheetDto={selectedDaySheetDto} />
        )}
        <div className="h-full flex flex-col">
            <Title1 className='mb-5'>Kontrolle Arbeitszeit</Title1>
            <Table
                data={daySheets}
                columns={[
                    {
                        header: "Datum",
                        title: "date",
                    },
                    {
                        header: "Notizen",
                        title: "dayNotes"
                    },
                    {
                        header: "Erfasste Arbeitszeit",
                        titleFunction: (daySheet: DaySheetDto) => {
                            const miliseconds = daySheet.timeSum || 0;
                            return convertMilisecondsToTimeString(miliseconds);
                        }
                    },
                    {
                        header: "Teilnehmer",
                        titleFunction: (daySheet: DaySheetDto) => {
                            return daySheet.owner?.email || "";
                        }
                    }
                ]}
                actions={[
                    {
                        icon: Checkmark24Regular,
                        label: "Bestätigen",
                        onClick: (id) => {
                            let daySheetDto: DaySheetDto | undefined = daySheets[id];
                            if (daySheetDto !== undefined) {
                                if (daySheetDto.id != undefined) {
                                    confirmDaySheet(daySheetDto.id);
                                }
                            }
                        }
                    },
                    {
                        icon: Edit24Regular,
                        onClick: (id) => {
                            setSelectedDaySheetDto(daySheets[id]);
                            navigateToSingleDay();
                        }
                    },
                    {
                        icon: NoteAddRegular,
                        label: "Notizen öffnen",
                        onClick: (id) => {
                            setSelectedDaySheetDto(daySheets[id]);
                            setShowDayNotesModal(true);
                        },
                    }

                ]}
                loading={loading}
            />
        </div>
      </>
    );
};
