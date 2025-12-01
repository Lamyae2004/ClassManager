"use client";

import * as React from "react";
import { useState } from "react";
import { FileText, Calendar, Edit, Trash2, Eye, Plus, Download } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
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
  }
];

export function TimetableList() {
  const [timetables, setTimetables] = useState(initialTimetables);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [timetableToDelete, setTimetableToDelete] = useState(null);

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

  const handleView = (timetable) => {
    // Ouvrir le PDF dans un nouvel onglet
    window.open(timetable.fileUrl, '_blank');
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