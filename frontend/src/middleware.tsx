import { getSession, withMiddlewareAuthRequired, type Session } from "@auth0/nextjs-auth0/edge";
import { NextResponse, type NextRequest } from "next/server";
import Roles from "./constants/roles";
import { getMiddleWareControllerApi } from "./openapi/connector";

const defaultRole = Roles.PARTICIPANT;

const homeRoute = "/home";
const participantRoutes = ["/moods", "/working-hours"];
const socialWorkerRoutes = ["/working-hours-check", "/incidents", "/overview"];
const adminRoutes = ["/users"];

export default withMiddlewareAuthRequired(async (request: NextRequest) => {
  const requestedPath = request.nextUrl.pathname;
  const response = NextResponse.next();
  const { user } = await getSession(request, response) as Session;

  const isLoggedIn = !!user;

  if (isLoggedIn) {
    let userRole = defaultRole;
    try {
      const userControllerApi = await getMiddleWareControllerApi();

      if (user?.sub) {
        const backendUser = await userControllerApi.getUserById({ id: user.sub });
        userRole = backendUser?.role?.toString() as Roles;
      }
    } catch(error) {
      // do nothing
    }

    const isParticipant = userRole === Roles.PARTICIPANT;
    const isSocialWorker = userRole === Roles.SOCIAL_WORKER;
    const isAdmin = userRole === Roles.ADMIN;

    if (!isParticipant && participantRoutes.includes(requestedPath)) {
      return NextResponse.redirect(new URL(homeRoute, request.url))
    }

    if (!isSocialWorker && !isAdmin && socialWorkerRoutes.includes(requestedPath)) {
      return NextResponse.redirect(new URL(homeRoute, request.url))
    }

    if (!isAdmin && adminRoutes.includes(requestedPath)) {
      return NextResponse.redirect(new URL(homeRoute, request.url))
    }

    if (requestedPath === "/") {
      return NextResponse.redirect(new URL(homeRoute, request.url))
    }
  }

  return response;
});

export const config = {
  matcher: ["/home", "/incidents", "/moods", "/working-hours", "/working-hours-check", "/overview", "/users"],
};