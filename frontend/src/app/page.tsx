'use client';
import { useUser } from '@auth0/nextjs-auth0/client';

export default function Page() {
    const { user, isLoading, error } = useUser();

    return (
        <div>
            <h1>Hello, Welcome to Compass</h1>
            <div>
              <button>
                {user ? (
                  <a href="/api/auth/logout">Logout</a>
                ) : (
                  <a href="/api/auth/login?returnTo=/profile">Login</a>
                )}
              </button>
                <p>User: {JSON.stringify(user)}</p>
                <p>is Loading: {JSON.stringify(isLoading)}</p>
                <p>Error: {JSON.stringify(error)}</p>
            </div>
        </div>
    )
}