import { Button } from '@/components/ui/Button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import axios from 'axios';
import React, { useEffect, useState } from 'react'
const API_URL = "http://localhost:8080";

function ResponsibleHistory() {
     const [niveau, setNiveau] = useState("");
     const [filiere, setFiliere] = useState("");
     const [date, setDate] = useState(null);
     const [history, setHistory] = useState([]);
     const [filtredHistory, setFiltredHistory] = useState([]);
     
     useEffect(()=>{
        fetchHistory();
     },[]);
    useEffect(() => {
    const filtered = history.filter(r =>
      {
         if (date) {
      const rStart = new Date(r.startDate);
      const rEnd = new Date(r.endDate);
      const selectedDate = new Date(date);

      return (
        (filiere === "" || r.filiere === filiere) &&
        (niveau === "" || r.niveau === niveau) &&
        selectedDate >= rStart &&
        selectedDate <= rEnd
      );
    } else {
      // Si aucune date sélectionnée, ne filtre que par filiere et niveau
      return (
        (filiere === "" || r.filiere === filiere) &&
        (niveau === "" || r.niveau === niveau)
      );
    }
      }
     
    );
    setFiltredHistory(filtered);
  }, [niveau, filiere,date]);
     const fetchHistory = async () => {
    try {
      const res = await axios.get(
        `${API_URL}/api/responsible/history`,
        {
          withCredentials: true
        }
      );
      setHistory(res.data);
      setFiltredHistory(res.data);
    } catch (err) {
      alert("Erreur lors du chargement de l'historique");
    }
  };
  return (
    <Card className="w-full max-w-5l ml-5 p-4 ">
      <CardHeader>
        <CardTitle className="text-xl text-center">
          Historique des Responsables
        </CardTitle>
      </CardHeader>

      <CardContent className="space-y-4 mr-9">
             {/* Filtres */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <Label>Filière</Label>
            <Select onValueChange={setFiliere}>
              <SelectTrigger>
                <SelectValue placeholder="Toutes" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="INFO">Génie Informatique</SelectItem>
                <SelectItem value="INDUS">Génie Industriel</SelectItem>
                <SelectItem value="CIVIL">Génie Civil</SelectItem>
                <SelectItem value="RST">Réseaux & Systèmes</SelectItem>
                <SelectItem value="MECA">Mécatronique</SelectItem>
                <SelectItem value="ELEC">Electrique</SelectItem>
                <SelectItem value="NONE">Aucune</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div>
            <Label>Niveau</Label>
            <Select onValueChange={setNiveau}>
              <SelectTrigger>
                <SelectValue placeholder="Tous" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="CP1">CP1</SelectItem>
                <SelectItem value="CP2">CP2</SelectItem>
                <SelectItem value="CI1">CI1</SelectItem>
                <SelectItem value="CI2">CI2</SelectItem>
                <SelectItem value="CI3">CI3</SelectItem>
              </SelectContent>
            </Select>
              </div>
             <div>
            <Label>Date</Label>
            <Input   type="date"
          className="border rounded px-2 py-1 w-full"
          value={date || ""}               
          onChange={(e) => setDate(e.target.value)} />
        
          </div>
        </div>
        <table className="w-full text-sm border-collapse">
            <thead>
              <tr className="border-b">
                <th className="text-left py-2">Prénom</th>
                <th className="text-left py-2">Nom</th>
                <th className="text-left py-2">Email</th>
                <th className="text-left py-2">Filière</th>
                <th className="text-left py-2">Niveau</th>
                <th className="text-left py-2">Date d'affectation</th>
                <th className="text-left py-2">Date de fin </th>
              </tr>
            </thead>
            <tbody>
              {filtredHistory.map((r) => (
                
                <tr key={r.assignmentId} className="border-b hover:bg-gray-50">
                  <td className="py-2">{r.firstname}</td>
                  <td className="py-2">{r.lastname}</td>
                  <td className="py-2">{r.email}</td>
                  <td className="py-2">{r.filiere}</td>
                  <td className="py-2">{r.niveau}</td>
                  <td className="py-2">
                    {new Date(r.startDate).toLocaleDateString()}
                  </td>
                  <td className="py-2">
                    {new Date(r.endDate).toLocaleDateString()}
                  </td>
                
                </tr>
              ))}
            </tbody>
          </table>
         </CardContent>
    </Card>
  )
}

export default ResponsibleHistory
