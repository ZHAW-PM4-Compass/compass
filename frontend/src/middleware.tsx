import { getSession, withMiddlewareAuthRequired, type Session } from "@auth0/nextjs-auth0/edge";
import { NextResponse, type NextRequest } from "next/server";
import Roles from "./constants/roles";
import { getMiddleWareControllerApi } from "./openapi/connector";

const defaultRole = Roles.ADMIN;

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
      const userControllerApi = getMiddleWareControllerApi();
      const backendUser = user?.sub && await userControllerApi.getUserById({ id: user.sub });
      userRole = backendUser?.role ?? userRole;
    } catch(error) {
      // do nothing
    }

    const isSocialWorker = userRole === Roles.SOCIAL_WORKER;
    const isAdmin = userRole === Roles.ADMIN;

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