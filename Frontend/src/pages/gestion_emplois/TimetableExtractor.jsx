"use client";

import React, { useState } from "react";
import Tesseract from "tesseract.js";
import * as pdfjsLib from "pdfjs-dist/legacy/build/pdf";

export default function TimetableExtractor() {
  const [timetable, setTimetable] = useState(null);
  const [loading, setLoading] = useState(false);
  const [pdfText, setPdfText] = useState("");

  const DAYS = ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi"];
  const TIME_SLOTS = ["8h30→10h30", "10h45→12h45", "14h → 16h", "16h15→18h15"];

 const handlePdfUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setLoading(true);
    try {
      let imageDataUrl;

      if (file.type === "application/pdf" || file.name.toLowerCase().endsWith(".pdf")) {
        // lire PDF en ArrayBuffer
        const arrayBuffer = await file.arrayBuffer();
        // pdfjs: render première page
        const pdf = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;
        const page = await pdf.getPage(1);
        const viewport = page.getViewport({ scale: 2 }); // augmente la résolution
        const canvas = document.createElement("canvas");
        canvas.width = Math.floor(viewport.width);
        canvas.height = Math.floor(viewport.height);
        const ctx = canvas.getContext("2d", { willReadFrequently: true });
        await page.render({ canvasContext: ctx, viewport }).promise;
        imageDataUrl = canvas.toDataURL("image/png");
      } else {
       // image (png/jpg)
        imageDataUrl = await readFileAsDataUrl(file);
      }

      // prétraitement (grayscale, contraste, redim)
      const preprocessed = await preprocessImage(imageDataUrl);

      // OCR avec options
     const result = await Tesseract.recognize(preprocessed, "fra", {
        logger: m => console.log(m),
        tessedit_pageseg_mode: Tesseract.PSM.SPARSE_TEXT,
     });

      const text = result.data?.text || "";
      console.log("TEXTE OCR:", text);
      setPdfText(text);

      const parsed = parseTimetableByColumns(result.data, text);
      
      console.log("EMPLOI DU TEMPS PARSÉ:", parsed);
      setTimetable(parsed);
    } catch (err) {
      console.error("Erreur OCR:", err);
      alert("Impossible d'extraire le document via OCR.");
    } finally {
      setLoading(false);
    }
  };
   // util: lire fichier en dataURL
  const readFileAsDataUrl = (file) => new Promise((res, rej) => {
    const reader = new FileReader();
    reader.onload = () => res(reader.result);
    reader.onerror = rej;
    reader.readAsDataURL(file);
  });

  // prétraitement canvas: resize large, grayscale, simple threshold/contrast
  const preprocessImage = (dataUrl, targetWidth = 1800) => new Promise((res) => {
    const img = new Image();
    img.onload = () => {
      const scale = Math.min(1, targetWidth / img.width) || 1;
      const w = Math.floor(img.width * scale);
      const h = Math.floor(img.height * scale);
      const c = document.createElement("canvas");
      c.width = w;
      c.height = h;
      const ctx = c.getContext("2d", { willReadFrequently: true });
      ctx.drawImage(img, 0, 0, w, h);

        // récupérer pixels, appliquer grayscale + contraste + léger sharpen via convolution si besoin
      const imageData = ctx.getImageData(0, 0, w, h);
      const d = imageData.data;
      // simple contrast & grayscale
      for (let i = 0; i < d.length; i += 4) {
        const r = d[i], g = d[i+1], b = d[i+2];
        let gray = 0.299*r + 0.587*g + 0.114*b;
        // augmenter contraste
        const contrast = 1.2;
        gray = ((gray - 128) * contrast) + 128;
        d[i] = d[i+1] = d[i+2] = gray;
      }
      ctx.putImageData(imageData, 0, 0);

      // optional: global threshold to improve text contrast
      const imgData2 = ctx.getImageData(0, 0, w, h);
      const d2 = imgData2.data;
      // calculer moyenne rapide
      let sum = 0, cnt = 0;
      for (let i = 0; i < d2.length; i += 4) { sum += d2[i]; cnt++; }
      const mean = sum / cnt;
      const thresh = Math.max(120, mean - 10);
      for (let i = 0; i < d2.length; i += 4) {
        const v = d2[i] > thresh ? 255 : 0;
        d2[i] = d2[i+1] = d2[i+2] = v;
        }
      ctx.putImageData(imgData2, 0, 0);

      res(c.toDataURL("image/png"));
    };
    img.onerror = () => res(dataUrl); // fallback
    img.src = dataUrl;
  });

  // parsing robuste: normaliser, splitter, heuristiques
 const parseTimetableByColumns = (ocrData, rawText) => {
    const text = rawText.toLowerCase();
    
    // Initialiser grille vide
    const timetable = DAYS.map(day => ({ 
      jour: capitalize(day), 
      slot1: "", 
      slot2: "", 
      slot3: "", 
      slot4: "" 
    }));
// Essayer de détecter ordre des horaires et jours via le texte brut
    const lines = text.split("\n").map(l => l.trim()).filter(l => l.length > 2);
    
    // Grouper par bloc logique (jour + ses 4 slots)
    let dayBlocks = [];
    let currentBlock = {};

    lines.forEach(line => {
      // Détecter jour
      const dayMatch = DAYS.find(d => line.includes(d));
      if (dayMatch) {
        if (Object.keys(currentBlock).length > 0) dayBlocks.push(currentBlock);
        currentBlock = { jour: dayMatch, content: [] };
      } else if (Object.keys(currentBlock).length > 0) {
        currentBlock.content.push(line);
      }
    });
  if (Object.keys(currentBlock).length > 0) dayBlocks.push(currentBlock);

    // Pour chaque bloc jour, splitter en 4 slots
    dayBlocks.forEach(block => {
      const dayIdx = DAYS.indexOf(block.jour);
      if (dayIdx === -1) return;

      // Joindre contenu et splitter par détecteurs d'horaire
      const fullContent = block.content.join(" ");
      
      // Splitter heuristique : chercher patterns "8h30", "10h45", etc.
      const slotRegex = /(8h30|8h:30|10h45|10h:45|14h|16h15|16h:15)/gi;
      const matches = [];
      let match;
      while ((match = slotRegex.exec(fullContent)) !== null) {
        matches.push({ time: match[0].toLowerCase(), idx: match.index });
      }
    // Extraire contenu pour chaque slot
      for (let slotNum = 0; slotNum < 4; slotNum++) {
        let slotStart = matches[slotNum]?.idx || 0;
        let slotEnd = matches[slotNum + 1]?.idx || fullContent.length;
        let slotContent = fullContent.substring(slotStart, slotEnd);

        // Nettoyer et limiter à première phrase/nom de cours
        slotContent = slotContent
          .replace(/^(8h|10h|14h|16h)[^a-z]*/i, "")
          .split(/pr\.|d'|salle|d'information/i)[0]
          .trim();

        if (slotContent.length > 3) {
          const key = `slot${slotNum + 1}`;
          timetable[dayIdx][key] = capitalize(slotContent.split(/\s{2,}/)[0]);
        }
      }
    });

    return timetable;
  };





  const capitalize = (s) => s && s.length ? s.charAt(0).toUpperCase() + s.slice(1) : s;



  return (
    <div style={{ padding: 20, fontFamily: "Arial, sans-serif", maxWidth: 1200, margin: "0 auto" }}>
      <h1 style={{ color: "#2c3e50", marginBottom: 20, textAlign: "center" }}>
        📅 Extractor d'Emploi du Temps - Génie Électrique (OCR)
      </h1>

      <div style={{ marginBottom: 30, padding: 25, backgroundColor: "#f8f9fa", borderRadius: 10, border: "2px dashed #dee2e6", textAlign: "center" }}>
        <h3 style={{ color: "#495057", marginBottom: 15 }}>
          Téléchargez votre emploi du temps au format PDF ou image
        </h3>
        <input
          type="file"
          accept=".pdf,image/*"
          onChange={handlePdfUpload}
          style={{ padding: 15, border: "2px solid #007bff", borderRadius: 8, width: "100%", maxWidth: 400, cursor: "pointer", backgroundColor: "#fff", margin: "0 auto", display: "block" }}
        />
      </div>

      {loading && <p style={{ color: "#007bff" }}>Extraction OCR en cours…</p>}

      {timetable && (
        <table style={{ width: "100%", borderCollapse: "collapse", marginTop: 20 }}>
          <thead>
            <tr style={{ backgroundColor: "#2c3e50", color: "white" }}>
              <th style={{ padding: 10 }}>Jour</th>
              {TIME_SLOTS.map((slot, i) => <th key={i} style={{ padding: 10 }}>{slot}</th>)}
            </tr>
          </thead>
          <tbody>
            {timetable.map((row, i) => (
              <tr key={i} style={{ backgroundColor: i % 2 === 0 ? "#fff" : "#f1f3f5" }}>
                <td style={{ padding: 10, fontWeight: "bold" }}>{row.jour}</td>
                <td style={{ padding: 10 }}>{row.slot1 || "Pas de cours"}</td>
                <td style={{ padding: 10 }}>{row.slot2 || "Pas de cours"}</td>
                <td style={{ padding: 10 }}>{row.slot3 || "Pas de cours"}</td>
                <td style={{ padding: 10 }}>{row.slot4 || "Pas de cours"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {pdfText && (
        <details style={{ marginTop: 20 }}>
          <summary>📄 Afficher le texte OCR extrait</summary>
          <pre style={{ maxHeight: 300, overflow: "auto", backgroundColor: "#f8f9fa", padding: 10 }}>{pdfText}</pre>
        </details>
      )}
    </div>
  );
}
