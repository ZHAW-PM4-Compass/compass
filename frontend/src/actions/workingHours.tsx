"use server";

import { getDaySheetControllerApi } from "@/api/connector";
import { CreateDaySheetDto } from "@/api/compassClient";

export async function createDaySheet(formData: FormData) {
    const daySheetApi = await getDaySheetControllerApi();
    const createDaySheetDto: CreateDaySheetDto = {
        date: formData.get("date") as string,
        day_report: formData.get("dayReport") as string,
    };
    
    let response;
    try {
        response = await daySheetApi.createDaySheet(createDaySheetDto);
    } catch (error) {
        console.error(error);
    }
    return response;
}