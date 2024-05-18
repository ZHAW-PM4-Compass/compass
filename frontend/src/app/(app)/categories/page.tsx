"use client";

import { useState, useEffect } from "react";
import Modal from "@/components/modal";
import Button from "@/components/button";
import Input from "@/components/input";
import Select from "@/components/select";
import Table from "@/components/table";
import { PersonAdd24Regular, Save24Regular } from "@fluentui/react-icons";
import {
  getCategoryControllerApi,
  getUserControllerApi,
} from "@/openapi/connector";
import type { CategoryDto, UserDto } from "@/openapi/compassClient/models";

interface CategoryModalProps {
  close: () => void;
  onSave: (category: CategoryDto) => void;
  category?: CategoryDto | null;
}

const CategoryModal: React.FC<CategoryModalProps> = ({
  close,
  onSave,
  category,
}) => {
  const [name, setName] = useState<string>(category?.name || "");
  const [min, setMin] = useState<number>(category?.min || 0);
  const [max, setMax] = useState<number>(category?.max || 0);
  const [assignment, setAssignment] = useState<string>("Global");
  const [selectedParticipants, setSelectedParticipants] = useState<UserDto[]>(
    []
  );
  const [participants, setParticipants] = useState<UserDto[]>([]);

  useEffect(() => {
    async function fetchUsers() {
      const userApi = getUserControllerApi();
      const allParticipants = await userApi.getAllParticipants();
      setParticipants(allParticipants);
    }
    fetchUsers();
  }, []);

  const handleSubmit = async () => {
    const newCategory: CategoryDto = {
      id: category?.id || Date.now(),
      name: name,
      minimumValue: min,
      maximumValue: max,
    };
    const catApi = getCategoryControllerApi();

    if (category) {
      await catApi.linkUsersToExistingCategory({
        categoryDto: newCategory,
      });
    } else {
      await catApi.createCategory({ categoryDto: newCategory });
    }

    onSave(newCategory);
    close();
  };

  return (
    <Modal
      title={category ? "Kategorie bearbeiten" : "Kategorie erstellen"}
      footerActions={
        <Button Icon={Save24Regular} onClick={handleSubmit}>
          Save
        </Button>
      }
      close={close}
      onSubmit={function (formData: FormData): void {
        throw new Error("Function not implemented.");
      }}
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

const TaxonomyPage: React.FC = () => {
  const [showModal, setShowModal] = useState<boolean>(false);
  const [selectedCategory, setSelectedCategory] = useState<CategoryDto | null>(
    null
  );
  const [categories, setCategories] = useState<CategoryDto[]>([]);

  useEffect(() => {
    async function fetchCategories() {
      const categoryApi = getCategoryControllerApi();
      const categoriesResponse = await categoryApi.getAllCategories();
      setCategories(categoriesResponse);
    }
    fetchCategories();
  }, []);

  const handleSaveCategory = (category: CategoryDto) => {
    const updatedCategories = categories.filter((c) => c.id !== category.id);
    setCategories([...updatedCategories, category].sort((a, b) => a.id - b.id));
  };

  return (
    <>
      {showModal && (
        <CategoryModal
          close={() => setShowModal(false)}
          onSave={handleSaveCategory}
          category={selectedCategory}
        />
      )}
      <div className="flex flex-col">
        <Button
          className="max-w-[200px] self-end mb-4"
          Icon={PersonAdd24Regular}
          onClick={() => {
            setShowModal(true);
            setSelectedCategory(null);
          }}
        >
          Kategorie erstellen
        </Button>
        <Table
          data={categories}
          columns={[
            { header: "ID", title: "id" },
            { header: "Category", title: "name" },
            { header: "Min", title: "min" },
            { header: "Max", title: "max" },
            { header: "Assignment", title: "assignment" },
          ]}
          actions={[
            {
              icon: PersonAdd24Regular,
              label: "Edit",
              onClick: (id) => {
                const category = categories.find((c) => c.id === id);
                setSelectedCategory(category || null);
                setShowModal(true);
              },
            },
          ]}
        />
      </div>
    </>
  );
};

export default TaxonomyPage;
