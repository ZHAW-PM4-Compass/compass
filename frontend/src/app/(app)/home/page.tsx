'use client';
import React, {useEffect, useState} from 'react';
import {ParticipantDto, UserDto, WorkHourDto} from "@/openapi/compassClient";
import Table from "@/components/table";
import {Checkmark24Regular, Delete24Regular, Edit24Regular} from "@fluentui/react-icons";

const Home: React.FC = () => {
    // Sample data for demonstration
    const mockdata: WorkHourDto[] = [
        { daySheetId: 0, date: '2024-04-14', confirmed: false, workHours: 2.0, participant: { id: 0, name: "Hans"} as ParticipantDto } as WorkHourDto,
        { daySheetId: 1, date: '2024-04-13', confirmed: false, workHours: 3.5, participant: { id: 0, name: "Alice"} as ParticipantDto } as WorkHourDto,
        { daySheetId: 2, date: '2024-04-12', confirmed: true, workHours: 4.0, participant: { id: 0, name: "Bob"} as ParticipantDto } as WorkHourDto,
        { daySheetId: 3, date: '2024-04-11', confirmed: false, workHours: 1.5, participant: { id: 0, name: "Eve"} as ParticipantDto } as WorkHourDto,
    ];

    const [data, setData] = useState<WorkHourDto[]>([]);
    let initLoad = false;

    useEffect(() => {
        if (!initLoad) {
            initLoad = true;
            // Method to call when the component mounts
            getAllDaysheets()
                .then((result) => {
                    let myList: WorkHourDto[] = []
                    mockdata.forEach((entry) => {
                        console.log(entry.confirmed);
                        if (!entry.confirmed) {
                            myList.push(entry);
                        }
                    });
                    setData(myList);
                    alert('Done fetching daysheets');
                })
                .catch(() => {
                    let myList: WorkHourDto[] = []
                    mockdata.forEach((entry) => {
                        console.log(entry.confirmed);
                        if (!entry.confirmed) {
                            myList.push(entry);
                        }
                    });
                    setData(myList);
                    alert('Error fetching daysheets, using mocked data');
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


    const handleConfirm = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        alert('Confirm button clicked!');
    };

    const handleEdit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        alert('Edit button clicked!');
    };

    const getTotalTrackedTime = () => {
        let total = 0;
        data.forEach((item) => {
            if (item.workHours) {
                total += item.workHours;
            }
        });
        return total.toFixed(2); // Round to 2 decimal places
    };

    return (
        <div>
            <Table
                data={data}
                columns={[
                    {
                        header: "Datum",
                        title: "date"
                    },
                    {
                        header: "Teilnehmer",
                        title: "participant?.name"
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
                        onClick: () => {
                        }
                    },
                    {
                        icon: Edit24Regular,
                        onClick: (id) => {
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
