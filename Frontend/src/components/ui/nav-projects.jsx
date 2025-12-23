"use client";

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

  
  const projectMenus = {
    "Gestion des emplois": [
      { icon: Folder, label: "Créer un emploi", link: "/upload" },
      { icon: Forward, label: "Consulter un emploi", link: "/timetable" },
      { separator: true },
      { icon: Trash2, label: "Extraire un emploi", link: "/extract" },
    ],
    "Gestion des absences": [
      { icon: UserCheck, label: "Enregistrer l'absence", link: "/absences/create" },
      { icon: ClipboardList, label: "Consulter les absences", link: "/absences/consulter" },
      { separator: true },
     
    ],
    "Gestion des annonces": [
      { icon: Folder, label: "Créer une annonce", link: "/annonces/creer" },
      { icon: Forward, label: "Consulter les annonces", link: "/annonces/consulter" },
    ],
      "Gestion des users": [
      { icon: Folder, label: "Ajouter les étudiants", link: "/add-Students" },
      { icon: Folder, label: "Ajouter les professeurs", link: "/add-Teachers" },
    ],

  };

  return (
    <SidebarGroup className="group-data-[collapsible=icon]:hidden">
      <SidebarMenu>
        
        {projects.map((item) => (
          
          <SidebarMenuItem key={item.name}>
            <SidebarMenuButton asChild>
              <a href={item.url}>
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
                className="w-48 rounded-lg"
                side={isMobile ? "bottom" : "right"}
                align={isMobile ? "end" : "start"}
              >

                {projectMenus[item.name]?.map((menuItem, index) =>
                  menuItem.separator ? (
                    <DropdownMenuSeparator key={index} />
                  ) : (
                    <DropdownMenuItem key={index}>
                      <menuItem.icon className="text-muted-foreground" />
                      <Link to={menuItem.link}>{menuItem.label}</Link>
                    </DropdownMenuItem>
                  )
                )}
              </DropdownMenuContent>
            </DropdownMenu>
          </SidebarMenuItem>
        ))}

        <SidebarMenuItem>
          <SidebarMenuButton className="text-sidebar-foreground/70">
            <MoreHorizontal className="text-sidebar-foreground/70" />
            <span>More</span>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarGroup>
  );
}