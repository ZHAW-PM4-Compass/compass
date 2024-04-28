'use client';
import React, {useEffect, useState} from 'react';
import {ParticipantDto, UpdateDaySheetDto, WorkHourDto} from "@/openapi/compassClient";
import Table from "@/components/table";
import {Checkmark24Regular, Edit24Regular} from "@fluentui/react-icons";
import {toast} from "react-hot-toast";
import {useRouter} from "next/navigation";
import Title1 from "@/components/title1";

const Home: React.FC = () => {
    // Sample data for demonstration
    const mockdata: WorkHourDto[] = [
        { daySheetId: 0, date: '2024-04-14', confirmed: false, workHours: 2.0, participant: { id: 0, name: "Hans"} as ParticipantDto } as WorkHourDto,
        { daySheetId: 1, date: '2024-04-13', confirmed: false, workHours: 3.5, participant: { id: 0, name: "Alice"} as ParticipantDto } as WorkHourDto,
        { daySheetId: 2, date: '2024-04-12', confirmed: true, workHours: 4.0, participant: { id: 0, name: "Bob"} as ParticipantDto } as WorkHourDto,
        { daySheetId: 3, date: '2024-04-11', confirmed: false, workHours: 1.5, participant: { id: 0, name: "Eve"} as ParticipantDto } as WorkHourDto,
    ];

    const [workHourDtos, setWorkHourDtos] = useState<WorkHourDto[]>([]);
    const [selectedWorkHourDto, setSelectedWorkHourDto] = useState<WorkHourDto>();
    const router = useRouter();

    let initLoad = false;
    useEffect(() => {
        if (!initLoad) {
            initLoad = true;
            // Method to call when the component mounts
            getAllDaysheets()
                .then((rsult) => {
                    let myList: WorkHourDto[] = []
                    rsult.forEach((entry: WorkHourDto) => {
                        if (!entry.confirmed) {
                            myList.push(entry);
                        }
                    });
                    setWorkHourDtos(myList);
                    toast.success('Done fetching daysheets');
                })
                .catch(() => {
                    let myList: WorkHourDto[] = []
                    mockdata.forEach((entry) => {
                        if (!entry.confirmed) {
                            myList.push(entry);
                        }
                    });
                    setWorkHourDtos(myList);
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

    const updateDaySheet = async (updateDay: UpdateDaySheetDto) => {
        try {
            // Make a PUT request using the fetch API
            const response = await fetch(`http://localhost:8080/api/daysheet/`, {
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

    const mapWorkHourDtoToUpdate = (workHourDto: WorkHourDto | undefined) => {
        if (workHourDto != undefined) {
            let updateDaySheetDto: UpdateDaySheetDto = {};
            updateDaySheetDto.id = workHourDto.daySheetId;
            updateDaySheetDto.date = workHourDto.date;
            return updateDaySheetDto;
        }
        return {};
    }

    const getTotalTrackedTime = () => {
        let total = 0;
        workHourDtos.forEach((item) => {
            if (item.workHours) {
                total += item.workHours;
            }
        });
        return total.toFixed(2); // Round to 2 decimal places
    };

    const navigateToSingleDay = () => {
        if (selectedWorkHourDto != undefined && selectedWorkHourDto.date != undefined) {
            const dateString = encodeURIComponent(selectedWorkHourDto.date.toString());
            router.push(`/working-hours-single-day?date=${dateString}`);
        }
    };

    const participantName = (workHourDto: WorkHourDto): string => {
        if (workHourDto !== undefined) {
            if (workHourDto.participant !== undefined) {
                if (workHourDto.participant.name != undefined) return workHourDto.participant.name;
            }
        }
        return '';
    }

    return (
        <div>
            <Title1>Kontrolle Arbeitszeit</Title1>
            <Table
                data={workHourDtos}
                columns={[
                    {
                        header: "Datum",
                        title: "date"
                    },
                    {
                        header: "Teilnehmer",
                        titleFunction: participantName
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
                            if (workHourDtos[id] !== undefined) {
                                updateDaySheet(mapWorkHourDtoToUpdate(workHourDtos[id]));
                            }
                        }
                    },
                    {
                        icon: Edit24Regular,
                        onClick: (id) => {
                            setSelectedWorkHourDto(workHourDtos[id]);
                            navigateToSingleDay();
                        }
                    }
                ]}>

            </Table>
            <div>
                <hr className="border-gray-200 mb-2"/>
                <div
                    className="px-6 py-4 bg-white text-sm text-black font-bold">Total: {getTotalTrackedTime()} Stunden
                </div>
            </div>
        </div>
    );
};

export default Home;
