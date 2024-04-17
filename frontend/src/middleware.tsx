import { getSession, withMiddlewareAuthRequired, type Session } from "@auth0/nextjs-auth0/edge";
import { NextResponse } from "next/server";
import Roles from "./constants/roles";

const homeRoute = "/home";

const participantRoutes = ["/incidents", "/moods", "/working-hours"];
const socialWorkerRoutes = ["/working-hours-check", "/overview"];
const adminRoutes = ["/users"];

export default withMiddlewareAuthRequired(async (request) => {
  const requestedPath = request.nextUrl.pathname;
  const response = NextResponse.next();
  const { user } = await getSession(request, response) as Session;

  const isLoggedIn = !!user;
  const isSocialWorker = user["compass/roles"].includes(Roles.SOCIAL_WORKER);
  const isAdmin = user["compass/roles"].includes(Roles.ADMIN);

  if (!isSocialWorker && !isAdmin && socialWorkerRoutes.includes(requestedPath)) {
    return NextResponse.redirect(new URL(homeRoute, request.url))
  }

  if (!isAdmin && adminRoutes.includes(requestedPath)) {
    return NextResponse.redirect(new URL(homeRoute, request.url))
  }

  if (requestedPath === "/" && isLoggedIn) {
    return NextResponse.redirect(new URL("/home", request.url))
  }

  return null;
});

export const config = {
  matcher: [homeRoute, ...participantRoutes, ...socialWorkerRoutes, ...adminRoutes],
};