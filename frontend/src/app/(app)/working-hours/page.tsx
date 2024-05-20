'use client';

import Title1 from "@/components/title1";
import Table from "@/components/table";
import Input from "@/components/input";
import { ArrowLeft24Regular, ArrowRight24Regular, Delete24Regular, Edit24Regular, Save24Regular} from "@fluentui/react-icons";
import { useEffect, useState } from "react";
import { getDaySheetControllerApi, getTimestampControllerApi} from "@/openapi/connector";
import Button from "@/components/button";
import Modal from "@/components/modal";
import toast from "react-hot-toast";
import toastMessages from "@/constants/toastMessages";
import { CreateDaySheetRequest, CreateTimestampRequest, DaySheetDto, TimestampDto } from "@/openapi/compassClient";
import IconButton from "@/components/iconbutton";

const convertMilisecondsToTimeString = (miliseconds: number): string => {
  const hours = Math.floor(miliseconds / 3600000);
  const minutes = Math.floor((miliseconds % 3600000) / 60000);
  return `${hours}h ${minutes}min`;
}

function TimeStampUpdateModal({ close, onSave, timestamp }: Readonly<{
  close: () => void;
  onSave: () => void;
  timestamp: TimestampDto | undefined;
}>) {
  const [updatedTimestamp, setTimestamp] = useState<{ startTime: string; endTime: string;}>({ startTime: '', endTime: ''});

  const onSubmit = (formData: FormData) => {
    const editedTimestamp: TimestampDto = {
        id: timestamp?.id || 0,
        daySheetId: timestamp?.daySheetId || 0,
        startTime: formData.get('startTime') as string,
        endTime: formData.get('endTime') as string
    };

    if (editedTimestamp.endTime && editedTimestamp.startTime && editedTimestamp.endTime <= editedTimestamp.startTime) {
      toast.error(toastMessages.STARTTIME_AFTER_ENDTIME);
      return;
    }

    const updateTimestampAction = () => getTimestampControllerApi().putTimestamp({ timestampDto: editedTimestamp}).then(() => {
      close();
      onSave();
    })

    toast.promise(updateTimestampAction(), {
      loading: toastMessages.UPDATING,
      success: toastMessages.TIMESTAMP_UPDATED,
      error: toastMessages.TIMESTAMP_NOT_UPDATED
    });
  }

  const handleTimeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setTimestamp(prevState => ({ ...prevState, [name]: value }));
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
  const [loading, setLoading] = useState(true);
  const [daySheet, setDaySheet] = useState<DaySheetDto>({ id: 0, date: new Date(), dayNotes: '', timestamps: [], timeSum: 0, confirmed: false });
  const [timestamp, setTimestamp] = useState<{ startTime: string; endTime: string;}>({ startTime: '', endTime: ''});
  const [selectedTimestamp, setSelectedTimestamp] = useState<TimestampDto>();
  const [selectedDate, setSelectedDate] = useState<string>(new Date().toISOString().slice(0, 10));
  const [showUpdateModal, setShowUpdateModal] = useState(false);

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
    setLoading(true);
    getDaySheetControllerApi().getDaySheetDate({date: date}).then((daySheetDto: DaySheetDto) => {
        const loadedDaySheet: DaySheetDto = {
          id: daySheetDto.id || 0,
          date: new Date(daySheetDto.date || ''),
          dayNotes:  String(daySheetDto.dayNotes || ''),
          timestamps: [],
          timeSum: daySheetDto.timeSum || 0,
          confirmed: daySheetDto.confirmed || false
        };

        daySheetDto.timestamps?.sort((a: TimestampDto, b: TimestampDto) => {
          const startTimeAHour = a.startTime?.split(':').map(Number)[0] ?? 0;
          const startTimeBHour = b.startTime?.split(':').map(Number)[0] ?? 0;
          
          return startTimeAHour - startTimeBHour;
        }).forEach((timestamp: TimestampDto) => {
          if (loadedDaySheet?.timestamps && timestamp.startTime && timestamp.endTime) {
            loadedDaySheet.timestamps.push({
              id: timestamp.id || 0,
              daySheetId: timestamp.daySheetId || 0,
              startTime: timestamp.startTime.substring(0, 5),
              endTime: timestamp.endTime.substring(0, 5)
            });
          }
        });
        setDaySheet(loadedDaySheet);
        setLoading(false);
     }).catch(() => {
        const emptyDaySheet = {
          id: 0,
          date: new Date(),
          dayNotes: '',
          timestamps: [],
          timeSum: 0,
          confirmed: false
        };
        setDaySheet(emptyDaySheet);
        setLoading(false);
     });     
   }


  const handleCreateTimestampSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
  
    getDaySheetControllerApi().getDaySheetDate({date: selectedDate}).then((daySheetDto: DaySheetDto) => {
      if (daySheetDto && daySheetDto.id) {
        createNewTimestamp(daySheetDto.id);
      }
    }).catch(() => {
      const creatDaySheetDto: CreateDaySheetRequest = {
        daySheetDto: {
          date: new Date(selectedDate),
          dayNotes: '',
          timestamps: [],
          confirmed: false
        }
      };
      
      getDaySheetControllerApi().createDaySheet(creatDaySheetDto).then((createdDaySheet: DaySheetDto) => {
        if (createdDaySheet && createdDaySheet.id) {
          createNewTimestamp(createdDaySheet.id);
        }
      })
    });
  };

  const createNewTimestamp = (daysheetId: number) => {
    if (timestamp.endTime <= timestamp.startTime) {
      toast.error(toastMessages.STARTTIME_AFTER_ENDTIME);
      return;
    }

    const createTimestampRequest: CreateTimestampRequest = {
      timestampDto: {
        daySheetId: daysheetId,
        startTime: timestamp.startTime,
        endTime: timestamp.endTime
      }
    };

    const createAction = () => getTimestampControllerApi().createTimestamp(createTimestampRequest).then(() => {
      loadDaySheetByDate(selectedDate)
    });

    toast.promise(createAction(), {
      loading: toastMessages.CREATING,
      success: toastMessages.TIMESTAMP_CREATED,
      error: toastMessages.TIMESTAMP_NOT_CREATED
    });
  };


  const handleTimeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setTimestamp(prevState => ({ ...prevState, [name]: value }));
  };

  const deleteTimestamp = (timestamp: TimestampDto) => {
    const deleteAction = () => getTimestampControllerApi().deleteTimestamp({id: timestamp.id ?? 0}).then(() => {
      loadDaySheetByDate(selectedDate);
    });
    
    toast.promise(deleteAction(), {
      loading: toastMessages.DELETING,
      success: toastMessages.TIMESTAMP_DELETED,
      error: toastMessages.TIMESTAMP_NOT_DELETED
    });
  }

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
      <div className="h-full flex flex-col">
        <div className="flex flex-col sm:flex-row justify-between mb-5">
          <Title1>Arbeitszeit erfassen</Title1>
          <div className="mt-2 sm:mt-0 flex flex-row items-start space-x-4">
            <IconButton Icon={ArrowLeft24Regular} onClick={handlePrevDate}></IconButton>
            <Input type="date" name="date" value={selectedDate} onChange={handleDateChange} />
            <IconButton Icon={ArrowRight24Regular} onClick={handleNextDate}></IconButton>
          </div>
        </div>
        <Table
          data={daySheet?.timestamps ?? []} 
          columns={[
            {
              header: "Start-Uhrzeit",
              title: "startTime"
            },
            {
              header: "End-Uhrzeit",
              title: "endTime"
            },
            {
              header: "Dauer",
              titleFunction: (timestamp: TimestampDto) => {
                const startDate = new Date(`01/01/2000 ${timestamp.startTime}`);
                const endDate = new Date(`01/01/2000 ${timestamp.endTime}`);
                const timestampDuration = endDate.getTime() - startDate.getTime();
                return convertMilisecondsToTimeString(timestampDuration);
              }
            }
          ]}
          actions={[
            {
              icon: Delete24Regular,
              onClick: (id) => {
                daySheet?.timestamps && setSelectedTimestamp(daySheet.timestamps[id]);
                selectedTimestamp && deleteTimestamp(selectedTimestamp);
              }
            },
            {
              icon: Edit24Regular,
              onClick: (id) => {
                if (!daySheet.confirmed) {
                  daySheet?.timestamps && setSelectedTimestamp(daySheet.timestamps[id]);
                  setShowUpdateModal(true);
                } else {
                  toast.error(toastMessages.DAYSHEET_ALREADY_CONFIRMED);
                }
              }
            }
          ]}
          loading={loading}
          customBottom={
            <tr className="bg-white border-t-4 border-slate-100">
              <td colSpan={2} className="py-4 px-6 text-left text-sm font-bold">
                Gesamt
              </td>
              <td colSpan={2} className="py-4 px-6 text-left text-sm font-bold">
                {convertMilisecondsToTimeString(daySheet?.timeSum ?? 0)}
              </td>
            </tr>
          }
        />

        <div>
          <form className="mt-4" onSubmit={handleCreateTimestampSubmit}>
            <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="startTime" value={timestamp.startTime} onChange={handleTimeChange}/>
            <Input type="time" className="mb-4 mr-4 w-48 inline-block" name="endTime" value={timestamp.endTime} onChange={handleTimeChange}/> 
            <Button type="submit" className="mb-4 mr-4 bg-black text-white rounded-md inline-block">Erfassen</Button>
          </form>
        </div>
      </div>
    </>
  );
};