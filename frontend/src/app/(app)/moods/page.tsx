"use client";

import { useState, useEffect, useCallback, type SetStateAction } from "react";
import Modal from "@/components/modal";
import Button from "@/components/button";
import Input from "@/components/input";
import Select from "@/components/select";
import Table from "@/components/table";
import { Edit24Regular, Save24Regular } from "@fluentui/react-icons";
import Slider from "@/components/slider";
import {
  getUserControllerApi,
  getCategoryControllerApi,
  getRatingControllerApi,
  getDaySheetControllerApi,
} from "@/openapi/connector";
import { useUser } from "@auth0/nextjs-auth0/client";
import { debounce } from "lodash";
import toast from "react-hot-toast";
import {
  CategoryDto,
  type DaySheetDto,
  type RatingDto,
  type UserDto,
} from "@/openapi/compassClient";

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
  onSave: (ratings: any[]) => void;
  categories: any[];
  participants: { id: string; label: string }[] | undefined;
  selectedParticipant: string;
  setSelectedParticipant: (participant: string) => void;
  role: string;
}) => {
  const [moodValues, setMoodValues] = useState<{ [key: string]: number }>({});

  const handleMoodChange = (categoryId: any, value: number) => {
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
    close(); // Close the modal after saving
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
              data={participants || []} // Ensure that `data` is always an array
              value={selectedParticipant}
              onChange={(event) => setSelectedParticipant(event.target.value)}
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
              name={""}
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
  const [ratings, setRatings] = useState<RatingDto[]>([]);
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [selectedDate, setSelectedDate] = useState(
    new Date().toISOString().slice(0, 10)
  );
  const [participants, setParticipants] = useState<UserDto[]>([]);
  const [selectedParticipant, setSelectedParticipant] = useState(
    user?.sub || ""
  );
  const [daySheetId, setDaySheetId] = useState<number | null>(null);

  useEffect(() => {
    const fetchDaySheet = async () => {
      const daySheetApi = getDaySheetControllerApi();
      try {
        if (role === "PARTICIPANT") {
          const daySheet = await daySheetApi.getDaySheetDate({
            date: selectedDate,
          });
          setDaySheetId(daySheet?.id ?? null); // Provide a default value of null if daySheet?.id is undefined
          setRatings(daySheet?.moodRatings || []);
          return;
        } else {
          const daySheets =
            await daySheetApi.getAllDaySheetByParticipantAndMonth({
              userId: selectedParticipant,
              month: selectedDate.slice(0, 7),
            });

          const formattedSelectedDate = new Date(selectedDate.slice(0, 10));
          console.log("Formatted selected date:", formattedSelectedDate);
          console.log("Fetched daySheets:", daySheets);

          const formatDate = (date: Date) => {
            const year = date.getFullYear();
            const month = (date.getMonth() + 1).toString().padStart(2, "0");
            const day = date.getDate().toString().padStart(2, "0");
            return `${year}-${month}-${day}`;
          };

          const selectedDateString = formatDate(formattedSelectedDate);

          let matchingDaySheet: DaySheetDto | undefined;

          daySheets.forEach((daySheet) => {
            const daySheetDateString = formatDate(
              new Date(daySheet.date ? daySheet.date : new Date())
            );
            console.log("DaySheet date:", daySheetDateString);

            if (daySheetDateString === selectedDateString) {
              console.log("Found matching daySheet:", daySheet);
              matchingDaySheet = daySheet;
            }
          });

          if (matchingDaySheet) {
            setDaySheetId(matchingDaySheet.id || null);
            setRatings(matchingDaySheet.moodRatings || []);
          } else {
            setDaySheetId(null);
            setRatings([]);
          }

          console.log("Fetched daySheetId:", matchingDaySheet?.id);
        }
      } catch (error) {
        toast.error("Failed to fetch daySheet");
      }
    };
    fetchDaySheet();
  }, [selectedDate, selectedParticipant]);

  useEffect(() => {
    if (user) {
      fetchUserRole();
    }
  }, [user]);

  useEffect(() => {
    //fetchRatings();
  }, [selectedDate, selectedParticipant, role]);

  const fetchUserRole = async () => {
    try {
      const userApi = getUserControllerApi();
      const userDto = await userApi.getUserById({
        id: user?.sub ?? "",
      });
      setRole(userDto.role || "");
      if (userDto.role === "SOCIAL_WORKER" || userDto.role === "ADMIN") {
        const users = await userApi.getAllParticipants();
        setParticipants(users);
        //first returned user to be set as selected participant
        if (users.length > 0 && users[0]?.userId !== undefined) {
          setSelectedParticipant(users[0].userId);
          fetchCategories(users[0].userId);
        } else {
          setSelectedParticipant("");
        }
      } else if (userDto.role === "PARTICIPANT") {
        const categoryApi = getCategoryControllerApi();
        const categories = await categoryApi.getCategoryListByUserId({
          userId: user?.sub ?? "",
        });
        setCategories(categories);
      }
      console.log("Fetched user role:", userDto.role);
    } catch (error) {
      console.error("Failed to fetch user role", error);
    }
  };

  const fetchRatings = async () => {
    const daySheetApi = getDaySheetControllerApi();
    try {
      if (role === "PARTICIPANT") {
        const daySheet = await daySheetApi.getDaySheetDate({
          date: selectedDate,
        });
        setRatings(daySheet?.moodRatings || []);
      } else {
        const daySheets = await daySheetApi.getAllDaySheetByParticipantAndMonth(
          {
            userId: selectedParticipant,
            month: selectedDate.slice(0, 7),
          }
        );
        const formattedSelectedDate = new Date(selectedDate.slice(0, 10))
          .toISOString()
          .split("T")[0];
        const daySheet = daySheets.find(
          (sheet) =>
            sheet.date?.toISOString().split("T")[0] === formattedSelectedDate
        );
        setRatings(daySheet?.moodRatings || []);
      }
    } catch (error) {
      console.error("Failed to fetch ratings", error);
    }
  };

  const fetchCategories = async (participantId: string | undefined) => {
    const categoryApi = getCategoryControllerApi();
    try {
      const categories = await categoryApi.getCategoryListByUserId({
        userId: participantId || "",
      });
      setCategories(categories);
      console.log(
        "Fetched categories for participant:",
        participantId,
        categories
      );
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

        const createRatingDtos = ratings.map(
          (rating: {
            category: { id: any };
            rating: any;
            ratingRole: any;
          }) => ({
            categoryId: rating.category.id,
            rating: rating.rating,
            ratingRole: rating.ratingRole,
          })
        );

        console.log("Transformed createRatingDtos", createRatingDtos);
        await ratingApi.createRatingsByDaySheetId({
          daySheetId: daySheetId ?? 0,
          createRatingDto: createRatingDtos,
        });
        fetchRatings();
      } catch (error) {
        console.error("Failed to save rating", error);
      }
    }, 300),
    [daySheetId]
  );

  const handleDateChange = (event: {
    target: { value: SetStateAction<string> };
  }) => {
    setSelectedDate(event.target.value);
  };

  const handleParticipantChange = async (
    participantId: SetStateAction<string>
  ) => {
    if (typeof participantId === "function") {
      setSelectedParticipant((prevState) => {
        const newParticipantId = participantId(prevState);
        fetchCategories(newParticipantId);
        return newParticipantId;
      });
    } else {
      setSelectedParticipant(participantId);
      await fetchCategories(participantId);
    }
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
          {role === "SOCIAL_WORKER" || role === "ADMIN" ? (
            <Select
              className="mr-4 w-48 inline-block"
              name="participant"
              required
              data={participantsData}
              value={selectedParticipant}
              onChange={(event) => handleParticipantChange(event.target.value)}
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
          data={ratings.map((rating) => ({
            category: rating.category?.name?.trim() ?? "",
            participantName: rating.ratingRole,
            rating: rating.rating,
          }))}
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
