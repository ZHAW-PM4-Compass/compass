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
import Slider from "@/components/slider";
import {
  getUserControllerApi,
  getCategoryControllerApi,
  getRatingControllerApi,
  getDaySheetControllerApi,
} from "@/openapi/connector";
import {
  RatingDto,
  CategoryDto,
  UserDto,
  RatingDtoRatingRoleEnum,
  DaySheetDto,
} from "@/openapi/compassClient";
import { useUser } from "@auth0/nextjs-auth0/client";

const allParticipants = "ALL_PARTICIPANTS";

const MoodModal = ({
  close,
  onSave,
  categories,
  participants,
  selectedParticipant,
  setSelectedParticipant,
  role,
}: {
  close: () => void;
  onSave: (ratings: RatingDto[]) => void;
  categories: CategoryDto[];
  participants: UserDto[];
  selectedParticipant: string;
  setSelectedParticipant: (participant: string) => void;
  role: RatingDtoRatingRoleEnum;
}) => {
  const [moodValues, setMoodValues] = useState<{ [key: string]: number }>({});

  const handleMoodChange = (categoryId: number | undefined, value: number) => {
    setMoodValues((prevValues) => ({
      ...prevValues,
      [String(categoryId)]: value,
    }));
  };

  const handleSubmit = () => {
    const ratingsToSave = Object.keys(moodValues).map((categoryId) => ({
      category: { id: Number(categoryId) },
      rating: moodValues[categoryId],
      ratingRole: role,
    }));
    onSave(ratingsToSave);
  };

  return (
    <Modal
      title="Rating abgeben"
      footerActions={
        <>
          {participants && setSelectedParticipant && (
            <Select
              className="mr-4 w-48 inline-block"
              name="participant"
              required={true}
              data={participants}
              value={selectedParticipant}
              onChange={(event) => setSelectedParticipant(event.target.value)}
              disabled={role === "PARTICIPANT"}
            />
          )}
          <Button Icon={Save24Regular} type="submit" onClick={handleSubmit}>
            Speichern
          </Button>
        </>
      }
      close={close}
      onSubmit={handleSubmit}
    >
      <div className="flex flex-col">
        {categories.map((category) => (
          <div key={category.id} className="mb-4">
            <label>{category.name}</label>
            <Slider
              min={category.minimumValue}
              max={category.maximumValue}
              value={moodValues[category.id] || category.minimumValue}
              onChange={(value) => handleMoodChange(category.id, value)}
            />
          </div>
        ))}
      </div>
    </Modal>
  );
};

type RatingData = {
  date: Date;
  participantName: string;
  rating: RatingDto;
};

const MoodTrackingPage = () => {
  const { user } = useUser();
  const [role, setRole] = useState<RatingDtoRatingRoleEnum>("PARTICIPANT");
  const [showModal, setShowModal] = useState(false);
  const [ratings, setRatings] = useState<RatingData[]>([]);
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [selectedDate, setSelectedDate] = useState(
    new Date().toISOString().slice(0, 10)
  );
  const [participants, setParticipants] = useState<UserDto[]>([]);
  const [selectedParticipant, setSelectedParticipant] = useState(
    user?.sub || ""
  );
  const [daySheetId, setDaySheetId] = useState<number | undefined>(null);

  useEffect(() => {
    const fetchDaySheet = async () => {
      const daySheetApi = getDaySheetControllerApi();
      try {
        const daySheet = await daySheetApi.getDaySheetDate({
          date: selectedDate,
        });

        setDaySheetId(daySheet?.id);
      } catch (error) {
        console.error("Failed to fetch daySheet", error);
      }
    };
    fetchDaySheet();
  }, [selectedDate]);

  useEffect(() => {
    if (user) {
      fetchUserRole();
    }
  }, [user]);

  useEffect(() => {
    fetchRatings();
  }, [selectedDate, selectedParticipant, role, user]);

  const fetchUserRole = async () => {
    try {
      const userApi = getUserControllerApi();
      const userDto = await userApi.getUserById({ id: user.sub });
      setRole(userDto.role || "");
      if (userDto.role === "SOCIAL_WORKER" || userDto.role === "ADMIN") {
        const users = await userApi.getAllUsers();
        setParticipants(users);
      } else if (userDto.role === "PARTICIPANT") {
        const categoryApi = getCategoryControllerApi();
        const categories = await categoryApi.getCategoryListByUserId({
          userId: user.sub ?? "",
        });
        setCategories(categories);
      }
    } catch (error) {
      console.error("Failed to fetch user role", error);
    }
  };

  const fetchRatings = async () => {
    const ratingApi = getRatingControllerApi();
    try {
      const ratings = await ratingApi.getMoodRatingByDate({
        date: selectedDate,
        userId:
          role === "PARTICIPANT"
            ? user?.sub
            : selectedParticipant !== allParticipants
              ? selectedParticipant
              : undefined,
      });
      setRatings(ratings);
    } catch (error) {
      console.error("Failed to fetch ratings", error);
    }
  };

  const fetchCategories = async (participantId: string) => {
    const categoryApi = getCategoryControllerApi();
    try {
      const categories = await categoryApi.getCategoryListByUserId({
        userId: participantId,
      });
      setCategories(categories);
    } catch (error) {
      console.error("Failed to fetch categories", error);
    }
  };

  const handleSaveRating = async (ratings: RatingDto[]) => {
    const ratingApi = getRatingControllerApi();
    try {
        console.log("daySheetId", daySheetId);
        console.log("Original ratings", ratings);

        // Map ratings to categoryDto
        const categoryDto = ratings.map(rating => ({
            id: rating.category.id,
            name: rating.category.name,
            minimumValue: rating.category.minimumValue,
            maximumValue: rating.category.maximumValue
        }));

        console.log("Transformed categoryDto", categoryDto);

        await ratingApi.recordCategoryRatingsByDaySheetAndUserId({
            daySheetId: daySheetId!,
            categoryDto: categoryDto
        });

        fetchRatings();
    } catch (error) {
        console.error("Failed to save rating", error);
    }
};




  const handleDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSelectedDate(event.target.value);
  };

  const handleParticipantChange = async (participantId: string) => {
    setSelectedParticipant(participantId);
    await fetchCategories(participantId);
  };

  const participantsData = participants.map((participant) => ({
    id: participant.userId ?? "",
    label: participant.email ?? "",
  }));

  return (
    <>
      {showModal && (
        <MoodModal
          close={() => setShowModal(false)}
          onSave={handleSaveRating}
          categories={categories}
          participants={
            role === "SOCIAL_WORKER" || role === "ADMIN"
              ? participantsData
              : undefined
          }
          selectedParticipant={selectedParticipant}
          setSelectedParticipant={setSelectedParticipant}
          role={role}
        />
      )}
      <div className="flex flex-col">
        <div className="flex flex-row justify-between mr-2 mt-2 mb-4 space-x-2 w-full">
          <Button
            className="max-w-[200px] self-start"
            Icon={Edit24Regular}
            onClick={() => {
              setShowModal(true);
            }}
          >
            Rating abgeben
          </Button>
          <Select
            className="mr-4 w-48 inline-block"
            name="participant"
            required={true}
            data={participantsData}
            value={selectedParticipant}
            onChange={(event) => handleParticipantChange(event.target.value)}
            disabled={role === "PARTICIPANT"}
          />
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
            { header: "Kategorie", title: "rating.category.name" },
            { header: "Teilnehmer", title: "participantName" },
            { header: "Value", title: "rating.rating" },
          ]}
          actions={[
            {
              icon: Edit24Regular,
              label: "Edit",
              onClick: (id) => {
                const rating = ratings.find(
                  (r) => r.rating?.category?.id === id
                )?.rating;
                setSelectedDate(rating || null);
                setShowModal(true);
              },
            },
            {
              icon: Delete24Regular,
              label: "Delete",
              onClick: (id) => {
                setRatings(
                  ratings.filter((r) => r.rating?.category?.id !== id)
                );
              },
            },
          ]}
        />
      </div>
    </>
  );
};

export default MoodTrackingPage;
