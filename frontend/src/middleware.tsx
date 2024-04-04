import { withMiddlewareAuthRequired } from "@auth0/nextjs-auth0/edge";

export default withMiddlewareAuthRequired();

export const config = {
  matcher: ["/home","/incidents", "/moods", "/working-hours", "/users"],
};