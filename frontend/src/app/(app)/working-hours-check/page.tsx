'use client';
import React, {useEffect, useState} from 'react';
import {
    DaySheetDto,
    TimestampDto,
    type UpdateConfirmedRequest,
} from "@/openapi/compassClient";
import Table from "@/components/table";
import {Checkmark24Regular, Edit24Regular, NoteAddRegular, Save24Regular} from "@fluentui/react-icons";
import {toast} from "react-hot-toast";
import {useRouter} from "next/navigation";
import Title1 from "@/components/title1";
import {getDaySheetControllerApi} from "@/openapi/connector";
import toastMessages from "@/constants/toastMessages";
import { useUser } from '@auth0/nextjs-auth0/client';
import Modal from '@/components/modal';
import Button from '@/components/button';
import TextArea from '@/components/textarea';

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
    getDaySheetControllerApi().updateDayNotes({ 
      updateDaySheetDayNotesDto: {
        id: daySheetDto?.id,
        dayNotes: notes
      }
    }).then(() => {
      close();
      onSave();
      toast.success(toastMessages.DAYNOTES_UPDATED);
    }).catch(() => {
      toast.error(toastMessages.DAYNOTES_NOT_UPDATED);
    })
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
    const [showDayNotesModal, setShowDayNotesModal] = useState(false);
    const [daySheetDtos, setDaySheetDtos] = useState<DaySheetDto[]>([]);
    const [selectedDaySheetDto, setSelectedDaySheetDto] = useState<DaySheetDto>();
    const { user } = useUser();
    const router = useRouter();

    const mockUserId = 'auth0|6640a6df7d1d70fe02cc72c9';
    const mockData: DaySheetDto[] = [{id: 99, date: Date.prototype, dayNotes: 'undefined', confirmed: false, timestamps:[{id: 98, daySheetId:99, startTime:'start', endTime:'end'} as TimestampDto], timeSum: 90}];

    const loadDaySheets = () => {
      const queryParams = new URLSearchParams(window.location.search);
      const userId = queryParams.get('userId');
      if (userId) {
          getDaySheetControllerApi().getAllDaySheetByParticipant({ userId: userId }).then(daySheetDtos => {
              close();
              toast.success(toastMessages.DAYSHEETS_LOADED);
              console.log(daySheetDtos);

              const notConfirmedDaySheetDtos = daySheetDtos.filter(daySheetDto => !daySheetDto.confirmed);
              setDaySheetDtos(notConfirmedDaySheetDtos);
          }).catch(() => {
              toast.error(toastMessages.DATA_NOT_LOADED);
          });
      } else {
          toast.error(toastMessages.USER_NOT_SELECTED);
      }
    }

    const confirmDaySheet = async (id: number) => {
        const updateDayRequest: UpdateConfirmedRequest = {
          id: id
        };

        getDaySheetControllerApi().updateConfirmed(updateDayRequest).then(() => {
            close();
            toast.success(toastMessages.DAYSHEET_CONFIRMED);
        }).catch(() => {
            toast.error(toastMessages.DAYSHEET_CONFIRMED_ERROR);
        });
    };

    const navigateToSingleDay = () => {
        if (selectedDaySheetDto != undefined && selectedDaySheetDto.date != undefined) {
            const dateString = encodeURIComponent(selectedDaySheetDto.date.toString());
            router.push(`/working-hours-single-day?date=${dateString}`);
        }
    };

    const dateFunction = (daySheetDto: DaySheetDto): string => {
        if (daySheetDto != undefined) {
            if (daySheetDto.date) return daySheetDto.date.toLocaleDateString();
        }
        return '';
    }

    const timeSumFunction = (daySheetDto: DaySheetDto): string => {
        if (daySheetDto && daySheetDto.timeSum !== undefined) {
            const totalSeconds = Math.floor(daySheetDto.timeSum / 1000); // converting milliseconds to seconds
            const hours = Math.floor(totalSeconds / 3600);
            const minutes = Math.floor((totalSeconds % 3600) / 60);
            return `${hours} hours and ${minutes} minutes`;
        }
        return '';
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
        <div>
            <Title1>Kontrolle Arbeitszeit</Title1>
            <Table
                data={daySheetDtos}
                columns={[
                    {
                        header: "Datum",
                        titleFunction: dateFunction
                    },
                    {
                        header: "Notizen",
                        title: "dayNotes"
                    },
                    {
                        header: "Erfasste Arbeitszeit",
                        titleFunction: timeSumFunction
                    }
                ]}
                actions={[
                    {
                        icon: Checkmark24Regular,
                        label: "Bestätigen",
                        onClick: (id) => {
                            let daySheetDto: DaySheetDto | undefined = daySheetDtos[id];
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
                            setSelectedDaySheetDto(daySheetDtos[id]);
                            navigateToSingleDay();
                        }
                    },
                    {
                        icon: NoteAddRegular,
                        label: "Notizen öffnen",
                        onClick: (id) => {
                            setSelectedDaySheetDto(daySheetDtos[id]);
                            setShowDayNotesModal(true);
                        },
                    }

                ]}>

            </Table>
        </div>
      </>
    );
};
