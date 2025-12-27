"use client";
import React, { useEffect, useState, useContext } from "react";
import { AuthContext } from "@/context/AuthContext";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Pencil, CalendarDays, Layers, Target, BookOpen, GraduationCap, User, Building2 } from "lucide-react";
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from "@/components/ui/table";

// ------------------ Composant cellule ------------------
const TimetableCellView = ({ cellData, isEmpty, onEdit }) => {
    const cellHeight = "165px";
    if (isEmpty || (!cellData.cours && !cellData.professeur && !cellData.salle)) {
        return (
            <div
                className="relative group p-3 flex items-center justify-center border border-dashed border-muted-foreground/30 rounded-lg bg-muted/20 hover:bg-muted/30 transition-colors"
                style={{ height: cellHeight }}
            >
                <Button
                    variant="ghost"
                    size="icon"
                    className="absolute top-2 right-2 h-7 w-7 opacity-0 group-hover:opacity-100 transition-opacity"
                    onClick={onEdit}
                >
                    <Target className="h-3.5 w-3.5" />
                </Button>
                <span className="text-muted-foreground italic text-sm">Pas de cours</span>
            </div>
        );
    }

    return (
        <div className="relative group min-h-[100px] p-3 border rounded-lg bg-gradient-to-br from-background to-muted/30 hover:shadow-md transition-all">
            <Button
                variant="ghost"
                size="icon"
                className="absolute top-2 right-2 h-7 w-7 opacity-0 group-hover:opacity-100 transition-opacity"
                onClick={onEdit}
            >
                <Target className="h-3.5 w-3.5" />
            </Button>

            <div className="space-y-2 pr-8">
                {cellData.type && (
                    <div className="flex items-center gap-2">
                        <BookOpen className="h-3.5 w-3.5 text-purple-600 dark:text-purple-400 shrink-0" />
                        <Badge variant="secondary" className="text-xs">
                            {cellData.type}
                        </Badge>
                    </div>
                )}

                {cellData.cours && (
                    <div className="flex items-start gap-2">
                        <GraduationCap className="h-4 w-4 text-primary mt-0.5 shrink-0" />
                        <span className="font-semibold text-sm leading-tight text-foreground">{cellData.cours}</span>
                    </div>
                )}

                {cellData.classe && cellData.filiere && (
                    <div className="flex items-center gap-2">
                        <Layers className="h-4 w-4 text-yellow-600 mt-0.5 shrink-0" />
                        <span className="text-xs text-muted-foreground">
                            {cellData.classe} - {cellData.filiere}
                        </span>
                    </div>
                )}

                {cellData.salle && (
                    <div className="flex items-center gap-2">
                        <Building2 className="h-3.5 w-3.5 text-green-600 dark:text-green-400 shrink-0" />
                        <Badge variant="outline" className="text-xs">{cellData.salle}</Badge>
                    </div>
                )}
            </div>
        </div>
    );
};

// ------------------ Page Prof ------------------
const TIME_SLOTS = ["8:30h-10:30h", "10:45h-12:45h", "14:00h-16:00h", "16:15h-18:15h"];

const ProfTimetablePage = () => {
    const { user } = useContext(AuthContext);
    const [timetable, setTimetable] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchProfTimetable = async () => {
            setLoading(true);
            try {
                const res = await fetch(`http://localhost:8080/emploi/prof/${user.id}`);
                console.log(res);
                if (!res.ok) throw new Error("Impossible de charger l'emploi du temps");
                const data = await res.json();
                const parsed = transformBackendTimetable(data);
                setTimetable(parsed);
            } catch (err) {
                console.error(err);
                setTimetable([]);
            } finally {
                setLoading(false);
            }
        };

        if (user) fetchProfTimetable();
    }, [user]);

    const handleEditCell = (rowIndex, slotKey) => {
        console.log("Edit cell", rowIndex, slotKey);
    };

    return (
        <div className="p-4">
            <div className="mb-8">
                <div className="flex flex-col items-center gap-3">
                    <div className="flex items-center gap-3">
                     <div className="relative">
  <div className="p-3 rounded-full bg-gradient-to-br from-primary/10 to-primary/5 border border-primary/20">
    <CalendarDays className="h-5 w-5 text-primary" />
  </div>
  <div className="absolute -inset-1 bg-primary/10 rounded-full blur-sm -z-10"></div>
</div>
                        <h1 className="text-2xl font-bold tracking-tight text-foreground">
                            Emploi du temps
                        </h1>
                    </div>

                    <div className="flex items-center gap-2 px-5 py-2 rounded-full bg-gradient-to-r from-muted/20 to-muted/10 border">
                        <User className="h-4 w-4 text-primary" />
                        <span className="font-medium text-foreground">
                            {user.firstname} {user.lastname}
                        </span>
                        <Badge variant="secondary" className="ml-2">
                            Professeur
                        </Badge>
                    </div>
                </div>
            </div>

            {loading ? (
                <p className="text-center">Chargement...</p>
            ) : (
                <div className="rounded-md border overflow-x-auto">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead className="font-semibold w-32">Jour</TableHead>
                                {TIME_SLOTS.map((slot, i) => (
                                    <TableHead key={i} className="text-center font-semibold w-48">{slot}</TableHead>
                                ))}
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {timetable.map((row, i) => (
                                <TableRow key={i}>
                                    <TableCell className="font-medium align-top pt-4">
                                        <div className="font-semibold text-base">{row.jour}</div>
                                    </TableCell>
                                    {TIME_SLOTS.map((_, idx) => {
                                        const slotKey = `slot${idx + 1}`;
                                        return (
                                            <TableCell key={idx} className="align-top">
                                                <TimetableCellView
                                                    cellData={row[slotKey]}
                                                    isEmpty={!row[slotKey] || (!row[slotKey].cours && !row[slotKey].professeur && !row[slotKey].salle)}
                                                    onEdit={() => handleEditCell(i, slotKey)}
                                                />
                                            </TableCell>
                                        );
                                    })}
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </div>
            )}
        </div>
    );
};

export default ProfTimetablePage;

// ------------------ Transformation backend -> frontend ------------------
function transformBackendTimetable(data) {
    const jours = ["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"];

    return jours.map((jour) => {
        const daySlots = { jour };
        TIME_SLOTS.forEach((slot, idx) => {
            const [slotStart, slotEnd] = slot.split("-").map(s => parseInt(s.split(":")[0].trim()));
            const slotData = data.find(d => {
                if (d.jour.toLowerCase() !== jour.toLowerCase()) return false;
                const cStart = parseInt(d.creneauDebut.split(":")[0]);
                return cStart >= slotStart && cStart < slotEnd;
            });

            if (slotData) {
                daySlots[`slot${idx + 1}`] = {
                    type: "Cours",
                    cours: slotData.matiereNom,
                    classe: slotData.classeNom,
                    filiere: slotData.filiere,
                    salle: slotData.salleNom,
                    professeur: "",
                };
            } else {
                daySlots[`slot${idx + 1}`] = {};
            }
        });

        return daySlots;
    });
}
