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
import type { CreateDaySheetRequest, CreateTimestampRequest, DaySheetDto, DeleteTimestampRequest, GetDaySheetById1Request, PutTimestampRequest, TimestampDto } from "@/openapi/compassClient";
import toast from "react-hot-toast";
import toastMessages from "@/constants/toastMessages";

export default function WorkingHoursPage() {
  const [daySheet, setDaySheet] = useState<{ id: number; date: string; dayNotes: string; timestamps: Timestamp[];}>({ id: 0, date: '', dayNotes: '', timestamps: []});
  const [timestamp, setTimestamp] = useState<{ startTime: string; endTime: string;}>({ startTime: '', endTime: ''});
  const [selectedTimestamp, setSelectedTimestamp] = useState<TimestampDto>();
  const [selectedDate, setSelectedDate] = useState<string>(new Date().toISOString().slice(0, 10));
  
  interface Timestamp {
    id?: number;
    daySheetId: number;
    startTime: string;
    endTime: string;
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
      const duration = calculateDuration(timestamp.startTime, timestamp.startTime);
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
    const getDaySheetById1Request: GetDaySheetById1Request = {
      date: selectedDate
    };

    getDaySheetControllerApi().getDaySheetById1(getDaySheetById1Request).catch((response) => {
      if (response.status != 404) {
        return Promise.resolve();
      }

      const createDaySheetRequest: CreateDaySheetRequest = {
        daySheetDto: {
          date: new Date(selectedDate),
          dayNotes: '',
          timestamps: []
        }
      };

      return getDaySheetControllerApi().createDaySheet(createDaySheetRequest);
    }).then(daySheet => {
      if (!daySheet) {
        return Promise.resolve();
      }

      const createTimestampRequest: CreateTimestampRequest = {
        timestampDto: {
          daySheetId: daySheet.id,
          startTime: new Date(timestamp.startTime),
          endTime: new Date(timestamp.endTime)
        }
      };

      return getTimestampControllerApi().createTimestamp(createTimestampRequest);
    }).then(timestamp => {
      daySheet.timestamps.push(timestamp as Timestamp);
      setDaySheet(daySheet);
      setTimestamp({ startTime: '', endTime: '' });
      toast.success(toastMessages.TIMESTAMP_CREATED);
    }).catch(() => {
      toast.error(toastMessages.TIMESTAMP_NOT_CREATED);
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
    if (!timestamp.id) {
      toast.error(toastMessages.TIMESTAMP_NOT_DELETED);
      return;
    }

    const deleteTimestampRequest: DeleteTimestampRequest = {
      id: timestamp.id
    };

    getTimestampControllerApi().deleteTimestamp(deleteTimestampRequest).then(() => {
      toast.success(toastMessages.TIMESTAMP_DELETED);
      setDaySheet({...daySheet, timestamps: daySheet.timestamps.filter(timestampItem => timestampItem.id !== timestamp.id)});
    }).catch(() => {
      toast.error(toastMessages.TIMESTAMP_NOT_DELETED);
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
          daySheetId: timestamp?.daySheetId,
          startTime: new Date(formData.get("start_time") as string),
          endTime: new Date(formData.get("end_time") as string)
      };

      const putTimestampRequest: PutTimestampRequest = {
        timestampDto: editedTimestamp
      };
    
      getTimestampControllerApi().putTimestamp(putTimestampRequest).then(response => {
        close();
          toast.success(toastMessages.TIMESTAMP_UDPATED);
          
          let editedDaySheet = {...daySheet};
          const editedStartTime = editedTimestamp.startTime || ''; // Provide a default value if start_time is undefined
          const editedEndTime = editedTimestamp.endTime || ''; // Provide a default value if end_time is undefined
          const editedTimestampInList = editedDaySheet.timestamps.find(timestampItem => timestampItem.id === editedTimestamp.id);

          if (editedTimestampInList){
            editedTimestampInList.startTime = editedStartTime.toString();
            editedTimestampInList.endTime = editedEndTime.toString();
            editedTimestampInList.duration = calculateDuration(editedStartTime.toString(), editedEndTime.toString());
          }
          setDaySheet(editedDaySheet);
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
        <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="start_time" required={true} value={timestamp?.startTime} />
        <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="end_time" required={true} value={timestamp?.endTime} />
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

        <Input type="date" name="date" value={selectedDate} onChange={handleDateChange} />

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
          <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="start_time" value={timestamp.startTime} onChange={handleTimeChange}/>
          <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="start_time" value={timestamp.endTime} onChange={handleTimeChange}/> 
          <Button type="submit" className="mb-4 mr-4 bg-black text-white rounded-md">Anfügen</Button>
        </form>
      </div>

    </>
  );
};