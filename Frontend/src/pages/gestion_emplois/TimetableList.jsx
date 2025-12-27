"use client";
const BASE_URL = "http://localhost:8080";

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
  DialogFooter
} from "@/components/ui/dialog";
import { cn } from "@/lib/utils";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Pencil } from "lucide-react";

import { Document, Packer, Paragraph, Table as DocxTable, TableRow as DocxTableRow, TableCell as DocxTableCell, WidthType, TextRun } from "docx";

export function TimetableList() {
  const [timetables, setTimetables] = useState([]);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [timetableToDelete, setTimetableToDelete] = useState(null);

  // States pour l'extraction et visualisation
  const [viewingTimetable, setViewingTimetable] = useState(null);
  const [timetableData, setTimetableData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState(null);
  const [jszipReady, setJszipReady] = useState(false);
  const [teachersMap, setTeachersMap] = useState({});


  const DAYS = ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi"];
  const TIME_SLOTS = ["8h30→10h30", "10h45→12h45", "14h → 16h", "16h15→18h15"];


  const [editingCell, setEditingCell] = useState(null);
  const [editForm, setEditForm] = useState({
    type: "Cours",
    cours: "",
    professeur: "",
    salle: ""
  });
  const [editingTimetable, setEditingTimetable] = useState(null);



  const transformBackendTimetable = (data) => {
    const DAYS = ["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"];
    const TIME_SLOTS = ["8:30-10:30", "10:45-12:45", "14:00-16:00", "16:15-18:15"];

    const timetableByDay = DAYS.map(day => ({
      jour: day,
      slot1: null,
      slot2: null,
      slot3: null,
      slot4: null,
    }));


    data.forEach(item => {
      const dayObj = timetableByDay.find(d => d.jour.toLowerCase() === (item.jour || '').toLowerCase());
      if (!dayObj) return;

      // Normaliser heureDebut pour robustesse (ex: "14:" ou "14:00" -> detecter "14")
      const hd = (item.creneau?.heureDebut || '').trim().toLowerCase();

      let slotKey = null;
      if (hd.startsWith("8")) slotKey = "slot1";
      else if (hd.startsWith("10")) slotKey = "slot2";
      else if (hd.startsWith("14")) slotKey = "slot3";
      else if (hd.startsWith("16")) slotKey = "slot4";

      if (slotKey) {
        console.log("EMPLOI PROF =", item.profId);
        const teacher = teachersMap[item.profId];
        dayObj[slotKey] = {
          type: "Cours",
          cours: item.matiere?.nom || "",
          professeur: teacher
            ? `${teacher.firstname[0].toUpperCase()}${teacher.firstname.slice(1).toLowerCase()}
             ${teacher.lastname[0].toUpperCase()}${teacher.lastname.slice(1).toLowerCase()}`
            : "",

          salle: item.salle?.nom || ""
        };
      }
    });

    return timetableByDay;
  };



  useEffect(() => {
    const fetchTimetables = async () => {
      try {
        const response = await fetch(`${BASE_URL}/emploi`);
        if (response.ok) {
          const data = await response.json();

          // Grouper par classe + filière pour obtenir une "liste" de timetables
          const groups = {};
          data.forEach(item => {
            const className = item.classe?.nom || "unknown";
            const filiereName = item.classe?.filiere || "";
            const semesterVal = (item.semestre && (typeof item.semestre === "string" ? item.semestre : item.semestre.nom)) || item.semestre?.nom || item.semester || "";
            const storedFileName = item.fileName || ""; // ✅ Récupérer le fileName de la BDD

            const key = `${className}__${filiereName}__${semesterVal}`;

            if (!groups[key]) {
              groups[key] = {
                id: key,
                class: className,
                filiere: filiereName,
                semester: semesterVal,
                fileName: storedFileName, // ✅ Stocker le fileName du premier item
                items: []
              };
            }

            // ✅ Si le fileName n'est pas encore défini, utiliser celui de l'item actuel
            if (!groups[key].fileName && storedFileName) {
              groups[key].fileName = storedFileName;
            }

            groups[key].items.push(item);
          });

          const list = Object.values(groups).map(g => ({
            id: g.id,
            class: g.class,
            filiere: g.filiere,
            semester: g.semester,
            items: g.items,
            fileName: g.fileName || `emploi_${g.class}_${g.filiere}_${g.semester}.docx`, // ✅ Utiliser le fileName stocké ou un nom par défaut
            displayName: `${g.class.toUpperCase()}${g.filiere ? ' - ' + g.filiere : ''}${g.semester ? ' - ' + g.semester : ''}`, // ✅ Nom d'affichage
            fileUrl: "",
            uploadDate: g.items[0]?.createdAt || new Date().toISOString(),
            fileSize: "N/A"
          }));

          console.log("Timetables chargés:", list); // ✅ Pour debug

          setTimetables(list);
        }
      } catch (err) {
        console.error("Erreur chargement emplois:", err);
        setTimetables([]);
      }
    };
    fetchTimetables();
  }, []);


  useEffect(() => {
    const fetchTeachers = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/users/teachers");
        if (!res.ok) throw new Error("Erreur chargement profs");

        const teachers = await res.json();

        // Map : profId -> teacher
        const map = {};
        teachers.forEach(t => {
          map[t.id] = t;
        });

        setTeachersMap(map);
      } catch (err) {
        console.error("Erreur fetch teachers:", err);
      }
    };

    fetchTeachers();
  }, []);




  // Données pour les classes et filières (chargées du backend)
  const [backendClasses, setBackendClasses] = useState([]);
  const [backendFilieres, setBackendFilieres] = useState({});

  useEffect(() => {
    const fetchMetadata = async () => {
      try {
        const cRes = await fetch(`http://localhost:8080/emploi/classes`);

        if (!cRes.ok) {
          throw new Error("Erreur chargement classes");
        }

        const cData = await cRes.json();

        const filieresMap = {};
        const classesMap = new Map();

        (cData || []).forEach(c => {
          const className = (c.nom || "").toLowerCase().trim();
          if (!className) return;

          if (!classesMap.has(className)) {
            classesMap.set(className, {
              id: c.id,
              nom: c.nom,
              value: c.nom
            });
          }

          // si la filière est déjà incluse dans classe
          if (c.filiere) {
            if (!filieresMap[className]) {
              filieresMap[className] = [];
            }

            const exists = filieresMap[className].some(
              f => f === c.filiere
            );

            if (!exists) {
              filieresMap[className].push(c.filiere);
            }
          }
        });

        setBackendClasses(Array.from(classesMap.values()));
        setBackendFilieres(filieresMap);

      } catch (err) {
        console.error("Erreur chargement classes/filières:", err);
      }
    };

    fetchMetadata();
  }, []);








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

  const handleEditCell = (dayIndex, slotKey) => {
    const cellData = timetableData[dayIndex][slotKey];
    const data = cellData || { type: "Cours", cours: "", professeur: "", salle: "" };

    setEditingCell({ dayIndex, slotKey });
    setEditingTimetable(viewingTimetable);
    setEditForm({
      type: cellData.type || "Cours",
      cours: cellData.cours || "",
      professeur: cellData.professeur || "",
      salle: cellData.salle || "",
      type: data.type || "Cours",
      cours: data.cours || "",
      professeur: data.professeur || "",
      salle: data.salle || ""
    });
  };

  const generateDocxFromTimetable = async (data, filename = "emploi_modifie.docx") => {
    if (!data || !Array.isArray(data)) {
      throw new Error("Données invalides pour générer le DOCX");
    }

    const rows = [];

    // header
    const headerCells = [
      new DocxTableCell({ children: [new Paragraph({ children: [new TextRun("Jour")] })] }),
      ...TIME_SLOTS.map(slot => new DocxTableCell({ children: [new Paragraph({ children: [new TextRun(slot)] })] }))
    ];
    rows.push(new DocxTableRow({ children: headerCells }));

    // content rows
    data.forEach(row => {
      const cells = [];
      cells.push(new DocxTableCell({ children: [new Paragraph({ children: [new TextRun(row.jour || "")] })] }));

      for (let i = 1; i <= 4; i++) {
        const slot = row[`slot${i}`] || {};
        const paragraphs = [];

        if (slot.type && slot.type !== "Cours") {
          paragraphs.push(new Paragraph({ children: [new TextRun({ text: slot.type, bold: true })] }));
        }

        if (slot.cours) paragraphs.push(new Paragraph({ children: [new TextRun(slot.cours)] }));
        if (slot.professeur) paragraphs.push(new Paragraph({ children: [new TextRun(slot.professeur)] }));
        if (slot.salle) paragraphs.push(new Paragraph({ children: [new TextRun(slot.salle)] }));

        if (paragraphs.length === 0) paragraphs.push(new Paragraph({ children: [new TextRun("")] }));

        cells.push(new DocxTableCell({ children: paragraphs, width: { size: 4000, type: WidthType.DXA } }));
      }

      rows.push(new DocxTableRow({ children: cells }));
    });

    const table = new DocxTable({ rows });

    // ✅ Créer le document avec sections correctement
    const doc = new Document({
      sections: [
        {
          children: [
            new Paragraph({
              children: [new TextRun({ text: "Emploi du temps", bold: true, size: 32 })]
            }),
            new Paragraph({ text: "" }),
            table
          ]
        }
      ]
    });

    // Utiliser toBase64String puis convertir en Blob
    const base64 = await Packer.toBase64String(doc);
    const binary = atob(base64);
    const len = binary.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binary.charCodeAt(i);
    }
    const blob = new Blob([bytes], { type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document" });

    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  };
  // ...existing code...


  const handleSaveEdit = async () => {
    if (!editingCell || !editingTimetable) return;

    try {
      const dayName = timetableData[editingCell.dayIndex].jour;
      const slotIndex = parseInt(editingCell.slotKey.replace('slot', '')) - 1;
      const timeSlot = TIME_SLOTS[slotIndex];

      const creneauFormatted = timeSlot
        .replace(/h/g, ':')
        .replace(/→/g, '-')
        .replace(/\s+/g, '');

      const creneauStart = creneauFormatted.split('-')[0].substring(0, 2);

      // Chercher l'emploi existant
      let emploiToUpdate = editingTimetable.items.find(item =>
        item.jour.toLowerCase() === dayName.toLowerCase() &&
        item.creneau?.heureDebut?.startsWith(creneauStart)
      );

      const updateDTO = {
        matiere: editForm.cours.trim(),
        prof: editForm.professeur.trim(),
        salle: editForm.salle.trim(),
        jour: dayName
      };

      // ✅ Si aucun emploi n'existe pour cette cellule, créer un nouveau
      if (!emploiToUpdate) {
        // Créer un nouvel emploi pour cette cellule vide
        const newEmploiDTO = {
          classe: {
            id: editingTimetable.items[0]?.classe?.id,
            nom: editingTimetable.class
          },
          matiere: { nom: editForm.cours.trim() },
          prof: { nom: editForm.professeur.trim() },
          salle: { nom: editForm.salle.trim() },
          creneau: {
            heureDebut: creneauFormatted.split('-')[0],
            heureFin: creneauFormatted.split('-')[1]
          },
          jour: dayName,
          semestre: editingTimetable.semester
        };

        const createResponse = await fetch(`${BASE_URL}/emploi/create`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(newEmploiDTO)
        });

        if (!createResponse.ok) {
          throw new Error("Erreur lors de la création de l'emploi");
        }

        const createdEmploi = await createResponse.json();
        emploiToUpdate = createdEmploi;
      } else {
        // ✅ Si l'emploi existe, le mettre à jour normalement
        console.log("EMPLOI TO UPDATE =", emploiToUpdate);
        const updateResponse = await fetch(`${BASE_URL}/emploi/${emploiToUpdate.id}/cell`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(updateDTO)
        });

        if (!updateResponse.ok) {
          throw new Error("Erreur lors de la mise à jour");
        }
      }

      // 3. Mettre à jour l'affichage local
      const newTimetableData = [...timetableData];
      newTimetableData[editingCell.dayIndex][editingCell.slotKey] = {
        type: editForm.type || "Cours",
        cours: editForm.cours.trim(),
        professeur: editForm.professeur.trim(),
        salle: editForm.salle.trim()
      };

      setTimetableData(newTimetableData);
      setEditingCell(null);
      setEditForm({ type: "Cours", cours: "", professeur: "", salle: "" });

      // 4. Mettre à jour viewingTimetable.items
      const updatedItems = (editingTimetable.items || []).map(it => {
        if (it.id === emploiToUpdate.id) {
          return {
            ...it,
            matiere: { ...(it.matiere || {}), nom: updateDTO.matiere || editForm.cours },
            prof: { ...(it.prof || {}), nom: updateDTO.prof || editForm.professeur },
            salle: { ...(it.salle || {}), nom: updateDTO.salle || editForm.salle },
            jour: updateDTO.jour || dayName
          };
        }
        return it;
      });

      // ✅ Si c'est un nouvel emploi, l'ajouter à la liste
      if (!editingTimetable.items.find(it => it.id === emploiToUpdate.id)) {
        updatedItems.push(emploiToUpdate);
      }

      const updatedViewing = { ...editingTimetable, items: updatedItems };
      setViewingTimetable(updatedViewing);
      setEditingTimetable(updatedViewing);

    } catch (err) {
      console.error("Erreur lors de la sauvegarde:", err);
      alert("Erreur lors de la sauvegarde des modifications");
    }
  };

  const fetchGroupItems = async (timetable) => {
    try {
      const params = new URLSearchParams({ classe: timetable.class });
      if (timetable.filiere) params.append('filiere', timetable.filiere);
      if (timetable.semester) params.append('semester', timetable.semester);
      const res = await fetch(`${BASE_URL}/emploi/group?${params.toString()}`);
      if (!res.ok) return [];
      return await res.json();
    } catch (err) {
      console.error("Erreur fetchGroupItems:", err);
      return [];
    }
  };




  const handleDelete = (timetable) => {
    setTimetableToDelete(timetable);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    try {
      // ✅ Utiliser l'endpoint /group avec les paramètres
      const params = new URLSearchParams({
        classe: timetableToDelete.class
      });

      if (timetableToDelete.filiere) {
        params.append('filiere', timetableToDelete.filiere);
      }

      if (timetableToDelete.semester) {
        params.append('semester', timetableToDelete.semester);
      }

      const response = await fetch(`${BASE_URL}/emploi/group?${params.toString()}`, {
        method: "DELETE"
      });

      if (response.ok) {
        setTimetables(timetables.filter(t => t.id !== timetableToDelete.id));
        setDeleteDialogOpen(false);
        setTimetableToDelete(null);
      } else {
        throw new Error("Erreur lors de la suppression");
      }
    } catch (err) {
      console.error("Erreur suppression:", err);
      alert("Erreur lors de la suppression de l'emploi du temps");
    }
  };
  const handleDownload = async (timetable) => {
    try {
      // Construire le nom du fichier : Classe_Filiere_Semestre
      const buildFileName = () => {
        const parts = [];
        if (timetable.class) parts.push(timetable.class.toUpperCase());
        if (timetable.filiere) parts.push(timetable.filiere.toUpperCase());
        if (timetable.semester) parts.push(timetable.semester.toUpperCase());

        const baseName = parts.length > 0 ? parts.join("_") : "emploi";
        return `${baseName}.docx`;
      };

      const customFileName = buildFileName();

      // 1) Si la grille modifiée est ouverte pour ce timetable -> générer à partir de timetableData
      if (viewingTimetable && timetable.id === viewingTimetable.id && timetableData) {
        await generateDocxFromTimetable(timetableData, customFileName);
        return;
      }

      // 2) Sinon tenter de récupérer les items du groupe, construire la grille et générer le docx
      const items = await fetchGroupItems(timetable);
      if (items && items.length > 0) {
        const parsed = transformBackendTimetable(items);
        await generateDocxFromTimetable(parsed, customFileName);
        return;
      }

      // 3) Fallback : télécharger le fichier stocké sur le serveur avec le nouveau nom
      if (!timetable.fileName) {
        alert("Aucun fichier associé !");
        return;
      }

      const url = `${BASE_URL}/emploi/${timetable.fileName}/file`;
      const res = await fetch(url);
      if (!res.ok) throw new Error(`Erreur ${res.status}: ${res.statusText}`);
      const blob = await res.blob();

      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = customFileName; // ✅ Utiliser le nom personnalisé
      document.body.appendChild(link);
      link.click();
      link.remove();
      setTimeout(() => URL.revokeObjectURL(link.href), 100);
    } catch (err) {
      console.error("Erreur téléchargement:", err);
      alert(`Erreur lors du téléchargement: ${err.message}`);
    }
  };

  const uploadFile = async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    const res = await fetch('http://localhost:8080/emploi/upload', {
      method: 'POST',
      body: formData 

    });

    const text = await res.text();
    console.log('Upload response:', res.status, text);
  };
  // Fonctions utilitaires pour l'affichage
  const getClassLabel = (classValue) => {
    const cls = backendClasses.find(c => c.value === classValue || c.nom === classValue);
    return cls ? cls.nom || cls.label : classValue?.toUpperCase() || "-";
  };

  const getFiliereLabel = (filiereValue) => {
    if (!filiereValue) return "-";
    return typeof filiereValue === "string" ? filiereValue : String(filiereValue);
  };

  const getSemesterLabel = (semesterValue) => {
    if (!semesterValue) return "-";
    return semesters[semesterValue] || semesterValue;
    const key = String(semesterValue);
    return semesters[key] || semesterValue;


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
  const TimetableCellView = ({ cellData, isEmpty, onEdit }) => {
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
  // Modifier handleView pour afficher des données de démonstration

  const handleView = async (timetable) => {
    setViewingTimetable(timetable);
    setError(null);
    setLoading(true);
    setProgress(0);

    try {
      // Utiliser les items déjà groupés si présents
      let items = timetable.items || [];

      // Construire la grille à partir des entrées du backend
      const parsed = transformBackendTimetable(items);
      setTimetableData(parsed);
      setProgress(100);
    } catch (err) {
      console.log("Erreur pendant la génération de la grille:", err);
      setError("Impossible de générer l'emploi du temps.");
    } finally {
      setLoading(false);
      setTimeout(() => setProgress(0), 500);
    }


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
                            onEdit={() => handleEditCell(i, "slot1")}

                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCellView
                            cellData={row.slot2}
                            isEmpty={!row.slot2 || (!row.slot2.cours && !row.slot2.professeur && !row.slot2.salle)}
                            onEdit={() => handleEditCell(i, "slot2")}
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCellView
                            cellData={row.slot3}
                            isEmpty={!row.slot3 || (!row.slot3.cours && !row.slot3.professeur && !row.slot3.salle)}
                            onEdit={() => handleEditCell(i, "slot3")}
                          />
                        </TableCell>
                        <TableCell className="align-top">
                          <TimetableCellView
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

                    <TableCell>
                      <div className="flex justify-end gap-2">
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleView(timetable)}
                          title="Voir et modifier"
                        >
                          <Edit className="h-4 w-4" />
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


      {/* Dialog d'édition de cellule */}
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