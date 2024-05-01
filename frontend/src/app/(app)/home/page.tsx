'use client';
import React, {useEffect, useState} from 'react';
import {DaySheetDto, ParticipantDto,} from "@/openapi/compassClient";
import Table from "@/components/table";
import {Checkmark24Regular, Edit24Regular} from "@fluentui/react-icons";
import {toast} from "react-hot-toast";
import {useRouter} from "next/navigation";
import Title1 from "@/components/title1";

const Home: React.FC = () => {
    // Sample data for demonstration
    const mockdata: DaySheetDto[] = [
        { daySheetId: 0, date: '2024-04-14', confirmed: false, workHours: 2.0, participant: { id: 0, name: "Hans"} as ParticipantDto } as DaySheetDto,
        { daySheetId: 1, date: '2024-04-13', confirmed: false, workHours: 3.5, participant: { id: 0, name: "Alice"} as ParticipantDto } as DaySheetDto,
        { daySheetId: 2, date: '2024-04-12', confirmed: true, workHours: 4.0, participant: { id: 0, name: "Bob"} as ParticipantDto } as DaySheetDto,
        { daySheetId: 3, date: '2024-04-11', confirmed: false, workHours: 1.5, participant: { id: 0, name: "Eve"} as ParticipantDto } as DaySheetDto,
    ];

    const [daySheetDtos, setDaySheetDtos] = useState<DaySheetDto[]>([]);
    const [selectedDaySheetDto, setSelectedDaySheetDto] = useState<DaySheetDto>();
    const router = useRouter();

    let initLoad = false;
    useEffect(() => {
        if (!initLoad) {
            initLoad = true;
            // Method to call when the component mounts
            getAllDaysheets()
                .then((rsult) => {
                    let myList: DaySheetDto[] = []
                    rsult.forEach((entry: DaySheetDto) => {
                        if (!entry.confirmed) {
                            myList.push(entry);
                        }
                    });
                    setDaySheetDtos(myList);
                    toast.success('Done fetching daysheets');
                })
                .catch(() => {
                    let myList: DaySheetDto[] = []
                    mockdata.forEach((entry) => {
                        if (!entry.confirmed) {
                            myList.push(entry);
                        }
                    });
                    setDaySheetDtos(myList);
                    toast.error('Error fetching daysheets, using mocked data');
                });
        }
    }, []); // Empty dependency array ensures the effect runs only once, similar to componentDidMount

    const getAllDaysheets = async () => {
        try {
            // Make a GET request using the fetch API
            const response = await fetch(`http://localhost:8080/api/daysheet/getAll/`);

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

    const confirmDaySheet = async (updateDay: DaySheetDto) => {
        try {
            // Make a PUT request using the fetch API
            const response = await fetch(`http://localhost:8080/api/daysheet/updateConfirmed`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updateDay)
            });

            // Check if the response is successful (status code 200)
            if (!response.ok) {
                // Handle non-successful response (e.g., throw an error)
                throw new Error(`Failed to update day sheet`);
            }

            // Parse the response body as JSON
            // Return the data
            return await response.json();
        } catch (error) {
            // Handle any errors (e.g., log the error)
            console.error('Error updating day sheet:', error);
            // rethrow the error to let the caller handle it
            throw error;
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

export default Home;
