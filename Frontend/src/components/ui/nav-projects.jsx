"use client";

import {
  Folder,
  Forward,
  MoreHorizontal,
  Trash2,
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

  return (
    <SidebarGroup className="group-data-[collapsible=icon]:hidden">
      <SidebarGroupLabel>Projects</SidebarGroupLabel>
      <SidebarMenu>
        
        {projects.map((item) => (
          
          <SidebarMenuItem key={item.name}>
            <SidebarMenuButton asChild>
              <a href={item.url}>
                <item.icon />
                <span>{item.name}</span>
              </a>
            </SidebarMenuButton>
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
                {item.name === "Gestion des users" && (
                  <>
                  <DropdownMenuItem>
                  <Folder className="text-muted-foreground" />
                  <Link to="/add-Students">Ajouter les étudiants</Link>
                </DropdownMenuItem>

                <DropdownMenuItem>
                  <Folder className="text-muted-foreground" />
                  <Link to="/add-Teachers">Ajouter les professeurs</Link>              
                </DropdownMenuItem>
                </>)
                }

                {item.name === "Gestion des emplois" && (<>
                  <DropdownMenuItem>
                  <Folder className="text-muted-foreground" />
                  <Link to="/upload">Créer un emploi</Link>
                </DropdownMenuItem>

                <DropdownMenuItem>
                  <Forward className="text-muted-foreground" />
                  <Link to="/timetable">Consulter un emploi</Link>              
                </DropdownMenuItem>
                </>)}
              
               
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