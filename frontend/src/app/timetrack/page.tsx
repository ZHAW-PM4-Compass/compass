"use client";

import { useRef, useState} from 'react';

const SimplePage: React.FC = () => {
  const [daySheetData, setDaySheetData] = useState<any>(null);
  const [sheetId, setSheetId] = useState<string>('');

  const dateRef = useRef<HTMLInputElement>(null);
  const sheetIdRef = useRef<HTMLInputElement>(null);

  const handleSubmitDaysheet = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const dateValue = dateRef.current?.value;
    const dayReport = "This is a report";
    

    if ( !dateValue || dayReport === undefined) {
      console.error('All fields are required');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/daysheet', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          date: dateValue,
          dayReport: dayReport
        }),
      });

      if (response.ok) {
        // Timestamp created successfully
        console.log('Timestamp created successfully');
        // You can handle success here, e.g., show a success message or redirect the user
      } else {
        // Error occurred while creating timestamp
        console.error('Error creating timestamp:', response.statusText);
        // You can handle error here, e.g., show an error message to the user
      }
    } catch (error: any) {
      console.error('Error creating timestamp:', error.message);
      // You can handle error here, e.g., show an error message to the user
    }
  };



  const fetchDaySheet = async (sheetId: string) => {
    try {
      const response = await fetch('http://localhost:8080/api/daysheet/' + sheetId , {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        setDaySheetData(data);
        console.log('');
      } else {
        console.log(`Error: ${response.statusText}`);
      }
    } catch (error) {
      console.log(`Error: ${error}`);
    }
  };
  const handleGetDaySheet = async () => {
    await fetchDaySheet(sheetId);
  };


  return (
    <div>
      <h1>Erstelle einen Zeiteintrag</h1>
      <br></br>
      <form onSubmit={handleSubmitDaysheet}>
      <div>
          <label>
            Date:
            <input
              type="date"
              ref={dateRef}
              required
            />
          </label>
        </div>
        <button className="border: 1px" type="submit">Create Daysheet</button>
      </form>
      <hr/>

      <div>
         <label>
          Enter Sheet ID:
          <input
            type="text"
            value={sheetId}
            onChange={(e) => setSheetId(e.target.value)}
            />
          </label><br/>
      <button onClick={handleGetDaySheet}>Get Day Sheet</button>
      
        {daySheetData && (
          <div>
            <h2>Day Sheet Data:</h2>
           
            <p>Date: {daySheetData.date}</p>
            {/* Render other fields from the day sheet data */}
          </div>
          
        )}
       
    </div>
    </div>
  );
};

export default SimplePage;
