'use client';
import React, { useState, ChangeEvent } from "react";
// Adjust the import paths according to your project structure
import { DaySheetControllerApi, CreateDaySheetDto } from './../../api/compassClient';

interface TimetrackExampleProps {}

const TimetrackExample: React.FC<TimetrackExampleProps> = () => {
  const [daySheet, setDaySheet] = useState<{ id: string; date: string; dayReport: string }>({ id: '', date: '', dayReport: '' });
  // Instantiate the API client with Axios configuration if needed
  const api = new DaySheetControllerApi();

  const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setDaySheet({ ...daySheet, [name]: value });
  };

  const handleCreateSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    // Ensure correct date formatting if required by your backend, e.g., ISO string
    const createDaySheetDto: CreateDaySheetDto = {
      date: daySheet.date, // Assuming 'date' is expected to be a string in ISO format
      day_report: daySheet.dayReport,
    };

    try {
      // Directly pass parameters without wrapping in an object
      const response = await api.createDaySheet(createDaySheetDto);
      alert("Day sheet created successfully!");
      console.log(response.data); // Accessing data property for Axios response
    } catch (error) {
      console.error("Failed to create day sheet:", error);
      alert("Failed to create day sheet.");
    }
  };

  const fetchDataById = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const id = parseInt(daySheet.id);
      // Directly pass parameters without wrapping in an object
      const response = await api.getDaySheetById(id);
      console.log(response.data); // Accessing data property for Axios response
      const element = document.getElementById('dataDisplay');
      if (element) {
        element.innerHTML = JSON.stringify(response.data, null, 2);
      }
    } catch (error) {
      console.error("Failed to fetch day sheet:", error);
      const element = document.getElementById('dataDisplay');
      if (element) {
        element.innerHTML = "Failed to fetch day sheet.";
      }
    }
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24 bg-gradient-to-br from-gray-50 to-gray-100">
      <h1 className="text-4xl font-bold dark:text-white">Time Tracking ⏲️</h1>

      <form className="mb-8" onSubmit={handleCreateSubmit}>
        <h2 className="text-xl font-semibold">Create Day Sheet</h2>
        <input type="date" name="date" placeholder="Date" value={daySheet.date} onChange={handleInputChange} />
        <textarea name="dayReport" placeholder="Day Report" value={daySheet.dayReport} onChange={handleInputChange} />
        <button type="submit" className="px-4 py-2 bg-blue-500 text-white rounded-md">Create Day Sheet</button>
      </form>

      <form onSubmit={fetchDataById}>
        <h2 className="text-xl font-semibold">Get Day Sheet By ID</h2>
        <input type="text" name="id" placeholder="ID" value={daySheet.id} onChange={handleInputChange} />
        <button type="submit" className="px-4 py-2 bg-green-500 text-white rounded-md">Fetch Day Sheet</button>
      </form>

      <pre id="dataDisplay" className="text-left"></pre>
    </main>
  );
};

export default TimetrackExample;
