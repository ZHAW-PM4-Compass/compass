"use client";

import { useUser } from "@auth0/nextjs-auth0/client";
import Image from "next/image";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";

const SubTitle: React.FC<{ collapsed: boolean, label: string, withLine?: boolean }> = ({ collapsed, label, withLine }) => {
  return (
    collapsed ? (
      withLine && (
        <div className="px-2">
          <div className="h-[1px] mt-3 mb-3 bg-gray-400 w-full"></div>
        </div>
      )
    ) : (
      <p className="mt-5 text-sm text-gray-400 font-semibold">{label}</p>
    )
  );
}

const MenuItem: React.FC<{ collapsed: boolean, icon: string; label: string, route: string, className?: any }> = ({ collapsed, icon, label, route, className }) => {
  const router = useRouter();
  const pathname = usePathname();

  return (
    <div 
      className={`${className} ${collapsed ? "mt-3 px-1.5 py-1.5 " : "mt-1 flex flex-row px-3 py-2.5"} rounded-lg cursor-pointer hover:bg-gray-100 duration-150 ${pathname === route ? 'bg-gradient-to-r from-gray-100 to-gray-200' : ''}`}
      onClick={() => router.push(route)}
      >
      <img src={icon} className="w-5 h-5 mr-2.5" />
      {!collapsed ? (<p className="text-sm">{label}</p>) : null}
    </div>
  );
}

const Profile: React.FC<{user: any}> = ({ user }) => {
  const [showMenu, setShowMenu] = useState(false);

  console.log(user)
  const handleClickOutsideMenu = (event: MouseEvent) => {
    const menu = document.getElementById("profile-menu");
    if (menu && !menu.contains(event.target as Node)) {
      setShowMenu(false);
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutsideMenu);
    return () => {
      document.removeEventListener("mousedown", handleClickOutsideMenu);
    };
  }, []);

  return (
    <>
      <button
        className="absolute top-5 right-5 rounded-full flex duration-150 hover:bg-gray-200 cursor-pointer"
        onClick={() => setShowMenu(!showMenu)}
      >
        <span className="leading-10 mx-4 text-sm">{user.given_name ? user.given_name : user.nickname}</span>
        <div className="h-10 w-10 relative">
          <Image fill={true} src={user.picture} alt="" className="border-2 border-gray-400 bg-gray-400 rounded-full" />
        </div>
      </button>
      {showMenu && (
        <div id="profile-menu" className="left-5 sm:left-auto absolute top-20 right-5 px-8 py-7 bg-white rounded-3xl flex flex-col drop-shadow-sm">
          {
            user.name !== user.email ? ( <span className="font-bold text-sm">{user.name}</span> ) : null
          }
          <span className="mb-4 text-sm">{user.email}</span>
          <a href="/api/auth/logout" className="pt-4 border-t-[1px] border-gray-200 text-sm hover:text-gray-600 duration-150">Logout</a>
        </div>
      )}
    </>
  );
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const { user } = useUser();
  const [menuOpen, setMenuOpen] = useState(true);

  return (
    <>
      <div className="sm:flex sm:flex-row h-screen w-screen absolute">
        <div className={`${menuOpen ? "w-full sm:w-64" : "hidden sm:block sm:w-16"} absolute sm:relative border-r-0 border-gray-300 z-20 h-full bg-white`}>
          <div className={`${menuOpen ? "px-7 w-full" : "px-4"} py-6`}>
            <div className="flex">
              {menuOpen ? (
                <h1 className="text-lg font-bold grow leading-9">Compass 🧭</h1>
              ) : (
                <h1 className="text-lg px-1.5 py-1.5">🧭</h1>
              )}
              <button className="p-2 bg-gray-100 hover:bg-gray-200 duration-150 rounded-md sm:hidden" onClick={() => setMenuOpen(!menuOpen)}>
                <img src="/icons/close.svg" className="w-5 h-5" />
              </button>
            </div>
            <SubTitle collapsed={!menuOpen} label="Allgemein" />
            <MenuItem collapsed={!menuOpen} icon="/icons/home.svg" label="Home" route="/home"/>
            <SubTitle collapsed={!menuOpen} label="Erfassen" withLine={true} />
            <MenuItem collapsed={!menuOpen} icon="/icons/timetrack.svg" label="Zeit" route="/times" />
            <MenuItem collapsed={!menuOpen} icon="/icons/mood.svg" label="Stimmung" route="/moods" />
            <MenuItem collapsed={!menuOpen} icon="/icons/incident.svg" label="Vorfall" route="/incidents" />
            <SubTitle collapsed={!menuOpen} label="Verwalten" withLine={true} />
            <MenuItem collapsed={!menuOpen} icon="/icons/user.svg" label="Benutzer" route="/users" />
            <MenuItem collapsed={!menuOpen} icon="/icons/user.svg" label="Benutzer" route="/users" />
          </div>
        </div>
        <div className="absolute: sm:relative grow z-10 pt-20 sm:pt-0 bg-gray-100">
          {children}
          {
            user ? (
              <Profile user={user} />
            ) : null
          }
        </div>
        <button className="absolute left-5 top-5 block sm:hidden p-2 bg-white hover:bg-gray-200 duration-150 border-2 rounded-md" onClick={() => setMenuOpen(!menuOpen)}>
          <img src="/icons/menu.svg" className="w-5 h-5" />
        </button>
      </div>
    </>
  );
}
