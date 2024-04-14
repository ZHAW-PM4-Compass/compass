'use client';
import React from 'react';

const Home: React.FC = () => {
    // Sample data for demonstration
    const data = [
        { date: '2024-04-14', participant: 'John', trackedTime: 2.0 },
        { date: '2024-04-13', participant: 'Alice', trackedTime: 3.5 },
        { date: '2024-04-12', participant: 'Bob', trackedTime: 4.0 },
        { date: '2024-04-11', participant: 'Eve', trackedTime: 1.5 },
    ];

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
            total += item.trackedTime;
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
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{item.participant}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{item.trackedTime} Stunden</td>
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
