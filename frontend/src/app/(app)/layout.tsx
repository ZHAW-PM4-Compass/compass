"use client";

import { useUser } from "@auth0/nextjs-auth0/client";
import Image from "next/image";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";

import HomeIcon from "@fluentui/svg-icons/icons/home_24_regular.svg";
import HomeIconFilled from "@fluentui/svg-icons/icons/home_24_filled.svg";
import TimeIcon from "@fluentui/svg-icons/icons/timeline_24_regular.svg";
import TimeIconFilled from "@fluentui/svg-icons/icons/timeline_24_filled.svg";
import MoodIcon from "@fluentui/svg-icons/icons/communication_person_24_regular.svg";
import MoodIconFilled from "@fluentui/svg-icons/icons/communication_person_24_filled.svg";
import IncidentIcon from "@fluentui/svg-icons/icons/alert_24_regular.svg";
import IncidentIconFilled from "@fluentui/svg-icons/icons/alert_24_filled.svg";
import UserIcon from "@fluentui/svg-icons/icons/person_24_regular.svg";
import UserIconFilled from "@fluentui/svg-icons/icons/person_24_filled.svg";
import ExpandMenuIcon from "@fluentui/svg-icons/icons/chevron_right_24_regular.svg";
import CollapseMenuIcon from "@fluentui/svg-icons/icons/chevron_left_24_regular.svg";

const SubTitle: React.FC<{ collapsed: boolean, label: string, withLine?: boolean }> = ({ collapsed, label, withLine }) => {
  return (
    collapsed ? (
      withLine && (
        <div className="px-2">
          <div className="h-[1px] mt-4 mb-1 bg-slate-400 w-full"></div>
        </div>
      )
    ) : (
      <p className="mt-5 text-sm text-slate-400 font-semibold">{label}</p>
    )
  );
}

const MenuItem: React.FC<{ collapsed: boolean, icon: { src: string }; iconActive?: { src: string }; label: string, route?: string, onClick?: any, className?: any }> = ({ collapsed, icon, iconActive, label, route, onClick, className }) => {
  const router = useRouter();
  const pathname = usePathname();

  const onClickHandler = () => {
    if (route) router.push(route);
    if (onClick) onClick();
  }

  const isActive = pathname === route;
  const iconSrc = isActive && iconActive ? iconActive.src : icon.src;

  return (
    <div 
      className={`${className} ${collapsed ? "mt-3 px-1.5 py-1.5 " : "mt-1 flex flex-row px-3 py-2.5"} rounded-lg cursor-pointer hover:bg-slate-100 ${isActive ? 'bg-gradient-to-r from-slate-100 to-slate-200' : ''}`}
      onClick={onClickHandler}
      >
      <img src={iconSrc} className="w-5 h-5 mr-2.5" />
      {!collapsed ? (<p className="text-sm">{label}</p>) : null}
    </div>
  );
}

const Profile: React.FC<{user: any}> = ({ user }) => {
  const [showMenu, setShowMenu] = useState(false);

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
        className="absolute top-5 right-5 rounded-full flex duration-150 hover:bg-slate-200 cursor-pointer"
        onClick={() => setShowMenu(!showMenu)}
      >
        <span className="leading-10 mx-4 text-sm">{user.given_name ? user.given_name : user.nickname}</span>
        <div className="h-10 w-10 relative">
          <Image fill={true} src={user.picture} alt="" className="border-2 border-slate-400 bg-slate-400 rounded-full" />
        </div>
      </button>
      {showMenu && (
        <div id="profile-menu" className="left-5 sm:left-auto absolute top-20 right-5 px-8 py-7 bg-white rounded-3xl flex flex-col drop-shadow-sm">
          {
            user.name !== user.email ? ( <span className="font-bold text-sm">{user.name}</span> ) : null
          }
          <span className="mb-4 text-sm">{user.email}</span>
          <a href="/api/auth/logout" className="pt-4 border-t-[1px] border-slate-200 text-sm hover:text-slate-600 duration-150">Logout</a>
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
  const [menuOpen, setMenuOpen] = useState(false);

  const toggleMenu = () => setMenuOpen(!menuOpen);
  const handleMobileClick = () => window.innerWidth < 640 && toggleMenu();

  return (
    <>
      <div className="sm:flex sm:flex-row h-screen w-screen absolute">
        <div className={`${menuOpen ? "w-full sm:w-64" : "hidden sm:block sm:w-16"} absolute sm:relative border-r-0 border-slate-300 z-20 h-full bg-white overflow-y-auto`}>
          <div className={`${menuOpen ? "p-5 w-full" : "p-4"}  flex flex-col h-full`}>
            <div className="flex">
              {menuOpen ? (
                <h1 className="text-lg font-bold grow leading-9">Compass 🧭</h1>
              ) : (
                <h1 className="text-lg px-1.5 py-1.5">🧭</h1>
              )}
              <button className="p-2 bg-slate-100 hover:bg-slate-200 duration-150 rounded-md sm:hidden" onClick={() => setMenuOpen(!menuOpen)}>
                <img src="/icons/close.svg" className="w-5 h-5" />
              </button>
            </div>
            <SubTitle collapsed={!menuOpen} label="Allgemein" />
            <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={HomeIcon} iconActive={HomeIconFilled} label="Home" route="/home" />
            <SubTitle collapsed={!menuOpen} label="Erfassen" withLine={true} />
            <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={TimeIcon} iconActive={TimeIconFilled} label="Zeit" route="/times" />
            <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={MoodIcon} iconActive={MoodIconFilled} label="Stimmung" route="/moods" />
            <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={IncidentIcon} iconActive={IncidentIconFilled} label="Vorfall" route="/incidents" />
            <SubTitle collapsed={!menuOpen} label="Verwalten" withLine={true} />
            <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={UserIcon} iconActive={UserIconFilled} label="Benutzer" route="/users" />
            <div className="grow"></div>
            {
              menuOpen ? (
                <MenuItem className="hidden sm:flex" collapsed={!menuOpen} icon={CollapseMenuIcon} label="Zuklappen" onClick={toggleMenu} />
              ) : (
                <MenuItem className="hidden sm:flex" collapsed={true} icon={ExpandMenuIcon} label="Expandieren" onClick={toggleMenu} />
              )
            }
          </div>
        </div>
        <div className="sm:relative grow z-10 pt-20 sm:pt-0 bg-slate-100 h-full">
          {children}
          {
            user ? (
              <Profile user={user} />
            ) : null
          }
        </div>
        <button className="absolute left-5 top-5 block sm:hidden p-2 bg-white hover:bg-slate-100 duration-150 rounded-md" onClick={toggleMenu}>
          <img src="/icons/menu.svg" className="w-5 h-5" />
        </button>
      </div>
    </>
  );
}
