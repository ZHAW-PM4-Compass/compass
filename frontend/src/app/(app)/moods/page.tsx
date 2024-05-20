"use client";

import { useState, useEffect } from "react";
import Modal from "@/components/modal";
import Button from "@/components/button";
import Input from "@/components/input";
import Select from "@/components/select";
import Table from "@/components/table";
import { Edit24Regular, Save24Regular, Delete24Regular } from "@fluentui/react-icons";
import Slider from "@/components/slider"; // Import the Slider component
import { getCategoryControllerApi, getRatingControllerApi } from "@/openapi/connector";
import { RatingDto, CategoryDto } from "@/openapi/compassClient";

interface MoodModalProps {
  close: () => void;
  onSave: (rating: RatingDto) => void;
  rating?: RatingDto | null;
  categories: CategoryDto[];
}

const MoodModal: React.FC<MoodModalProps> = ({ close, onSave, rating, categories }) => {
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

      const ratingApi = getRatingControllerApi();
      try {
        const savedRating = await ratingApi.createRating({ ratingDto: newRating });
        onSave(savedRating);
        close();
      } catch (error) {
        console.error("Failed to save rating", error);
      }
    }
  };

  return (
    <Modal
      title="Rating abgeben"
      footerActions={<Button Icon={Save24Regular} onClick={handleSubmit}>Speichern</Button>}
      close={close}
    >
      <div className="flex flex-col">
        {categories.map((category) => (
          <div key={category.id} className="mb-4">
            <label>{category.name}</label>
            <Slider min={category.minimumValue} max={category.maximumValue} value={mood} onChange={setMood} />
          </div>
        ))}
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
      try {
        const categories = await categoryApi.getAllCategories();
        setCategories(categories);
      } catch (error) {
        console.error("Failed to fetch categories", error);
      }
    }
    fetchCategories();
  }, []);

  const handleSaveRating = async (rating: RatingDto) => {
    const ratingApi = getRatingControllerApi();
    try {
      const savedRating = await ratingApi.createRating({ ratingDto: rating });
      const updatedRatings = ratings.filter((r) => r.category?.id !== savedRating.category?.id);
      setRatings([...updatedRatings, savedRating]);
    } catch (error) {
      console.error("Failed to save rating", error);
    }
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
