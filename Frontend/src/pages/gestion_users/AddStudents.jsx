import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import axios from "axios";
import { Upload } from "lucide-react";
import { useState } from "react";

const addStudents = () =>{
    const [niveau,setNiveau]=useState("");
    const [field,setField]=useState("");
    const [file,setFile]=useState(null);
    
    const handleSubmit = async (e) =>{
          e.preventDefault();
        if (!niveau || !field || !file) {
           alert("Veuillez remplir tous les champs.");
           return;
         }
        const formData = new FormData();
        formData.append("file", file);
        formData.append("niveau", niveau);
        formData.append("filiere", field);
        try {
          const res = await axios.post("http://localhost:9090/admin/createStudents",formData);
          alert(res.data);
        } catch (error) {
          console.error(error);
          alert("Erreur lors de l’envoi");
        }
      };
    return(
     <Card className="w-full max-w-xl mx-auto mt-10 ">
      <CardHeader>
        <CardTitle className="text-xl font-bold text-center">Ajouter les étudiants </CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit}>
          <div className="space-y-2">
            <Label className="mb-2">Filière:</Label>
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
              </SelectContent>
            </Select>
          </div>

         <div className="space-y-2">
            <Label className="mb-2">Niveau:</Label>
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
            <Label className="mb-2">Importer un fichier</Label>
            <div className="mb-2">
              <Input
                type="file"
                onChange={(e) => setFile(e.target.files[0])}
                className="cursor-pointer"
               />
            </div>
              <Button type="submit" className="w-full bg-black hover:bg-gray-600">Ajouter</Button>
          

          </div>
        


        </form>
      </CardContent>

    </Card>
  );
}
export default addStudents;