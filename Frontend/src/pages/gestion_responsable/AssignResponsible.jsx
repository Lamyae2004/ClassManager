import { Button } from '@/components/ui/Button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Label } from '@/components/ui/Label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import axios from 'axios'
import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
const API_URL =  "http://localhost:8080";
function AssignResponsible() {
    const [niveau,setNiveau]=useState("");
      const [field,setField]=useState("");
      const navigate = useNavigate();
      const [errorMessage, setErrorMessage] = useState("");

    const handleSubmit = async (e) =>{

        e.preventDefault();
        if (!niveau || !field) {
           alert("Veuillez remplir tous les champs.");
           return;
         }
       /* const filiereToSend = (niveau === "CP1" || niveau === "CP2") ? "NONE" : field;
        if (!filiereToSend) {
          alert("Veuillez sélectionner une filière.");
          return;
        }*/
         if ((niveau === "CP1" ||  niveau === "CP2")&& field != "NONE" ) {
           setErrorMessage("Si CP1 ou CP2 la filière devra etre none !");
           return;
         }

        try {
          const res = await axios.post(`${API_URL}/api/responsible/assign-Random`,  { niveau, filiere: field }, 
         {withCredentials: true });
          
           navigate("/responsable-details", {
              state: {
                responsable: res.data
              }
            });

        } catch (error) {
           const message = error.response?.data?.error || error.response?.data?.message || "Erreur serveur";
           setErrorMessage(message)
        }
    }
  return (
    <Card className="w-full max-w-xl mx-auto mt-10 ">
         <CardHeader>
           <CardTitle className="text-xl font-bold  text-center">Choisir un responsable</CardTitle>
         </CardHeader>
         {errorMessage && (
    <div className="mt-4 ml-6 mr-5 flex items-start gap-3 rounded-lg border border-red-200 bg-red-50 p-4 text-red-700 shadow-sm">
        <svg
            className="h-5 w-5 mt-0.5 text-red-500"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            viewBox="0 0 24 24"
        >
            <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M12 9v2m0 4h.01M5.07 19h13.86c1.54 0 2.5-1.67 1.73-3L13.73 4c-.77-1.33-2.69-1.33-3.46 0L3.34 16c-.77 1.33.19 3 1.73 3z"
            />
        </svg>

        <span className="text-sm font-medium">
            {errorMessage}
        </span>
    </div>
)}

         <CardContent>
           <form onSubmit={handleSubmit}>
             <div className="space-y-2">
               <Label className="mb-2 text-gray-600">Filière:</Label>
               <Select onValueChange={setField}>
                 <SelectTrigger>
                   <SelectValue placeholder="Sélectionnez..." />
                 </SelectTrigger>
                 <SelectContent>
                   <SelectItem value="INFO">Génie Informatique</SelectItem>
                   <SelectItem value="INDUS">Génie Industriel</SelectItem>
                   <SelectItem value="CIVIL">Génie Civil</SelectItem>
                   <SelectItem value="RST">Génie Réseaux et Systèmes</SelectItem>
                   <SelectItem value="MECA">Génie Mécatronique</SelectItem>
                   <SelectItem value="ELEC">Génie Electrique</SelectItem>
                   <SelectItem value="NONE">None</SelectItem>
                 </SelectContent>
               </Select>
             </div>
   
            <div className="space-y-2">
               <Label className="mb-2 text-gray-600">Niveau:</Label>
               <Select onValueChange={setNiveau}>
                 <SelectTrigger>
                   <SelectValue placeholder="Sélectionnez..." />
                 </SelectTrigger>
                 <SelectContent>
                   <SelectItem value="CP1">Cp1</SelectItem>
                   <SelectItem value="CP2">Cp2</SelectItem>
                   <SelectItem value="CI1">CI1</SelectItem>
                   <SelectItem value="CI2">CI2</SelectItem>
                   <SelectItem value="CI3">CI3</SelectItem>
                 </SelectContent>
               </Select>
             </div>
              <div className="space-y-2">
              
            
                 <Button type="submit" className="w-full">Afficher</Button>
             
   
             </div>
           
   
   
           </form>
         </CardContent>
   
       </Card>
  )
}

export default AssignResponsible
