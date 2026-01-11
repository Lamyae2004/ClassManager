"use client";
import React, { useState, useEffect,useContext  } from "react";
import { AuthContext } from "@/context/AuthContext";
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

export default function HistoriqueAbsences() {
  const [classe, setClasse] = useState(null);
  const [matiere, setMatiere] = useState(null);
  const [searchStudent, setSearchStudent] = useState("");
  const [classes, setClasses] = useState([]);


  const [seances, setSeances] = useState([]);
  const [etudiants, setEtudiants] = useState([]);
  const [emploi, setEmploi] = useState([]);
  const [matieres, setMatieres] = useState([]);
  const [creneaux, setCreneaux] = useState([]);
  const [salles, setSalles] = useState([]);

    const { user} = useContext(AuthContext);
     
    const role = user.role;
  const currentUserId = user.id;

  // Function to handle accept/reject justification
  const handleJustificationStatus = async (absenceId, justifie) => {
    // For reject, show confirmation dialog
    if (!justifie) {
      const confirmed = window.confirm(
        "Êtes-vous sûr de vouloir rejeter cette justification ?\n\n" +
        "Le fichier sera supprimé et l'étudiant pourra téléverser un nouveau justificatif."
      );
      if (!confirmed) {
        return;
      }
    }

    try {
      const res = await fetch(`http://localhost:8083/absences/${absenceId}/justification?justifie=${justifie}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!res.ok) {
        throw new Error('Erreur lors de la mise à jour');
      }

      // Show success message
      if (justifie) {
        alert("Justification acceptée avec succès ✓");
      } else {
        alert("Justification rejetée. Le fichier a été supprimé.");
      }

      // Refresh seances data
      const fetchSeances = async () => {
        try {
          const res = await fetch(`http://localhost:8083/absences/classes/${classe}/user/${currentUserId}`);
          if (res.ok) {
            const data = await res.json();
            if (Array.isArray(data)) {
              setSeances(data);
            }
          }
        } catch (error) {
          console.error("Erreur fetchSeances:", error);
        }
      };
      fetchSeances();
    } catch (error) {
      console.error("Erreur lors de la mise à jour de la justification:", error);
      alert("Erreur lors de la mise à jour de la justification");
    }
  };

  // Helper function to get filename from filePath
  const getFilenameFromPath = (filePath) => {
    if (!filePath) return null;
    // Extract filename from path (e.g., "uploads/justifications/absence_3_1768128616412.pdf" -> "absence_3_1768128616412.pdf")
    const parts = filePath.split('/');
    return parts[parts.length - 1];
  };

  useEffect(() => {
    const fetchClasses = async () => {
      let url = role === "ADMIN"
        ? `http://localhost:8080/emploi/classes`  // toutes les classes pour admin
        : `http://localhost:8080/emploi/classes/prof/${currentUserId}`; // seulement celles du prof
      const res = await fetch(url);
      const data = await res.json();
      setClasses(data);
    };
    fetchClasses();
  }, [currentUserId, role]);





  useEffect(() => {
    if (!classe) return;

    const fetchMatieres = async () => {
      try {
        let url = role === "ADMIN"
          ? `http://localhost:8080/emploi/matieres/classe/${classe}`
          : `http://localhost:8080/emploi/matieres/classe/prof/${classe}/${currentUserId}`;
        const res = await fetch(url);
        const data = await res.json();
        console.log("Matieres reçues:", data); // <-- Vérifier ici
        setMatieres(data);
      } catch (err) {
        console.error("Erreur fetchMatieres:", err);
        setMatieres([]);
      }
    };

    fetchMatieres();
  }, [classe, currentUserId, role]);




  useEffect(() => {
  const fetchStudents = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/users/students');
      const data = await res.json();
      console.log("Étudiants reçus :", data); 
      setEtudiants(data);
    } catch (err) {
      console.error("Erreur fetchStudents:", err);
      setEtudiants([]);
    }
  };
  fetchStudents();
}, []);




  useEffect(() => {

    if (!classe) return; // si aucune classe sélectionnée, ne rien faire

    const fetchSeances = async () => {
      try {
        const res = await fetch(`http://localhost:8083/absences/classes/${classe}/user/${currentUserId}`);
        const text = await res.clone().text();
        console.log("Réponse brute fetchSeances:", text);
        if (!res.ok) {
          console.error("Erreur lors de la récupération des séances:", res.status, res.statusText);
          setSeances([]); // reset pour éviter l'erreur .filter
          return;
        }

        const data = await res.json();

        if (Array.isArray(data)) {
          setSeances(data);
        } else {
          console.warn("Data reçue n'est pas un tableau :", data);
          setSeances([]);
        }
      } catch (error) {
        console.error("Erreur fetchSeances:", error);
        setSeances([]);
      }
    };

    fetchSeances();
  }, [classe, currentUserId]);


  useEffect(() => {
    if (!classe) return;

    const fetchEmploi = async () => {
      const res = await fetch(`http://localhost:8080/emploi/classe/${classe}`);
      const data = await res.json();
       console.log("emplois reçus :", data); 
      setEmploi(data);
    };

    fetchEmploi();
  }, [classe]);



  // Filtrer les séances selon la classe, matière et rôle
  const seancesClasse = seances
    .filter(s => {
      // séance doit appartenir à la classe
      if (s.classeId !== Number(classe)) return false;

      // rôle prof
      if (role === "TEACHER" && s.profId !== currentUserId) return false;

      // filtre matière (via emploi du temps)
      if (matiere && emploi.length > 0) {
        const edt = emploi.find(e =>
          Number(e.classeId) === Number(s.classeId) &&
         Number(e.profId) === Number(s.profId) &&
          Number(e.creneauId) === Number(s.creneauId)
        );

        if (!edt) return false;
        if (Number(edt.matiereId) !== Number(matiere)) return false;
      }

      return true;
    })
    .sort((a, b) => new Date(b.date) - new Date(a.date));


  // Filtrer les étudiants selon la classe et le rôle
  const etudiantsClasse = etudiants
    .filter(e => e.id_classe === Number(classe))
    .filter(e => role === "TEACHER" ? emploi.some(emp => emp.id_classe === Number(classe) && emp.id_prof === currentUserId) : true)
    .filter(e => {
      if (!searchStudent) return true;
      const search = searchStudent.toLowerCase();
      return e.nom.toLowerCase().startsWith(search) || e.prenom.toLowerCase().startsWith(search);
    });

  // Statistiques globales
  const stats = {
    totalSeances: seancesClasse.length,

    totalAbsences: seancesClasse.reduce((total, seance) => {
      const absencesSeance = seance.absences || [];
      return total + absencesSeance.filter(a => !a.present).length;
    }, 0),

    etudiantsAbsents: new Set(
      seancesClasse.flatMap(seance =>
        (seance.absences || [])
          .filter(a => !a.present)
          .map(a => a.etudiantId)
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
      const filePath = absence.filePath || absence.justificatif;
      const filename = getFilenameFromPath(filePath);
      const hasFile = filename && filename.trim() !== "";

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
                        onClick={() => window.open(`http://localhost:8083/absences/justifications/${filename}`, '_blank')}
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
                          link.href = `http://localhost:8083/absences/justifications/${filename}`;
                          link.download = filename || `justificatif_${absence.id}.pdf`;
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

    // Non justifié - peut avoir un fichier en attente de validation
    const filePath = absence.filePath || absence.justificatif;
    const filename = getFilenameFromPath(filePath);
    const hasFile = filename && filename.trim() !== "";

    return (
      <div className="flex items-center justify-center">
        <div className="flex flex-col items-center gap-2">
          <Badge variant="outline" className="bg-red-50 text-red-700 border-red-300 px-3 py-1">
            <div className="flex items-center">
              <FileX className="h-3 w-3 mr-1" />
              Non justifié
            </div>
          </Badge>

          {hasFile && (role === "ADMIN" || role === "TEACHER") && (
            <div className="flex items-center gap-2 mt-1">
              <TooltipProvider>
                <Tooltip>
                  <TooltipTrigger asChild>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="h-8 px-2 text-xs"
                      onClick={() => window.open(`http://localhost:8083/absences/justifications/${filename}`, '_blank')}
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
                      variant="default"
                      size="sm"
                      className="h-8 px-2 text-xs bg-green-600 hover:bg-green-700"
                      onClick={() => handleJustificationStatus(absence.id, true)}
                    >
                      <CheckCircle className="h-3 w-3 mr-1" />
                      Accepter
                    </Button>
                  </TooltipTrigger>
                  <TooltipContent>
                    <p>Accepter la justification</p>
                  </TooltipContent>
                </Tooltip>
              </TooltipProvider>

              <TooltipProvider>
                <Tooltip>
                  <TooltipTrigger asChild>
                    <Button
                      variant="destructive"
                      size="sm"
                      className="h-8 px-2 text-xs"
                      onClick={() => handleJustificationStatus(absence.id, false)}
                    >
                      <XCircle className="h-3 w-3 mr-1" />
                      Rejeter
                    </Button>
                  </TooltipTrigger>
                  <TooltipContent>
                    <p>Rejeter la justification</p>
                  </TooltipContent>
                </Tooltip>
              </TooltipProvider>
            </div>
          )}

          {!hasFile && (role === "ADMIN" || role === "TEACHER") && (
            <Button
              variant="outline"
              size="sm"
              className="h-7 px-2 text-xs border-dashed"
              onClick={() => {
                // Logique pour ajouter un justificatif
                console.log("Ajouter justificatif pour:", absence.id);
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
      const filePath = absence.filePath || absence.justificatif;
      const filename = getFilenameFromPath(filePath);
      const hasFile = filename && filename.trim() !== "";

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
                    onClick={() => window.open(`http://localhost:8083/absences/justifications/${filename}`, '_blank')}
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
                      onClick={() => window.open(`http://localhost:8083/absences/justifications/${filename}`, '_blank')}
                    >
                      Voir
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      className="h-6 px-2 text-xs"
                      onClick={() => {
                        const link = document.createElement('a');
                        link.href = `http://localhost:8083/absences/justifications/${filename}`;
                        link.download = filename || `justificatif_${absence.id}.pdf`;
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

    // Non justifié - peut avoir un fichier en attente de validation
    const filePath = absence.filePath || absence.justificatif;
    const filename = getFilenameFromPath(filePath);
    const hasFile = filename && filename.trim() !== "";

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
              {hasFile && (role === "ADMIN" || role === "TEACHER") && (
                <div className="space-y-2">
                  <Button
                    variant="outline"
                    size="sm"
                    className="h-6 px-2 text-xs w-full"
                    onClick={() => window.open(`http://localhost:8083/absences/justifications/${filename}`, '_blank')}
                  >
                    <Eye className="h-3 w-3 mr-1" />
                    Voir le justificatif
                  </Button>
                  <div className="flex gap-2">
                    <Button
                      variant="default"
                      size="sm"
                      className="h-6 px-2 text-xs flex-1 bg-green-600 hover:bg-green-700"
                      onClick={() => handleJustificationStatus(absence.id, true)}
                    >
                      <CheckCircle className="h-3 w-3 mr-1" />
                      Accepter
                    </Button>
                    <Button
                      variant="destructive"
                      size="sm"
                      className="h-6 px-2 text-xs flex-1"
                      onClick={() => handleJustificationStatus(absence.id, false)}
                    >
                      <XCircle className="h-3 w-3 mr-1" />
                      Rejeter
                    </Button>
                  </div>
                </div>
              )}
              {!hasFile && (role === "ADMIN" || role === "TEACHER") && (
                <Button
                  variant="outline"
                  size="sm"
                  className="h-6 px-2 text-xs w-full"
                  onClick={() => {
                    console.log("Ajouter justificatif pour:", absence.id);
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
                        <div className="flex items-center justify-between w-full">
                          <span>{c.nom}</span>
                          {c.filiere && (
                            <Badge variant="outline" className="ml-2">
                              {c.filiere}
                            </Badge>
                          )}
                        </div>
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
                      {matieres.map(m => (
                        <SelectItem key={m.id} value={m.id.toString()}>
                          {m.nom}
                        </SelectItem>
                      ))}
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
              const edt = emploi.find(e =>
                Number(e.classeId) === Number(seance.classeId) &&
                Number(e.profId) === Number(seance.profId) &&
                Number(e.creneauId) === Number(seance.creneauId)
              );
              const absencesSeance = seance.absences || [];

              const matiereNom = edt?.matiereNom || "Matière inconnue";
              const salleNom = edt?.salleNom || "Salle inconnue";
              const creneauLabel = edt
                ? `${edt.creneauDebut} - ${edt.creneauFin}`
                : "Créneau inconnu";

// Appliquer filtre étudiant
// Appliquer filtre étudiant
const filteredAbsences = searchStudent
  ? absencesSeance.filter(a => {
      const e = etudiants.find(et => et.id === a.etudiantId);
      if (!e) return false;

      const search = searchStudent.toLowerCase();
      // Vérifie si le prénom ou le nom commence par la recherche
      return (
        e.firstname.toLowerCase().startsWith(search) ||
        e.lastname.toLowerCase().startsWith(search) ||
        `${e.firstname} ${e.lastname}`.toLowerCase().startsWith(search) ||
        `${e.lastname} ${e.firstname}`.toLowerCase().startsWith(search)
      );
    })
  : absencesSeance;



              const absentsCount = absencesSeance.filter(a => !a.present).length;
              const presentsCount = absencesSeance.filter(a => a.present).length;
              const dateFormatee = new Date(seance.date).toLocaleDateString("fr-FR", {
                weekday: "long",
                year: "numeric",
                month: "long",
                day: "numeric"
              });


              return (
                <Card key={seance.id} className="overflow-hidden">
                  <CardHeader className="bg-gradient-to-r from-blue-50 to-white">
                    <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                      <div className="space-y-2">
                        <div className="flex items-center gap-3">
                          <Badge className="bg-blue-100 text-blue-700 border-blue-200 hover:bg-blue-100">
                            <BookOpen className="h-3 w-3 mr-1" />
                            {matiereNom}
                          </Badge>
                          <Badge variant="outline" className="flex items-center gap-1">
                            <Building className="h-3 w-3" />
                            {salleNom}
                          </Badge>
                        </div>
                        <div className="flex items-center gap-4 text-sm text-gray-600">
                          <span className="flex items-center gap-1">
                            <Calendar className="h-3 w-3" />
                            {dateFormatee}
                          </span>
                          <span className="flex items-center gap-1">
                            <Clock className="h-3 w-3" />
                            {creneauLabel}
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
                          {filteredAbsences.map((absence, index) => {
                            const etu = etudiants.find(e => e.id === absence.etudiantId);
                            return (
                              <TableRow key={absence.id} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                                <TableCell className="text-center font-medium text-gray-900">
                                  {index + 1}
                                </TableCell>
                                <TableCell>
                                  <div className="font-medium text-gray-900">
                                   {etu?.firstname} {etu?.lastname}
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