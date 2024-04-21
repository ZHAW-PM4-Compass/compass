"use client";

import { useUser } from "@auth0/nextjs-auth0/client";
import Image from "next/image";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";

import HomeIcon from "@fluentui/svg-icons/icons/home_24_regular.svg";
import HomeIconFilled from "@fluentui/svg-icons/icons/home_24_filled.svg";

// participant
import WorkingHoursIcon from "@fluentui/svg-icons/icons/shifts_24_regular.svg";
import WorkingHoursIconFilled from "@fluentui/svg-icons/icons/shifts_24_filled.svg";
import MoodIcon from "@fluentui/svg-icons/icons/person_voice_24_regular.svg";
import MoodIconFilled from "@fluentui/svg-icons/icons/person_voice_24_filled.svg";
import IncidentIcon from "@fluentui/svg-icons/icons/alert_24_regular.svg";
import IncidentIconFilled from "@fluentui/svg-icons/icons/alert_24_filled.svg";

// social worker
import WorkingHoursCheckIcon from "@fluentui/svg-icons/icons/shifts_checkmark_24_regular.svg";
import WorkingHoursCheckIconFilled from "@fluentui/svg-icons/icons/shifts_checkmark_24_filled.svg";
import OverviewIcon from "@fluentui/svg-icons/icons/arrow_trending_lines_24_regular.svg";
import OverviewIconFilled from "@fluentui/svg-icons/icons/arrow_trending_lines_24_filled.svg";

// admin
import UserIcon from "@fluentui/svg-icons/icons/people_24_regular.svg";
import UserIconFilled from "@fluentui/svg-icons/icons/people_24_filled.svg";

import ExpandMenuIcon from "@fluentui/svg-icons/icons/chevron_right_24_regular.svg";
import CollapseMenuIcon from "@fluentui/svg-icons/icons/chevron_left_24_regular.svg";
import MenuIcon from "@fluentui/svg-icons/icons/list_24_regular.svg";
import MenuCloseIcon from "@fluentui/svg-icons/icons/dismiss_24_regular.svg";
import Roles from "@/constants/roles";

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
        <span className="leading-10 ml-4 mr-3 text-sm">{user.given_name ? user.given_name : user.nickname}</span>
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
        <div className={`${menuOpen ? "w-full sm:w-64" : "hidden sm:block sm:w-16"} absolute sm:relative border-r-[1px] border-slate-300 z-20 h-full bg-white overflow-y-scroll`}>
          <div className={`${menuOpen ? "p-5 w-full" : "p-4"}  flex flex-col h-full`}>
            <div className="flex">
              {menuOpen ? (
                <h1 className="text-lg font-bold grow leading-9">Compass 🧭</h1>
              ) : (
                <h1 className="text-lg px-1.5 py-1.5">🧭</h1>
              )}
              <button className="p-2 bg-white hover:bg-slate-100 duration-150 rounded-md sm:hidden" onClick={() => setMenuOpen(!menuOpen)}>
                <img src={MenuCloseIcon.src} className="w-5 h-5" />
              </button>
            </div>
            <SubTitle collapsed={!menuOpen} label="Allgemein" />
            <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={HomeIcon} iconActive={HomeIconFilled} label="Home" route="/home" />
            <SubTitle collapsed={!menuOpen} label="Teilnehmer" withLine={true} />
            <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={WorkingHoursIcon} iconActive={WorkingHoursIconFilled} label="Arbeitszeit" route="/working-hours" />
            <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={MoodIcon} iconActive={MoodIconFilled} label="Stimmung" route="/moods" />
            <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={IncidentIcon} iconActive={IncidentIconFilled} label="Vorfall" route="/incidents" />

            { user && ((user["compass/roles"] as Array<string>).includes(Roles.SOCIAL_WORKER) || (user["compass/roles"] as Array<string>).includes(Roles.ADMIN)) && (
              <>
                <SubTitle collapsed={!menuOpen} label="Sozialarbeiter" withLine={true} />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={WorkingHoursCheckIcon} iconActive={WorkingHoursCheckIconFilled} label="Arbeitszeit" route="/working-hours-check" />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={OverviewIcon} iconActive={OverviewIconFilled} label="Übersicht" route="/overview" />
              </>
            )}

            { user && (user["compass/roles"] as Array<string>).includes(Roles.ADMIN) && (
              <>
                <SubTitle collapsed={!menuOpen} label="Admin" withLine={true} />
                <MenuItem onClick={handleMobileClick} collapsed={!menuOpen} icon={UserIcon} iconActive={UserIconFilled} label="Benutzer" route="/users" />
              </>
            )}
            
            <div className="grow"></div>
            { menuOpen ? (
              <MenuItem className="hidden sm:flex" collapsed={!menuOpen} icon={CollapseMenuIcon} label="Zuklappen" onClick={toggleMenu} />
            ) : (
              <MenuItem className="hidden sm:flex mb-2" collapsed={true} icon={ExpandMenuIcon} label="Expandieren" onClick={toggleMenu} />
            )}
          </div>
        </div>
        <div className="sm:relative grow z-10 pt-20 md:pt-0 bg-slate-100 h-full">
          <div className="w-full h-full md:container md:mx-auto px-5 md:px-24 lg:px-48 md:pt-24 overflow-y-scroll">
          {children}
          {
            user && (
              <Profile user={user} />
            )
          }
          </div>
        </div>
        <button className="absolute left-5 top-5 block sm:hidden p-2 hover:bg-slate-200 duration-150 rounded-md" onClick={toggleMenu}>
          <img src={MenuIcon.src} className="w-5 h-5" />
        </button>
      </div>
    </>
  );
}
