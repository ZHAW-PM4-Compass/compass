'use client';
import React, {useEffect, useState} from 'react';
import {ParticipantDto, UpdateDaySheetDto, WorkHourDto} from "@/openapi/compassClient";
import Table from "@/components/table";
import {Checkmark24Regular, Edit24Regular, NoteAddRegular} from "@fluentui/react-icons";
import {toast} from "react-hot-toast";
import {useRouter} from "next/navigation";
import Title1 from "@/components/title1";

const Home: React.FC = () => {
    // Sample data for demonstration
    const mockdata: WorkHourDto[] = [
        { daySheetId: 0, date: '2024-04-14', confirmed: false, workHours: 2.0, id: 1, name: "Hans", notes:"foo" } as WorkHourDto,
        { daySheetId: 1, date: '2024-04-13', confirmed: false, workHours: 3.5,  id:2,  name:  "Alice", notes:""} as WorkHourDto,
        { daySheetId: 2, date: '2024-04-12', confirmed: true, workHours: 4.0,  id:3, name: "Bob", notes:"bar"} as WorkHourDto,
        { daySheetId: 3, date: '2024-04-11', confirmed: false, workHours: 1.5, id: 4, name: "Eve" , notes:""} as WorkHourDto,
    ];


    const [notes, setNotes] = useState(''); // Declare a state variable
    const [selectedWorkerId, setSelectedWorkerIndex] = useState(-1);
    const [selectedWorkerHourDto, setSelectedWorkerHourDto] = useState({} as WorkHourDto);


    const [workHourDtos, setWorkHourDtos] = useState<WorkHourDto[]>([]);
    const [selectedWorkHourDto, setSelectedWorkHourDto] = useState<WorkHourDto>();
    const router = useRouter();

    let initLoad = false;
    useEffect(() => {
        if (!initLoad) {
            initLoad = true;
            // Method to call when the component mounts
            getAllDaysheets()
                .then((rsult) => {
                    let myList: WorkHourDto[] = []
                    rsult.forEach((entry: WorkHourDto) => {
                        if (!entry.confirmed) {
                            myList.push(entry);
                        }
                    });
                    setWorkHourDtos(myList);
                    toast.success('Done fetching daysheets');
                })
                .catch(() => {
                    let myList: WorkHourDto[] = []
                    mockdata.forEach((entry) => {
                        if (!entry.confirmed) {
                            myList.push(entry);
                        }
                    });
                    setWorkHourDtos(myList);
                    toast.error('Error fetching daysheets, using mocked data');
                });
        }
    }, []); // Empty dependency array ensures the effect runs only once, similar to componentDidMount

    const getAllDaysheets = async () => {
        try {
            // Make a GET request using the fetch API
            const response = await fetch(`http://localhost:8080/api/daysheet/getAll/`);

            // Check if the response is successful (status code 200)
            if (!response.ok) {
                // Handle non-successful response (e.g., throw an error)
                throw new Error(`Failed to fetch data`);
            }

            // Parse the response body as JSON
            // Return the data
            return await response.json();
        } catch (error) {
            // Handle any errors (e.g., log the error)
            console.error('Error fetching data:', error);
            // rethrow the error to let the caller handle it
            throw error;
        }
    };

    const saveNotesModal = async () => {

       try {
           const response = ( await axios.put("http://localhost:8080/api/",{notes: notes, workerId: selectedWorkerId})).data;
           setNotes('')
           closeNotesModal()
           toast.success('DayNotes saved');
       }
       catch (error){
           console.log(error)
           toast.error('Error Occurred: Could not save DayNotes');
       }
    }

    const cancelNotesModal = async () => {
        setNotes('')
        closeNotesModal()
    }

    const closeNotesModal = async () => {
        var modal = document.getElementById("myModal")!;
        modal.style.display = "none";
    }

    const openNotesModal = async (index: number) => {
      setSelectedWorkerIndex(index)
        setSelectedWorkerHourDto( workHourDtos[index]!)
        setNotes(selectedWorkerHourDto.notes)
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

    const updateDaySheet = async (updateDay: UpdateDaySheetDto) => {
        try {
            // Make a PUT request using the fetch API
            const response = await fetch(`http://localhost:8080/api/daysheet/`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updateDay)
            });

            // Check if the response is successful (status code 200)
            if (!response.ok) {
                // Handle non-successful response (e.g., throw an error)
                throw new Error(`Failed to update day sheet`);
            }

            // Parse the response body as JSON
            // Return the data
            return await response.json();
        } catch (error) {
            // Handle any errors (e.g., log the error)
            console.error('Error updating day sheet:', error);
            // rethrow the error to let the caller handle it
            throw error;
        }
    };

    const mapWorkHourDtoToUpdate = (workHourDto: WorkHourDto | undefined) => {
        if (workHourDto != undefined) {
            let updateDaySheetDto: UpdateDaySheetDto = {};
            updateDaySheetDto.id = workHourDto.daySheetId;
            updateDaySheetDto.date = workHourDto.date;
            return updateDaySheetDto;
        }
        return {};
    }

    const getTotalTrackedTime = () => {
        let total = 0;
        workHourDtos.forEach((item) => {
            if (item.workHours) {
                total += item.workHours;
            }
        });
        return total.toFixed(2); // Round to 2 decimal places
    };

    const navigateToSingleDay = () => {
        if (selectedWorkHourDto != undefined && selectedWorkHourDto.date != undefined) {
            const dateString = encodeURIComponent(selectedWorkHourDto.date.toString());
            router.push(`/working-hours-single-day?date=${dateString}`);
        }
    };

    const participantName = (workHourDto: WorkHourDto): string => {
        if (workHourDto !== undefined) {
            if (workHourDto.participant !== undefined) {
                if (workHourDto.participant.name != undefined) return workHourDto.participant.name;
            }
        }
        return '';
    }

    return (
        <div>
            <Title1>Kontrolle Arbeitszeit</Title1>
            <Table
                data={workHourDtos}
                columns={[
                    {
                        header: "Datum",
                        title: "date"
                    },
                    {
                        header: "Teilnehmer",
                        titleFunction: participantName
                    },
                    {
                        header: "Erfasste Arbeitszeit",
                        title: "workHours"
                    },
                ]}
                actions={[
                    {
                        icon: Checkmark24Regular,
                        label: "Bestätigen",
                        onClick: (id) => {
                            if (workHourDtos[id] !== undefined) {
                                updateDaySheet(mapWorkHourDtoToUpdate(workHourDtos[id]));
                            }
                        }
                    },
                    {
                        icon: Edit24Regular,
                        onClick: (id) => {
                            setSelectedWorkHourDto(workHourDtos[id]);
                            navigateToSingleDay();
                        }
                    },
                    {
                        icon: NoteAddRegular,
                        label: "Notizen öffnen",
                        onClick: (id) => {
                          openNotesModal(id)
                        },
                    }
                ]}>

            </Table>
            <div>
                <hr className="border-gray-200 mb-2"/>
                <div
                    className="px-6 py-4 bg-white text-sm text-black font-bold">Total: {getTotalTrackedTime()} Stunden
                </div>
            </div>
            <div id="myModal" className="modal hidden top-0 left-0 w-full h-full z-1 bg-gray-500/80 absolute justify-center items-center ">
                <div className="modal-content bg-slate-100 flex flex-col justify-center w-4/5 m-auto">
                    <div className="flex justify-center p-4 mb-4">Notizen erfassen für: ID: {selectedWorkerHourDto?.id}, Name: {selectedWorkerHourDto?.name}</div>
                    <div className="flex justify-center">
                        <textarea value={notes} // ...force the input's value to match the state variable...
                                  onChange={e => setNotes(e.target.value)}  rows="10" cols="50"></textarea>
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

export default Home;
