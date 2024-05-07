'use client';
import React, {useEffect, useState} from 'react';
import {DaySheetDto, type UpdateConfirmedRequest, UpdateDaySheetDayNotesDto} from "@/openapi/compassClient";
import Table from "@/components/table";
import {Checkmark24Regular, Edit24Regular, NoteAddRegular} from "@fluentui/react-icons";
import {toast} from "react-hot-toast";
import {useRouter} from "next/navigation";
import Title1 from "@/components/title1";
import {getDaySheetControllerApi} from "@/openapi/connector";
import toastMessages from "@/constants/toastMessages";

const mockDaySheetData = [
    {
        id: 1,
        date: "2024-05-01",
        participant: "Max Mustermann",
        workHours: 8,
        confirmed: false,
        dayNotes: "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    },
    {
        id: 2,
        date: "2024-05-02",
        participant: "Anna Beispiel",
        workHours: 7.5,
        confirmed: false,
        dayNotes: "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    },
    // Weitere Dummy-Daten hier hinzufügen...
];

export default function HomePage() {
    //const [daySheetDtos, setDaySheetDtos] = useState<DaySheetDto[]>([]);
    const [daySheetDtos, setDaySheetDtos] = useState(mockDaySheetData);
    const [selectedDaySheetDto, setSelectedDaySheetDto] = useState<DaySheetDto>();
    const userId = ""; //TODO daysheet: Somehow select user in frontend, maybe a dropdown?
    const router = useRouter();

    const [notesInput, setNotesInput] = useState(''); // Declare a state variable


    useEffect(() => {
      getDaySheetControllerApi().getAllDaySheetByParticipant({userId: userId}).then(daySheetDtos => {
        close();
        toast.success(toastMessages.DAYSHEETS_LOADED);

        const notConfirmedDaySheetDtos = daySheetDtos.filter(daySheetDto => !daySheetDto.confirmed);
        setDaySheetDtos(notConfirmedDaySheetDtos);
      }).catch(() => {
          toast.error(toastMessages.DATA_NOT_LOADED);
      });
    }, []);

    const confirmDaySheet = async (id: number) => {
        const updateDayRequest: UpdateConfirmedRequest = {
          id: id
        };

        getDaySheetControllerApi().updateConfirmed(updateDayRequest).then(() => {
            close();
            toast.success(toastMessages.DAYSHEET_CONFIRMED);
        }).catch(() => {
            toast.error(toastMessages.DAYSHEET_CONFIRMED_ERROR);
        });
    };



    const saveNotesModal = async () => {

        try {

            const updateDayNotesDto: UpdateDaySheetDayNotesDto = {}
            updateDayNotesDto.id = selectedDaySheetDto?.id
            updateDayNotesDto.dayNotes = notesInput

            const response = await getDaySheetControllerApi().updateDayNotes({updateDaySheetDayNotesDto: updateDayNotesDto})


            setNotesInput('')
            await closeNotesModal()
            toast.success('DayNotes saved');
            daySheetDtos.find(daysheetDto => daysheetDto.id === selectedDaySheetDto!.id!)!.dayNotes = response.dayNotes
        }
        catch (error){
            console.log(error)
            toast.error('Error Occurred: Could not save DayNotes');
        }
    }

    const cancelNotesModal = async () => {
        setNotesInput('')
        closeNotesModal()
    }

    const closeNotesModal = async () => {
        var modal = document.getElementById("myModal")!;
        modal.style.display = "none";
    }

    const openNotesModal = async (index: number) => {

        setNotesInput(selectedDaySheetDto?.dayNotes!)
        // Get the modal
        var modal = document.getElementById("myModal")!;
        modal.style.display = "flex";

// When the user clicks anywhere outside of the modal, close it
        window.onclick = function(event) {
            if (event.target == modal) {
                cancelNotesModal();
            }
        }
    }


    const navigateToSingleDay = () => {
        if (selectedDaySheetDto != undefined && selectedDaySheetDto.date != undefined) {
            const dateString = encodeURIComponent(selectedDaySheetDto.date.toString());
            router.push(`/working-hours-single-day?date=${dateString}`);
        }
    };

    return (
        <div>
            <Title1>Kontrolle Arbeitszeit</Title1>
            <Table
                data={daySheetDtos}
                columns={[
                    {
                        header: "Datum",
                        title: "date"
                    },
                    {
                        header: "Teilnehmer",
                        title: "Peter"
                    },
                    {
                        header: "Erfasste Arbeitszeit",
                        title: "workHours"
                    }
                ]}
                actions={[
                    {
                        icon: Checkmark24Regular,
                        label: "Bestätigen",
                        onClick: (id) => {
                            let daySheetDto = daySheetDtos[id];
                            if (daySheetDto !== undefined && daySheetDto.id != undefined) {
                                confirmDaySheet(daySheetDto.id);
                            }
                        }
                    },
                    {
                        icon: Edit24Regular,
                        onClick: (id) => {
                            setSelectedDaySheetDto(daySheetDtos[id]);
                            navigateToSingleDay();
                        }
                    },
                    {
                        icon: NoteAddRegular,
                        label: "Notizen öffnen",
                        onClick: (id) => {
                            setSelectedDaySheetDto(daySheetDtos[id]);
                            openNotesModal(id)
                        },
                    }
                ]}>

            </Table>
            <div id="myModal" className="modal hidden top-0 left-0 w-full h-full z-1 bg-gray-500/80 absolute justify-center items-center ">
                <div className="modal-content bg-slate-100 flex flex-col justify-center w-4/5 m-auto">
                    <div className="flex justify-center p-4 mb-4">Notizen erfassen für:  Daysheet ID: {selectedDaySheetDto?.id}</div>
                    <div className="flex justify-center">
                        <textarea value={notesInput} // ...force the input's value to match the state variable...
                                  onChange={e => setNotesInput(e.target.value)}  rows={10} cols={50}></textarea>
                    </div>
                    <div className="flex justify-center my-4">
                        <button onClick={() => saveNotesModal()} className="bg-green-400 p-3 rounded text-white ">Speichern
                        </button>
                        <button onClick={() => cancelNotesModal()} className="bg-red-400 p-3 rounded text-white ml-2 ">Cancel
                        </button>
                    </div>
                </div>

        </div>
            </div>

    );
};
