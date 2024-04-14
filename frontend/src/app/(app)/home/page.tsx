'use client';
import React from 'react';

const Home: React.FC = () => {
    // Sample data for demonstration
    const data = [
        { date: '2024-04-14', participant: 'John', trackedTime: '2 hours' },
        { date: '2024-04-13', participant: 'Alice', trackedTime: '3.5 hours' },
        { date: '2024-04-12', participant: 'Bob', trackedTime: '4 hours' },
        { date: '2024-04-11', participant: 'Eve', trackedTime: '1.5 hours' },
    ];

    const handleConfirm = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        alert('Confirm button clicked!');
    };

    const handleEdit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        alert('Edit button clicked!');
    };

    return (
        <div className="p-5 sm:p-10 bg-slate-100 w-full h-full">
            <h1 className="text-xl font-bold">Home</h1>
            <div className="mt-5">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                    <tr>
                        <th
                            scope="col"
                            className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        >
                            Datum
                        </th>
                        <th
                            scope="col"
                            className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        >
                            Teilnehmer
                        </th>
                        <th
                            scope="col"
                            className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        >
                            Erfasste Arbeitszeit
                        </th>
                        <th
                            scope="col"
                            className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        >
                            Bestätigen
                        </th>
                        <th
                            scope="col"
                            className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        >
                            Bearbeiten
                        </th>
                    </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                    {data.map((item, index) => (
                        <tr key={index}>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{item.date}</td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{item.participant}</td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{item.trackedTime}</td>
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
        </div>
    );
};

export default Home;
