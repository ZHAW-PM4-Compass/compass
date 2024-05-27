"use client";

import { useState, useEffect } from "react";
import Modal from "@/components/modal";
import Button from "@/components/button";
import Input from "@/components/input";
import Select from "@/components/select";
import Table from "@/components/table";
import { AppsAddIn24Regular, Edit24Regular, PersonAdd24Regular, Save24Regular } from "@fluentui/react-icons";
import { getUserControllerApi, getCategoryControllerApi, getRatingControllerApi } from "@/openapi/connector";
import { CategoryDto, UserDto } from "@/openapi/compassClient";
import Title1 from "@/components/title1";
import { toast } from "react-hot-toast";
import toastMessages from "@/constants/toastMessages";

function CategoryCreateModal({ close, onSave, category }: Readonly<{
  close: () => void;
  onSave: () => void;
  category: CategoryDto | null;
}>) {
  const [name, setName] = useState<string>(category?.name || "");
  const [min, setMin] = useState<number>(category?.minimumValue || 0);
  const [max, setMax] = useState<number>(category?.maximumValue || 10);
  const [assignment, setAssignment] = useState<string>("global");
  const [selectedParticipants, setSelectedParticipants] = useState<UserDto[]>([]);
  const [participants, setParticipants] = useState<UserDto[]>([]);

  useEffect(() => {
    async function fetchUsers() {
      const userApi = getUserControllerApi();
      try {
        const users = await userApi.getAllUsers();
        setParticipants(users);
      } catch (error) {
        console.error("Failed to fetch users", error);
      }
    }
    fetchUsers();
  }, []);

  const handleSubmit = () => {
    const newCategory: CategoryDto = {
      name: name,
      minimumValue: min,
      maximumValue: max,
    };

    const createAction = () => getCategoryControllerApi().createCategory({ categoryDto: newCategory }).then(() => {
      onSave();
      close();
    });

    toast.promise(createAction(), {
      loading: toastMessages.CREATING,
      success: toastMessages.CATEGORY_CREATED,
      error: toastMessages.CATEGORY_NOT_CREATED,
    });
  };

  return (
    <Modal
      title="Kategorie erstellen"
      footerActions={
        <Button Icon={Save24Regular} onClick={handleSubmit}>
          Speichern
        </Button>
      }
      close={close}
    >
      <div className="flex">
        <div className="w-1/2 pr-2">
          <Input
            type="text"
            placeholder="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="mb-4"
          />
          <div className="flex">
            <div className="w-1/2 pr-2">
              <label>
                Min:
                <Input
                  type="number"
                  placeholder="Min"
                  value={min.toString()}
                  onChange={(e) => setMin(Number(e.target.value))}
                  className="mb-4"
                />
              </label>
            </div>
            <div className="w-1/2 pl-2">
              <label>
                Max:
                <Input
                  type="number"
                  placeholder="Max"
                  value={max.toString()}
                  onChange={(e) => setMax(Number(e.target.value))}
                  className="mb-4"
                />
              </label>
            </div>
          </div>
          <label>
            Zuteilung:
            <Select
              value={assignment}
              onChange={(e) => setAssignment(e.target.value)}
              className="mb-4"
              data={[
                { id: "global", label: "Alle Teilnehmer" },
                { id: "custom", label: "Teilnehmer auswählen" },
              ]}
            />
          </label>
        </div>
        <div className="w-1/2 pl-2">
          {assignment === "custom" && (
            <div className="overflow-auto max-h-64">
              {participants.map((participant) => (
                <div
                  key={participant.userId}
                  className="flex items-center mb-2"
                >
                  <input
                    type="checkbox"
                    checked={selectedParticipants.some(
                      (p) => p.userId === participant.userId
                    )}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setSelectedParticipants([
                          ...selectedParticipants,
                          participant,
                        ]);
                      } else {
                        setSelectedParticipants(
                          selectedParticipants.filter(
                            (p) => p.userId !== participant.userId
                          )
                        );
                      }
                    }}
                  />
                  <span className="ml-2">
                    {participant.givenName} {participant.familyName}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </Modal>
  );
};

export default function CategoryPage() {
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [selectedCategory, setSelectedCategory] = useState<CategoryDto | null>(
    null
  );
  const [categories, setCategories] = useState<CategoryDto[]>([]);

  const loadCategories = () => {
    setLoading(true);
    getCategoryControllerApi().getAllCategories().then((categories) => {
      setCategories(categories);
    }).catch(() => {
      toast.error(toastMessages.CATEGORIES_NOT_LOADED);
    }).finally(() => {
      setLoading(false);
    });
  }

  useEffect(loadCategories, []);

  return (
    <>
      {showModal && (
        <CategoryCreateModal
          close={() => setShowModal(false)}
          onSave={loadCategories}
          category={selectedCategory}
        />
      )}
      <div className="h-full flex flex-col">
        <div className="flex flex-col sm:flex-row justify-between mb-4">
          <Title1>Kategorien</Title1>
          <div className="mt-2 sm:mt-0">
            <Button Icon={AppsAddIn24Regular} onClick={() => {
              setShowModal(true);
              setSelectedCategory(null);
            }}>Erstellen</Button>
          </div>
        </div>
        <Table
          data={categories}
          columns={[
            {
              header: "Kategorie",
              title: "name"
            },
            {
              header: "Min",
              title: "minimumValue"
            },
            {
              header: "Max",
              title: "maximumValue"
            },
            {
              header: "Zuweisung",
              title: "assignment"
            },
          ]}
          actions={[]}
          loading={loading}
        />
      </div >
    </>
  );
};