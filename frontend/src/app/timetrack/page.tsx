'use client';
// Ensure all imports are at the top
import React, { useState, ChangeEvent } from "react";
import { DaySheetControllerApi, CreateDaySheetDto } from './../../../compassClient';

// Typing the component props (if any) enhances readability and maintainability
interface TimetrackExampleProps {}

const TimetrackExample: React.FC<TimetrackExampleProps> = () => {
  const [daySheet, setDaySheet] = useState<{ id: string; date: string; dayReport: string }>({ id: '', date: '', dayReport: '' });
  const api = new DaySheetControllerApi();

  const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setDaySheet({ ...daySheet, [name]: value });
  };

  const handleCreateSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const createDaySheetDto: CreateDaySheetDto = {
      date: new Date(daySheet.date),
      dayReport: daySheet.dayReport,
    };

    try {
      const response = await api.createDaySheet({ createDaySheetDto });
      alert("Day sheet created successfully!");
      console.log(response);
    } catch (error) {
      console.error("Failed to create day sheet:", error);
      alert("Failed to create day sheet.");
    }
  };

  const fetchDataById = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const id = parseInt(daySheet.id);
      const data = await api.getDaySheetById({ id });
      console.log(data);
      const element = document.getElementById('dataDisplay');
      if (element) {
        element.innerHTML = JSON.stringify(data, null, 2);
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
