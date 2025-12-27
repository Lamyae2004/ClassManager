import React from "react"
import { AppSidebar } from "@/components/ui/app-sidebar"
import { Outlet, useLocation } from "react-router-dom";
import { useState ,useEffect } from "react";
import axios from "axios";
import { AuthContext } from "@/context/AuthContext";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar"
import { useNavigate } from "react-router-dom";
import { useContext } from "react";
export default function Page() {
  const { user, loading } = useContext(AuthContext);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
  if (!loading && !user) {
     navigate("/login");
    }
  }, [loading, user, navigate]);
  if (loading) return <p>Loading...</p>;
  if (!user) return null;
  

  const getBreadcrumbs = () => {
    const pathname = location.pathname;
    const items = [{ label: "Dashboard", href: "/" }];

    if (pathname === "/absences/create") {
      items.push({ label: "Enregistrer Absence" });
    } else if (pathname === "/absences/consulter") {
      items.push({ label: "Consulter les absences" });
    } else if (pathname === "/upload") {
      items.push({ label: "Créer un emploi du temps" });
    } else if (pathname === "/timetable") {
      items.push({ label: "Consulter les emplois du temps" });
    }

    return items;
  };
  const breadcrumbs = getBreadcrumbs();
   return (
    <SidebarProvider>
      <AppSidebar />
       <SidebarInset className="min-h-screen bg-gradient-to-br from-gray-50 to-blue-50">
        <header className="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-data-[collapsible=icon]/sidebar-wrapper:h-12">
          <div className="flex items-center gap-2 px-4">
            <SidebarTrigger className="-ml-1" />
            <Separator orientation="vertical" className="mr-2 data-[orientation=vertical]:h-4" />
            <Breadcrumb>
              <BreadcrumbList>
                {breadcrumbs.map((bc, idx) => (
                  <React.Fragment key={idx}>
                    <BreadcrumbItem className="hidden md:block">
                      {bc.href ? (
                        <BreadcrumbLink href={bc.href}>{bc.label}</BreadcrumbLink>
                      ) : (
                        <BreadcrumbPage>{bc.label}</BreadcrumbPage>
                      )}
                    </BreadcrumbItem>
                    {idx < breadcrumbs.length - 1 && <BreadcrumbSeparator className="hidden md:block" />}
                  </React.Fragment>
                ))}
              </BreadcrumbList>
            </Breadcrumb>
          </div>
        </header>

        <div className="flex flex-1 flex-col gap-4">
          <Outlet />
        </div>
      </SidebarInset>
    </SidebarProvider>
  );
}