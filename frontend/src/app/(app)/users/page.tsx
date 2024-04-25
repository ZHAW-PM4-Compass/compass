"use client"

import { createUser } from "@/actions/users";
import Button from "@/components/button";
import Input from "@/components/input";
import Modal from "@/components/modal";
import Select from "@/components/select";
import Table from "@/components/table";
import Title1 from "@/components/title1";
import Roles from "@/constants/roles";
import { PersonAdd24Regular, Delete24Regular, Edit24Regular, Save24Regular } from "@fluentui/react-icons";
import { useEffect, useState } from "react";

export default function UsersPage() {
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [users, setUsers] = useState([]);

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

  useEffect(() => {
    fetch("/proxy/users")
      .then(res => res.json())
      .then(data => setUsers(data));
  }, []);

  return (
    <>
      {showCreateModal && (

        <form action={createUser}>
          <Modal
            title="Benutzer erstellen"
            close={() => setShowCreateModal(false)}
            footerActions={
              <Button Icon={Save24Regular} type="submit">Speichern</Button>
            }
          >
            <Input type="text" placeholder="Vorname" className="mb-4 mr-4 w-48 inline-block" name="given_name" />
            <Input type="text" placeholder="Nachname" className="mb-4 mr-4 w-48 inline-block" name="family_name" />
            <Select
              className="mb-4 mr-4 w-32 block"
              placeholder="Rolle wählen"
              data={roles} />
            <Input type="email" placeholder="Email" className="mb-4 mr-4 w-64 block" name="email" />
            <Input type="password" placeholder="Initiales Passwort" className="mb-4 mr-4 w-48 block" name="password" />
          </Modal>
        </form>
      )}
      {showUpdateModal && (
        <Modal
          title="Benutzer bearbeiten"
          close={() => setShowUpdateModal(false)}
          footerActions={
            <Button Icon={Save24Regular}>Speichern</Button>
          }
        >
          <Input type="text" placeholder="Vorname" className="mb-4 mr-4 w-48 inline-block" />
          <Input type="text" placeholder="Nachname" className="mb-4 mr-4 w-48 inline-block" />
          <Select
            className="mb-4 mr-4 w-32 block"
            placeholder="Rolle wählen"
            data={roles} />
          <Input type="email" placeholder="Email" className="mb-4 mr-4 w-64 block" disabled={true} />
        </Modal>
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
            onClick: () => {}
          },
          {
            icon: Edit24Regular,
            onClick: () => setShowUpdateModal(true)
          }
        ]}/>
    </>
  );
}