import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { useState } from "react";
import axios from "axios";
const API_URL = "http://localhost:8080";

const AddTeachers = ()=>{
    
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    
    const [file,setFile]=useState("");
     const handleSubmit = async (e) =>{
        e.preventDefault();
        if (!file) {
           setError("Veuillez inserer d'abord le fichier .");
           return;
         }
        const formData = new FormData();
        formData.append("file", file);
        try {
          const res = await axios.post(`${API_URL}/admin/createTeachers`,formData,{ withCredentials: true });
          setMessage(res.data);
        } catch (error) {
          console.error(error);
          setError("Erreur lors de l’enregistrement des profs ");
        }
      };
    return(
     <Card className="w-full max-w-xl mx-auto mt-20 ">
      <CardHeader>
        <CardTitle className="text-xl font-bold text-center">Ajouter les professeurs</CardTitle>
      </CardHeader>
      <CardContent>
        {message && (
        <div className="mt-4 p-3 bg-green-100 text-green-800 border border-green-300 rounded text-center transition duration-300 ease-in-out">
          {message}
        </div>
      )}
      {error && (
        <div className="mt-4 p-3 bg-red-100 text-red-800 border border-red-300 rounded text-center transition duration-300 ease-in-out">
          {error}
        </div>
      )}
        <form onSubmit={handleSubmit}>    
           <div className="space-y-2">
            <div className="mb-2"><Label>Importer un fichier Excel</Label></div>
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
export default AddTeachers;