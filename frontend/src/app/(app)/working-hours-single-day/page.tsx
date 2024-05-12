'use client';
import React, {useEffect, useState} from 'react';
import Table from "@/components/table";
import {Delete24Regular, Edit24Regular, Save24Regular} from "@fluentui/react-icons";
import Title1 from "@/components/title1";
import Button from "@/components/button";
import {toast} from "react-hot-toast";
import Modal from "@/components/modal";
import Input from "@/components/input";
import {GetAllTimestampByDaySheetIdRequest, TimestampDto} from "@/openapi/compassClient";
import {getTimestampControllerApi} from "@/openapi/connector";
import toastMessages from "@/constants/toastMessages";

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
            startTime: formData.get("startTime") as string,
            endTime: formData.get("endTime") as string
        };

        if (editedTimestamp.startTime?.length == 5) {
            editedTimestamp.startTime += ":00";
        }

        if (editedTimestamp.endTime?.length == 5) {
            editedTimestamp.endTime += ":00";
        }

        getTimestampControllerApi().putTimestamp({timestampDto: editedTimestamp}).then(() => {
            close();//
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

    useEffect(() => {setTimestamp({startTime: timestamp?.startTime || "00:00", endTime: timestamp?.endTime || "00:00"})}, []);

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

const DaySheetViewSingleDay: React.FC = () => {
    const [currentDate, setCurrentDate] = useState<Date | undefined>();
    const [selectedTimestamp, setSelectedTimestamp] = useState<TimestampDto>();
    const [timestamps, setTimestamps] = useState<TimestampDto[]>([]);
    const [showUpdateModal, setShowUpdateModal] = useState(false);
    const [daySheetId, setDaySheetId] = useState<number | null>();

    let initLoad = false;
    useEffect(() => {
        if (!initLoad) {
            initLoad = true;
            const queryParams = new URLSearchParams(window.location.search);
            const daySheetId = queryParams.get('day-sheet');
            const id = parseInt(daySheetId!);
            setDaySheetId(id);
            fetchTimestamps(id);
        }
    }, []);

    function fetchTimestamps(id: number) {
        const param: GetAllTimestampByDaySheetIdRequest = { id };

        getTimestampControllerApi().getAllTimestampByDaySheetId(param).then(timestampDtos => {
                close();
                toast.success(toastMessages.TIMESTAMPS_LOADED);
                timestampDtos.sort((a, b) => {
                    if (a.id === undefined && b.id === undefined) return 0; // Both IDs are undefined, maintain current order
                    if (a.id === undefined) return 1; // Place undefined ID at the end
                    if (b.id === undefined) return -1; // Place undefined ID at the end
                    return a.id - b.id; // Compare IDs if both are defined
                });
                setTimestamps(timestampDtos);
            })
            .catch(() => {
                toast.error(toastMessages.DATA_NOT_LOADED);
            });
    }

    // Function to calculate the difference between start and end times
    function calculateTimeDifference(start_time: string, end_time: string): string {
        if (!start_time || !end_time) {
            return ''; // Handle case where either start or end time is not provided
        }

        // Parse hours and minutes from time strings
        const [startHours, startMinutes] = start_time.split(':').map(Number);
        const [endHours, endMinutes] = end_time.split(':').map(Number);

        // Calculate the difference in minutes
        if (startHours != undefined && startMinutes != undefined && endHours != undefined && endMinutes != undefined) {
            const startTimeInMinutes = startHours * 60 + startMinutes;
            const endTimeInMinutes = endHours * 60 + endMinutes;
            const differenceInMinutes = endTimeInMinutes - startTimeInMinutes;

            // Convert difference in minutes back to hours and minutes format
            const hours = Math.floor(differenceInMinutes / 60);
            const minutes = differenceInMinutes % 60;

            // Return the difference in hh:mm format
            return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
        }
        return '';
    }

    const calculateDuration = (ts: TimestampDto): string => {
        if (ts != undefined) {
            if (ts.endTime != undefined && ts.startTime) return calculateTimeDifference(ts.startTime, ts.endTime) + " Stunden:Minuten";
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
                    onSave={() => {
                        if (daySheetId) fetchTimestamps(daySheetId);
                    }}
                    timestamp={selectedTimestamp}/>
            )}
            <div className="flex flex-col sm:flex-row justify-between">
                <Title1>Kontrolle Arbeitszeit</Title1>
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
                            onDelete(key)
                            if (daySheetId) fetchTimestamps(daySheetId)
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

export default DaySheetViewSingleDay;
