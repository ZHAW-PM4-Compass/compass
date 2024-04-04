'use client';

import { createDaySheet } from "@/actions/workingHours";

export default async function WorkingHoursPage() {
  return (
    <div className="p-5 sm:p-10 w-full h-full">
      <h1 className="text-xl font-bold">Day Sheet erfassen</h1>
      <form className="mt-5" action={createDaySheet}>
        <div className="flex flex-col w-96">
          <input type="date" name="date" className="border border-gray-300 rounded-md p-2" />
          <textarea name="dayReport" className="border border-gray-300 rounded-md p-2 mt-2" />
        </div> 
        <button type="submit" className="bg-blue-500 text-white rounded-md p-2 mt-2">Speichern</button>
      </form>
    </div>
  );
};