"use client";

import * as React from "react";
import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Upload, FileText, Calendar, School, BookOpen, ArrowLeft } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Alert, AlertDescription } from "@/components/ui/alert";

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
  const [isUploading, setIsUploading] = useState(false);
  const [uploadStatus, setUploadStatus] = useState("");
  const [isEditMode, setIsEditMode] = useState(false);

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
        console.log("Chargement des données pour l'édition:", timetable);
      } else {
        console.warn("Aucun emploi du temps trouvé avec l'ID:", timetableId);
      }
    }
  }, [id]);

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file && file.type === "application/pdf") {
      setSelectedFile(file);
      setUploadStatus("");
    } else {
      setSelectedFile(null);
      setUploadStatus("Veuillez sélectionner un fichier PDF valide.");
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

    

      <Card className="w-full max-w-2xl mx-auto">
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
          {/* Zone d'upload de fichier */}
          <div className="space-y-2">
            <Label htmlFor="file-upload">
              Fichier PDF {isEditMode && "(Optionnel)"}
            </Label>
            <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center hover:border-gray-400 transition-colors">
              <Input
                id="file-upload"
                type="file"
                accept=".pdf"
                onChange={handleFileChange}
                className="hidden"
              />
              <Label htmlFor="file-upload" className="cursor-pointer">
                <FileText className="mx-auto h-12 w-12 text-gray-400 mb-2" />
                <div className="text-sm text-gray-600">
                  {selectedFile ? (
                    <span className="text-green-600 font-medium">
                      {selectedFile.name}
                    </span>
                  ) : isEditMode ? (
                    <div>
                      <span className="font-medium">
                        Cliquez pour changer le fichier (optionnel)
                      </span>
                      <br />
                      <span className="text-xs text-muted-foreground">
                        Fichier actuel: {sampleTimetables[id]?.fileName}
                      </span>
                    </div>
                  ) : (
                    <>
                      <span className="font-medium">Cliquez pour uploader</span>
                      <br />
                      <span>ou glissez-déposez un fichier PDF</span>
                    </>
                  )}
                </div>
              </Label>
            </div>
            {isEditMode && (
              <p className="text-xs text-muted-foreground">
                Laissez vide pour conserver le fichier actuel.
              </p>
            )}
          </div>

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
    </div>
  );
}