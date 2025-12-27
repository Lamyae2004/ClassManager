"use client";

import * as React from "react";
import { ChevronsUpDown, Plus } from "lucide-react";

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuShortcut,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar";

export function TeamSwitcher({ teams }) {
  const { isMobile } = useSidebar();
  const [activeTeam, setActiveTeam] = React.useState(teams[0]);

  if (!activeTeam) {
    return null;
  }

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
           <SidebarMenuButton
  size="lg"
  className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground hover:bg-sidebar-accent/10 transition-all duration-200 group rounded-lg"
>
  <div className="relative flex aspect-square size-8 items-center justify-center rounded-lg bg-gradient-to-br from-white to-gray-50 border border-gray-200 shadow-xs group-hover:shadow-sm group-hover:border-[#1370fc]/20 transition-all duration-200">
    <activeTeam.logo 
      className="w-5 h-5 text-[#1370fc] group-hover:text-[#1370fc] transition-transform duration-200 group-hover:scale-110" 
    />
  </div>
  
  <div className="grid flex-1 text-left text-sm leading-tight ml-3 space-y-0.5">
    <div className="flex items-center gap-1">
       <span className="truncate font-semibold text-foreground group-hover:text-[#1370fc] transition-colors duration-200">  
         {activeTeam.name}
      </span>
      {activeTeam.badge && (
        <span className="text-[10px] font-medium px-1.5 py-0.5 rounded-full bg-[#1370fc]/10 text-[#1370fc]">
          {activeTeam.badge}
        </span>
      )}
    </div>
    <span className="truncate text-xs text-gray-500">
      {activeTeam.plan}
    </span>
  </div>
  
  <ChevronsUpDown className="ml-auto text-gray-400 group-hover:text-gray-600 group-hover:rotate-180 transition-all duration-300 size-4" />
</SidebarMenuButton>
          </DropdownMenuTrigger>

          <DropdownMenuContent
            className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
            align="start"
            side={isMobile ? "bottom" : "right"}
            sideOffset={4}
          >
            <DropdownMenuLabel className="text-muted-foreground text-xs">
              Teams
            </DropdownMenuLabel>

            {teams.map((team, index) => (
              <DropdownMenuItem
                key={team.name}
                onClick={() => setActiveTeam(team)}
                className="gap-2 p-2"
              >
                <div className="flex size-6 items-center justify-center rounded-md border">
                  <team.logo className="size-3.5 shrink-0" />
                </div>
                {team.name}
                <DropdownMenuShortcut>⌘{index + 1}</DropdownMenuShortcut>
              </DropdownMenuItem>
            ))}

            <DropdownMenuSeparator />

            <DropdownMenuItem className="gap-2 p-2">
              <div className="flex size-6 items-center justify-center rounded-md border bg-transparent">
                <Plus className="size-4" />
              </div>
              <div className="text-muted-foreground font-medium">Add team</div>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </SidebarMenuItem>
    </SidebarMenu>
  );
}
