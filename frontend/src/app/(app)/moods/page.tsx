"use client";

import { useState, useEffect } from "react";
import Modal from "@/components/modal";
import Button from "@/components/button";
import Input from "@/components/input";
import Select from "@/components/select";
import Table from "@/components/table";
import {
  Edit24Regular,
  Save24Regular,
  Delete24Regular,
} from "@fluentui/react-icons";
import {
  getCategoryControllerApi,
  getRatingControllerApi,
} from "@/openapi/connector";
import type { CategoryDto, RatingDto } from "@/openapi/compassClient/models";

interface MoodModalProps {
  close: () => void;
  onSave: (rating: RatingDto) => void;
  rating?: RatingDto | null;
  categories: CategoryDto[];
}

const MoodModal: React.FC<MoodModalProps> = ({
  close,
  onSave,
  rating,
  categories,
}) => {
  const [selectedCategory, setSelectedCategory] = useState<CategoryDto | null>(
    rating?.category || null
  );
  const [mood, setMood] = useState<number>(rating?.rating || 0);

  const handleSubmit = async () => {
    if (selectedCategory) {
      const newRating: RatingDto = {
        category: selectedCategory,
        rating: mood,
        ratingRole: "PARTICIPANT",
      };

      onSave(newRating);
      close();
    }
  };

  return (
    <Modal
      title="Rating abgeben"
      footerActions={<Button Icon={Save24Regular}>Save</Button>}
      close={() => { } } onSubmit={function (formData: FormData): void {
        throw new Error("Function not implemented.");
      } }    >
      <div className="flex flex-col">
        <div className="mb-4">
          <label>Category 1</label>
          <input type="range" min="0" max="10" className="w-full" />
        </div>
        <div className="mb-4">
          <label>Category 2</label>
          <input type="range" min="0" max="10" className="w-full" />
        </div>
        <div className="mb-4">
          <label>Category 3</label>
          <input type="range" min="0" max="10" className="w-full" />
        </div>
      </div>
    </Modal>
  );
};

const MoodTrackingPage: React.FC = () => {
  const [showModal, setShowModal] = useState<boolean>(false);
  const [selectedRating, setSelectedRating] = useState<RatingDto | null>(null);
  const [ratings, setRatings] = useState<RatingDto[]>([]);
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [selectedDate, setSelectedDate] = useState<string>(
    new Date().toISOString().slice(0, 10)
  );

  useEffect(() => {
    async function fetchCategories() {
      const categoryApi = getCategoryControllerApi();
      const categoriesResponse = await categoryApi.getAllCategories();
      setCategories(categoriesResponse);
    }
    fetchCategories();
  }, []);

  const handleSaveRating = (rating: RatingDto) => {
    const updatedRatings = ratings.filter(
      (r) => r.category?.id !== rating.category?.id
    );
    setRatings([...updatedRatings, rating]);
  };

  const handleDateChange = (date: any) => {
    setSelectedDate(date.target.value);
  };

  return (
    <>
      {showModal && (
        <MoodModal
          close={() => setShowModal(false)}
          onSave={handleSaveRating}
          rating={selectedRating}
          categories={categories}
        />
      )}
      <div className="flex flex-col">
        <div className="flex flex-row justify-between mr-2 mt-2 mb-4 space-x-2 w-full">
          <Button
            className="max-w-[200px] self-start"
            Icon={Edit24Regular}
            onClick={() => {
              setShowModal(true);
              setSelectedRating(null);
            }}
          >
            Rating abgeben
          </Button>
          <Input
            type="date"
            name="date"
            value={selectedDate}
            onChange={handleDateChange}
          />
        </div>

        <Table
          data={ratings}
          columns={[
            { header: "Kategorie", title: "category.name" },
            { header: "Rating", title: "rating" },
          ]}
          actions={[
            {
              icon: Edit24Regular,
              label: "Edit",
              onClick: (id) => {
                const rating = ratings.find((r) => r.category?.id === id);
                setSelectedRating(rating || null);
                setShowModal(true);
              },
            },
            {
              icon: Delete24Regular,
              label: "Delete",
              onClick: (id) => {
                setRatings(ratings.filter((r) => r.category?.id !== id));
              },
            },
          ]}
        />
      </div>
    </>
  );
};

export default MoodTrackingPage;
