"use client"
import { useState } from "react";
import Modal from "@/components/modal";
import Button from "@/components/button";
import Input from "@/components/input";
import Select from "@/components/select";
import Table from "@/components/table";
import { PersonAdd24Regular, Save24Regular } from "@fluentui/react-icons";

const categoriesData = [
  { id: 1, name: "Effort", min: 0, max: 10, assignment: "All (Global)" },
  { id: 2, name: "Independence", min: 0, max: 5, assignment: "Custom" }
];

function CategoryModal({ close, onSave, category, users }) {
  const [name, setName] = useState(category?.name || "");
  const [min, setMin] = useState(category?.min || 0);
  const [max, setMax] = useState(category?.max || 0);
  const [assignment, setAssignment] = useState(category?.assignment || "Global");
  const [selectedUsers, setSelectedUsers] = useState([]);

  const handleSubmit = () => {
    const newCategory = { id: Date.now(), name, min, max, assignment, users: selectedUsers };
    console.log("Submitted category:", newCategory); // Placeholder for actual API call
    close();
    onSave(newCategory);
  };

  return (
    <Modal
      title={category ? "Edit Category" : "Create Category"}
      footerActions={<Button Icon={Save24Regular} onClick={handleSubmit}>Save</Button>}
      close={close}
    >
      <Input type="text" placeholder="Name" value={name} onChange={e => setName(e.target.value)} className="mb-4" />
      <Input type="number" placeholder="Min" value={min} onChange={e => setMin(Number(e.target.value))} className="mb-4" />
      <Input type="number" placeholder="Max" value={max} onChange={e => setMax(Number(e.target.value))} className="mb-4" />
      <Select
        placeholder="Assignment"
        value={assignment}
        onChange={(e: React.ChangeEvent<HTMLSelectElement>) => setAssignment(e.target.value)}
        options={[{ label: "Global", value: "Global" }, { label: "Custom", value: "Custom" }]}
        className="mb-4"
      />
      {assignment === "Custom" && (
        <Select
          placeholder="Select Users"
          multiple={true}
          value={selectedUsers}
          onChange={e => setSelectedUsers([...e.target.selectedOptions].map(o => o.value))}
          options={users.map(user => ({ label: user.name, value: user.id }))}
          className="mb-4"
        />
      )}
    </Modal>
  );
}

export default function TaxonomyPage() {
  const [showModal, setShowModal] = useState(false);
  const [categories, setCategories] = useState(categoriesData);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [users] = useState([{ id: '1', name: 'User 1' }, { id: '2', name: 'User 2' }]); // Example users data

  const handleSaveCategory = (category) => {
    const updatedCategories = categories.filter(c => c.id !== category.id);
    setCategories([...updatedCategories, category].sort((a, b) => a.id - b.id));
  };

  return (
    <>
      {showModal && (
        <CategoryModal
          close={() => setShowModal(false)}
          onSave={handleSaveCategory}
          category={selectedCategory}
          users={users}
        />
      )}
      <div className="flex flex-col">
        <Button Icon={PersonAdd24Regular} onClick={() => { setShowModal(true); setSelectedCategory(null); }}>Add Category</Button>
        <Table
          data={categories}
          columns={[
            { header: "ID", title: "id" },
            { header: "Category", title: "name" },
            { header: "Min", title: "min" },
            { header: "Max", title: "max" },
            { header: "Assignment", title: "assignment" }
          ]}
          actions={[
            {
              icon: PersonAdd24Regular,
              label: "Edit",
              onClick: (id) => {
                const category = categories.find(c => c.id === id);
                setSelectedCategory(category);
                setShowModal(true);
              }
            }
          ]}
        />
      </div>
    </>
  );
}
