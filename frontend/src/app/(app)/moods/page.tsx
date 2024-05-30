"use client";

import { useState, useEffect, useCallback } from "react";
import Modal from "@/components/modal";
import Button from "@/components/button";
import Input from "@/components/input";
import Select from "@/components/select";
import Table from "@/components/table";
import { Edit24Regular, Save24Regular } from "@fluentui/react-icons";
import Slider from "@/components/slider";
import { getUserControllerApi, getCategoryControllerApi, getRatingControllerApi, getDaySheetControllerApi } from "@/openapi/connector";
import { useUser } from "@auth0/nextjs-auth0/client";
import { debounce } from "lodash";

const allParticipants = "ALL_PARTICIPANTS";

const MoodModal = ({ close, onSave, categories, participants, selectedParticipant, setSelectedParticipant, role }: { close: any, onSave: any, categories: any, participants: any, selectedParticipant: any, setSelectedParticipant: any, role: any }) => {
  const [moodValues, setMoodValues] = useState({});

  const handleMoodChange = (categoryId, value) => {
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
    console.log("Saving ratings:", ratingsToSave);
    onSave(ratingsToSave);
  };

  return (
    <Modal
      title="Rating abgeben"
      footerActions={
        <>
          {role === "SOCIAL_WORKER" || role === "ADMIN" ? (
            <Select
              className="mr-4 w-48 inline-block"
              name="participant"
              required
              data={participants}
              value={selectedParticipant}
              onChange={(event) => setSelectedParticipant(event.target.value)}
              disabled={role === "PARTICIPANT"}
            />
          ) : null}
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

const MoodTrackingPage = () => {
  const { user } = useUser();
  const [role, setRole] = useState("PARTICIPANT");
  const [showModal, setShowModal] = useState(false);
  const [ratings, setRatings] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().slice(0, 10));
  const [participants, setParticipants] = useState([]);
  const [selectedParticipant, setSelectedParticipant] = useState(user?.sub || "");
  const [daySheetId, setDaySheetId] = useState(null);

  useEffect(() => {
    const fetchDaySheet = async () => {
      const daySheetApi = getDaySheetControllerApi();
      try {
        const daySheet = await daySheetApi.getDaySheetDate({ date: selectedDate });
        setDaySheetId(daySheet?.id);
        console.log("Fetched daySheetId:", daySheet?.id);
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
  }, [selectedDate, selectedParticipant, role]);

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
        const categories = await categoryApi.getCategoryListByUserId({ userId: user.sub ?? "" });
        setCategories(categories);
      }
      console.log("Fetched user role:", userDto.role);
    } catch (error) {
      console.error("Failed to fetch user role", error);
    }
  };

  const fetchRatings = async () => {
    const ratingApi = getRatingControllerApi();
    try {
      const ratings = await ratingApi.getMoodRatingByDate({
        date: selectedDate,
        userId: role === "PARTICIPANT" ? user?.sub : selectedParticipant !== allParticipants ? selectedParticipant : undefined,
      });

      const formattedRatings = ratings.map(rating => ({
        category: rating.rating.category.name.trim(),
        participantName: rating.participantName,
        rating: rating.rating.rating
      }));

      setRatings(formattedRatings);
      console.log("Fetched ratings:", formattedRatings);
    } catch (error) {
      console.error("Failed to fetch ratings", error);
    }
  };

  const fetchCategories = async (participantId) => {
    const categoryApi = getCategoryControllerApi();
    try {
      const categories = await categoryApi.getCategoryListByUserId({ userId: participantId });
      setCategories(categories);
      console.log("Fetched categories for participant:", participantId, categories);
    } catch (error) {
      console.error("Failed to fetch categories", error);
    }
  };

  const handleSaveRating = useCallback(
    debounce(async (ratings) => {
      const ratingApi = getRatingControllerApi();
      try {
        console.log("daySheetId", daySheetId);
        console.log("Original ratings", ratings);

        const createRatingDtos = ratings.map((rating) => ({
          categoryId: rating.category.id,
          rating: rating.rating,
          ratingRole: rating.ratingRole,
        }));

        console.log("Transformed createRatingDtos", createRatingDtos);
        await ratingApi.createRatingsByDaySheetId({
          daySheetId: daySheetId,
          createRatingDto: createRatingDtos,
        });
        fetchRatings();
      } catch (error) {
        console.error("Failed to save rating", error);
      }
    }, 300),
    [daySheetId]
  );

  const handleDateChange = (event) => {
    setSelectedDate(event.target.value);
  };

  const handleParticipantChange = async (participantId) => {
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
          participants={role === "SOCIAL_WORKER" || role === "ADMIN" ? participantsData : undefined}
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
          {role === "SOCIAL_WORKER" || role === "ADMIN" ? (
            <Select
              className="mr-4 w-48 inline-block"
              name="participant"
              required
              data={participantsData}
              value={selectedParticipant}
              onChange={(event) => handleParticipantChange(event.target.value)}
              disabled={role === "PARTICIPANT"}
            />
          ) : null}
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
            { header: "Kategorie", title: "category" },
            { header: "Teilnehmer", title: "participantName" },
            { header: "Angabe", title: "rating" },
          ]}
          actions={[]}
        />
      </div>
    </>
  );
};

export default MoodTrackingPage;
