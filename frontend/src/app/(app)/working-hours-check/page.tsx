'use client';
import React, { use, useEffect, useState } from 'react';
import {
  DaySheetDto,
  type ConfirmRequest,
} from "@/openapi/compassClient";
import Table from "@/components/table";
import { Edit24Regular, ShiftsCheckmark24Regular } from "@fluentui/react-icons";
import { toast } from "react-hot-toast";
import { useRouter } from "next/navigation";
import Title1 from "@/components/title1";
import { getDaySheetControllerApi, getUserControllerApi } from "@/openapi/connector";
import toastMessages from "@/constants/toastMessages";
import { convertMilisecondsToTimeString } from '@/utils/time';
import Select from '@/components/select';

const allParticipants = "ALL_PARTICIPANTS";

export default function WorkingHoursCheckPage() {
  const [loading, setLoading] = useState(true);
  const [daySheets, setDaySheets] = useState<DaySheetDto[]>([]);
  const [daySheetsFiltered, setDaySheetsFiltered] = useState<DaySheetDto[]>([]);
  const [selectedDaySheet, setSelectedDaySheet] = useState<DaySheetDto>();
  const [participantSelection, setParticipantSelection] = useState<any>();
  const [participantSelections, setParticipantSelections] = useState<{ id: string, label: string }[]>([]);
  const router = useRouter();

  const loadDaySheets = () => {
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
    }).catch(() => {
      toast.error(toastMessages.DAYSHEETS_NOT_LOADED);
    }).finally(() => {
      setLoading(false);
    });
  }

  const filterDaySheets = () => {
    setDaySheetsFiltered(daySheets.filter(daySheet => {
      if (participantSelection === allParticipants) {
        return true;
      }
      return daySheet.owner?.userId === participantSelection ? true : false;
    }));
  }

  const confirmDaySheet = (id: number) => {
    const updateDayRequest: ConfirmRequest = {
      id: id
    };

    const confirmAction = () => getDaySheetControllerApi().confirm(updateDayRequest).then(() => {
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
    if (selectedDaySheet?.id) router.push(`/working-hours-check/${selectedDaySheet.id}`);
  };

  useEffect(() => {
    loadDaySheets();
    getUserControllerApi().getAllParticipants().then(participants => {
      const participantsSelections = participants.map(participant => participant && ({
        id: participant.userId ?? "",
        label: participant.email ?? ""
      })) ?? [];

      participantsSelections.unshift({ id: allParticipants, label: "Alle Teilnehmer" });
      setParticipantSelections(participantsSelections);
      setParticipantSelection(allParticipants);
    });
  }, []);

  useEffect(() => {
    filterDaySheets();
  }, [daySheets, participantSelection]);

  return (
    <>
      <div className="h-full flex flex-col">
        <div className="flex flex-col sm:flex-row justify-between mb-4">
          <Title1>Kontrolle Arbeitszeit</Title1>
          <div className="mt-2 sm:mt-0">
            <Select
              className="w-40 inline-block mb-4"
              placeholder="Teilnehmer"
              data={participantSelections}
              value={participantSelection}
              onChange={(e) => setParticipantSelection(e.target.value)} />
          </div>
        </div>
        <Table
          data={daySheetsFiltered}
          columns={[
            {
              header: "Datum",
              title: "date",
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
              icon: ShiftsCheckmark24Regular,
              label: "Bestätigen",
              onClick: (id) => {
                const daySheetId = daySheets[id]?.id;
                daySheetId && confirmDaySheet(daySheetId);
              }
            },
            {
              icon: Edit24Regular,
              onClick: (id) => {
                setSelectedDaySheet(daySheets[id]);
                navigateToSingleDay();
              }
            }
          ]}
          loading={loading}
        />
      </div>
    </>
  );
};
