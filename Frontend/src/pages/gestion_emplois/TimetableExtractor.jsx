"use client";


import React, { useState, useEffect } from "react";

import * as pdfjsLib from "pdfjs-dist/legacy/build/pdf";
import pdfjsWorker from "pdfjs-dist/legacy/build/pdf.worker.min?url";

export default function TimetableExtractor() {
  const [timetable, setTimetable] = useState(null);
  const [editingCell, setEditingCell] = useState(null);
  const [editValue, setEditValue] = useState("");
  const [loading, setLoading] = useState(false);
  const [pdfText, setPdfText] = useState("");

  const DAYS = ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi"];
  const TIME_SLOTS = ["8h30→10h30", "10h45→12h45", "14h → 16h", "16h15→18h15"];

 useEffect(() => {
    pdfjsLib.GlobalWorkerOptions.workerSrc = pdfjsWorker;
  }, []);


  const handlePdfUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setLoading(true);
    try {
      const arrayBuffer = await file.arrayBuffer();
      const pdf = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;
      
      let fullText = "";
      for (let i = 1; i <= pdf.numPages; i++) {
        const page = await pdf.getPage(i);
        const textContent = await page.getTextContent();
        const pageText = textContent.items.map(item => item.str).join(" ");
        fullText += pageText + "\n";
      }

      console.log("TEXTE PDF BRUT:", fullText);
      setPdfText(fullText);

      // Parser le texte structuré
      const parsed = parseTimetableFromText(fullText);
      console.log("EMPLOI DU TEMPS PARSÉ:", parsed);
      setTimetable(parsed);

    } catch (err) {
      console.error("Erreur PDF:", err);
      alert("Erreur lors de l'extraction du PDF.");
    } finally {
      setLoading(false);
    }
  };

  // Parser intelligent pour structure : Jour / Horaire / Cours
  const parseTimetableFromText = (rawText) => {
    const text = rawText.toLowerCase();
    const lines = text.split("\n").map(l => l.trim()).filter(l => l.length > 1);

    // Initialiser grille
    const timetable = DAYS.map(day => ({ 
      jour: capitalize(day), 
      slot1: "", 
      slot2: "", 
      slot3: "", 
      slot4: "" 
    }));

    const TIME_SLOTS_REGEX = [
      { slot: 1, regex: /8h30\s*→?\s*10h30/ },
      { slot: 2, regex: /10h45\s*→?\s*12h45/ },
      { slot: 3, regex: /14h\s*→?\s*16h/ },
      { slot: 4, regex: /16h15\s*→?\s*18h15/ }
    ];

    // Étape 1 : splitter par horaire
    let sections = [];
    let currentSlot = 0;
    let currentContent = [];

    lines.forEach(line => {
      // Vérifier si c'est un horaire
      let foundSlot = false;
      TIME_SLOTS_REGEX.forEach(({ slot, regex }) => {
        if (regex.test(line)) {
          if (currentContent.length > 0) {
            sections.push({ slot: currentSlot, content: currentContent });
          }
          currentSlot = slot;
          currentContent = [];
          foundSlot = true;
        }
      });

      if (!foundSlot && line.length > 1) {
        currentContent.push(line);
      }
    });
    if (currentContent.length > 0) {
      sections.push({ slot: currentSlot, content: currentContent });
    }

    console.log("Sections par horaire:", sections);

    // Étape 2 : pour chaque section, extraire jour + cours
    sections.forEach(({ slot, content }) => {
      let currentDayIdx = -1;
      let courseBuffer = [];

      content.forEach(line => {
        // Détecter jour
        const dayMatch = DAYS.find(d => line.includes(d));
        if (dayMatch) {
          // Sauvegarder cours du jour précédent
          if (courseBuffer.length > 0 && currentDayIdx !== -1) {
            const courseText = cleanCourseText(courseBuffer.join(" "));
            if (courseText.length > 2) {
              const key = `slot${slot}`;
              timetable[currentDayIdx][key] = courseText;
            }
            courseBuffer = [];
          }
          currentDayIdx = DAYS.indexOf(dayMatch);
          return;
        }

        // Ignorer lignes vides, prof, salle, etc.
        if (line && !line.startsWith("pr.") && !line.includes("salle") && line.length > 2) {
          courseBuffer.push(line);
        }
      });

      // Flush dernier jour
      if (courseBuffer.length > 0 && currentDayIdx !== -1) {
        const courseText = cleanCourseText(courseBuffer.join(" "));
        if (courseText.length > 2) {
          const key = `slot${slot}`;
          timetable[currentDayIdx][key] = courseText;
        }
      }
    });

    return timetable;
  };

  // Nettoyer le texte de cours
  const cleanCourseText = (text) => {
    return text
      .replace(/cours|td|tp|td\/tp|cours td\/tp/gi, "")
      .replace(/\(.*?\)/g, "") // enlever parenthèses
      .replace(/pr\.|salle|d'information/gi, "")
      .trim()
      .split(/\s{2,}/)[0]; // première partie
  };

  const readFileAsDataUrl = (file) =>
    new Promise((res, rej) => {
      const reader = new FileReader();
      reader.onload = () => res(reader.result);
      reader.onerror = rej;
      reader.readAsDataURL(file);
    });

  const capitalize = (s) => s && s.length ? s.charAt(0).toUpperCase() + s.slice(1) : s;

  // Éditer une cellule
  const handleCellClick = (dayIdx, slotKey) => {
    setEditingCell({ dayIdx, slotKey });
    setEditValue(timetable[dayIdx][slotKey] || "");
  };

  // Sauvegarder l'édition
  const handleSaveEdit = () => {
    if (!editingCell) return;
    const newTimetable = [...timetable];
    newTimetable[editingCell.dayIdx][editingCell.slotKey] = editValue;
    setTimetable(newTimetable);
    setEditingCell(null);
    setEditValue("");
  };

  // Exporter en JSON
  const handleExport = () => {
    const dataStr = JSON.stringify(timetable, null, 2);
    const dataBlob = new Blob([dataStr], { type: "application/json" });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "emploi_du_temps.json";
    link.click();
  };

  return (
    <div style={{ padding: 20, fontFamily: "Arial, sans-serif", maxWidth: 1400, margin: "0 auto" }}>
      <h1 style={{ color: "#2c3e50", marginBottom: 20, textAlign: "center" }}>
        📅 Extracteur d'Emploi du Temps (PDF Structuré)
      </h1>

      <div style={{ marginBottom: 30, padding: 25, backgroundColor: "#f8f9fa", borderRadius: 10, border: "2px dashed #dee2e6", textAlign: "center" }}>
        <h3 style={{ color: "#495057", marginBottom: 15 }}>Téléchargez votre emploi du temps (PDF)</h3>
        <input
          type="file"
          accept=".pdf"
          onChange={handlePdfUpload}
          disabled={loading}
          style={{ padding: 15, border: "2px solid #007bff", borderRadius: 8, width: "100%", maxWidth: 400, cursor: "pointer", backgroundColor: "#fff", display: "block", margin: "0 auto" }}
        />
      </div>

      {loading && <p style={{ color: "#007bff", textAlign: "center" }}>⏳ Extraction en cours…</p>}

      {editingCell && (
        <div style={{ marginBottom: 20, padding: 15, backgroundColor: "#fff3cd", border: "1px solid #ffc107", borderRadius: 5 }}>
          <label>Éditer : <strong>{DAYS[editingCell.dayIdx].toUpperCase()} - {TIME_SLOTS[Object.keys(timetable[0]).indexOf(editingCell.slotKey) - 1]}</strong></label>
          <textarea
            value={editValue}
            onChange={(e) => setEditValue(e.target.value)}
            style={{ width: "100%", padding: 8, marginTop: 10, marginBottom: 10, borderRadius: 4, border: "1px solid #ffc107", minHeight: "80px" }}
            placeholder="Entrez le nom du cours"
          />
          <button onClick={handleSaveEdit} style={{ padding: "8px 15px", backgroundColor: "#28a745", color: "white", border: "none", borderRadius: 4, cursor: "pointer", marginRight: 10 }}>
            ✓ Sauvegarder
          </button>
          <button onClick={() => setEditingCell(null)} style={{ padding: "8px 15px", backgroundColor: "#dc3545", color: "white", border: "none", borderRadius: 4, cursor: "pointer" }}>
            ✗ Annuler
          </button>
        </div>
      )}

      {timetable && (
        <>
          <table style={{ width: "100%", borderCollapse: "collapse", marginTop: 20, boxShadow: "0 2px 8px rgba(0,0,0,0.1)" }}>
            <thead>
              <tr style={{ backgroundColor: "#2c3e50", color: "white" }}>
                <th style={{ padding: 12, textAlign: "left" }}>Jour</th>
                {TIME_SLOTS.map((slot, i) => <th key={i} style={{ padding: 12, textAlign: "center", fontSize: "12px" }}>{slot}</th>)}
              </tr>
            </thead>
            <tbody>
              {timetable.map((row, dayIdx) => (
                <tr key={dayIdx} style={{ backgroundColor: dayIdx % 2 === 0 ? "#fff" : "#f8f9fa", borderBottom: "1px solid #dee2e6" }}>
                  <td style={{ padding: 12, fontWeight: "bold", color: "#2c3e50" }}>{row.jour}</td>
                  {[1, 2, 3, 4].map((slotNum) => {
                    const slotKey = `slot${slotNum}`;
                    return (
                      <td
                        key={slotKey}
                        onClick={() => handleCellClick(dayIdx, slotKey)}
                        style={{
                          padding: 12,
                          textAlign: "center",
                          cursor: "pointer",
                          backgroundColor: editingCell?.dayIdx === dayIdx && editingCell?.slotKey === slotKey ? "#e7f3ff" : "inherit",
                          border: editingCell?.dayIdx === dayIdx && editingCell?.slotKey === slotKey ? "2px solid #007bff" : "1px solid #dee2e6",
                          minHeight: "80px",
                          verticalAlign: "middle",
                          fontSize: "12px",
                          transition: "all 0.2s",
                          whiteSpace: "pre-wrap",
                          wordBreak: "break-word"
                        }}
                      >
                        {row[slotKey] || <span style={{ color: "#999" }}>Cliquer pour ajouter</span>}
                      </td>
                    );
                  })}
                </tr>
              ))}
            </tbody>
          </table>

          <div style={{ marginTop: 20, textAlign: "center" }}>
            <button
              onClick={handleExport}
              style={{
                padding: "12px 25px",
                backgroundColor: "#007bff",
                color: "white",
                border: "none",
                borderRadius: 5,
                cursor: "pointer",
                fontSize: "16px",
                fontWeight: "bold"
              }}
            >
              💾 Exporter en JSON
            </button>
          </div>
        </>
      )}

      {pdfText && (
        <details style={{ marginTop: 30 }}>
          <summary style={{ cursor: "pointer", fontWeight: "bold", color: "#2c3e50" }}>📄 Afficher le texte PDF extrait</summary>
          <pre style={{ maxHeight: 300, overflow: "auto", backgroundColor: "#f8f9fa", padding: 15, marginTop: 10, borderRadius: 5, border: "1px solid #dee2e6", fontSize: "11px" }}>{pdfText}</pre>
        </details>
      )}
    </div>
  );
}