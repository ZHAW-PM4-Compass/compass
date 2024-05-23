"use client"

import { getIncidentControllerApi, getUserControllerApi } from "@/openapi/connector";
import Button from "@/components/button";
import Input from "@/components/input";
import Modal from "@/components/modal";
import Table from "@/components/table";
import Title1 from "@/components/title1";
import Roles from "@/constants/roles";
import { Delete24Regular, Edit24Regular, Save24Regular, Add24Regular } from "@fluentui/react-icons";
import { useEffect, useState } from "react";
import { toast } from 'react-hot-toast';
import toastMessages from "@/constants/toastMessages";
import type { IncidentDto, UserDto } from "@/openapi/compassClient";
import { useUser } from "@auth0/nextjs-auth0/client";
import type { CreateIncidentRequest, UpdateIncidentRequest } from "@/openapi/compassClient/apis/IncidentControllerApi";
import TextArea from "@/components/textarea";
import Select from "@/components/select";
import React from "react";

enum formFields {
  DATE = "date",
  TITLE = "title",
  DESCRIPTION = "description",
  PARTICIPANT = "participant"
}

function IncidentCreateModal({ close, onSave, userId, partSelectActive, participants }: Readonly<{
  close: () => void;
  onSave: () => void;
  userId?: string;
  partSelectActive?: boolean;
  participants?: UserDto[];
}>) {
  const onSubmit = (formData: FormData) => {
    const dateString = formData.get(formFields.DATE) as string;
    const date = new Date(dateString);

    const createIncidentRequest: CreateIncidentRequest = {
      incidentDto: {
        date: date,
        title: formData.get(formFields.TITLE) as string,
        description: formData.get(formFields.DESCRIPTION) as string,
        user: {
          userId: partSelectActive ? formData.get(formFields.PARTICIPANT) as string : userId
        }
      }
    };

    const createAction = () => getIncidentControllerApi().createIncident(createIncidentRequest).then(() => {
      close();
      onSave();
    })

    toast.promise(createAction(), {
      loading: toastMessages.CREATING,
      success: toastMessages.INCIDENT_CREATED,
      error: toastMessages.INCIDENT_NOT_CREATED,
    });
  }

  const participantsData = participants?.map(participant => ({
    id: participant?.userId,
    label: participant?.email
  }));

  return (
    <Modal
      title="Vorfall erfassen"
      footerActions={
        partSelectActive ? (
          <>
            <Select
              className="mr-4 w-48 inline-block"
              name={formFields.PARTICIPANT}
              required={true}
              data={participantsData ?? []} />
            <Button Icon={Save24Regular} type="submit">Speichern</Button>
          </>
        ) : (
          <Button Icon={Save24Regular} type="submit">Speichern</Button>
        )
      }
      close={close}
      onSubmit={onSubmit}
    >
      <Input type="date" placeholder="Datum" className="mb-4 mr-4 w-48 inline-block" name={formFields.DATE} required={true} />
      <Input type="text" placeholder="Titel" className="mb-4 mr-4 w-48 inline-block" name={formFields.TITLE} required={true} />
      <TextArea placeholder="Beschreibung" className="mb-4 mr-4 min-w-72 min-h-24 block" name={formFields.DESCRIPTION} />
    </Modal>
  );
}

function IncidentUpdateModal({ close, onSave, incidentDto, partSelectActive }: Readonly<{
  close: () => void;
  onSave: () => void;
  incidentDto: IncidentDto | undefined;
  partSelectActive?: boolean;
}>) {
  const [title, setTitle] = useState(incidentDto?.title);
  const [description, setDescription] = useState(incidentDto?.description);

  const yearString = incidentDto?.date?.getFullYear();
  const month = incidentDto?.date?.getMonth() && incidentDto?.date?.getMonth() + 1;
  const monthString = month?.toString().padStart(2, '0');
  const dayString = incidentDto?.date?.getDate()?.toString().padStart(2, '0');

  const dateString = `${yearString}-${monthString}-${dayString}`;

  const onSubmit = (formData: FormData) => {
    const dateString = formData.get(formFields.DATE) as string;
    const date = new Date(dateString);

    const updateIncidentRequest: UpdateIncidentRequest = {
      incidentDto: {
        id: incidentDto?.id,
        date: date,
        title: title,
        description: description,
        user: {
          userId: incidentDto?.user?.userId
        }
      }
    };

    const updateAction = () => getIncidentControllerApi().updateIncident(updateIncidentRequest).then(() => {
      close();
      onSave();
    })

    toast.promise(updateAction(), {
      loading: toastMessages.UPDATING,
      success: toastMessages.INCIDENT_UPDATED,
      error: toastMessages.INCIDENT_NOT_UPDATED,
    });
  }

  return (
    <Modal
      title="Vorfall bearbeiten"
      footerActions={
        partSelectActive ? (
          <>
            <Input className="mr-4 w-48 inline-block" name={formFields.PARTICIPANT} disabled={true} value={incidentDto?.user?.email} />
            <Button Icon={Save24Regular} type="submit">Speichern</Button>
          </>
        ) : (
          <Button Icon={Save24Regular} type="submit">Speichern</Button>
        )
      }
      close={close}
      onSubmit={onSubmit}
    >
      <Input type="date" placeholder="Datum" className="mb-4 mr-4 w-48 inline-block" name={formFields.DATE} disabled={true} value={dateString} />
      <Input type="text" placeholder="Titel" className="mb-4 mr-4 w-48 inline-block" name={formFields.TITLE} required={true} value={title} onChange={(e) => setTitle(e.target.value)} />
      <TextArea placeholder="Beschreibung" className="mb-4 mr-4 min-w-72 min-h-24 block" name={formFields.DESCRIPTION} value={description} onChange={(e) => setDescription(e.target.value)} />
    </Modal>
  );
}

export default function IncidentsPage() {
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [partSelectActive, setPartSelectActive] = useState(true);
  const [incidents, setIncidents] = useState<IncidentDto[]>([]);
  const [selectedIncident, setSelectedIncident] = useState<IncidentDto>();
  const [userId, setUserId] = useState("");
  const [participants, setParticipants] = useState<UserDto[]>([]);

  const { user } = useUser();

  const loadIncidents = () => {
    setLoading(true);
    getIncidentControllerApi().getAllIncidents().then(incidentDtos => {
      setIncidents(incidentDtos);
    }).catch(() => {
      toast.error(toastMessages.INCIDENTS_NOT_LOADED)
    }).finally(() => {
      setLoading(false);
    });
  }

  const deleteIncident = (id: number) => {
    const deleteAction = () => getIncidentControllerApi().deleteIncident({ id }).then(() => {
      loadIncidents();
    })

    toast.promise(deleteAction(), {
      loading: toastMessages.DELETING,
      success: toastMessages.INCIDENT_DELETED,
      error: toastMessages.INCIDENT_NOT_DELETED,
    });
  }

  useEffect(() => {
    setUserId(user?.sub || "");
    user?.sub && getUserControllerApi().getUserById({ id: user.sub }).then(userDto => {
      if (userDto.role === Roles.SOCIAL_WORKER || userDto.role === Roles.ADMIN) {
        setPartSelectActive(true);
        getUserControllerApi().getAllParticipants().then(participants => {
          setParticipants(participants);
        });
      }
    });
    loadIncidents();
  }, [user]);

  return (
    <>
      {showCreateModal && (
        <IncidentCreateModal
          close={() => setShowCreateModal(false)}
          onSave={loadIncidents}
          userId={userId}
          partSelectActive={partSelectActive}
          participants={participants} />
      )}
      {showUpdateModal && (
        <IncidentUpdateModal
          close={() => setShowUpdateModal(false)}
          onSave={loadIncidents}
          incidentDto={selectedIncident}
          partSelectActive={partSelectActive} />
      )}
      <div className="h-full flex flex-col">
        <div className="flex flex-col sm:flex-row justify-between mb-4">
          <Title1>Vorfälle</Title1>
          <div className="mt-2 sm:mt-0">
            <Button Icon={Add24Regular} onClick={() => setShowCreateModal(true)}>Erstellen</Button>
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
              header: "Teilnehmer",
              titleFunction: (incident: IncidentDto) => incident.user?.email
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
          ]}
          loading={loading} />
      </div>
    </>
  );
}