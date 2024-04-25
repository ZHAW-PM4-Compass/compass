import { getUserControllerApi } from "@/api/connector";

export async function GET() {
    const userApi = await getUserControllerApi();
    
    let response;
    try {
        response = await userApi.getAll();
    } catch (error) {
        console.error(error);
    }

    return Response.json(response?.data as Object);
}