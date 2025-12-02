"use client";

import * as React from "react";
import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Upload, FileText, Calendar, School, BookOpen, ArrowLeft, CheckCircle2, Loader2, AlertCircle, Pencil, GraduationCap, User, Building2 } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { cn } from "@/lib/utils";

// Données statiques pour simuler la base de données
const sampleTimetables = {
  1: {
    id: 1,
    fileName: "emploi_cp1_s1.pdf",
    class: "cp1",
    filiere: "",
    semester: "s1",
    uploadDate: "2024-01-15",
    fileSize: "2.4 MB",
    fileUrl: "/emplois/emploi_cp1_s1.pdf"
  },
  2: {
    id: 2,
    fileName: "emploi_ci1_gi_s5.pdf",
    class: "ci1",
    filiere: "gi",
    semester: "s5",
    uploadDate: "2024-01-10",
    fileSize: "3.1 MB",
    fileUrl: "/emplois/emploi_ci1_gi_s5.pdf"
  },
  3: {
    id: 3,
    fileName: "emploi_cp2_s3.pdf",
    class: "cp2",
    filiere: "",
    semester: "s3",
    uploadDate: "2024-01-08",
    fileSize: "2.8 MB",
    fileUrl: "/emplois/emploi_cp2_s3.pdf"
  }
};

export function TimetableUpload() {
  const { id } = useParams(); // Récupère l'ID depuis l'URL
  const [selectedFile, setSelectedFile] = useState(null);
  const [selectedClass, setSelectedClass] = useState("");
  const [selectedFiliere, setSelectedFiliere] = useState("");
  const [selectedSemester, setSelectedSemester] = useState("");
  const [timetableName, setTimetableName] = useState("");
  const [isUploading, setIsUploading] = useState(false);
  const [uploadStatus, setUploadStatus] = useState("");
  const [isEditMode, setIsEditMode] = useState(false);
  
  // States pour l'extraction DOCX (même que TimetableExtractor)
  const [timetable, setTimetable] = useState(null);
  const [loading, setLoading] = useState(false);
  const [documentText, setDocumentText] = useState("");
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState(null);
  const [jszipReady, setJszipReady] = useState(false);
  const [editingCell, setEditingCell] = useState(null);
  const [editForm, setEditForm] = useState({ type: "Cours", cours: "", professeur: "", salle: "" });

  const DAYS = ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi"];
  const TIME_SLOTS = ["8h30→10h30", "10h45→12h45", "14h → 16h", "16h15→18h15"];

  // Données pour les classes et filières
  const classes = [
    { value: "cp1", label: "CP1" },
    { value: "cp2", label: "CP2" },
    { value: "ci1", label: "CI1" },
    { value: "ci2", label: "CI2" },
    { value: "ci3", label: "CI3" }
  ];

  const filieres = {
    ci1: [
      { value: "gi", label: "Génie informatique" },
      { value: "gm", label: "Génie mécatronique" },
      { value: "ge", label: "Génie électrique" },
      { value: "gc", label: "Génie civil" },
      { value: "gind", label: "Génie industriel" },
      { value: "grst", label: "Génie réseaux et télécommunications" }
    ],
    ci2: [
      { value: "gi", label: "Génie informatique" },
      { value: "gm", label: "Génie mécatronique" },
      { value: "ge", label: "Génie électrique" },
      { value: "gc", label: "Génie civil" },
      { value: "gind", label: "Génie industriel" },
      { value: "grst", label: "Génie réseaux et télécommunications" }
    ],
    ci3: [
      { value: "gi", label: "Génie informatique" },
      { value: "gm", label: "Génie mécatronique" },
      { value: "ge", label: "Génie électrique" },
      { value: "gc", label: "Génie civil" },
      { value: "gind", label: "Génie industriel" },
      { value: "grst", label: "Génie réseaux et télécommunications" }
    ]
  };

  const allSemesters = [
    { value: "s1", label: "Semestre 1" },
    { value: "s2", label: "Semestre 2" },
    { value: "s3", label: "Semestre 3" },
    { value: "s4", label: "Semestre 4" },
    { value: "s5", label: "Semestre 5" },
    { value: "s6", label: "Semestre 6" },
    { value: "s7", label: "Semestre 7" },
    { value: "s8", label: "Semestre 8" },
    { value: "s9", label: "Semestre 9" } 
  ];

  // Filtrer les semestres selon la classe
  const availableSemesters = React.useMemo(() => {
    if (!selectedClass) return allSemesters;
    
    switch (selectedClass) {
      case "cp1":
        return allSemesters.filter(sem => sem.value === "s1" || sem.value === "s2");
      case "cp2":
        return allSemesters.filter(sem => sem.value === "s3" || sem.value === "s4");
      case "ci1":
        return allSemesters.filter(sem => sem.value === "s5" || sem.value === "s6");
      case "ci2":
        return allSemesters.filter(sem => sem.value === "s7" || sem.value === "s8");
      case "ci3":
        return allSemesters.filter(sem => sem.value === "s9");
      default:
        return allSemesters;
    }
  }, [selectedClass]);

  // Charger JSZip depuis CDN (même code que TimetableExtractor)
  useEffect(() => {
    if (typeof window === 'undefined') return;

    const loadJSZip = async () => {
      if (window.JSZip) {
        console.log('JSZip déjà disponible');
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
              console.log('JSZip chargé avec succès');
              setJszipReady(true);
            }
          }, 100);
        };
        script.onerror = () => {
          console.error('Erreur lors du chargement de JSZip');
          setJszipReady(false);
        };
        document.body.appendChild(script);
      } catch (error) {
        console.error('Erreur lors du chargement de JSZip:', error);
        setJszipReady(false);
      }
    };

    loadJSZip();
  }, []);

  // Charger les données en mode édition
  useEffect(() => {
    if (id) {
      const timetableId = parseInt(id);
      if (sampleTimetables[timetableId]) {
        const timetable = sampleTimetables[timetableId];
        setIsEditMode(true);
        setSelectedClass(timetable.class);
        setSelectedFiliere(timetable.filiere);
        setSelectedSemester(timetable.semester);
        setTimetableName(timetable.fileName || "");
        console.log("Chargement des données pour l'édition:", timetable);
      } else {
        console.warn("Aucun emploi du temps trouvé avec l'ID:", timetableId);
      }
    }
  }, [id]);

  // Fonction pour parser une cellule et extraire type, cours, professeur, salle (même que TimetableExtractor)
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
      
      // Ajouter "Pr." avant le nom si ce n'est pas déjà présent
      if (professeur && !professeur.toLowerCase().startsWith("pr.") && !professeur.toLowerCase().startsWith("pr ")) {
        professeur = `Pr. ${professeur}`;
      }
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

  const extractRawText = async (file) => {
    try {
      const arrayBuffer = await file.arrayBuffer();
      const zip = await window.JSZip.loadAsync(arrayBuffer);
      const documentXml = await zip.file("word/document.xml").async("string");
      const parser = new DOMParser();
      const doc = parser.parseFromString(documentXml, "text/xml");
      
      const textNodes = doc.getElementsByTagName("w:t");
      let text = "";
      for (let i = 0; i < textNodes.length; i++) {
        const node = textNodes[i];
        if (node.childNodes[0] && node.childNodes[0].nodeValue) {
          text += node.childNodes[0].nodeValue;
        }
      }
      return text;
    } catch (error) {
      console.error("Erreur extraction texte:", error);
      return "Impossible d'extraire le texte";
    }
  };

  // Fonction pour ouvrir le dialog d'édition
  const handleEditCell = (dayIndex, slotKey) => {
    const cellData = timetable[dayIndex][slotKey];
    setEditingCell({ dayIndex, slotKey });
    setEditForm({
      type: cellData.type || "Cours",
      cours: cellData.cours || "",
      professeur: cellData.professeur || "",
      salle: cellData.salle || ""
    });
  };

  // Fonction pour sauvegarder les modifications
  const handleSaveEdit = () => {
    if (!editingCell) return;
    
    const newTimetable = [...timetable];
    newTimetable[editingCell.dayIndex][editingCell.slotKey] = {
      type: editForm.type || "Cours",
      cours: editForm.cours.trim(),
      professeur: editForm.professeur.trim(),
      salle: editForm.salle.trim()
    };
    
    setTimetable(newTimetable);
    setEditingCell(null);
    setEditForm({ type: "Cours", cours: "", professeur: "", salle: "" });
  };

  // Composant pour afficher une cellule avec style (même que TimetableExtractor)
  const TimetableCell = ({ cellData, isEmpty, onEdit }) => {
    if (isEmpty || (!cellData.cours && !cellData.professeur && !cellData.salle)) {
      return (
        <div className="relative group min-h-[100px] p-3 flex items-center justify-center border border-dashed border-muted-foreground/30 rounded-lg bg-muted/20 hover:bg-muted/30 transition-colors">
          <Button
            variant="ghost"
            size="icon"
            className="absolute top-2 right-2 h-7 w-7 opacity-0 group-hover:opacity-100 transition-opacity"
            onClick={onEdit}
          >
            <Pencil className="h-3.5 w-3.5" />
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
          <Pencil className="h-3.5 w-3.5" />
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

  const handleFileChange = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    setSelectedFile(file);
    setError(null);
    setTimetable(null);
    setDocumentText("");
    setLoading(false);
    setProgress(0);

    const fileName = file.name.toLowerCase();
    const isDocx = fileName.endsWith('.docx') || file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
    const isPdf = fileName.endsWith('.pdf') || file.type === 'application/pdf';

    if (isDocx) {
      if (!jszipReady) {
        setError("JSZip n'est pas encore chargé. Veuillez attendre quelques secondes et réessayer.");
        return;
      }

      setLoading(true);
      try {
        setProgress(20);
        const tables = await extractTablesFromDocx(file);
        setProgress(60);

        if (tables.length === 0) {
          throw new Error("Aucun tableau trouvé dans le document.");
        }

        const textResult = await extractRawText(file);
        setDocumentText(textResult);

        let bestTable = tables[0];
        if (tables.length > 1) {
          bestTable = tables.reduce((max, table) => 
            table.length > max.length ? table : max
          );
        }

        setProgress(80);
        const parsed = parseTableToTimetable(bestTable);
        setProgress(100);
        setTimetable(parsed);
        setUploadStatus("");
      } catch (err) {
        console.error("Erreur extraction:", err);
        setError(`Erreur lors de l'extraction: ${err.message || "Erreur inconnue"}`);
      } finally {
        setLoading(false);
        setTimeout(() => setProgress(0), 1000);
      }
    } else if (isPdf) {
      setUploadStatus("");
    } else {
      setSelectedFile(null);
      setUploadStatus("Veuillez sélectionner un fichier PDF ou DOCX valide.");
    }
  };

  const handleClassChange = (value) => {
    setSelectedClass(value);
    setSelectedFiliere("");
    setSelectedSemester("");
  };

  const handleSubmit = async () => {
    if (!selectedFile && !isEditMode) {
      setUploadStatus("Veuillez sélectionner un fichier PDF.");
      return;
    }

    if (!selectedClass) {
      setUploadStatus("Veuillez sélectionner une classe.");
      return;
    }

    if ((selectedClass === "ci1" || selectedClass === "ci2" || selectedClass === "ci3") && !selectedFiliere) {
      setUploadStatus("Veuillez sélectionner une filière.");
      return;
    }

    if (!selectedSemester) {
      setUploadStatus("Veuillez sélectionner un semestre.");
      return;
    }

    setIsUploading(true);
    setUploadStatus("");

    try {
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      // Simulation de sauvegarde
      console.log("Données sauvegardées:", {
        file: selectedFile,
        class: selectedClass,
        filiere: selectedFiliere,
        semester: selectedSemester,
        timetableName: timetableName || (selectedFile ? selectedFile.name : ""),
        isEditMode,
        timetableId: id
      });

      setUploadStatus("success");
      
      // Redirection après succès
      setTimeout(() => {
        window.location.href = '/timetable';
      }, 1500);
      
    } catch (error) {
      setUploadStatus("error");
    } finally {
      setIsUploading(false);
    }
  };

  const handleBack = () => {
    window.location.href = '/timetable';
  };

  const showFiliereSelect = selectedClass && (selectedClass === "ci1" || selectedClass === "ci2" || selectedClass === "ci3");

  return (
    <div className="container mx-auto p-6 space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="outline" size="icon" onClick={handleBack}>
          <ArrowLeft className="h-4 w-4" />
        </Button>
      
      </div>

    

      <Card className="w-full max-w-7xl mx-auto">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Upload className="h-5 w-5" />
            {isEditMode ? "Modifier l'Emploi du Temps" : "Ajouter un Emploi du Temps"}
          </CardTitle>
          <CardDescription>
            {isEditMode 
              ? "Modifiez les informations de l'emploi du temps existant."
              : "Téléchargez un fichier PDF contenant l'emploi du temps et sélectionnez les informations correspondantes."
            }
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Zone d'upload de fichier - Style TimetableExtractor */}
          <div className="space-y-4">
            <div className="flex items-center justify-center w-full">
              <label htmlFor="file-upload" className="cursor-pointer">
                <Input
                  id="file-upload"
                  type="file"
                  accept=".pdf,.docx,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                  onChange={handleFileChange}
                  className="hidden"
                  disabled={!jszipReady || loading || isUploading}
                />
                <Button
                  type="button"
                  variant="outline"
                  size="lg"
                  className="w-full max-w-md"
                  disabled={!jszipReady || loading || isUploading}
                  asChild
                >
                  <span>
                    <FileText className="mr-2 h-4 w-4" />
                    {loading ? "Traitement en cours..." : selectedFile ? selectedFile.name : isEditMode ? "Changer le fichier (optionnel)" : "Choisir un fichier PDF ou DOCX"}
                  </span>
                </Button>
              </label>
            </div>

            {loading && (
              <div className="space-y-2">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-muted-foreground">Extraction en cours...</span>
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

            {selectedFile && !loading && !error && (
              <div className="text-center">
                <p className="text-sm text-green-600 font-medium">
                  ✓ {selectedFile.name}
                </p>
              </div>
            )}

            {isEditMode && !selectedFile && (
              <p className="text-xs text-muted-foreground text-center">
                Fichier actuel: {sampleTimetables[id]?.fileName} - Laissez vide pour conserver le fichier actuel.
              </p>
            )}
          </div>

          {/* Affichage du tableau extrait - Même style que TimetableExtractor */}
          {timetable && (
            <div className="space-y-4">
              <div className="flex items-center gap-2">
                <Calendar className="h-5 w-5 text-primary" />
                <h3 className="font-semibold text-lg">Emploi du Temps Extraît</h3>
              </div>
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
                    {timetable.map((row, i) => (
                      <TableRow key={i}>
                        <TableCell className="font-medium align-top pt-4">
                          <div className="font-semibold text-base">{row.jour}</div>
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCell
                            cellData={row.slot1}
                            isEmpty={!row.slot1 || (!row.slot1.cours && !row.slot1.professeur && !row.slot1.salle)}
                            onEdit={() => handleEditCell(i, "slot1")}
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCell
                            cellData={row.slot2}
                            isEmpty={!row.slot2 || (!row.slot2.cours && !row.slot2.professeur && !row.slot2.salle)}
                            onEdit={() => handleEditCell(i, "slot2")}
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCell
                            cellData={row.slot3}
                            isEmpty={!row.slot3 || (!row.slot3.cours && !row.slot3.professeur && !row.slot3.salle)}
                            onEdit={() => handleEditCell(i, "slot3")}
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCell
                            cellData={row.slot4}
                            isEmpty={!row.slot4 || (!row.slot4.cours && !row.slot4.professeur && !row.slot4.salle)}
                            onEdit={() => handleEditCell(i, "slot4")}
                          />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </div>
          )}

          {/* Champ pour modifier le nom de l'emploi du temps (en mode édition) */}
          {isEditMode && (
            <div className="space-y-2">
              <Label htmlFor="timetable-name">Nom de l'emploi du temps</Label>
              <Input
                id="timetable-name"
                type="text"
                placeholder="Entrez le nom de l'emploi du temps"
                value={timetableName}
                onChange={(e) => setTimetableName(e.target.value)}
              />
            </div>
          )}

          {/* Sélecteur de classe */}
          <div className="space-y-2">
            <Label htmlFor="class-select">Classe</Label>
            <Select value={selectedClass} onValueChange={handleClassChange}>
              <SelectTrigger id="class-select">
                <SelectValue placeholder="Sélectionnez une classe" />
              </SelectTrigger>
              <SelectContent>
                {classes.map((classe) => (
                  <SelectItem key={classe.value} value={classe.value}>
                    <div className="flex items-center gap-2">
                      <School className="h-4 w-4" />
                      {classe.label}
                    </div>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {/* Sélecteur de filière (uniquement pour CI) */}
          {showFiliereSelect && (
            <div className="space-y-2">
              <Label htmlFor="filiere-select">Filière</Label>
              <Select value={selectedFiliere} onValueChange={setSelectedFiliere}>
                <SelectTrigger id="filiere-select">
                  <SelectValue placeholder="Sélectionnez une filière" />
                </SelectTrigger>
                <SelectContent>
                  {filieres[selectedClass]?.map((filiere) => (
                    <SelectItem key={filiere.value} value={filiere.value}>
                      {filiere.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          )}

          {/* Sélecteur de semestre */}
          <div className="space-y-2">
            <Label htmlFor="semester-select">Semestre</Label>
            <Select 
              value={selectedSemester} 
              onValueChange={setSelectedSemester}
              disabled={availableSemesters.length === 0}
            >
              <SelectTrigger id="semester-select">
                <SelectValue 
                  placeholder={
                    selectedClass 
                      ? `Sélectionnez un semestre (${availableSemesters.length} disponible${availableSemesters.length > 1 ? 's' : ''})`
                      : "Sélectionnez d'abord une classe"
                  } 
                />
              </SelectTrigger>
              <SelectContent>
                {availableSemesters.map((semester) => (
                  <SelectItem key={semester.value} value={semester.value}>
                    <div className="flex items-center gap-2">
                      <BookOpen className="h-4 w-4" />
                      {semester.label}
                    </div>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {/* Boutons d'action */}
          <div className="flex gap-4">
            <Button variant="outline" onClick={handleBack} className="flex-1">
              Annuler
            </Button>
            <Button 
              onClick={handleSubmit} 
              disabled={isUploading}
              className="flex-1"
              size="lg"
            >
              {isUploading ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2" />
                  {isEditMode ? "Modification..." : "Upload en cours..."}
                </>
              ) : (
                <>
                  <Upload className="h-4 w-4 mr-2" />
                  {isEditMode ? "Modifier l'emploi du temps" : "Uploader l'emploi du temps"}
                </>
              )}
            </Button>
          </div>

          {/* Messages de statut */}
          {uploadStatus === "success" && (
            <Alert className="bg-green-50 border-green-200">
              <AlertDescription className="text-green-800">
                ✅ {isEditMode ? "L'emploi du temps a été modifié avec succès !" : "L'emploi du temps a été uploadé avec succès !"}
              </AlertDescription>
            </Alert>
          )}

          {uploadStatus === "error" && (
            <Alert className="bg-red-50 border-red-200">
              <AlertDescription className="text-red-800">
                ❌ Une erreur est survenue. Veuillez réessayer.
              </AlertDescription>
            </Alert>
          )}

          {uploadStatus && uploadStatus !== "success" && uploadStatus !== "error" && (
            <Alert className="bg-yellow-50 border-yellow-200">
              <AlertDescription className="text-yellow-800">
                {uploadStatus}
              </AlertDescription>
            </Alert>
          )}
        </CardContent>
      </Card>

      {/* Edit Dialog - Même que TimetableExtractor */}
      <Dialog open={editingCell !== null} onOpenChange={(open) => !open && setEditingCell(null)}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Modifier le cours</DialogTitle>
            <DialogDescription>
              Modifiez les informations du cours. Cliquez sur Enregistrer lorsque vous avez terminé.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="type">Type</Label>
              <Select
                value={editForm.type}
                onValueChange={(value) => setEditForm({ ...editForm, type: value })}
              >
                <SelectTrigger id="type">
                  <SelectValue placeholder="Sélectionner le type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Cours">Cours</SelectItem>
                  <SelectItem value="TD/TP">TD/TP</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="cours">
                {editForm.type === "TD/TP" ? "Sujet (TD/TP)" : "Cours"}
              </Label>
              <Input
                id="cours"
                placeholder={editForm.type === "TD/TP" ? "Nom du sujet" : "Nom du cours"}
                value={editForm.cours}
                onChange={(e) => setEditForm({ ...editForm, cours: e.target.value })}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="professeur">Professeur</Label>
              <Input
                id="professeur"
                placeholder="Nom du professeur"
                value={editForm.professeur}
                onChange={(e) => setEditForm({ ...editForm, professeur: e.target.value })}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="salle">Salle / Amphi</Label>
              <Input
                id="salle"
                placeholder="Numéro de salle ou Amphi"
                value={editForm.salle}
                onChange={(e) => setEditForm({ ...editForm, salle: e.target.value })}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setEditingCell(null)}>
              Annuler
            </Button>
            <Button onClick={handleSaveEdit}>
              Enregistrer
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}