'use client';

import Title1 from "@/components/title1";
import Table from "@/components/table";
import Input from "@/components/input";
import ArrowLeftIcon from "@fluentui/svg-icons/icons/arrow_left_24_filled.svg";
import ArrowRightIcon from "@fluentui/svg-icons/icons/arrow_right_24_filled.svg";
import { Delete24Regular, Edit24Regular, Save24Regular} from "@fluentui/react-icons";
import { useEffect, useState } from "react";
import { getDaySheetControllerApi, getTimestampControllerApi} from "@/openapi/connector";
import Button from "@/components/button";
import Modal from "@/components/modal";
import type { CreateDaySheetRequest, CreateTimestampRequest, PutTimestampRequest, TimestampDto } from "@/openapi/compassClient";
import toast from "react-hot-toast";
import toastMessages from "@/constants/toastMessages";

 // interfaces necessary for timestamp duration
 interface Daysheet {
  id: number ;
  date: Date ;
  dayNotes: string;
  timestamps: Timestamp[];
  timeSum: number;
}

interface Timestamp {
  id: number;
  daySheetId: number;
  startTime: string;
  endTime: string;
  duration?: string;
}


function TimeStampUpdateModal({ close, onSave, timestamp }: Readonly<{
  close: () => void;
  onSave: () => void;
  timestamp: Timestamp | undefined;
}>) {
  const [updatedTimestamp, setTimestamp] = useState<{ startTime: string; endTime: string;}>({ startTime: '', endTime: ''});

  const onSubmit = (formData: FormData) => {
    const startTime = new Date(`1970-01-01T${formData.get("startTime")}:00`);
    const endTime = new Date(`1970-01-01T${formData.get("endTime")}:00`);

    const editedTimestamp: TimestampDto = {
        id: timestamp?.id || 0,
        daySheetId: timestamp?.daySheetId || 0,
        startTime,
        endTime
    };

    getTimestampControllerApi().putTimestamp({ timestampDto: editedTimestamp }).then(() => {
      close();//
      setTimeout(() => onSave(), 1000);
      toast.success(toastMessages.TIMESTAMP_UPDATED);
    }).catch(() => {
      toast.error(toastMessages.TIMESTAMP_NOT_UPDATED);
    });
  }

  const handleTimeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    if (name === "startTime" && updatedTimestamp.endTime && value >= updatedTimestamp.endTime || name === "endTime" && updatedTimestamp.startTime && value <= updatedTimestamp.startTime) {
      toast.error(toastMessages.STARTTIME_AFTER_ENDTIME);
      return;
    }
    setTimestamp(prevState => ({ ...prevState, [name]: value }));
    return;
  };

  useEffect(() => {setTimestamp({startTime: timestamp?.startTime || "00:00", endTime: timestamp?.endTime || "00:00"})}, []);

  return (
    <Modal
      title="Zeiteintrag bearbeiten"
      footerActions={
        <Button Icon={Save24Regular} type="submit">Speichern</Button>
      }
      close={close}
      onSubmit={onSubmit}
    >
      <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="startTime" required={true} value={updatedTimestamp.startTime} onChange={handleTimeChange}/>
      <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="endTime" required={true} value={updatedTimestamp.endTime} onChange={handleTimeChange} />
    </Modal>
  );
}


export default function WorkingHoursPage() {
  const [daySheet, setDaySheet] = useState<Daysheet>({ id: 0, date: new Date(), dayNotes: '', timestamps: [], timeSum: 0});
  const [timestamp, setTimestamp] = useState<{ startTime: string; endTime: string;}>({ startTime: '', endTime: ''});
  const [selectedTimestamp, setSelectedTimestamp] = useState<Timestamp>();
  const [selectedDate, setSelectedDate] = useState<string>(new Date().toISOString().slice(0, 10));
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  
  
  function calculateDuration(start: Date, end: Date) {
    const durationMs = start.getTime() - end.getTime();
    return formatMilisecondsToHourMinute(durationMs);
  }

  function formatMilisecondsToHourMinute(timeMilisceonds: number) {
    const time = new Date(timeMilisceonds);
    const hours = time.getUTCHours();
    const minutes = time.getUTCMinutes();
    return `${hours}h ${minutes}m`;
  }

  const handleDateChange = (date: any) => {
    setSelectedDate(date.target.value);
  };
  
  const handlePrevDate = () => {
    let selectedDateObj = new Date(selectedDate);
    const newDate = new Date(selectedDate);
    
    newDate.setDate(selectedDateObj.getDate() - 1);
    setSelectedDate(newDate.toISOString().slice(0, 10));
  };

  const handleNextDate = () => {
    let selectedDateObj = new Date(selectedDate);
    const newDate = new Date(selectedDate);
    
    newDate.setDate(selectedDateObj.getDate() + 1); 
    setSelectedDate(newDate.toISOString().slice(0, 10));
  };

	const loadDaySheetByDate = (date: string) => {

    getDaySheetControllerApi().getDaySheetDate({date: date}).then(daySheetDto => {
        const loadedDaySheet: Daysheet = {
          id: daySheetDto.id || 0,
          date: new Date(daySheetDto.date || ''),
          dayNotes:  String(daySheetDto.dayNotes || ''),
          timestamps: [],
          timeSum: daySheetDto.timeSum || 0
        };

        daySheetDto.timestamps?.sort((a: TimestampDto, b: TimestampDto) => {
          const startTimeA = a.startTime?.getHours() || 0;
          const startTimeB = b.startTime?.getHours() || 0;
          
          return startTimeA - startTimeB;
        }).forEach((timestamp: TimestampDto) => {
          if (timestamp.startTime && timestamp.endTime) {
            loadedDaySheet.timestamps.push({
              id: timestamp.id || 0,
              daySheetId: timestamp.daySheetId || 0,
              startTime: `${timestamp.startTime.getHours()}:${timestamp.startTime.getMinutes()}`,
              endTime: `${timestamp.endTime.getHours()}:${timestamp.endTime.getMinutes()}`, 
              duration: calculateDuration(timestamp.startTime, timestamp.endTime)
            });
          }
        });
        setDaySheet(loadedDaySheet);
        
     }).catch(() => {
        const emptyDaySheet = {
          id: 0,
          date: new Date(),
          dayNotes: '',
          timestamps: [],
          timeSum: 0
        };
        setDaySheet(emptyDaySheet);
     });     
   }


  const handleCreateTimestampSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
        
    // check if daysheet already exists
    getDaySheetControllerApi().getDaySheetDate({date: selectedDate}).then(daySheetDto => {
      // add to existing
      if (daySheetDto && daySheetDto.id) {
        createNewTimestamp(daySheetDto.id);
      }
    }).catch(() => {
      // new daysheet
      const creatDaySheetDto: CreateDaySheetRequest = {
        daySheetDto: {
        date: new Date(selectedDate),
        dayNotes: '',
        timestamps: []
        }
      };
      
      getDaySheetControllerApi().createDaySheet(creatDaySheetDto).then(createdDaySheet => {
        toast.success(toastMessages.DAYSHEET_CREATED)
        if (createdDaySheet && createdDaySheet.id) {
          createNewTimestamp(createdDaySheet.id);
        }
      }).catch(() => toast.error(toastMessages.DAYSHEET_NOT_CREATED));
    });

  };

  const createNewTimestamp = (daysheetId: number) => {

    const startTime = new Date(`1970-01-01T${timestamp.startTime}:00`);
    const endTime = new Date(`1970-01-01T${timestamp.endTime}:00`);

    const createTimestampRequest: CreateTimestampRequest = {
      timestampDto: {
        daySheetId: daysheetId,
        startTime,
        endTime
      }
    };
    
    getTimestampControllerApi().createTimestamp(createTimestampRequest).then(() => {
      toast.success(toastMessages.TIMESTAMP_CREATED)
    }).then(() => loadDaySheetByDate(selectedDate)).catch(() =>toast.error(toastMessages.TIMESTAMP_NOT_CREATED));
  };


  const handleTimeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    if (name === "startTime" && timestamp.endTime && value >= timestamp.endTime || name === "endTime" && timestamp.startTime && value <= timestamp.startTime) {
      toast.error(toastMessages.STARTTIME_AFTER_ENDTIME);
      return;
    }
    setTimestamp(prevState => ({ ...prevState, [name]: value }));
    return;
  };

  const deleteTimestamp = () => {
    if (selectedTimestamp?.id && selectedTimestamp?.id > 0) {
      getTimestampControllerApi().deleteTimestamp({id: selectedTimestamp.id}).then(() => {
        loadDaySheetByDate(selectedDate);
        toast.success(toastMessages.TIMESTAMP_DELETED);
      }).catch(() => {
        toast.error(toastMessages.TIMESTAMP_NOT_DELETED);
      });
    } else {
      toast.error(toastMessages.TIMESTAMP_NOT_DELETED);
    }
  }

  // When selectedDate is set, loadDaySheetByDate is called
  useEffect(() => loadDaySheetByDate(selectedDate), [selectedDate]);

  return (
    <>
      {showUpdateModal && (
        <TimeStampUpdateModal
					close={() => {
            setShowUpdateModal(false)
            loadDaySheetByDate(selectedDate)
          }}
					onSave={() => loadDaySheetByDate(selectedDate)}
					timestamp={selectedTimestamp} />
      )}
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
            title: "startTime"
          },
          {
            header: "Enduhrzeit",
            title: "endTime"
          },
          {
            header: "Dauer",
            title: "duration"
          }
        ]}
        
        actions={[
          {
            icon: Delete24Regular,
            onClick: (id) => {
              setSelectedTimestamp(daySheet.timestamps[id]);
              deleteTimestamp();
            }
          },
          {
            icon: Edit24Regular,
            onClick: (id) => {
              setSelectedTimestamp(daySheet.timestamps[id]);
              setShowUpdateModal(true);
            }
          }
        ]}
      />

      <div className="mt-4">
        <p>Total Duration: {formatMilisecondsToHourMinute(daySheet.timeSum)}</p>
      </div>

      <div>
        <form className="mb-8 flex flex-col md:flex-row md:items-center md:space-x-4 mt-4" onSubmit={handleCreateTimestampSubmit}>
          <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="startTime" value={timestamp.startTime} onChange={handleTimeChange}/>
          <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="endTime" value={timestamp.endTime} onChange={handleTimeChange}/> 
          <Button type="submit" className="mb-4 mr-4 bg-black text-white rounded-md">Anfügen</Button>
        </form>
      </div>

    </>
  );
};