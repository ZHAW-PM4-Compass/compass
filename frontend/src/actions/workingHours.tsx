"use server";

import { getSession } from "@auth0/nextjs-auth0";
import { Configuration, DaySheetControllerApi, CreateDaySheetDto } from "@/api/compassClient";

export async function createDaySheet(formData: FormData) {
    const session = await getSession();
    const config = new Configuration({
        basePath: process.env.REACT_APP_API_BASE_PATH,
        baseOptions: {
            headers: {
                Authorization: `Bearer ${session?.accessToken}`,
            }
        }
    });

    const daySheetApi = new DaySheetControllerApi(config);
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