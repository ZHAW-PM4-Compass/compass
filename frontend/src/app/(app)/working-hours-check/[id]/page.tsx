'use client';
import React, {useEffect, useState} from 'react';
import Table from "@/components/table";
import {Delete24Regular, Edit24Regular, Save24Regular} from "@fluentui/react-icons";
import Title1 from "@/components/title1";
import Button from "@/components/button";
import {toast} from "react-hot-toast";
import Modal from "@/components/modal";
import Input from "@/components/input";
import {TimestampDto} from "@/openapi/compassClient";
import {getTimestampControllerApi} from "@/openapi/connector";
import toastMessages from "@/constants/toastMessages";
import { convertMilisecondsToTimeString } from '@/utils/time';

function TimeStampUpdateModal({ close, onSave, timestamp }: Readonly<{
    close: () => void;
    onSave: () => void;
    timestamp: TimestampDto | undefined;
}>) {
    const [updatedTimestamp, setTimestamp] = useState<{ startTime: string; endTime: string;}>({ startTime: '', endTime: ''});

    const onSubmit = (formData: FormData) => {
        const editedTimestamp: TimestampDto = {
            id: timestamp?.id || 0,
            daySheetId: timestamp?.daySheetId || 0,
            startTime: formData.get('startTime') as string,
            endTime: formData.get('endTime') as string
        };

        getTimestampControllerApi().putTimestamp({timestampDto: editedTimestamp}).then(() => {
            close();
            setTimeout(() => onSave(), 1000);
            toast.success(toastMessages.TIMESTAMP_UPDATED);
        }).catch(() => {
            toast.error(toastMessages.TIMESTAMP_NOT_UPDATED);
        });
    }

    const handleTimeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        setTimestamp(prevState => ({ ...prevState, [name]: value }));
    };

    useEffect(() => {
        setTimestamp({startTime: timestamp?.startTime || "00:00", endTime: timestamp?.endTime || "00:00"})
    }, []);

    return (
        <Modal
            title="Zeiteintrag bearbeiten"
            footerActions={
                <Button Icon={Save24Regular} type="submit">Speichern</Button>
            }
            close={close}
            onSubmit={onSubmit}
        >
            <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="startTime" required={true} value={updatedTimestamp.startTime} onChange={handleTimeChange}/>
            <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="endTime" required={true} value={updatedTimestamp.endTime} onChange={handleTimeChange} />
        </Modal>
    );
}

export default function WorkingHoursCheckByIdPage({ params }: { params: { id: number } }) {
    const [currentDate, setCurrentDate] = useState<Date | undefined>();
    const [selectedTimestamp, setSelectedTimestamp] = useState<TimestampDto>();
    const [timestamps, setTimestamps] = useState<TimestampDto[]>([]);
    const [showUpdateModal, setShowUpdateModal] = useState(false);

    useEffect(() => {
        fetchTimestamps();
    }, []);

    function fetchTimestamps() {
        getTimestampControllerApi().getAllTimestampByDaySheetId({ id: params.id }).then(timestamps => {
            close();
            timestamps.sort((a, b) => {
                const startTimeAHour = a.startTime?.split(':').map(Number)[0] ?? 0;
                const startTimeBHour = b.startTime?.split(':').map(Number)[0] ?? 0;
                
                return startTimeAHour - startTimeBHour;
            });
            setTimestamps(timestamps);
        }).catch(() => {
            toast.error(toastMessages.TIMESTAMPS_NOT_LOADED);
        });
    }

    const calculateDuration = (timestamp: TimestampDto): string => {
        if (timestamp.startTime && timestamp.endTime) {
            const startDate = new Date(`01/01/2000 ${timestamp.startTime}`);
            const endDate = new Date(`01/01/2000 ${timestamp.endTime}`);
            const timestampDuration = endDate.getTime() - startDate.getTime();
            return convertMilisecondsToTimeString(timestampDuration);
        }
        return '';
    }

    const onDelete = (key: number) => {
        if (timestamps != undefined) {
            const id = timestamps[key]?.id;
            if (id)
            getTimestampControllerApi().deleteTimestamp({ id }).then((response) => {
                close();
                toast.success(toastMessages.TIMESTAMP_DELETED);
                fetchTimestamps();
            }).catch(() => {
                toast.error(toastMessages.TIMESTAMP_NOT_DELETED);
            });
        }
    }

    return (
        <div>
            {showUpdateModal && (
                <TimeStampUpdateModal
                    close={() => setShowUpdateModal(false)}
                    onSave={fetchTimestamps}
                    timestamp={selectedTimestamp}/>
            )}
            <div className="flex flex-col sm:flex-row justify-between">
                <Title1 className="mb-5">Kontrolle Arbeitszeit</Title1>
                <div className="mt-2 sm:mt-0 font-bold">
                    {currentDate && <p>{currentDate.toDateString()}</p>}
                </div>
            </div>
            <Table
                data={timestamps}
                columns={[
                    {
                        header: "Startuhrzeit",
                        title: "startTime"
                    },
                    {
                        header: "Enduhrzeit",
                        title: "endTime"
                    },
                    {
                        header: "Dauer",
                        titleFunction: calculateDuration
                    }
                ]}
                actions={[
                    {
                        icon: Delete24Regular,
                        label: "Löschen",
                        onClick: (key) => {
                            onDelete(key);
                            fetchTimestamps();
                        }
                    },
                    {
                        icon: Edit24Regular,
                        onClick: (id) => {
                            setSelectedTimestamp(timestamps[id]);
                            setShowUpdateModal(true);
                        }
                    }
                ]}>

            </Table>
        </div>
    );
};
