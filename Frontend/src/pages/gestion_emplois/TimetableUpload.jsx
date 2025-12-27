"use client";
const BASE_URL = "http://localhost:8082";
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
export function TimetableUpload() {
  const { id } = useParams();
  // charger les donnée depuis la base de donnée

  const [classes, setClasses] = useState([]);
  const [filieresList, setFilieresList] = useState([]);
  const [profs, setProfs] = useState([]);
  const [matieres, setMatieres] = useState([]);
  const [salles, setSalles] = useState([]);
  const [progress, setProgress] = useState(0);
  const [documentText, setDocumentText] = useState("");
  const [timetable, setTimetable] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [jszipReady, setJszipReady] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isEditMode, setIsEditMode] = useState(false);
  const [timetableName, setTimetableName] = useState("");
  const [selectedClass, setSelectedClass] = useState("");
  const [selectedFiliere, setSelectedFiliere] = useState("");
  const [selectedSemester, setSelectedSemester] = useState("");
  const [showFiliereSelect, setShowFiliereSelect] = useState(false);
  const [availableSemesters, setAvailableSemesters] = useState([]);
  const [editingCell, setEditingCell] = useState(null);
  const [editForm, setEditForm] = useState({ type: "Cours", cours: "", professeur: "", salle: "" });
  const [isUploading, setIsUploading] = useState(false);
  const [uploadStatus, setUploadStatus] = useState("");

  const CLASSES_LIST = [
    { value: "CP1", label: "CP1" },
    { value: "CP2", label: "CP2" },
    { value: "CI1", label: "CI1" },
    { value: "CI2", label: "CI2" },
    { value: "CI3", label: "CI3" },
  ];

  const CI_CLASSES = ["ci1", "ci2", "ci3"];
  const FILIERES_CI = ["RST", "INFO", "CIVIL", "INDUS", "MECA", "ELEC"];


  const DAYS = ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi"];
  const TIME_SLOTS = ["8h30→10h30", "10h45→12h45", "14h → 16h", "16h15→18h15"];
  // map statique des semestres par classe (modifiable)
  const SEMESTERS_MAP = {
    cp1: [{ value: "S1", label: "S1" }, { value: "S2", label: "S2" }],
    cp2: [{ value: "S3", label: "S3" }, { value: "S4", label: "S4" }],
    ci1: [{ value: "S5", label: "S5" }, { value: "S6", label: "S6" }],
    ci2: [{ value: "S7", label: "S7" }, { value: "S8", label: "S8" }],
    ci3: [{ value: "S9", label: "S9" }]
  };


  const normalizeCreneauLabelToRange = (label) => label; // placeholder
  const sampleTimetables = {};
  const filieres = {};

  // Hook pour récupérer les classes et filières depuis le backend
  const useClassesAndFilieres = () => {
    const [classes, setClasses] = useState([]);
    const [filieres, setFilieres] = useState({});

    useEffect(() => {
      const fetchAll = async () => {
        try {
          const [cRes, fRes] = await Promise.all([
            fetch(`http://localhost:8080/emploi/classes`),
            fetch(`${BASE_URL}/filieres`)
          ]);

          if (!cRes.ok || !fRes.ok) throw new Error("Erreur chargement ressources");

          const [cData, fData] = await Promise.all([cRes.json(), fRes.json()]);

          setClasses(cData || []);
          setFilieres(fData || []);

        } catch (err) {
          console.error("Erreur fetching metadata:", err);
        }
      };

      fetchAll();
    }, []);


    return { classes, filieres };
  };

 /* useEffect(() => {
    const fetchAll = async () => {
      try {
        const [cRes, fRes, pRes, mRes, sRes] = await Promise.all([
          fetch(`http://localhost:8080/emploi/classes`),
          fetch(`${BASE_URL}/filieres`),
          fetch(`${BASE_URL}/profs`),
          fetch(`${BASE_URL}/matieres`),
          fetch(`${BASE_URL}/salles`)
        ]);
        if (!cRes.ok || !fRes.ok) throw new Error("Erreur chargement ressources");

        const [cData, fData, pData, mData, sData] = await Promise.all([
          cRes.json(), fRes.json(), pRes.json(), mRes.json(), sRes.json()
        ]);

        console.log("Classes brutes du backend:", cData);
        console.log("Filières brutes du backend:", fData);


        const classesMap = new Map();
        const filieresMap = {};

        (cData || []).forEach(c => {
          const className = (c.nom || "").toLowerCase().trim();
          if (!className) return;

          // Grouper les classes par nom
          if (!classesMap.has(className)) {
            classesMap.set(className, {
              id: c.id,
              nom: c.nom,
              filieres: []
            });
          }

          // Ajouter la filière à la map par classe
          if (c.filiere && c.filiere !== "NONE") {
            if (!filieresMap[className]) {
              filieresMap[className] = [];
            }

            // Vérifier si la filière n'est pas déjà dans la liste
            const exists = filieresMap[className].some(f => f.id === c.filiere.id);
            if (!exists) {
              filieresMap[className].push({
                id: c.filiere.id,
                nom: c.filiere.nom
              });
            }

            // Ajouter aussi dans l'objet classe
            const classObj = classesMap.get(className);
            if (!classObj.filieres.includes(c.filiere)) {
              classObj.filieres.push(c.filiere);
            }
          }
        });

        // Convertir la map en array pour les classes uniques
        const uniqueClasses = Array.from(classesMap.values());

        console.log("Classes traitées:", uniqueClasses);
        console.log("Filières par classe (filieresMap):", filieresMap);
        console.log("Clés disponibles dans filieresMap:", Object.keys(filieresMap));

        setClasses(uniqueClasses);
        setFilieresList(filieresMap);
        setProfs(pData || []);
        setMatieres(mData || []);
        setSalles(sData || []);
      } catch (err) {
        console.error("Erreur fetching metadata:", err);
      }
    };

    fetchAll();
  }, []);*/

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




  const TimetableCell = ({ cellData, isEmpty }) => {  // ✅ Enlever onEdit
    if (isEmpty || (!cellData.cours && !cellData.professeur && !cellData.salle)) {
      return (
        <div className="min-h-[100px] p-3 flex items-center justify-center border border-dashed border-muted-foreground/30 rounded-lg bg-muted/20">
          <span className="text-muted-foreground italic text-sm">Pas de cours</span>
        </div>
      );
    }

    return (
      <div className="min-h-[100px] p-3 border rounded-lg bg-gradient-to-br from-background to-muted/30">
        {/* ✅ Supprimer le bouton avec le crayon */}
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

  const classKey = (value || "").toLowerCase().trim();

  // afficher filière uniquement pour CI
  const isCI = ["ci1", "ci2", "ci3"].includes(classKey);
  setShowFiliereSelect(isCI);

  // charger les semestres
  setAvailableSemesters(SEMESTERS_MAP[classKey] || []);

  console.log("Classe sélectionnée :", value);
  console.log("isCI :", isCI);
};

  const buildImportPayload = (classe, filiere, timetableData, semester, fileName) => {
    const emplois = [];

    timetableData.forEach(day => {
      TIME_SLOTS.forEach((slot, index) => {
        const slotKey = `slot${index + 1}`;
        const cellData = day[slotKey];

        if (cellData && (cellData.cours || cellData.professeur || cellData.salle)) {
          const creneauFormatted = slot
            .replace(/h/g, ':')
            .replace(/→/g, '-')
            .replace(/\s+/g, '');

          emplois.push({
            jour: day.jour,
            creneau: creneauFormatted,
            matiere: cellData.cours || "",
            prof: cellData.professeur || "",
            salle: cellData.salle || "",
            semestre: semester || ""
          });
        }
      });
    });

    return {
      classe: classe,
      filiere: filiere || null,
      semestre: semester || "",
      fileName: fileName,  
      emplois: emplois
    };
  };

  

  const handleSubmit = async () => {
    if (!selectedFile && !isEditMode) {
      setUploadStatus("Veuillez sélectionner un fichier PDF ou DOCX.");
      return;
    }
    if (!selectedClass) {
      setUploadStatus("Veuillez sélectionner une classe.");
      return;
    }
    if (showFiliereSelect && !selectedFiliere) {
      setUploadStatus("Veuillez sélectionner une filière.");
      return;
    }
    if (!selectedSemester) {
      setUploadStatus("Veuillez sélectionner un semestre.");
      return;
    }
    if (!timetable) {
      setUploadStatus("Aucun emploi extrait à sauvegarder.");
      return;
    }

    setIsUploading(true);
    setUploadStatus("");

    try {
      // ✅ Upload du fichier
      const formData = new FormData();
      formData.append("file", selectedFile);

      const uploadRes = await fetch(`http://localhost:8080/emploi/upload`, {
        method: "POST",
        body: formData
      });

      if (!uploadRes.ok) {
        throw new Error("Erreur lors de l'upload du fichier");
      }

      const uploadData = await uploadRes.json();
      const uploadedFileName = uploadData.fileName || selectedFile.name;

      // ✅ Import des données
      const payload = buildImportPayload(
        selectedClass,
        selectedFiliere,
        timetable,
        selectedSemester,
        uploadedFileName
      );
      console.log("Payload à envoyer :", payload);


      const importRes = await fetch(`http://localhost:8080/emploi/import`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)

      });

      if (!importRes.ok) {
        const text = await importRes.text();
        throw new Error(text || "Erreur import");
      }

      setUploadStatus("success");
      setTimeout(() => window.location.href = "/timetable", 1000);
    } catch (err) {
      console.error(err);
      setUploadStatus("error");
    } finally {
      setIsUploading(false);
    }
  };


  const handleBack = () => {
    window.location.href = '/timetable';
  };
  const handleUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setSelectedFile(file);

    const formData = new FormData();
    formData.append("file", file);

    try {
      setIsUploading(true);
      setUploadStatus("");

      const res = await fetch(`http://localhost:8080/emploi/upload`, {
        method: "POST",
        body: formData

      });

      if (!res.ok) {
        throw new Error("Erreur lors de l'envoi");
      }

      const msg = await res.text();
      setUploadStatus(msg);
    } catch (err) {
      console.error(err);
      setUploadStatus("Échec de l'upload !");
    } finally {
      setIsUploading(false);
    }
  };


  const uploadFile = async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    const res = await fetch(`http://localhost:8080/emploi/upload`, {
      method: 'POST',
      body: formData 

    });

    const data = await res.json();
    console.log('Upload response:', data);

    // data.fileName contient le nom réel du fichier enregistré côté serveur
    return data.fileName;
  };

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
                  accept=".pdf,.png,.jpg,.jpeg,.docx"
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
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCell
                            cellData={row.slot2}
                            isEmpty={!row.slot2 || (!row.slot2.cours && !row.slot2.professeur && !row.slot2.salle)}
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCell
                            cellData={row.slot3}
                            isEmpty={!row.slot3 || (!row.slot3.cours && !row.slot3.professeur && !row.slot3.salle)}
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCell
                            cellData={row.slot4}
                            isEmpty={!row.slot4 || (!row.slot4.cours && !row.slot4.professeur && !row.slot4.salle)}
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
                {CLASSES_LIST.map((classe) => (
                  <SelectItem key={classe.value} value={classe.value}>
                    {classe.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>


          </div>

          { /* Sélecteur de filière (uniquement pour CI) */}
          {showFiliereSelect &&  (
            <Select value={selectedFiliere} onValueChange={setSelectedFiliere}>
              <SelectTrigger id="filiere-select">
                <SelectValue placeholder="Sélectionnez une filière" />
              </SelectTrigger>
              <SelectContent>
                {FILIERES_CI.map((filiere) => (
                  <SelectItem key={filiere} value={filiere}>
                    {filiere}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
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




    </div>


  );
}