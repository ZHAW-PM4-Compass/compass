'use client';
import React, {useEffect} from 'react';
import {WorkHourDto} from "@/api/compassClient";

const Home: React.FC = () => {
    // Sample data for demonstration
    const mockdata: WorkHourDto[] = [
        { daySheetId: 0, date: '2024-04-14', confirmed: false, workHours: 2.0, participant: { id: 0, name: "Hans"} },
        { daySheetId: 0, date: '2024-04-13', confirmed: false, workHours: 3.5, participant: { id: 0, name: "Alice"} },
        { daySheetId: 0, date: '2024-04-12', confirmed: false, workHours: 4.0, participant: { id: 0, name: "Bob"} },
        { daySheetId: 0, date: '2024-04-11', confirmed: false, workHours: 1.5, participant: { id: 0, name: "Eve"} },
    ];

    let data: WorkHourDto[] = [];
    let initLoad = false;

    useEffect(() => {
        if (!initLoad) {
            initLoad = true;
            // Method to call when the component mounts
            getAllDaysheets()
                .then((result) => {
                    data = result;
                    alert('Done fetching daysheets');
                })
                .catch(() => {
                    data = mockdata;
                    alert('Error fetching daysheets, using mock data');
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
        <div className="p-5 sm:p-10 bg-slate-100 w-full h-full flex justify-center">
            <div className="w-2/4">
                <h1 className="text-xl font-bold mb-5">Home</h1>
                <div>
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-200">
                        <tr>
                            <th
                                scope="col"
                                className="px-6 py-3 text-left text-xs font-bold text-black uppercase tracking-wider"
                            >
                                Datum
                            </th>
                            <th
                                scope="col"
                                className="px-6 py-3 text-left text-xs font-bold text-black uppercase tracking-wider"
                            >
                                Teilnehmer
                            </th>
                            <th
                                scope="col"
                                className="px-6 py-3 text-left text-xs font-bold text-black uppercase tracking-wider"
                            >
                                Erfasste Arbeitszeit
                            </th>
                            <th
                                scope="col"
                                className="px-6 py-3 text-left text-xs font-bold text-black uppercase tracking-wider"
                            >
                                Bestätigen
                            </th>
                            <th
                                scope="col"
                                className="px-6 py-3 text-left text-xs font-bold text-black uppercase tracking-wider"
                            >
                                Bearbeiten
                            </th>
                        </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                        {data.map((item, index) => (
                            <tr key={index}>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{item.date}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{item.participant?.name}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{item.workHours} Stunden</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    <form onSubmit={handleConfirm}>
                                        <button type="submit" className="text-blue-600 hover:text-blue-900">
                                            Bestätigen
                                        </button>
                                    </form>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    <form onSubmit={handleEdit}>
                                        <button type="submit" className="text-blue-600 hover:text-blue-900">
                                            Bearbeiten
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
                <div>
                    <hr className="border-gray-200 mb-2"/>
                    <div className="px-6 py-4 bg-white text-sm text-black font-bold">Total: {getTotalTrackedTime()} Stunden</div>
                </div>
            </div>
        </div>
    );
};

export default Home;
