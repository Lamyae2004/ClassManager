"use client";
import React, { useState } from "react";
import {
  classes, etudiants, emploi, seances, absences, matieres, creneaux, salles
} from "./data";
import {
  Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter
} from "@/components/ui/card";
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import {
  Calendar, Clock, Building, BookOpen, Users, CheckCircle, XCircle, Search,
  FileText, CalendarDays, ChevronRight, Info, FileCheck, FileX, FileQuestion,
  Download, Eye, AlertCircle
} from "lucide-react";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "@/components/ui/hover-card";

export default function HistoriqueAbsences({ role = "admin", currentProfId = null }) {
  const [classe, setClasse] = useState(null);
  const [matiere, setMatiere] = useState(null);
  const [searchStudent, setSearchStudent] = useState("");

  // Filtrer les séances selon la classe, matière et rôle
  const seancesClasse = seances
    .filter(s => {
      const edt = emploi.find(e => e.id_edt === s.id_edt);
      if (!edt) return false;
      if (edt.id_classe !== Number(classe)) return false;
      if (matiere && edt.id_matiere !== Number(matiere)) return false;
      if (role === "prof" && edt.id_prof !== currentProfId) return false;
      return true;
    })
    .sort((a, b) => new Date(b.date_seance) - new Date(a.date_seance));

  // Filtrer les étudiants selon la classe et le rôle
  const etudiantsClasse = etudiants
    .filter(e => e.id_classe === Number(classe))
    .filter(e => role === "prof" ? emploi.some(emp => emp.id_classe === Number(classe) && emp.id_prof === currentProfId) : true)
    .filter(e => {
      if (!searchStudent) return true;
      const search = searchStudent.toLowerCase();
      return e.nom.toLowerCase().startsWith(search) || e.prenom.toLowerCase().startsWith(search);
    });

  // Statistiques globales
  const stats = {
    totalSeances: seancesClasse.length,
    totalAbsences: seancesClasse.reduce((total, seance) => {
      const absencesSeance = absences.filter(a => a.id_seance === seance.id_seance && !a.present);
      return total + absencesSeance.length;
    }, 0),
    etudiantsAbsents: new Set(
      seancesClasse.flatMap(seance =>
        absences
          .filter(a => a.id_seance === seance.id_seance && !a.present)
          .map(a => a.id_etudiant)
      )
    ).size
  };

  // Fonction pour formater la justification
  const renderJustification = (absence) => {
    if (absence.present) {
      return (
        <div className="flex items-center justify-center">
          <Badge 
            variant="outline" 
            className="bg-green-50 text-green-700 border-green-200 px-3 py-1"
          >
            <CheckCircle className="h-3 w-3 mr-1" />
            Présent
          </Badge>
        </div>
      );
    }

    if (absence.justifie) {
      const hasFile = absence.justificatif && absence.justificatif.trim() !== "";
      
      return (
        <div className="flex items-center justify-center">
          <div className="flex flex-col items-center gap-2">
            <Badge className="bg-emerald-100 text-emerald-800 hover:bg-emerald-100 border-emerald-200 px-3 py-1">
              <div className="flex items-center">
                <FileCheck className="h-3 w-3 mr-1" />
                Justifié
              </div>
            </Badge>
            
            {hasFile && (
              <div className="flex items-center gap-2 mt-1">
                <TooltipProvider>
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="h-8 px-2 text-xs"
                        onClick={() => window.open(`/${absence.justificatif}`, '_blank')}
                      >
                        <Eye className="h-3 w-3 mr-1" />
                        Voir
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>
                      <p>Voir le justificatif</p>
                    </TooltipContent>
                  </Tooltip>
                </TooltipProvider>
                
                <TooltipProvider>
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="h-8 px-2 text-xs"
                        onClick={() => {
                          // Télécharger le fichier
                          const link = document.createElement('a');
                          link.href = `/${absence.justificatif}`;
                          link.download = `justificatif_${absence.id_absence}.pdf`;
                          link.click();
                        }}
                      >
                        <Download className="h-3 w-3 mr-1" />
                        Télécharger
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>
                      <p>Télécharger le justificatif</p>
                    </TooltipContent>
                  </Tooltip>
                </TooltipProvider>
              </div>
            )}
            
            {!hasFile && (
              <HoverCard>
                <HoverCardTrigger asChild>
                  <div className="cursor-help">
                    <Info className="h-3 w-3 text-emerald-600" />
                  </div>
                </HoverCardTrigger>
                <HoverCardContent className="w-80">
                  <div className="flex justify-between space-x-4">
                    <div className="space-y-1">
                      <h4 className="text-sm font-semibold">Justification manuelle</h4>
                      <p className="text-sm text-gray-600">
                        L'absence a été justifiée par l'administration sans pièce jointe.
                      </p>
                    </div>
                  </div>
                </HoverCardContent>
              </HoverCard>
            )}
          </div>
        </div>
      );
    }

    // Non justifié
    return (
      <div className="flex items-center justify-center">
        <div className="flex flex-col items-center gap-2">
          <Badge variant="outline" className="bg-red-50 text-red-700 border-red-300 px-3 py-1">
            <div className="flex items-center">
              <FileX className="h-3 w-3 mr-1" />
              Non justifié
            </div>
          </Badge>
          
          {/* Option: Bouton pour ajouter un justificatif (si admin/prof) */}
          {(role === "admin" || role === "prof") && (
            <Button
              variant="outline"
              size="sm"
              className="h-7 px-2 text-xs border-dashed"
              onClick={() => {
                // Logique pour ajouter un justificatif
                console.log("Ajouter justificatif pour:", absence.id_absence);
              }}
            >
              <FileCheck className="h-3 w-3 mr-1" />
              Justifier
            </Button>
          )}
        </div>
      </div>
    );
  };

  // Version alternative plus compacte pour la colonne
  const renderJustificationCompact = (absence) => {
    if (absence.present) {
      return (
        <div className="flex items-center justify-center">
          <Badge 
            variant="outline" 
            className="bg-green-50 text-green-700 border-green-200 px-2 py-0.5"
          >
            <CheckCircle className="h-3 w-3" />
          </Badge>
        </div>
      );
    }

    if (absence.justifie) {
      const hasFile = absence.justificatif && absence.justificatif.trim() !== "";
      
      return (
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger asChild>
              <div className="flex items-center justify-center">
                {hasFile ? (
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-7 w-7 p-0 bg-emerald-50 text-emerald-700 hover:bg-emerald-100"
                  >
                    <FileCheck className="h-4 w-4" />
                  </Button>
                ) : (
                  <Badge className="bg-emerald-100 text-emerald-800 hover:bg-emerald-100 border-emerald-200 px-2 py-0.5">
                    <FileCheck className="h-3 w-3" />
                  </Badge>
                )}
              </div>
            </TooltipTrigger>
            <TooltipContent>
              <div className="space-y-1">
                <p className="font-medium">Absence justifiée</p>
                {hasFile ? (
                  <div className="flex items-center gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      className="h-6 px-2 text-xs"
                      onClick={() => window.open(`/${absence.justificatif}`, '_blank')}
                    >
                      Voir
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      className="h-6 px-2 text-xs"
                      onClick={() => {
                        const link = document.createElement('a');
                        link.href = `/${absence.justificatif}`;
                        link.download = `justificatif_${absence.id_absence}.pdf`;
                        link.click();
                      }}
                    >
                      Télécharger
                    </Button>
                  </div>
                ) : (
                  <p className="text-sm text-gray-600">Justification manuelle</p>
                )}
              </div>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      );
    }

    // Non justifié
    return (
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger asChild>
            <div className="flex items-center justify-center">
              <Badge variant="outline" className="bg-red-50 text-red-700 border-red-300 px-2 py-0.5">
                <FileX className="h-3 w-3" />
              </Badge>
            </div>
          </TooltipTrigger>
          <TooltipContent>
            <div className="space-y-2">
              <p className="font-medium">Absence non justifiée</p>
              {(role === "admin" || role === "prof") && (
                <Button
                  variant="outline"
                  size="sm"
                  className="h-6 px-2 text-xs w-full"
                  onClick={() => {
                    console.log("Ajouter justificatif pour:", absence.id_absence);
                  }}
                >
                  <FileCheck className="h-3 w-3 mr-1" />
                  Justifier maintenant
                </Button>
              )}
            </div>
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>
    );
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-blue-50 p-4 md:p-6">
      <div className="max-w-6xl mx-auto space-y-6">
        {/* En-tête */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold tracking-tight text-gray-900">
                Historique des absences
              </h1>
            </div>
            <FileText className="h-10 w-10 text-blue-600" />
          </div>
          <Separator />
        </div>

        {/* Carte de sélection et statistiques */}
        <div className="grid gap-6 md:grid-cols-3">
          {/* Sélection de classe, matière et étudiant */}
          <Card className="md:col-span-2">
            <CardHeader>
              <CardTitle>Sélection</CardTitle>
              <CardDescription>Choisissez une classe, un module et un étudiant</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* Classe */}
              <div className="space-y-3">
                <Label htmlFor="classe-select">Classe</Label>
                <Select onValueChange={(val) => { setClasse(val); setMatiere(null); setSearchStudent(""); }} value={classe || ""}>
                  <SelectTrigger id="classe-select" className="w-full">
                    <SelectValue placeholder="Sélectionner une classe" />
                  </SelectTrigger>
                  <SelectContent>
                    {classes.map(c => (
                      <SelectItem key={c.id} value={c.id.toString()}>
                        {c.nom}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              {/* Module */}
              {classe && (
                <div className="space-y-3">
                  <Label htmlFor="matiere-select">Module</Label>
                  <Select onValueChange={setMatiere} value={matiere || ""}>
                    <SelectTrigger id="matiere-select" className="w-full">
                      <SelectValue placeholder="Sélectionner un module" />
                    </SelectTrigger>
                    <SelectContent>
                      {emploi
                        .filter(e => e.id_classe === Number(classe) && (role !== "prof" || e.id_prof === currentProfId))
                        .map(e => {
                          const m = matieres.find(m => m.id_matiere === e.id_matiere);
                          return (
                            <SelectItem key={e.id_edt} value={e.id_matiere.toString()}>
                              {m?.nom_matiere}
                            </SelectItem>
                          );
                        })}
                    </SelectContent>
                  </Select>
                </div>
              )}

              {/* Étudiant */}
              {classe && (
                <div className="space-y-3">
                  <Label htmlFor="student-search">Étudiant</Label>
                  <input
                    type="text"
                    id="student-search"
                    placeholder="Tapez le début du nom"
                    className="w-full border rounded p-2"
                    value={searchStudent}
                    onChange={(e) => setSearchStudent(e.target.value)}
                  />
                  {searchStudent && etudiantsClasse.length > 0 && (
                    <div className="border rounded mt-1 bg-white max-h-40 overflow-y-auto">
                      {etudiantsClasse.map(e => (
                        <div
                          key={e.id_etudiant}
                          className="p-2 hover:bg-blue-50 cursor-pointer"
                          onClick={() => setSearchStudent(`${e.nom} ${e.prenom}`)}
                        >
                          {e.nom} {e.prenom}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}

              {classe && seancesClasse.length === 0 && (
                <div className="rounded-lg border border-dashed border-gray-300 p-8 text-center">
                  <Search className="h-12 w-12 mx-auto text-gray-400 mb-4" />
                  <h3 className="font-semibold text-gray-900 mb-2">Aucune séance trouvée</h3>
                  <p className="text-gray-600">Aucune séance enregistrée pour cette sélection.</p>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Statistiques */}
          <Card>
            <CardHeader>
              <CardTitle>Statistiques</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {classe ? (
                <>
                  <div className="grid grid-cols-2 gap-3">
                    <div className="rounded-lg bg-blue-50 p-4 text-center border border-blue-100">
                      <div className="text-2xl font-bold text-blue-600">{stats.totalSeances}</div>
                      <div className="text-sm text-blue-700">Séances</div>
                    </div>
                    <div className="rounded-lg bg-red-50 p-4 text-center border border-red-100">
                      <div className="text-2xl font-bold text-red-600">{stats.totalAbsences}</div>
                      <div className="text-sm text-red-700">Absences totales</div>
                    </div>
                  </div>
                  <div className="rounded-lg bg-amber-50 p-4 text-center border border-amber-100">
                    <div className="text-2xl font-bold text-amber-600">{stats.etudiantsAbsents}</div>
                    <div className="text-sm text-amber-700">Étudiants absents</div>
                  </div>
                </>
              ) : (
                <div className="text-center py-8 text-gray-500">
                  <Users className="h-12 w-12 mx-auto mb-4 opacity-50" />
                  <p>Sélectionnez une classe pour voir les statistiques</p>
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Liste des séances */}
        {classe && seancesClasse.length > 0 && (
          <div className="space-y-6">
            <div className="flex items-center justify-between">
              <h2 className="text-xl font-semibold text-gray-900">
                Séances enregistrées ({seancesClasse.length})
              </h2>
              <Badge variant="outline" className="flex items-center gap-2">
                <CalendarDays className="h-3 w-3" />
                Du plus récent au plus ancien
              </Badge>
            </div>

            {seancesClasse.map(seance => {
              const edt = emploi.find(e => e.id_edt === seance.id_edt);
              const matiere = matieres.find(m => m.id_matiere === edt?.id_matiere);
              const creneau = creneaux.find(c => c.id === edt?.id_creneau);
              const salle = salles.find(s => s.id_salle === edt?.id_salle);
              let absencesSeance = absences.filter(a => a.id_seance === seance.id_seance);

              // Appliquer filtre étudiant
              if (searchStudent) {
                absencesSeance = absencesSeance.filter(a => {
                  const e = etudiants.find(et => et.id_etudiant === a.id_etudiant);
                  return `${e.nom} ${e.prenom}`.toLowerCase().startsWith(searchStudent.toLowerCase());
                });
              }

              const absentsCount = absencesSeance.filter(a => !a.present).length;
              const presentsCount = absencesSeance.filter(a => a.present).length;
              const dateFormatee = new Date(seance.date_seance).toLocaleDateString('fr-FR', {
                weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
              });

              return (
                <Card key={seance.id_seance} className="overflow-hidden">
                  <CardHeader className="bg-gradient-to-r from-blue-50 to-white">
                    <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                      <div className="space-y-2">
                        <div className="flex items-center gap-3">
                          <Badge className="bg-blue-100 text-blue-700 border-blue-200 hover:bg-blue-100">
                            <BookOpen className="h-3 w-3 mr-1" />
                            {matiere?.nom_matiere || "Matière inconnue"}
                          </Badge>
                          <Badge variant="outline" className="flex items-center gap-1">
                            <Building className="h-3 w-3" />
                            {salle?.nom_salle || "Salle inconnue"}
                          </Badge>
                        </div>
                        <div className="flex items-center gap-4 text-sm text-gray-600">
                          <span className="flex items-center gap-1">
                            <Calendar className="h-3 w-3" />
                            {dateFormatee}
                          </span>
                          <span className="flex items-center gap-1">
                            <Clock className="h-3 w-3" />
                            {creneau ? `${creneau.debut} - ${creneau.fin}` : "Créneau inconnu"}
                          </span>
                        </div>
                      </div>
                      <div className="flex gap-3">
                        <Badge variant="outline" className="bg-green-50 text-green-700 border-green-200">
                          <CheckCircle className="h-3 w-3 mr-1" />
                          {presentsCount} présents
                        </Badge>
                        <Badge variant="outline" className="bg-red-50 text-red-700 border-red-200">
                          <XCircle className="h-3 w-3 mr-1" />
                          {absentsCount} absents
                        </Badge>
                      </div>
                    </div>
                  </CardHeader>

                  <CardContent className="p-0">
                    <div className="overflow-x-auto">
                      <Table>
                        <TableHeader>
                          <TableRow className="bg-gray-50">
                            <TableHead className="w-12 text-center">#</TableHead>
                            <TableHead>Étudiant</TableHead>
                            <TableHead className="text-center">Statut</TableHead>
                            <TableHead className="text-center">Justification</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {absencesSeance.map((absence, index) => {
                            const etu = etudiants.find(e => e.id_etudiant === absence.id_etudiant);
                            return (
                              <TableRow key={absence.id_absence} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                                <TableCell className="text-center font-medium text-gray-900">
                                  {index + 1}
                                </TableCell>
                                <TableCell>
                                  <div className="font-medium text-gray-900">
                                    {etu?.nom} {etu?.prenom}
                                  </div>
                                </TableCell>
                                <TableCell className="text-center">
                                  {absence.present ? (
                                    <Badge className="bg-green-100 text-green-800 hover:bg-green-100 border border-green-200">
                                      <CheckCircle className="h-3 w-3 mr-1" />
                                      Présent
                                    </Badge>
                                  ) : (
                                    <Badge variant="outline" className="text-red-600 border-red-300 bg-red-50">
                                      <XCircle className="h-3 w-3 mr-1" />
                                      Absent
                                    </Badge>
                                  )}
                                </TableCell>
                                <TableCell className="text-center">
                                
                                  {renderJustificationCompact(absence)}
                                 
                                </TableCell>
                              </TableRow>
                            );
                          })}
                        </TableBody>
                      </Table>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}