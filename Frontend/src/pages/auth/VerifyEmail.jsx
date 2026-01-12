import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { useState } from "react";
import {useNavigate} from "react-router-dom";
import axios from "axios";
const API_URL =  "http://localhost:8080";
const VerifyEmail = ({ mode })=>{
    const [email,setEmail]=useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate("");
    const handleVerifyEmail = async(e)=>{
         e.preventDefault();
         setMessage("");
         setError("");
      if(!email){
         setError("Vous devez taper votre email d'abord !");
         return;
        }
        try {
          const url =
        mode === "validate"
          ? `${API_URL}/api/v1/auth/validate-account/request/${email}`
          : `${API_URL}/api/v1/auth/forgot-password/request/${email}`;
          const res = await axios.post(url);
          setMessage(res.data);
          const dest =   mode === "validate" ? "/verifyOtp": "/forgotOtp";
          setTimeout(() => {
          navigate(dest, { state: { email } });
          }, 2000);        
        } catch (error) {
          console.error(error);
          setError("Erreur lors de l’envoi Vérifiez votre connexion !");
        }
      };

return(
    <div className="flex items-center justify-center min-h-screen">
        <form onSubmit={handleVerifyEmail} 
        className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg">
        <h2 className="text-2xl font-bold mb-6 text-blue-700 text-center">
          Verify your Email
        </h2>
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