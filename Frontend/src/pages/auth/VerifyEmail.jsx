import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useState } from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";
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
          ? `http://localhost:8080/api/v1/auth/validate-account/request/${email}`
          : `http://localhost:8080/api/v1/auth/forgot-password/request/${email}`;
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
        <h2 className="text-2xl font-bold mb-6 text-center">
          Verify your Email
        </h2>
        <div className="mb-4">
         <Label>Email institutionnel:</Label>
         <Input 
         type="email"
         value={email}
         onChange={(e)=>setEmail(e.target.value)} />
         </div>
         <Button type="submit" className="w-full bg-black hover:bg-gray-600">Send Message</Button>
        </form>
    </div>
);
};
export default VerifyEmail;