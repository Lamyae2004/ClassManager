"use client";
import React, { useState, useEffect } from "react";
import { classes, etudiants, creneaux, emploi, matieres, salles } from "./data";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle
} from "@/components/ui/card";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import { Calendar, Clock, Users, CheckCircle, XCircle } from "lucide-react";
import { Separator } from "@/components/ui/separator";

export default function AbsencePage() {
    const [classe, setClasse] = useState(null);
    const [creneau, setCreneau] = useState(null);
    const [presences, setPresences] = useState({});
    const [filiere, setFiliere] = useState(null);
    const [classes, setClasses] = useState([]);


    // 🔹 Prof connecté (simulé pour l'exemple)
    const profConnecte = 9;

    // 🔹 Détecter automatiquement le jour actuel
    const jours = ["Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"];
    const aujourdhui = new Date();
    const jourSelectionne = jours[aujourdhui.getDay()];
    const dateAujourdhui = aujourdhui.toLocaleDateString('fr-FR', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });

   useEffect(() => {
    fetch(`http://localhost:8080/classes/prof/${profConnecte}`)
        .then(res => res.json())
        .then(data => setClasses(data))
        .catch(err => console.error(err));
}, []);


    const handlePresenceChange = (id, value) => {
        setPresences(prev => ({ ...prev, [id]: value }));
    };

    // 🔹 Étudiants filtrés selon la classe
    const filteredStudents = etudiants.filter(e => e.id_classe === Number(classe));

    // 🔹 Créneaux filtrés selon classe + jour + prof
    const todayCreneaux = emploi.filter(
        e => e.id_classe === Number(classe) &&
            e.jour === jourSelectionne &&
            e.id_prof === profConnecte
    );

    // Trouver le créneau sélectionné
    const selectedCreneau = todayCreneaux.find(c => c.id_edt === Number(creneau));
    const horaire = selectedCreneau ? creneaux.find(cr => cr.id === selectedCreneau.id_creneau) : null;
    const matiere = selectedCreneau && matieres.find(m => m.id_matiere === selectedCreneau.id_matiere);
    const salle = selectedCreneau && salles.find(s => s.id_salle === selectedCreneau.id_salle);

    const handleSave = () => {
        console.log("Absences envoyées :", presences);
        alert("Absence enregistrée ✔ (simulation)");
    };

    // Calculer les statistiques
    const totalStudents = filteredStudents.length;
    const presentCount = Object.values(presences).filter(v => v === true).length;
    const absentCount = Object.values(presences).filter(v => v === false).length;

    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-50 to-blue-50 p-4 md:p-6">
            <div className="max-w-4xl mx-auto space-y-6">
                {/* En-tête */}
                <div className="space-y-2">

                    <div className="flex flex-wrap items-center gap-4 text-gray-600">
                        <div className="flex items-center gap-2">
                            <Calendar className="h-4 w-4" />
                            <span className="font-medium">{dateAujourdhui}</span>
                        </div>
                        <Badge variant="outline" className="flex items-center gap-2">
                            <Clock className="h-3 w-3" />
                            {jourSelectionne}
                        </Badge>
                    </div>
                </div>

                <div className="grid gap-6 md:grid-cols-3">
                    {/* Carte de sélection */}
                    <Card className="md:col-span-2">
                        <CardHeader>
                            <CardTitle>Configuration de la séance</CardTitle>
                            <CardDescription>
                                Sélectionnez la classe et le créneau pour commencer
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="space-y-6">
                            {/* Sélection de classe */}
                            <div className="space-y-3">
                                <Label htmlFor="classe">Classe</Label>
                                <Select onValueChange={(val) => {
                                    setClasse(val);
                                    const selected = classes.find(c => c.id.toString() === val);
                                    setFiliere(selected?.filiere || null);
                                }} value={classe || ""}>
                                    <SelectTrigger id="classe" className="w-full">
                                        <SelectValue placeholder="Sélectionner une classe" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {classes.map(c => (
                                            <SelectItem key={c.id} value={c.id.toString()}>
                                                {c.nom} ({c.filiere})
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            {/* Sélection de créneau */}
                            {classe && todayCreneaux.length > 0 && (
                                <div className="space-y-3">
                                    <Label htmlFor="creneau">Créneau horaire</Label>
                                    <Select onValueChange={setCreneau} value={creneau || ""}>
                                        <SelectTrigger id="creneau" className="w-full">
                                            <SelectValue placeholder="Sélectionner un créneau" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {todayCreneaux.map(c => {
                                                const horaire = creneaux.find(cr => cr.id === c.id_creneau);
                                                const matiereItem = matieres.find(m => m.id_matiere === c.id_matiere);
                                                return (
                                                    <SelectItem key={c.id_edt} value={c.id_edt.toString()}>
                                                        <div className="flex items-center gap-2">
                                                            <Clock className="h-3 w-3" />
                                                            <span>{horaire.debut} - {horaire.fin}</span>
                                                            <Badge variant="secondary" className="ml-2">
                                                                {matiereItem ? matiereItem.nom_matiere : "Matière inconnue"}
                                                            </Badge>
                                                        </div>
                                                    </SelectItem>
                                                );
                                            })}
                                        </SelectContent>
                                    </Select>
                                </div>
                            )}

                            {/* Aucun créneau disponible */}
                            {classe && todayCreneaux.length === 0 && (
                                <div className="rounded-lg border border-dashed border-gray-300 p-8 text-center">
                                    <Users className="h-12 w-12 mx-auto text-gray-400 mb-4" />
                                    <h3 className="font-semibold text-gray-900 mb-2">Aucun créneau disponible</h3>
                                    <p className="text-gray-600">
                                        Aucune séance n'est prévue pour cette classe aujourd'hui.
                                    </p>
                                </div>
                            )}
                        </CardContent>
                    </Card>

                    {/* Carte d'information */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Informations</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            {selectedCreneau && horaire && (
                                <>
                                    <div className="space-y-2">
                                        <h4 className="font-semibold text-sm text-gray-500">Détails du créneau</h4>
                                        <div className="space-y-1">
                                            <div className="flex justify-between">
                                                <span className="text-gray-600">Matière</span>
                                                <Badge variant="outline">{matiere.nom_matiere}</Badge>
                                            </div>
                                            <div className="flex justify-between">
                                                <span className="text-gray-600">Horaire</span>
                                                <span className="font-medium">{horaire.debut} - {horaire.fin}</span>
                                            </div>
                                            <div className="flex justify-between">
                                                <span className="text-gray-600">Salle</span>
                                                <span className="font-medium">{salle.nom_salle}</span>
                                            </div>
                                        </div>
                                    </div>
                                    <Separator />
                                </>
                            )}

                            {classe && creneau && (
                                <div className="space-y-2">
                                    <h4 className="font-semibold text-sm text-gray-500">Statistiques</h4>
                                    <div className="grid grid-cols-2 gap-2">
                                        <div className="rounded-lg bg-green-50 p-3 text-center">
                                            <div className="text-2xl font-bold text-green-600">{presentCount}</div>
                                            <div className="text-xs text-green-700">Présents</div>
                                        </div>
                                        <div className="rounded-lg bg-red-50 p-3 text-center">
                                            <div className="text-2xl font-bold text-red-600">{absentCount}</div>
                                            <div className="text-xs text-red-700">Absents</div>
                                        </div>
                                    </div>
                                    <div className="rounded-lg bg-blue-50 p-3 text-center">
                                        <div className="text-2xl font-bold text-blue-600">{totalStudents}</div>
                                        <div className="text-xs text-blue-700">Total étudiants</div>
                                    </div>
                                </div>
                            )}
                        </CardContent>
                    </Card>
                </div>

                {/* Liste des étudiants */}
                {classe && creneau && (
                    <Card>
                        <CardHeader>
                            <CardTitle>Feuille de présence</CardTitle>
                            <CardDescription>
                                Marquez la présence de chaque étudiant
                            </CardDescription>
                        </CardHeader>
                        <CardContent>
                            <div className="rounded-md border">
                                <Table>
                                    <TableHeader>
                                        <TableRow>
                                            <TableHead className="w-12"> </TableHead>
                                            <TableHead>Étudiant</TableHead>
                                            <TableHead className="text-center">Présent</TableHead>
                                            <TableHead className="text-center">Absent</TableHead>
                                            <TableHead className="text-center">Statut</TableHead>
                                        </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                        {filteredStudents.map((e, index) => (
                                            <TableRow key={e.id_etudiant}>
                                                <TableCell className="font-medium">{index + 1}</TableCell>
                                                <TableCell>
                                                    <div>
                                                        <div className="font-medium">{e.nom} {e.prenom}</div>

                                                    </div>
                                                </TableCell>
                                                <TableCell className="text-center">
                                                    <RadioGroup
                                                        value={presences[e.id_etudiant] === true ? "present" : ""}
                                                        onValueChange={() => handlePresenceChange(e.id_etudiant, true)}
                                                        className="flex justify-center"
                                                    >
                                                        <RadioGroupItem value="present" id={`present-${e.id_etudiant}`} />
                                                    </RadioGroup>
                                                </TableCell>
                                                <TableCell className="text-center">
                                                    <RadioGroup
                                                        value={presences[e.id_etudiant] === false ? "absent" : ""}
                                                        onValueChange={() => handlePresenceChange(e.id_etudiant, false)}
                                                        className="flex justify-center"
                                                    >
                                                        <RadioGroupItem value="absent" id={`absent-${e.id_etudiant}`} />
                                                    </RadioGroup>
                                                </TableCell>
                                                <TableCell className="text-center">
                                                    {presences[e.id_etudiant] === true ? (
                                                        <Badge className="bg-green-100 text-green-800 hover:bg-green-100">
                                                            <CheckCircle className="h-3 w-3 mr-1" />
                                                            Présent
                                                        </Badge>
                                                    ) : presences[e.id_etudiant] === false ? (
                                                        <Badge variant="outline" className="text-red-600 border-red-200">
                                                            <XCircle className="h-3 w-3 mr-1" />
                                                            Absent
                                                        </Badge>
                                                    ) : (
                                                        <Badge variant="outline" className="text-gray-500">
                                                            Non défini
                                                        </Badge>
                                                    )}
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </div>

                            {/* Bouton d'enregistrement */}
                            <div className="flex justify-between items-center mt-6">
                                <div className="text-sm text-gray-600">
                                    {totalStudents > 0 && (
                                        <>
                                            {presentCount + absentCount} / {totalStudents} étudiants marqués
                                            <span className="mx-2">•</span>
                                            {totalStudents - (presentCount + absentCount)} restants
                                        </>
                                    )}
                                </div>
                                <Button
                                    onClick={handleSave}
                                    size="lg"
                                    className="bg-blue-600 hover:bg-blue-700"
                                    disabled={presentCount + absentCount === 0}
                                >
                                    <CheckCircle className="h-4 w-4 mr-2" />
                                    Enregistrer les absences
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                )}
            </div>
        </div>
    );
}