'use client';

import Title1 from "@/components/title1";
import Table from "@/components/table";
import Input from "@/components/input";
import ArrowLeftIcon from "@fluentui/svg-icons/icons/arrow_left_24_filled.svg";
import ArrowRightIcon from "@fluentui/svg-icons/icons/arrow_right_24_filled.svg";
import SaveIcon from "@fluentui/svg-icons/icons/save_24_filled.svg";
import { useEffect, useState } from "react";
import { getDaySheetControllerApi, getTimestampControllerApi} from "@/openapi/connector";
import Button from "@/components/button";
import Modal from "@/components/modal";
import type { DaySheetDto, TimestampDto } from "@/openapi/compassClient";
import toast from "react-hot-toast";

export default function WorkingHoursPage() {
  const [daySheet, setDaySheet] = useState<{ id: number; date: string; day_report: string; timestamps: Timestamp[];}>({ id: 0, date: '', day_report: '', timestamps: []});
  const [timestamp, setTimestamp] = useState<{ start_time: string; end_time: string;}>({ start_time: '', end_time: ''});
  const [selectedTimestamp, setSelectedTimestamp] = useState<TimestampDto>();
  const [selectedDate, setSelectedDate] = useState<string>(new Date().toISOString().slice(0, 10));
  
  interface Timestamp {
    id?: number;
    day_sheet_id: number;
    start_time: string;
    end_time: string;
    duration?: string;
  }

  const calculateDuration = (start: string, end: string) => {
    const startTime = new Date(`2000-01-01 ${start}`).getTime();
    const endTime = new Date(`2000-01-01 ${end}`).getTime();
    const durationMs = endTime - startTime;
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    return `${hours}h ${minutes}m`;
  };

  function calculateTotalDuration(timestamps: Array<Timestamp>) {
    
    const calculateDuration = (start: string, end: string) => {
      const startTime = new Date(`2000-01-01 ${start}`).getTime();
      const endTime = new Date(`2000-01-01 ${end}`).getTime();
      const durationMs = endTime - startTime;
      return durationMs / (1000 * 60); // Return duration in minutes
    };
  
    const totalDurationMinutes = timestamps.reduce((total, timestamp) => {
      const duration = calculateDuration(timestamp.start_time, timestamp.end_time);
      return total + duration;
    }, 0);
  
    const totalHours = Math.floor(totalDurationMinutes / 60);
    const totalMinutes = totalDurationMinutes % 60;
  
    return `${totalHours}h ${totalMinutes}m`;
  }


  const handleDateChange = (date: any) => {
    setSelectedDate(date.target.value);
    loadDaySheetByDate(date.target.value);
  };
  
  const handlePrevDate = () => {
    let selectedDateObj = new Date(selectedDate);
    const newDate = new Date(selectedDate);
    
    newDate.setDate(selectedDateObj.getDate() - 1);
    setSelectedDate(newDate.toISOString().slice(0, 10));
    console.log("selected", newDate.toISOString().slice(0, 10));
    loadDaySheetByDate(newDate.toISOString().slice(0, 10));
  };

  const handleNextDate = () => {
    let selectedDateObj = new Date(selectedDate);
    const newDate = new Date(selectedDate);
    
    newDate.setDate(selectedDateObj.getDate() + 1); 
    setSelectedDate(newDate.toISOString().slice(0, 10));
    loadDaySheetByDate(newDate.toISOString().slice(0, 10));
  };


  const handleCreateTimestampSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // check if daysheet already exists
    getDaySheetControllerApi().getDaySheetById1(selectedDate).then(response => {
      var loadedDaySheet = null;

      if (response.status == 200) {
        // Daysheet exists -> add timestamp to existing daysheet
        loadedDaySheet = response?.data;

      } else if ( response.status == 404) {
        // Daysheet does not exist yet -> create new daysheet
        const newDaySheet: DaySheetDto = {
          date: selectedDate,
          day_notes: '',
          timestamps: []
        };

        // create new daysheet
        
        getDaySheetControllerApi().createDaySheet(newDaySheet).then(response => {
          if (response.status == 200) {
            loadedDaySheet = response?.data;
            
            console.log("Daysheet created successfully");
          } else{
            console.log("Daysheet could not be created");
          }

        }).catch(() => {
          console.error("Daysheet could not be created");
        });
        
      } else {
        console.error("Error occured while fetching daysheet");
      }

      // add timestamp
      if (loadedDaySheet != null) {
        const newTimestamp: Timestamp = {
          start_time: timestamp.start_time,
          end_time: timestamp.end_time,
          day_sheet_id: daySheet.id, 
          duration: calculateDuration(timestamp.start_time, timestamp.end_time)
        };
  
        getTimestampControllerApi().createTimestamp(newTimestamp).then(response => {
          if (response.status == 200) {
            newTimestamp.id = response?.data.id;
            daySheet.timestamps.push(newTimestamp);
            setDaySheet(daySheet);
            setTimestamp({ start_time: '', end_time: '' });
          } else{
            console.log("Timestamp could not be created");
          }
        });
      }
    });
  };


  const handleTimeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setTimestamp(prevState => ({ ...prevState, [name]: value }));
  };


	const loadDaySheetByDate = (date: String) => {
    /*
		getDaySheetControllerApi().getDaysheetByDate(date).then(response => {
			const daySheet = response?.data;

      daySheet.timestamps.forEach((timestamp: Timestamp) => {
        timestamp.duration = calculateDuration(timestamp.start_time, timestamp.end_time);
      });

      setDaySheet(daySheet); 
		}).catch(() => {
			toast.error(toastMessages.DATA_NOT_LOADED);
		});
    */
	}


  const deleteTimestamp = (timestamp: Timestamp) => {
    timestamp.id && getTimestampControllerApi().deleteTimestamp(timestamp.id).then(response => {
      if (response.status === 200) {
        toast.success("Zeiteintrag gelöscht");
        setDaySheet({...daySheet, timestamps: daySheet.timestamps.filter(timestampItem => timestampItem.id !== timestamp.id)});
      } else { 
        toast.success("Zeiteintrag konnte nicht gelöscht werden");
      }
    });
  }



  function TimeStampUpdateModal({ close, onSave, timestamp }: Readonly<{
    close: () => void;
    onSave: () => void;
    timestamp: Timestamp | undefined;
  }>) {
    const onSubmit = (formData: FormData) => {
      const editedTimestamp: TimestampDto = {
          id: timestamp?.id,
          day_sheet_id: timestamp?.day_sheet_id,
          start_time: formData.get("start_time") as string,
          end_time: formData.get("end_time") as string
      };
    
      getTimestampControllerApi().putTimestamp(editedTimestamp).then(response => {
        close();
        setTimeout(() => onSave(), 1000); 
        if (response.status === 200) {
          toast.success("Zeiteintrag bearbeitet");
          
          let editedDaySheet = {...daySheet};
          const editedStartTime = editedTimestamp.start_time || ''; // Provide a default value if start_time is undefined
          const editedEndTime = editedTimestamp.end_time || ''; // Provide a default value if end_time is undefined
          const editedTimestampInList = editedDaySheet.timestamps.find(timestampItem => timestampItem.id === editedTimestamp.id);

          if (editedTimestampInList){
            editedTimestampInList.start_time = editedStartTime;
            editedTimestampInList.end_time = editedEndTime;
            editedTimestampInList.duration = calculateDuration(editedStartTime, editedEndTime);
          }
          setDaySheet(editedDaySheet);
          
        } else {
          toast.error("Zeiteintrag konnte nicht berarbeitet werden");
        }
      }).catch(() => {
        toast.error("Zeiteintrag konnte nicht berarbeitet werden");
      });
    }
    
    return (
      <Modal
        title="Zeiteintrag bearbeiten"
        footerActions={
          <Button Icon={SaveIcon} type="submit">Speichern</Button>
        }
        close={close}
        onSubmit={onSubmit}
      >
        <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="start_time" required={true} value={timestamp?.start_time} />
        <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="end_time" required={true} value={timestamp?.start_time} />
      </Modal>
    );
  }

  useEffect(() => loadDaySheetByDate(selectedDate), []);

  return (
    <>
      <div className="flex flex-col sm:flex-row justify-between">
        <Title1>Zeitverwaltung</Title1>
      </div>
      <div className="flex flex-row justify-end items-start space-x-2 mt-2 mr-2">
        <button className="p-2 rounded-md " onClick={handlePrevDate}>
          <img src={ArrowLeftIcon.src} className="w-5 h-5" />
        </button>

        <input type="date" name="date" value={selectedDate} onChange={handleDateChange} />
        {/** Refresh doesn't work
          <Input type="date" name="date" value={selectedDate} onChange={handleDateChange}/>
         */}

        <button className="p-2 rounded-md " onClick={handleNextDate}>
          <img src={ArrowRightIcon.src} className="w-5 h-5" />
        </button>
      </div>
      <Table 
        className="mt-5"
        data={daySheet.timestamps}

        columns={[
          {
            header: "Startuhrzeit",
            title: "start_time"
          },
          {
            header: "Enduhrzeit",
            title: "end_time"
          },
          {
            header: "Dauer",
            title: "duration"
          }
        ]}
        
       />
        {/* TODO: Add delete and edit Action */}
      <div>
        <p>Total Duration: {calculateTotalDuration(daySheet.timestamps)}</p>
      </div>
      <div>
        <form className="mb-8 flex flex-col md:flex-row md:items-center md:space-x-4" onSubmit={handleCreateTimestampSubmit}>
          <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="start_time" value={timestamp.start_time} onChange={handleTimeChange}/>
          <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="start_time" value={timestamp.end_time} onChange={handleTimeChange}/> 
          <Button type="submit" className="mb-4 mr-4 bg-black text-white rounded-md">Anfügen</Button>
        </form>
      </div>

    </>
  );
};