"use client"

import { getUserControllerApi } from "@/openapi/connector";
import Button from "@/components/button";
import Input from "@/components/input";
import Modal from "@/components/modal";
import Select from "@/components/select";
import Table from "@/components/table";
import Title1 from "@/components/title1";
import Roles from "@/constants/roles";
import { PersonAdd24Regular, Delete24Regular, Edit24Regular, Save24Regular } from "@fluentui/react-icons";
import { useEffect, useState } from "react";
import { toast } from 'react-hot-toast';
import toastMessages from "@/constants/toastMessages";
import RoleTitles from "@/constants/roleTitles";
import type { CreateUserRequest, UpdateUserRequest, UserDto } from "@/openapi/compassClient";
import { useUser } from "@auth0/nextjs-auth0/client";

const roles = [
  {
    id: Roles.PARTICIPANT,
    label: RoleTitles[Roles.PARTICIPANT]
  },
  {
    id: Roles.SOCIAL_WORKER,
    label: RoleTitles[Roles.SOCIAL_WORKER]
  },
  {
    id: Roles.ADMIN,
    label: RoleTitles[Roles.ADMIN]
  }
]

enum formFields {
  GIVEN_NAME = "givenName",
  FAMILY_NAME = "familyName",
  ROLE = "role",
  EMAIL = "email",
  PASSWORD = "password"
}

function UserCreateModal({ close, onSave }: Readonly<{
    close: () => void;
		onSave: () => void;
  }>) {
  const onSubmit = (formData: FormData) => {
    const createUserDto: CreateUserRequest = {
      createAuthZeroUserDto: {
        email: formData.get(formFields.EMAIL) as string,
        givenName: formData.get(formFields.GIVEN_NAME) as string,
        familyName: formData.get(formFields.FAMILY_NAME) as string,
        role: formData.get(formFields.ROLE) as string,
        password: formData.get(formFields.PASSWORD) as string
      }
    };
  
    getUserControllerApi().createUser(createUserDto).then(() => {
      close();
      setTimeout(() => onSave(), 1000);
      toast.success(toastMessages.USER_CREATED);
    }).catch(() => {
      toast.error(toastMessages.USER_NOT_CREATED);
    })
  }

  return (
    <Modal
      title="Benutzer erstellen"
      footerActions={
        <Button Icon={Save24Regular} type="submit">Speichern</Button>
      }
      close={close}
			onSubmit={onSubmit}
    >
      <Input type="text" placeholder="Vorname" className="mb-4 mr-4 w-48 inline-block" name={formFields.GIVEN_NAME} required={true} />
      <Input type="text" placeholder="Nachname" className="mb-4 mr-4 w-48 inline-block" name={formFields.FAMILY_NAME} required={true} />
      <Select
        className="mb-4 mr-4 w-32 block"
        placeholder="Rolle wählen"
        name={formFields.ROLE}
        data={roles}
				required={true} />
      <Input type="email" placeholder="Email" className="mb-4 mr-4 w-64 block" name={formFields.EMAIL} required={true} />
      <Input type="password" placeholder="Initiales Passwort" className="mb-4 mr-4 w-48 block" name={formFields.PASSWORD} required={true} />
    </Modal>
  );
}

function UserUpdateModal({ close, onSave, user }: Readonly<{
	close: () => void;
	onSave: () => void;
	user: UserDto | undefined;
}>) {
  const [givenName, setGivenName] = useState(user?.givenName);
  const [familyName, setFamilyName] = useState(user?.familyName);

  const onSubmit = (formData: FormData) => {
    const updateUserRequest: UpdateUserRequest = {
      id: user?.userId as string,
      authZeroUserDto: {
        email: formData.get(formFields.EMAIL) as string,
        givenName: formData.get(formFields.GIVEN_NAME) as string,
        familyName: formData.get(formFields.FAMILY_NAME) as string,
        role: formData.get(formFields.ROLE) as string
      }
    };

  	getUserControllerApi().updateUser(updateUserRequest).then(() => {
  		close();
  		setTimeout(() => onSave(), 1000);
      toast.success(toastMessages.USER_UPDATED);
  	}).catch(() => {
  		toast.error(toastMessages.USER_NOT_UPDATED);
  	})
  }

  return (
    <Modal
      title="Benutzer bearbeiten"
      footerActions={
        <Button Icon={Save24Regular} type="submit">Speichern</Button>
      }
      close={close}
      onSubmit={onSubmit}
    >
      <Input type="text" placeholder="Vorname" className="mb-4 mr-4 w-48 inline-block" name={formFields.GIVEN_NAME} required={true} value={givenName} onChange={(e) => setGivenName(e.target.value)} />
      <Input type="text" placeholder="Nachname" className="mb-4 mr-4 w-48 inline-block" name={formFields.FAMILY_NAME} required={true} value={familyName} onChange={(e) => setFamilyName(e.target.value)} />
      <Select
        className="mb-4 mr-4 w-32 block"
        placeholder="Rolle wählen"
        name={formFields.ROLE}
        data={roles}
        required={true}
        value={user?.role} />
      <Input type="email" placeholder="Email" className="mb-4 mr-4 w-64 block" name={formFields.EMAIL} disabled={true} value={user?.email} />
    </Modal>
  );
}

export default function IncidentsPage() {
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [incidents, setIncidents] = useState<UserDto[]>([]);
	const [selectedIncident, setSelectedIncident] = useState<UserDto>();
  const { user } = useUser();

	const loadIncidents = () => {
    if (user?.sub){
      getUserControllerApi().getUserById({ id: user.sub }).then((userDto) => {
        if (userDto.role === Roles.SOCIAL_WORKER || userDto.role === Roles.ADMIN) {
          getUserControllerApi().getAllIncidents().then((incidents) => {
            setIncidents(incidents);
          });
        } else {
          getUserControllerApi().getIncidentsByUserId({ userId: user.sub }).then((incidents) => {
            setIncidents(incidents);
          }).catch(() => {
            toast.error(toastMessages.INCIDENTS_NOT_LOADED);
          });
        }
      });

    }
	}

  const deleteIncident = (id: string) => {
    
  }

  useEffect(() => {
    if (asdf)
    loadIncidents();
  }, []);

  return (
    <>
      {showCreateModal && (
        <UserCreateModal 
					close={() => setShowCreateModal(false)}
					onSave={loadIncidents} />
      )}
      {showUpdateModal && (
        <UserUpdateModal
					close={() => setShowUpdateModal(false)}
					onSave={loadIncidents}
					user={selectedIncident} />
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