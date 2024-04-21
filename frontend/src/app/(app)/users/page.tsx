"use client"

import Button from "@/components/button";
import Input from "@/components/input";
import Modal from "@/components/modal";
import Table from "@/components/table";
import Title1 from "@/components/title1";
import { PersonAdd24Regular, Delete24Regular, Edit24Regular, Save24Regular } from "@fluentui/react-icons";
import { useState } from "react";

export default function UsersPage() {
  const [showCreateModal, setShowCreateModal] = useState(false);

  const users = [
    {
      id: 1,
      firstName: "Noah",
      surname: "Baumgartner",
      role: "Admin"
    },
    {
      id: 2,
      firstName: "Noah",
      surname: "Baumgartner",
      role: "Admin"
    },
    {
      id: 3,
      firstName: "Noah",
      surname: "Baumgartner",
      role: "Admin"
    }
  ]

  return (
    <>
      {showCreateModal && (
        <Modal
          title="Benutzer erstellen"
          close={() => setShowCreateModal(false)}
          saveButton={
            <Button Icon={Save24Regular}>Speichern</Button>
          }
        >
          <Input type="text" placeholder="Vorname" className="mb-4 mr-4 w-48 inline-block" />
          <Input type="text" placeholder="Nachname" className="mb-4 inline-block" />
          <Input type="text" placeholder="Rolle" className="block" />
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
            header: "Id",
            title: "id"
          },
          {
            header: "Vorname",
            title: "firstName"
          },
          {
            header: "Nachname",
            title: "surname"
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
            onClick: () => {}
          }
        ]}/>
    </>
  );
}