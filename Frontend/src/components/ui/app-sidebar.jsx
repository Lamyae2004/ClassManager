"use client";

import * as React from "react";
import {
  AudioWaveform,
  BookOpen,
  Bot,
  Command,
  Calendar,
  GalleryVerticalEnd,
  Map,
  ClipboardCheck,
  Settings2,
  SquareTerminal,
  User,
} from "lucide-react";

import { NavMain } from "@/components/ui/nav-main";
import { NavProjects } from "@/components/ui/nav-projects";
import { NavUser } from "@/components/ui/nav-user";
import { TeamSwitcher } from "@/components/ui/team-switcher";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from "@/components/ui/sidebar";
import { AuthContext } from "@/context/AuthContext";

// Sample data
const data = {
  user: {
    name: "shadcn",
    email: "m@example.com",
    avatar: "/avatars/shadcn.jpg",
  },
  teams: [
    { name: "Class Manager", logo: GalleryVerticalEnd, plan: "" },

  ],
  projects: [

    { name: "Gestion des emplois", url: "#", icon: Calendar },
    { name: "Gestion des absences", url: "#", icon: ClipboardCheck },
    { name: "Gestion des annonces", url: "#", icon: Map },
    { name: "Gestion des users", url: "#", icon: User},
     { name: "Gestion des responsables", url: "#", icon: User},
  ],
};


export function AppSidebar(props) {
   const { user } = React.useContext(AuthContext);
   const filtredProjects = data.projects.filter((project)=>{
    if (project.name === "Gestion des users" && user?.role !== "ADMIN") {
      return false;
    }
    return true;
   })
  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <TeamSwitcher teams={data.teams} />
      </SidebarHeader>
      <SidebarContent>
        <NavProjects projects={filtredProjects} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser  />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  );
}