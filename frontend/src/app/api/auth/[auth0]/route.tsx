import { getSession, handleAuth } from '@auth0/nextjs-auth0';
export const GET = handleAuth({
    async token() {
        const session = await getSession();
        return Response.json({ accessToken: session?.accessToken });
    }
});