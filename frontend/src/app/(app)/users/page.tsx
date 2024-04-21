"use client"

import Button from "@/components/button";
import Input from "@/components/input";
import Modal from "@/components/modal";
import Select from "@/components/select";
import Table from "@/components/table";
import TextArea from "@/components/textarea";
import Title1 from "@/components/title1";
import { PersonAdd24Regular, Delete24Regular, Edit24Regular, Save24Regular } from "@fluentui/react-icons";
import type { title } from "process";
import { useState } from "react";

export default function UsersPage() {
  const [showCreateModal, setShowCreateModal] = useState(false);

  const users = [
    {
      email: "baumgartner.noah@outlook.com",
      firstName: "Noah",
      surname: "Baumgartner",
      role: "Admin"
    },
    {
      email: "baumgartner.noah@outlook.com",
      firstName: "Noah",
      surname: "Baumgartner",
      role: "Admin"
    },
    {
      email: "baumgartner.noah@outlook.com",
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
          footerActions={
            <Button Icon={Save24Regular}>Speichern</Button>
          }
        >
          <Input type="text" placeholder="Vorname" className="mb-4 mr-4 w-48 inline-block" />
          <Input type="text" placeholder="Nachname" className="mb-4 inline-block" />
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
            title: "firstName"
          },
          {
            header: "Nachname",
            title: "surname"
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
            onClick: () => {}
          }
        ]}/>
    </>
  );
}