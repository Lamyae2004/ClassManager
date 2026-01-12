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
           setErrorMessage("Veuillez remplir tous les champs.");
           return;
         }

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
        <div className="mt-4 p-3 bg-red-100 text-red-800 border border-red-300 rounded text-center transition duration-300 ease-in-out">
          {errorMessage}
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
