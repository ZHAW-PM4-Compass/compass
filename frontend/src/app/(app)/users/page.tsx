"use client"

import { UserDto } from "@/openapi/compassClient";
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

const roles = [
  {
    id: Roles.PARTICIPANT,
    label: "Teilnehmer"
  },
  {
    id: Roles.SOCIAL_WORKER,
    label: "Sozialarbeiter"
  },
  {
    id: Roles.ADMIN,
    label: "Admin"
  }
]

function UserCreateModal({ close, onSave }: Readonly<{
    close: () => void;
		onSave: () => void;
  }>) {
  const onSubmit = (formData: FormData) => {
		const createUserDto: UserDto = {
				email: formData.get("email") as string,
				given_name: formData.get("given_name") as string,
				family_name: formData.get("family_name") as string,
				password: formData.get("password") as string,
				role: formData.get("role") as string,
				connection: "Username-Password-Authentication",
		};

		getUserControllerApi().createUser(createUserDto).then(response => {
			close();
			setTimeout(() => onSave(), 1000);

      if (response.status === 200) {
        toast.success(toastMessages.USER_CREATED);
      } else {
        toast.error(toastMessages.USER_NOT_CREATED);
      }
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
      <Input type="text" placeholder="Vorname" className="mb-4 mr-4 w-48 inline-block" name="given_name" required={true} />
      <Input type="text" placeholder="Nachname" className="mb-4 mr-4 w-48 inline-block" name="family_name" required={true} />
      <Select
        className="mb-4 mr-4 w-32 block"
        placeholder="Rolle wählen"
        data={roles}
				required={true} />
      <Input type="email" placeholder="Email" className="mb-4 mr-4 w-64 block" name="email" required={true} />
      <Input type="password" placeholder="Initiales Passwort" className="mb-4 mr-4 w-48 block" name="password" required={true} />
    </Modal>
  );
}

function UserUpdateModal({ close, onSave, user }: Readonly<{
	close: () => void;
	onSave: () => void;
	user: UserDto | undefined;
}>) {
const onSubmit = (formData: FormData) => {
	const createUserDto: UserDto = {
			email: formData.get("email") as string,
			given_name: formData.get("given_name") as string,
			family_name: formData.get("family_name") as string,
			password: formData.get("password") as string,
			role: formData.get("role") as string,
			connection: "Username-Password-Authentication",
	};

	getUserControllerApi().createUser(createUserDto).then(response => {
		close();
		setTimeout(() => onSave(), 1000); 
    if (response.status === 200) {
      toast.success(toastMessages.USER_UPDATED);
    } else {
      toast.error(toastMessages.USER_NOT_UPDATED);
    }
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
		<Input type="text" placeholder="Vorname" className="mb-4 mr-4 w-48 inline-block" name="given_name" required={true} value={user?.given_name} />
		<Input type="text" placeholder="Nachname" className="mb-4 mr-4 w-48 inline-block" name="family_name" required={true} value={user?.family_name} />
		<Select
			className="mb-4 mr-4 w-32 block"
			placeholder="Rolle wählen"
			data={roles}
			required={true}
			value={user?.role} />
		<Input type="email" placeholder="Email" className="mb-4 mr-4 w-64 block" name="email" disabled={true} value={user?.email} />
	</Modal>
);
}

export default function UsersPage() {
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [users, setUsers] = useState<UserDto[]>([]);
	const [selectedUser, setSelectedUser] = useState<UserDto>();

	const loadUsers = () => {
		getUserControllerApi().getAll().then(response => {
			const users = response?.data;
			users.sort((a, b) => (a?.given_name || '').localeCompare(b?.given_name || ''));
			setUsers(users);
		}).catch(() => {
			toast.error(toastMessages.DATA_NOT_LOADED);
		})
	}

  useEffect(() => loadUsers(), []);

  return (
    <>
      {showCreateModal && (
        <UserCreateModal 
					close={() => setShowCreateModal(false)}
					onSave={loadUsers} />
      )}
      {showUpdateModal && (
        <UserUpdateModal
					close={() => setShowUpdateModal(false)}
					onSave={loadUsers}
					user={selectedUser} />
      )}
      <div className="flex flex-col sm:flex-row justify-between">
        <Title1>Benutzerverwaltung</Title1>
        <div className="mt-2 sm:mt-0">
          <Button Icon={PersonAdd24Regular} onClick={() => setShowCreateModal(true)}>Erstellen</Button>
        </div>
      </div>
      <Table 
        className="mt-5"
        data={users}
        columns={[
          {
            header: "Vorname",
            title: "given_name"
          },
          {
            header: "Nachname",
            title: "family_name"
          },
          {
            header: "Email",
            title: "email"
          },
          {
            header: "Rolle",
            title: "role"
          }
        ]}
        actions={[
          {
            icon: Delete24Regular,
            label: "Löschen",
            onClick: (id) => {}
          },
          {
            icon: Edit24Regular,
            onClick: (id) => {
							setSelectedUser(users[id]);
							setShowUpdateModal(true);
						}
          }
        ]} />
    </>
  );
}