"use server";

import type { UserDto } from "@/api/compassClient";
import { getUserControllerApi } from "@/api/connector";

export async function createUser(formData: FormData) {
  const userApi = await getUserControllerApi();
  const createUserDto: UserDto = {
      email: formData.get("email") as string,
      given_name: formData.get("given_name") as string,
      family_name: formData.get("family_name") as string,
      password: formData.get("password") as string,
      role: formData.get("role") as string,
      connection: "Username-Password-Authentication",
  };
  
  let response;
  try {
      response = await userApi.createUser(createUserDto);
  } catch (error) {
      console.error(error);
  }
  return;
}
