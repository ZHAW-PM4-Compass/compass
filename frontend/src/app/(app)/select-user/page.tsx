// Import necessary dependencies
"use client"; // Add the "use client" directive
import React, { useState, useEffect } from "react";
import UserDropdown from "@/components/userdropdown";
import toastMessages from "@/constants/toastMessages";
import { toast } from "react-hot-toast";
import Roles from "@/constants/roles";
import RoleTitles from "@/constants/roleTitles";
import { UserDto } from "@/openapi/compassClient";
import { getUserControllerApi } from "@/openapi/connector"; // Adjust the import path as needed

const MyPage = () => {
    const [users, setUsers] = useState<UserDto[] | undefined>(undefined); // Adjust the type to handle undefined
    const [selectedUser, setSelectedUser] = useState<UserDto | undefined>(undefined); // Track selected user

    const loadUsers = () => {
        getUserControllerApi()
            .getAllUsers()
            .then((userDtos: UserDto[]) => {
                const updatedUsers = userDtos.map((user) => ({
                    ...user,
                    role: user.role ?? Roles.PARTICIPANT,
                    roleTitle: RoleTitles[user.role as Roles],
                }));
                updatedUsers.sort(
                    (a, b) => (a?.givenName || "").localeCompare(b?.givenName || "")
                );
                setUsers(updatedUsers);
            })
            .catch(() => {
                toast.error(toastMessages.DATA_NOT_LOADED);
            });
    };

    useEffect(() => {
        loadUsers();
    }, []);

    // Map user given names to options, filtering out undefined values and converting to strings
    const userOptions: UserDto[] = (users || []).map((userDto) => userDto);

    // Callback function to handle user selection
    const handleUserSelect = (selectedOption: UserDto) => {
        setSelectedUser(selectedOption);
    };

    return (
        <div className="container mx-auto">
            <h1 className="text-2xl font-bold mb-4">Dropdown Example</h1>
            <UserDropdown options={userOptions} onSelect={handleUserSelect} />
            {selectedUser && (
                <p className="mt-4">Selected userId: {selectedUser.userId}</p>
            )}
        </div>
    );
};

export default MyPage;
