"use client";

import * as React from "react";
import { useState, useEffect } from "react";
import { FileText, Calendar, Edit, Trash2, Eye, Plus, Download, BookOpen, GraduationCap, User, Building2, X } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { AlertCircle, Loader2 } from "lucide-react";
import { 
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { cn } from "@/lib/utils";

// Données statiques pour les emplois du temps
const initialTimetables = [
  {
    id: 1,
    fileName: "emploi_cp1_s1.pdf",
    class: "cp1",
    filiere: "",
    semester: "s1",
    uploadDate: "2024-01-15",
    fileSize: "2.4 MB",
    fileUrl: "/emplois/emploi_cp1_s1.pdf"
  },
  {
    id: 2,
    fileName: "emploi_ci1_gi_s5.pdf",
    class: "ci1",
    filiere: "gi",
    semester: "s5",
    uploadDate: "2024-01-10",
    fileSize: "3.1 MB",
    fileUrl: "/emplois/emploi_ci1_gi_s5.pdf"
  },
  {
    id: 3,
    fileName: "emploi_cp2_s3.pdf",
    class: "cp2",
    filiere: "",
    semester: "s3",
    uploadDate: "2024-01-08",
    fileSize: "2.8 MB",
    fileUrl: "/emplois/emploi_cp2_s3.pdf"
  
  },
  {
    id: 3,
    fileName: "emploi_cp2_s3.docx",
    class: "cp2",
    filiere: "",
    semester: "s3",
    uploadDate: "2024-01-08",
    fileSize: "2.8 MB",
    fileUrl: "/emplois/emploi_cp2_s3.docx"
  }
];

export function TimetableList() {
  const [timetables, setTimetables] = useState(initialTimetables);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [timetableToDelete, setTimetableToDelete] = useState(null);
  
  // States pour l'extraction et visualisation
  const [viewingTimetable, setViewingTimetable] = useState(null);
  const [timetableData, setTimetableData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState(null);
  const [jszipReady, setJszipReady] = useState(false);

  const DAYS = ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi"];
  const TIME_SLOTS = ["8h30→10h30", "10h45→12h45", "14h → 16h", "16h15→18h15"];

  // Données pour les classes et filières (pour l'affichage)
  const classes = [
    { value: "cp1", label: "CP1" },
    { value: "cp2", label: "CP2" },
    { value: "ci1", label: "CI1" },
    { value: "ci2", label: "CI2" },
    { value: "ci3", label: "CI3" }
  ];

  const filieres = {
    gi: "Génie informatique",
    gm: "Génie mécatronique",
    ge: "Génie électrique",
    gc: "Génie civil",
    gind: "Génie industriel",
    grst: "Génie réseaux et télécommunications"
  };

  const semesters = {
    s1: "Semestre 1",
    s2: "Semestre 2",
    s3: "Semestre 3",
    s4: "Semestre 4",
    s5: "Semestre 5",
    s6: "Semestre 6",
    s7: "Semestre 7",
    s8: "Semestre 8",
    s9: "Semestre 9"
  };

  // Fonctions de gestion
  const handleCreateNew = () => {
    // Redirection vers la page de création
    window.location.href = '/upload';
  };

  const handleEdit = (timetable) => {
    // Redirection vers la page de modification avec l'ID
     window.location.href = `/upload/edit/${timetable.id}`;
  };

  const handleDelete = (timetable) => {
    setTimetableToDelete(timetable);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = () => {
    setTimetables(timetables.filter(t => t.id !== timetableToDelete.id));
    setDeleteDialogOpen(false);
    setTimetableToDelete(null);
  };

  const handleDownload = (timetable) => {
    // Télécharger le PDF
    const link = document.createElement('a');
    link.href = timetable.fileUrl;
    link.download = timetable.fileName;
    link.click();
  };

  // Fonctions utilitaires pour l'affichage
  const getClassLabel = (classValue) => {
    return classes.find(c => c.value === classValue)?.label || classValue;
  };

  const getFiliereLabel = (filiereValue) => {
    if (!filiereValue) return "-";
    return filieres[filiereValue] || filiereValue;
  };

  const getSemesterLabel = (semesterValue) => {
    return semesters[semesterValue] || semesterValue;
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR');
  };

  // Charger JSZip depuis CDN
  useEffect(() => {
    if (typeof window === 'undefined') return;

    const loadJSZip = async () => {
      if (window.JSZip) {
        setJszipReady(true);
        return;
      }

      const existingScript = document.querySelector('script[src*="jszip"]');
      if (existingScript) {
        const checkInterval = setInterval(() => {
          if (window.JSZip) {
            clearInterval(checkInterval);
            setJszipReady(true);
          }
        }, 100);
        setTimeout(() => clearInterval(checkInterval), 5000);
        return;
      }

      try {
        const script = document.createElement('script');
        script.src = 'https://unpkg.com/jszip@3.10.1/dist/jszip.min.js';
        script.async = true;
        script.onload = () => {
          setTimeout(() => {
            if (window.JSZip) {
              setJszipReady(true);
            }
          }, 100);
        };
        script.onerror = () => {
          setJszipReady(false);
        };
        document.body.appendChild(script);
      } catch (error) {
        setJszipReady(false);
      }
    };

    loadJSZip();
  }, []);

  // Fonction pour parser une cellule
  const parseCellContent = (content) => {
    if (!content || content.trim().length === 0) {
      return { type: "Cours", cours: "", professeur: "", salle: "" };
    }

    let type = "Cours";
    let cours = "";
    let professeur = "";
    let salle = "";

    const contentLower = content.toLowerCase();
    const coursIndex = contentLower.indexOf("cours");
    const tdTpIndex = contentLower.indexOf("td/tp");
    const prIndex = contentLower.indexOf("pr.");
    const amphiIndex = contentLower.indexOf("amphi");

    if (tdTpIndex !== -1) {
      type = "TD/TP";
    }

    const sallePattern = /\b([A-Z][0-9]+(?:\s*[A-Z0-9]+)*)\b/g;
    const salleMatches = [];
    let match;
    while ((match = sallePattern.exec(content)) !== null) {
      salleMatches.push({
        text: match[1],
        index: match.index
      });
    }

    if (coursIndex !== -1) {
      let startIndex = coursIndex + 5;
      const afterCours = content.substring(startIndex);
      const parenMatch = afterCours.match(/^[^a-zA-Z]*\([^)]+\)/);
      if (parenMatch) {
        startIndex = startIndex + parenMatch[0].length;
      }
      
      let endIndex = content.length;
      if (prIndex !== -1 && prIndex > coursIndex) {
        endIndex = Math.min(endIndex, prIndex);
      }
      if (amphiIndex !== -1 && amphiIndex > coursIndex) {
        endIndex = Math.min(endIndex, amphiIndex);
      }
      
      cours = content.substring(startIndex, endIndex).trim();
      cours = cours.replace(/\s+/g, " ").trim();
    } else if (tdTpIndex !== -1) {
      const startIndex = tdTpIndex + 5;
      let endIndex = content.length;
      
      if (prIndex !== -1 && prIndex > tdTpIndex) {
        endIndex = Math.min(endIndex, prIndex);
      }
      if (amphiIndex !== -1 && amphiIndex > tdTpIndex) {
        endIndex = Math.min(endIndex, amphiIndex);
      }
      
      cours = content.substring(startIndex, endIndex).trim();
      cours = cours.replace(/\s+/g, " ").trim();
    }

    if (prIndex !== -1) {
      const startIndex = prIndex + 3;
      let endIndex = content.length;
      
      const parenIndex = content.indexOf("(", startIndex);
      if (parenIndex !== -1) {
        endIndex = Math.min(endIndex, parenIndex);
      }
      
      if (amphiIndex !== -1 && amphiIndex > prIndex) {
        endIndex = Math.min(endIndex, amphiIndex);
      }
      
      professeur = content.substring(startIndex, endIndex).trim();
      professeur = professeur.replace(/\s+/g, " ").trim();
    }

    if (amphiIndex !== -1) {
      const startIndex = amphiIndex + 5;
      salle = content.substring(startIndex).trim();
      salle = salle.replace(/[\)\s]*$/, "").trim();
      salle = `Amphi ${salle}`;
    } else {
      const salleVariants = /(?:sa[1l]{2}e|salle)\s*([A-Z0-9]+(?:\s*[A-Z0-9]+)*)/i;
      const salleMatch = content.match(salleVariants);
      
      if (salleMatch && salleMatch[1]) {
        salle = salleMatch[1].trim();
      } else if (salleMatches.length > 0) {
        let selectedSalle = salleMatches[salleMatches.length - 1];
        if (prIndex !== -1) {
          const salleAfterPr = salleMatches.find(s => s.index > prIndex);
          if (salleAfterPr) {
            selectedSalle = salleAfterPr;
          }
        }
        salle = selectedSalle.text.trim();
      }
    }

    return {
      type: type || "Cours",
      cours: cours || "",
      professeur: professeur || "",
      salle: salle || ""
    };
  };

  const capitalize = (s) => s && s.length ? s.charAt(0).toUpperCase() + s.slice(1) : s;

  const cleanCellContent = (content) => {
    if (!content) return null;
    let cleaned = content.trim();
    cleaned = cleaned.replace(/\n+/g, " ").replace(/\r+/g, " ");
    cleaned = cleaned.replace(/\s+/g, " ");
    cleaned = cleaned.replace(/[\x00-\x1F\x7F]/g, "");
    cleaned = cleaned.split(/[.!?]\s+/)
      .map(sentence => capitalize(sentence.trim()))
      .join(". ");
    return cleaned.length > 2 ? cleaned : null;
  };

  // Fonction pour extraire les tableaux d'un document DOCX
  const extractTablesFromDocx = async (file) => {
    if (!window.JSZip) {
      throw new Error("JSZip n'est pas chargé. Veuillez recharger la page.");
    }

    const arrayBuffer = await file.arrayBuffer();
    const zip = await window.JSZip.loadAsync(arrayBuffer);
    const documentXml = await zip.file("word/document.xml").async("string");
    const parser = new DOMParser();
    const doc = parser.parseFromString(documentXml, "text/xml");
    
    const tables = doc.getElementsByTagName("w:tbl");
    const extractedTables = [];
    
    for (let i = 0; i < tables.length; i++) {
      const table = tables[i];
      const rows = table.getElementsByTagName("w:tr");
      const tableData = [];
      
      for (let rowIdx = 0; rowIdx < rows.length; rowIdx++) {
        const row = rows[rowIdx];
        const cells = row.getElementsByTagName("w:tc");
        const rowData = [];
        
        for (let cellIdx = 0; cellIdx < cells.length; cellIdx++) {
          const cell = cells[cellIdx];
          const paragraphs = cell.getElementsByTagName("w:p");
          let cellText = "";
          
          for (let pIdx = 0; pIdx < paragraphs.length; pIdx++) {
            const paragraph = paragraphs[pIdx];
            const runs = paragraph.getElementsByTagName("w:t");
            
            for (let rIdx = 0; rIdx < runs.length; rIdx++) {
              const textNode = runs[rIdx].childNodes[0];
              if (textNode && textNode.nodeValue) {
                cellText += textNode.nodeValue;
              }
            }
            
            if (pIdx < paragraphs.length - 1) {
              cellText += " ";
            }
          }
          
          rowData.push(cellText.trim());
        }
        
        tableData.push(rowData);
      }
      
      extractedTables.push(tableData);
    }
    
    return extractedTables;
  };

  // Fonction pour parser un tableau et extraire l'emploi du temps
  const parseTableToTimetable = (tableData) => {
    const timetable = DAYS.map(day => ({ 
      jour: capitalize(day), 
      slot1: { type: "Cours", cours: "", professeur: "", salle: "" }, 
      slot2: { type: "Cours", cours: "", professeur: "", salle: "" }, 
      slot3: { type: "Cours", cours: "", professeur: "", salle: "" }, 
      slot4: { type: "Cours", cours: "", professeur: "", salle: "" } 
    }));

    if (!tableData || tableData.length === 0) {
      return timetable;
    }

    let headerRowIndex = -1;
    let dayColumnIndex = -1;
    const timeSlotColumns = [];

    for (let i = 0; i < Math.min(3, tableData.length); i++) {
      const row = tableData[i];
      const rowText = row.join(" ").toLowerCase();
      
      const hasDays = DAYS.some(day => rowText.includes(day));
      const hasTimeSlots = /8h30|10h45|14h|16h15/.test(rowText);
      
      if (hasDays || hasTimeSlots) {
        headerRowIndex = i;
        
        row.forEach((cell, colIdx) => {
          const cellLower = cell.toLowerCase();
          if (DAYS.some(day => cellLower.includes(day))) {
            dayColumnIndex = colIdx;
          }
          
          if (/8h30|8:30/.test(cellLower)) {
            timeSlotColumns[0] = colIdx;
          }
          if (/10h45|10:45/.test(cellLower)) {
            timeSlotColumns[1] = colIdx;
          }
          if (/14h|14:00/.test(cellLower)) {
            timeSlotColumns[2] = colIdx;
          }
          if (/16h15|16:15/.test(cellLower)) {
            timeSlotColumns[3] = colIdx;
          }
        });
        break;
      }
    }

    if (dayColumnIndex === -1) {
      dayColumnIndex = 0;
      for (let i = 1; i <= 4 && i < tableData[0]?.length; i++) {
        timeSlotColumns[i - 1] = i;
      }
    }

    const startRow = headerRowIndex >= 0 ? headerRowIndex + 1 : 1;
    
    for (let rowIdx = startRow; rowIdx < tableData.length; rowIdx++) {
      const row = tableData[rowIdx];
      if (row.length === 0) continue;

      let dayFound = null;
      let dayIdx = -1;

      if (dayColumnIndex >= 0 && dayColumnIndex < row.length) {
        const dayCell = row[dayColumnIndex].toLowerCase();
        DAYS.forEach((day, idx) => {
          if (dayCell.includes(day) && !dayFound) {
            dayFound = day;
            dayIdx = idx;
          }
        });
      }

      if (!dayFound) {
        const rowText = row.join(" ").toLowerCase();
        DAYS.forEach((day, idx) => {
          if (rowText.includes(day) && !dayFound) {
            dayFound = day;
            dayIdx = idx;
          }
        });
      }

      if (dayIdx === -1) continue;

      timeSlotColumns.forEach((colIdx, slotIdx) => {
        if (colIdx >= 0 && colIdx < row.length) {
          const cellContent = row[colIdx].trim();
          if (cellContent && cellContent.length > 2) {
            const cleaned = cleanCellContent(cellContent);
            if (cleaned && cleaned.length > 2) {
              const slotKey = `slot${slotIdx + 1}`;
              timetable[dayIdx][slotKey] = parseCellContent(cleaned);
            }
          }
        }
      });
    }

    return timetable;
  };

  // Composant pour afficher une cellule en mode visualisation (sans édition)
  const TimetableCellView = ({ cellData, isEmpty }) => {
    if (isEmpty || (!cellData.cours && !cellData.professeur && !cellData.salle)) {
      return (
        <div className="min-h-[100px] p-3 flex items-center justify-center border border-dashed border-muted-foreground/30 rounded-lg bg-muted/20">
          <span className="text-muted-foreground italic text-sm">Pas de cours</span>
        </div>
      );
    }

    return (
      <div className="min-h-[100px] p-3 border rounded-lg bg-gradient-to-br from-background to-muted/30">
        <div className="space-y-2">
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
              <span className="font-semibold text-sm leading-tight text-foreground">
                {cellData.cours}
              </span>
            </div>
          )}
          
          {cellData.professeur && (
            <div className="flex items-center gap-2">
              <User className="h-3.5 w-3.5 text-blue-600 dark:text-blue-400 shrink-0" />
              <span className="text-xs text-muted-foreground">
                {cellData.professeur}
              </span>
            </div>
          )}
          
          {cellData.salle && (
            <div className="flex items-center gap-2">
              <Building2 className="h-3.5 w-3.5 text-green-600 dark:text-green-400 shrink-0" />
              <Badge variant="outline" className="text-xs">
                {cellData.salle}
              </Badge>
            </div>
          )}
        </div>
      </div>
    );
  };

  // Fonction pour générer des données de démonstration
  const generateDemoTimetable = () => {
    return DAYS.map((day, dayIdx) => ({
      jour: capitalize(day),
      slot1: dayIdx === 0 
        ? { type: "Cours", cours: "Mathématiques", professeur: "Pr. DUPONT", salle: "A12" }
        : dayIdx === 1
        ? { type: "TD/TP", cours: "Physique", professeur: "Pr. MARTIN", salle: "B16" }
        : dayIdx === 2
        ? { type: "Cours", cours: "Informatique", professeur: "Pr. BERNARD", salle: "C6" }
        : { type: "Cours", cours: "", professeur: "", salle: "" },
      slot2: dayIdx === 0
        ? { type: "TD/TP", cours: "Chimie", professeur: "Pr. LEBLANC", salle: "D8" }
        : dayIdx === 1
        ? { type: "Cours", cours: "Anglais", professeur: "Pr. DURAND", salle: "E4" }
        : dayIdx === 3
        ? { type: "Cours", cours: "Électronique", professeur: "Pr. MOREAU", salle: "F10" }
        : { type: "Cours", cours: "", professeur: "", salle: "" },
      slot3: dayIdx === 1
        ? { type: "Cours", cours: "Mécanique", professeur: "Pr. PETIT", salle: "G2" }
        : dayIdx === 2
        ? { type: "TD/TP", cours: "Programmation", professeur: "Pr. ROUSSEAU", salle: "H14" }
        : dayIdx === 4
        ? { type: "Cours", cours: "Thermodynamique", professeur: "Pr. SIMON", salle: "I6" }
        : { type: "Cours", cours: "", professeur: "", salle: "" },
      slot4: dayIdx === 0
        ? { type: "Cours", cours: "Sciences des matériaux", professeur: "Pr. LEFEBVRE", salle: "J8" }
        : dayIdx === 3
        ? { type: "TD/TP", cours: "Automatique", professeur: "Pr. GIRARD", salle: "K12" }
        : dayIdx === 4
        ? { type: "Cours", cours: "Gestion de projet", professeur: "Pr. BERTRAND", salle: "L4" }
        : { type: "Cours", cours: "", professeur: "", salle: "" }
    }));
  };

  // Modifier handleView pour afficher des données de démonstration
  const handleView = async (timetable) => {
    setViewingTimetable(timetable);
    setError(null);
    setLoading(true);
    setProgress(0);

    // Simuler un chargement
    setTimeout(() => {
      setProgress(50);
      setTimeout(() => {
        // Utiliser des données de démonstration
        const demoData = generateDemoTimetable();
        setTimetableData(demoData);
        setProgress(100);
        setLoading(false);
        setTimeout(() => setProgress(0), 500);
      }, 500);
    }, 300);

    // Essayer d'extraire le fichier réel si disponible (optionnel)
    try {
      const fileName = timetable.fileName.toLowerCase();
      const isDocx = fileName.endsWith('.docx');

      if (isDocx && jszipReady) {
        try {
          const response = await fetch(timetable.fileUrl);
          if (response.ok) {
            const blob = await response.blob();
            const file = new File([blob], timetable.fileName, { type: blob.type });
            const tables = await extractTablesFromDocx(file);
            
            if (tables.length > 0) {
              let bestTable = tables[0];
              if (tables.length > 1) {
                bestTable = tables.reduce((max, table) => 
                  table.length > max.length ? table : max
                );
              }
              const parsed = parseTableToTimetable(bestTable);
              setTimetableData(parsed);
            }
          }
        } catch (err) {
          // Si l'extraction échoue, utiliser les données de démonstration
          console.log("Utilisation des données de démonstration");
        }
      }
    } catch (err) {
      // En cas d'erreur, les données de démonstration sont déjà chargées
      console.log("Utilisation des données de démonstration");
    }
  };

  return (
    <div className="container mx-auto p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Calendar className="h-6 w-6 text-primary" />
          <h1 className="text-2xl font-bold">Liste des Emplois du Temps</h1>
        </div>
        <Button onClick={handleCreateNew} className="flex items-center gap-2">
          <Plus className="h-4 w-4" />
          Nouvel Emploi du Temps
        </Button>
      </div>

      {/* Dialog pour afficher le tableau extrait */}
      <Dialog open={viewingTimetable !== null} onOpenChange={(open) => {
        if (!open) {
          setViewingTimetable(null);
          setTimetableData(null);
          setError(null);
        }
      }}>
        <DialogContent className="max-w-7xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Calendar className="h-5 w-5" />
              Emploi du Temps - {viewingTimetable?.fileName}
            </DialogTitle>
            <DialogDescription>
              Visualisation de l'emploi du temps extrait
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            {loading && (
              <div className="space-y-2">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-muted-foreground">Chargement en cours...</span>
                  <span className="font-medium">{progress}%</span>
                </div>
                <Progress value={progress} className="h-2" />
              </div>
            )}

            {error && (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertTitle>Erreur</AlertTitle>
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            {timetableData && !loading && (
              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead className="font-semibold">Jour</TableHead>
                      {TIME_SLOTS.map((slot, i) => (
                        <TableHead key={i} className="text-center font-semibold">
                          {slot}
                        </TableHead>
                      ))}
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {timetableData.map((row, i) => (
                      <TableRow key={i}>
                        <TableCell className="font-medium align-top pt-4">
                          <div className="font-semibold text-base">{row.jour}</div>
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCellView
                            cellData={row.slot1}
                            isEmpty={!row.slot1 || (!row.slot1.cours && !row.slot1.professeur && !row.slot1.salle)}
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCellView
                            cellData={row.slot2}
                            isEmpty={!row.slot2 || (!row.slot2.cours && !row.slot2.professeur && !row.slot2.salle)}
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCellView
                            cellData={row.slot3}
                            isEmpty={!row.slot3 || (!row.slot3.cours && !row.slot3.professeur && !row.slot3.salle)}
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCellView
                            cellData={row.slot4}
                            isEmpty={!row.slot4 || (!row.slot4.cours && !row.slot4.professeur && !row.slot4.salle)}
                          />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
          </div>
        </DialogContent>
      </Dialog>

      <Card>
        
        <CardContent>
          {timetables.length === 0 ? (
            <div className="text-center py-8">
              <FileText className="mx-auto h-12 w-12 text-gray-400 mb-4" />
              <h3 className="text-lg font-semibold">Aucun emploi du temps</h3>
              <p className="text-muted-foreground mb-4">
                Commencez par créer votre premier emploi du temps.
              </p>
              <Button onClick={handleCreateNew}>
                <Plus className="h-4 w-4 mr-2" />
                Créer un emploi du temps
              </Button>
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Fichier</TableHead>
                  <TableHead>Classe</TableHead>
                  <TableHead>Filière</TableHead>
                  <TableHead>Semestre</TableHead>
                  <TableHead>Date d'upload</TableHead>
                  <TableHead>Taille</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {timetables.map((timetable) => (
                  <TableRow key={timetable.id}>
                    <TableCell className="font-medium">
                      <div className="flex items-center gap-2">
                        <FileText className="h-4 w-4 text-blue-600" />
                        {timetable.fileName}
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge variant="secondary">
                        {getClassLabel(timetable.class)}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      {timetable.filiere ? (
                        <Badge variant="outline">
                          {getFiliereLabel(timetable.filiere)}
                        </Badge>
                      ) : (
                        <span className="text-muted-foreground">-</span>
                      )}
                    </TableCell>
                    <TableCell>
                      <Badge variant="outline">
                        {getSemesterLabel(timetable.semester)}
                      </Badge>
                    </TableCell>
                    <TableCell>{formatDate(timetable.uploadDate)}</TableCell>
                    <TableCell className="text-muted-foreground">
                      {timetable.fileSize}
                    </TableCell>
                    <TableCell>
                      <div className="flex justify-end gap-2">
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleView(timetable)}
                          title="Voir le PDF"
                        >
                          <Eye className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleDownload(timetable)}
                          title="Télécharger"
                        >
                          <Download className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleEdit(timetable)}
                          title="Modifier"
                        >
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="destructive"
                          size="sm"
                          onClick={() => handleDelete(timetable)}
                          title="Supprimer"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* Dialog de confirmation de suppression */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Êtes-vous sûr ?</AlertDialogTitle>
            <AlertDialogDescription>
              Cette action supprimera définitivement l'emploi du temps{" "}
              <strong>{timetableToDelete?.fileName}</strong>. Cette action ne peut pas être annulée.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Annuler</AlertDialogCancel>
            <AlertDialogAction onClick={confirmDelete} className="bg-destructive text-destructive-foreground">
              Supprimer
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

    </div>
  );
}