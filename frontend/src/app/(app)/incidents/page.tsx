"use client"

import { getIncidentControllerApi, getUserControllerApi } from "@/openapi/connector";
import Button from "@/components/button";
import Input from "@/components/input";
import Modal from "@/components/modal";
import Table from "@/components/table";
import Title1 from "@/components/title1";
import Roles from "@/constants/roles";
import { PersonAdd24Regular, Delete24Regular, Edit24Regular, Save24Regular } from "@fluentui/react-icons";
import { useEffect, useState } from "react";
import { toast } from 'react-hot-toast';
import toastMessages from "@/constants/toastMessages";
import type { IncidentDto } from "@/openapi/compassClient";
import { useUser } from "@auth0/nextjs-auth0/client";
import type { CreateIncidentRequest } from "@/openapi/compassClient/apis/IncidentControllerApi";

enum formFields {
  DATE = "date",
  TITLE = "title",
  DESCRIPTION = "description",
  PARTICIPANT = "participant"
}

function IncidentCreateModal({ close, onSave }: Readonly<{
    close: () => void;
		onSave: () => void;
  }>) {
  const onSubmit = (formData: FormData) => {
    const dateString = formData.get(formFields.DATE) as string;
    const date = new Date(dateString);
    
    const createIncidentRequest: CreateIncidentRequest = {
      incidentDto: {
        date: date,
        title: formData.get(formFields.TITLE) as string,
        description: formData.get(formFields.DESCRIPTION) as string,
        userId: "auth0|6601d72a423e9ac1d785e113"
      }
    };
  
    getIncidentControllerApi().createIncident(createIncidentRequest).then(() => {
      close();
      onSave();
      toast.success(toastMessages.INCIDENT_CREATED);
    }).catch(error => {
      console.error(error);
      toast.error(toastMessages.INCIDENT_NOT_CREATED);
    })
  }

  return (
    <Modal
      title="Vorfall erfassen"
      footerActions={
        <Button Icon={Save24Regular} type="submit">Speichern</Button>
      }
      close={close}
			onSubmit={onSubmit}
    >
      <Input type="date" placeholder="Datum" className="mb-4 mr-4 w-48 inline-block" name={formFields.DATE} required={true} />
      <Input type="text" placeholder="Titel" className="mb-4 mr-4 w-48 inline-block" name={formFields.TITLE} required={true} />
      <Input type="text" placeholder="Beschreibung" className="mb-4 mr-4 w-48 inline-block" name={formFields.DESCRIPTION} required={true} />
    </Modal>
  );
}

function IncidentUpdateModal({ close, onSave }: Readonly<{
  close: () => void;
  onSave: () => void;
}>) {
const onSubmit = (formData: FormData) => {
  const dateString = formData.get(formFields.DATE) as string;
  const date = new Date(dateString);

  const createIncidentRequest: CreateIncidentRequest = {
    incidentDto: {
      date: date,
      title: formData.get(formFields.TITLE) as string,
      description: formData.get(formFields.DESCRIPTION) as string,
      userId: "auth0|6601d72a423e9ac1d785e113"
    }
  };

  getIncidentControllerApi().createIncident(createIncidentRequest).then(() => {
    close();
    onSave();
    toast.success(toastMessages.INCIDENT_CREATED);
  }).catch(error => {
    console.error(error);
    toast.error(toastMessages.INCIDENT_NOT_CREATED);
  })
}

return (
  <Modal
    title="Vorfall erfassen"
    footerActions={
      <Button Icon={Save24Regular} type="submit">Speichern</Button>
    }
    close={close}
    onSubmit={onSubmit}
  >
    <Input type="date" placeholder="Datum" className="mb-4 mr-4 w-48 inline-block" name={formFields.DATE} required={true} />
    <Input type="text" placeholder="Titel" className="mb-4 mr-4 w-48 inline-block" name={formFields.TITLE} required={true} />
    <Input type="text" placeholder="Beschreibung" className="mb-4 mr-4 w-48 inline-block" name={formFields.DESCRIPTION} required={true} />
  </Modal>
);
}

export default function IncidentsPage() {
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [incidents, setIncidents] = useState<IncidentDto[]>([]);
	const [selectedIncident, setSelectedIncident] = useState<IncidentDto>();
  const { user } = useUser();

	const loadIncidents = () => {
    if (user?.sub){
      getUserControllerApi().getUserById({ id: user.sub }).then((userDto) => {
        if (userDto.role === Roles.SOCIAL_WORKER || userDto.role === Roles.ADMIN) {
          getIncidentControllerApi().getAllIncidents().then((incidents) => {
            setIncidents(incidents)
          });
        } else {
          user.sub && getIncidentControllerApi().getAllIncidentsByParticipant({ userId: user.sub }).then((incidents) => {
            setIncidents(incidents);
          }).catch(() => {
            toast.error(toastMessages.INCIDENTS_NOT_LOADED);
          });
        }
      });

    }
	}

  const deleteIncident = (id: number) => {
    getIncidentControllerApi().deleteIncident({ id }).then(() => {
      loadIncidents();
      toast.success(toastMessages.INCIDENT_DELETED);
    }).catch(() => {
      toast.error(toastMessages.INCIDENT_NOT_DELETED);
    });
  }

  useEffect(() => {
    loadIncidents();
  }, []);

  return (
    <>
      {showCreateModal && (
        <IncidentCreateModal 
					close={() => setShowCreateModal(false)}
					onSave={loadIncidents} />
      )}
      <div className="h-full flex flex-col">
        <div className="flex flex-col sm:flex-row justify-between mb-5">
          <Title1>Vorfälle</Title1>
          <div className="mt-2 sm:mt-0">
            <Button Icon={PersonAdd24Regular} onClick={() => setShowCreateModal(true)}>Erstellen</Button>
          </div>
        </div>
        <Table
          data={incidents}
          columns={[
            {
              header: "Datum",
              title: "date"
            },
            {
              header: "Titel",
              title: "title"
            },
            {
              header: "Beschreibung",
              title: "description"
            }
          ]}
          actions={[
            {
              icon: Delete24Regular,
              label: "Löschen",
              onClick: (id) => {
                const incident = incidents[id];
                incident?.id && deleteIncident(incident.id)
              }
            },
            {
              icon: Edit24Regular,
              onClick: (id) => {
			  				setSelectedIncident(incidents[id]);
			  				setShowUpdateModal(true);
			  			}
            }
          ]} />
      </div>
    </>
  );
}