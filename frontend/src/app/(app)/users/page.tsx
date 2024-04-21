"use client"

import Button from "@/components/button";
import Modal from "@/components/modal";
import Table from "@/components/table";
import Title1 from "@/components/title1";
import { PersonAdd24Regular, Delete24Regular, Edit24Regular } from "@fluentui/react-icons";

export default function UsersPage() {
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
      <div className="flex flex-col sm:flex-row justify-between">
        <Title1>Benutzerverwaltung</Title1>
        <div className="mt-2 sm:mt-0">
          <Button Icon={PersonAdd24Regular}>Erstellen</Button>
        </div>
      </div>
      <Modal />
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