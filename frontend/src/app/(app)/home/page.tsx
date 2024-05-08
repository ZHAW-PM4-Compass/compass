'use client';
import React, {useEffect, useState} from 'react';
import {DaySheetDto, type UpdateConfirmedRequest} from "@/openapi/compassClient";
import Table from "@/components/table";
import {Checkmark24Regular, Edit24Regular} from "@fluentui/react-icons";
import {toast} from "react-hot-toast";
import {useRouter} from "next/navigation";
import Title1 from "@/components/title1";
import {getDaySheetControllerApi} from "@/openapi/connector";
import toastMessages from "@/constants/toastMessages";
import { useUser } from '@auth0/nextjs-auth0/client';

export default function HomePage() {
    const [daySheetDtos, setDaySheetDtos] = useState<DaySheetDto[]>([]);
    const [selectedDaySheetDto, setSelectedDaySheetDto] = useState<DaySheetDto>();
    const router = useRouter();
    const {user} = useUser();

    useEffect(() => {
      if (user?.sub) {
        getDaySheetControllerApi().getAllDaySheetByParticipant({ userId: user?.sub }).then(daySheetDtos => {
          close();
          toast.success(toastMessages.DAYSHEETS_LOADED);

          const notConfirmedDaySheetDtos = daySheetDtos.filter(daySheetDto => !daySheetDto.confirmed);
          setDaySheetDtos(notConfirmedDaySheetDtos);
        }).catch(() => {
            toast.error(toastMessages.DATA_NOT_LOADED);
        });
      }
    }, []);

    const confirmDaySheet = async (updateDay: DaySheetDto) => {
      if (updateDay.id) {
        const updateDayRequest: UpdateConfirmedRequest = {
          id: updateDay.id
        };

        getDaySheetControllerApi().updateConfirmed(updateDayRequest).then(() => {
            close();
            toast.success(toastMessages.DAYSHEET_CONFIRMED);
        }).catch(() => {
            toast.error(toastMessages.DAYSHEET_CONFIRMED_ERROR);
        });
      }
    };

    const navigateToSingleDay = () => {
        if (selectedDaySheetDto != undefined && selectedDaySheetDto.date != undefined) {
            const dateString = encodeURIComponent(selectedDaySheetDto.date.toString());
            router.push(`/working-hours-single-day?date=${dateString}`);
        }
    };

    return (
        <div>
            <Title1>Kontrolle Arbeitszeit</Title1>
            <Table
                data={daySheetDtos}
                columns={[
                    {
                        header: "Datum",
                        title: "date"
                    },
                    {
                        header: "Teilnehmer",
                        title: "Peter"
                    },
                    {
                        header: "Erfasste Arbeitszeit",
                        title: "workHours"
                    }
                ]}
                actions={[
                    {
                        icon: Checkmark24Regular,
                        label: "Bestätigen",
                        onClick: (id) => {
                            let daySheetDto = daySheetDtos[id];
                            if (daySheetDto !== undefined) {
                                confirmDaySheet(daySheetDto);
                            }
                        }
                    },
                    {
                        icon: Edit24Regular,
                        onClick: (id) => {
                            setSelectedDaySheetDto(daySheetDtos[id]);
                            navigateToSingleDay();
                        }
                    }
                ]}>

            </Table>
        </div>
    );
};
