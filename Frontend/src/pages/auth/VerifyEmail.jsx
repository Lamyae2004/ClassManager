import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { useState } from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";
const API_URL =  "http://localhost:8080";
const VerifyEmail = ({ mode })=>{
    const [email,setEmail]=useState("");
    const navigate = useNavigate("");
    const handleVerifyEmail = async(e)=>{
         e.preventDefault();
      if(!email){
        alert("Vous devez tapez votre email d'abord !")
        return;
        }
        try {
          const url =
        mode === "validate"
          ? `${API_URL}/api/v1/auth/validate-account/request/${email}`
          : `${API_URL}/api/v1/auth/forgot-password/request/${email}`;
          const res = await axios.post(url);
          alert(res.data);
          const dest =   mode === "validate" ? "/verifyOtp": "/forgotOtp";
          navigate(dest, { state: { email } });
        
        } catch (error) {
          console.error(error);
          alert("Erreur lors de l’envoi");
        }
      };

return(
    <div className="flex items-center justify-center min-h-screen">
        <form onSubmit={handleVerifyEmail} 
        className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg">
        <h2 className="text-2xl font-bold mb-6 text-blue-700 text-center">
          Verify your Email
        </h2>
        <div className="mb-4 text-gray-600">
         <Label>Email institutionnel:</Label>
         <Input 
         type="email"
         value={email}
         onChange={(e)=>setEmail(e.target.value)} />
         </div>
         <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700">Send Message</Button>
        </form>
    </div>
);
};
export default VerifyEmail;