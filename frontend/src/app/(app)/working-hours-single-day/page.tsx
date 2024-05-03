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

function TimestampUpdateModal({close, onSave, timestamp}: Readonly<{
    close: () => void;
    onSave: () => void;
    timestamp: TimestampDto | undefined;
}>) {
    return (
        <Modal
            title="Zeit bearbeiten"
            footerActions={
                <Button Icon={Save24Regular} type="submit">Speichern</Button>
            }
            close={close}
            onSubmit={onSave}
        >
            {/*<Input type="text" placeholder="Startuhrzeit" className="mb-4 mr-4 w-48 inline-block" name="given_name"
                   required={true} value={timestamp?.startTime}/>
            <Input type="text" placeholder="Enduhrzeit" className="mb-4 mr-4 w-48 inline-block" name="family_name"
        required={true} value={timestamp?.endTime}/>*/}
        </Modal>
    );
}

const DaySheetViewSingleDay: React.FC = () => {
    const [currentDate, setCurrentDate] = useState<Date | undefined>();
    const [selectedTimestamp, setSelectedTimestamp] = useState<TimestampDto>();
    const [timestamps, setTimestamps] = useState<TimestampDto[]>([]);
    const [showUpdateModal, setShowUpdateModal] = useState(false);

    const mockdata: TimestampDto[] = [
        {id: 0, day_sheet_id: 0, start_time: "08:00", end_time: "17:00"} as TimestampDto,
        {id: 1, day_sheet_id: 1, start_time: "08:15", end_time: "17:55"} as TimestampDto,
        {id: 2, day_sheet_id: 2, start_time: "08:20", end_time: "17:05"} as TimestampDto,
        {id: 3, day_sheet_id: 3, start_time: "08:45", end_time: "17:00"} as TimestampDto,
    ];

    let initLoad = false;
    useEffect(() => {
        if (!initLoad) {
            initLoad = true;
            const queryParams = new URLSearchParams(window.location.search);
            const dateString = queryParams.get('date');

            // If the date parameter exists in the query string, parse it and set it to the state
            if (dateString) {
                const parsedDate = new Date(dateString);
                setCurrentDate(parsedDate);
                //TODO daysheet: tranfer data of selected date from previous page or load it from backend, i did it over the url, maybe it works as well
            }
        }
    }, []);

    const getDaysheet = async (date: Date) => {
        try {
            // Make a GET request using the fetch API
            const response = await fetch(`http://localhost:8080/api/daysheet/getByDate/${date}`);

            // Check if the response is successful (status code 200)
            if (!response.ok) {
                // Handle non-successful response (e.g., throw an error)
                throw new Error(`Failed to fetch data`);
            }

            // Parse the response body as JSON
            // Return the data
            return await response.json();
        } catch (error) {
            // Handle any errors (e.g., log the error)
            console.error('Error fetching data:', error);
            // rethrow the error to let the caller handle it
            throw error;
        }
    };

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
            //if (ts.end_time != undefined && ts.start_time) return calculateTimeDifference(ts.start_time, ts.end_time) + " Stunden:Minuten";
        }
        return '';
    }

    const onDelete = (id: number) => {
        getTimestampControllerApi().deleteTimestamp({ id }).then((response) => {
            close();
            toast.success(toastMessages.TIMESTAMP_DELETED);
        }).catch(() => {
            toast.error(toastMessages.TIMESTAMP_NOT_DELETED);
        });
    }

    const onSave = (timestampDto: TimestampDto) => {
        getTimestampControllerApi().putTimestamp({ timestampDto }).then((response) => {
            close();
            toast.success(toastMessages.TIMESTAMP_UDPATED);
        }).catch(() => {
            toast.error(toastMessages.TIMESTAMP_NOT_UPDATED);
        });
    }

    return (
        <div>
            {showUpdateModal && (
                <TimestampUpdateModal
                    close={() => setShowUpdateModal(false)}
                    onSave={() => {
                        if (selectedTimestamp != undefined) onSave(selectedTimestamp);
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
                        title: "start_time"
                    },
                    {
                        header: "Enduhrzeit",
                        title: "end_time"
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
                        onClick: (id) => {
                            onDelete(id)
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
