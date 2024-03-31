"use client";

import { useRouter } from "next/navigation";

const MenuItem: React.FC<{ icon: string; label: string, route: string, className?: any }> = ({ icon, label, route, className }) => {
  const router = useRouter();

  return (
    <div 
      className={`${className} flex flex-row px-3 py-2.5 rounded-lg cursor-pointer hover:bg-gray-100 duration-150`}
      onClick={() => router.push(route)}
      >
      <img src={icon} className="w-5 h-5 mr-2.5" />
      <p className="text-sm">{label}</p>
    </div>
  );
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="flex flex-row h-screen w-screen">
      <div className="w-64">
        <div className="px-7 py-6 w-full">
          <h1 className="text-lg font-bold">Compass 🧭</h1>
          <p className="mt-5 text-sm text-gray-400 font-semibold">Allgemein</p>
          <MenuItem className="mt-1" icon="/icons/home.svg" label="Home" route="/home"/>
          <p className="mt-5 text-sm text-gray-400 font-semibold">Erfassen</p>
          <MenuItem className="mt-1" icon="/icons/timetrack.svg" label="Zeit" route="/times" />
          <MenuItem className="mt-0.5" icon="/icons/mood.svg" label="Stimmung" route="/moods" />
          <MenuItem className="mt-0.5" icon="/icons/incident.svg" label="Vorfall" route="/incidents" />
          <p className="mt-5 text-sm text-gray-400 font-semibold">Verwalten</p>
          <MenuItem className="mt-1" icon="/icons/user.svg" label="Benutzer" route="/users" />
        </div>
      </div>
      <div className="grow">
        {children}
      </div>
    </div>
  );
}
