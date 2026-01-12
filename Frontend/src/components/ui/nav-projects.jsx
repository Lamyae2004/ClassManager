"use client";
import React, { useContext } from "react";
import { AuthContext } from "@/context/AuthContext";
import {
  Folder,
  Forward,
  MoreHorizontal,
  Trash2,
  UserCheck,
  ClipboardList,
} from "lucide-react";

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuAction,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar";
import { Link } from "react-router-dom";

export function NavProjects({ projects }) {
  const { isMobile } = useSidebar();
  const { user, loading } = useContext(AuthContext);

  if (loading || !user) return null;


  const role = user.role;


  const projectMenus = {
    "Gestion des emplois": [
      ...(user.role !== "TEACHER"
        ? [{ icon: Folder, label: "Créer un emploi", link: "/upload" }]
        : []),
      {
        icon: Forward, label: "Consulter un emploi",
        // Redirection selon le rôle
        link: role === "ADMIN" ? "/timetable" : "/timetable/prof"
      },
     
    ],
    "Gestion des absences": [
      ...(user.role !== "ADMIN"
        ? [{ icon: UserCheck, label: "Enregistrer l'absence", link: "/absences/create" }]
        : []),
      { icon: ClipboardList, label: "Consulter les absences", link: "/absences/consulter" },
      

    ],
    "Gestion des annonces": [
      { icon: Folder, label: "Créer une annonce", link: "/annonces/creer" },
      { icon: Forward, label: "Consulter les annonces", link: "/annonces/consulter" },
    ],
    "Gestion des users": [
      { icon: Folder, label: "Ajouter les étudiants", link: "/add-Students" },
      { icon: Folder, label: "Ajouter les professeurs", link: "/add-Teachers" },
    ],
     "Gestion des responsables": [
      { icon: Folder, label: "Assign respo", link: "/assign-reponsible" },
      { icon: Folder, label: "Historiques", link: "/responsablesHistory" },
    ],

  };

  return (
    <SidebarGroup className="group-data-[collapsible=icon]:hidden">
      <SidebarMenu>

        {projects.map((item) => (

          <SidebarMenuItem key={item.name}>
            <SidebarMenuButton asChild>
              <a href={item.url} className="
    flex items-center gap-2 px-3 py-2 rounded-md
    text-sidebar-textp
    hover:bg-sidebar-accent
    hover:text-sidebar-accent-foreground
    transition-colors
  "
              >
                <item.icon />
                <span>{item.name}</span>
              </a>
            </SidebarMenuButton>

            {/* Dropdown menu spécifique au projet */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <SidebarMenuAction showOnHover>
                  <MoreHorizontal />
                  <span className="sr-only">More</span>
                </SidebarMenuAction>
              </DropdownMenuTrigger>

              <DropdownMenuContent
               className="w-48 rounded-lg text-blue-700"
                side={isMobile ? "bottom" : "right"}
                align={isMobile ? "end" : "start"}
              >

                {projectMenus[item.name]?.map((menuItem, index) =>
                  menuItem.separator ? (
                    <DropdownMenuSeparator key={index} />
                  ) : (
                    <DropdownMenuItem key={index}>
                      <menuItem.icon className="text-muted-textp  " />
                      <Link to={menuItem.link}>{menuItem.label}</Link>
                    </DropdownMenuItem>
                  )
                )}
              </DropdownMenuContent>
            </DropdownMenu>
          </SidebarMenuItem>
        ))}


      </SidebarMenu>
    </SidebarGroup>
  );
}