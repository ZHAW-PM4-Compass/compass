'use client';

import Button from "@/components/button";
import Loading from "@/components/loading";
import { useUser } from "@auth0/nextjs-auth0/client";
import { useRouter } from "next/navigation";

const Landing: React.FC = () => {
  const router = useRouter();
  const { user, isLoading } = useUser();

  if (user) router.push('/home');

  const login = () => router.push('/api/auth/login?returnTo=/home');

  return (
    isLoading || user ? <Loading /> :
    <div className="w-full h-screen bg-slate-100 backdrop-blur-lg bg-no-repeat bg-center bg-cover flex items-center justify-center">
        <div className="flex-col">
          <h1 className="text-4xl font-bold text-slate-900">Compass 🧭</h1>
          <Button className="mt-6" onClick={login}>Login</Button>
        </div>
    </div>
  );
}

export default Landing;