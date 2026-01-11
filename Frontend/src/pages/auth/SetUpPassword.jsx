import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { useState } from "react";
import { useLocation ,useNavigate} from "react-router-dom";
import axios from "axios";
const API_URL =  "http://localhost:8080";
const SetUpPassword = ({ mode }) =>{
    const [password,setPassword]=useState("");
    const [confirmPassword,setConfirmPassword]=useState("");
    const location = useLocation();
    const { email } = location.state || {};
    const navigate = useNavigate();
   
    const handlePasswordSetUp =async (e)=>{
      e.preventDefault();
      if(!email){
        alert("Vous devez tapez votre email d'abord !")
        return;
        }
       try {
           const url = mode === "validate"
          ? `${API_URL}/api/v1/auth/validate-account/set-password/${email}`
          : `${API_URL}/api/v1/auth/forgot-password/change/${email}`;
          const res = await axios.post(url,{password,repeatPassword: confirmPassword});
          alert(res.data);
          navigate("/login");
        } catch (error) {
          console.error(error);
          alert("Erreur lors de l’envoi");
        }
      

    };
   return(
    <div className="flex items-center justify-center min-h-screen ">
        <form onSubmit={handlePasswordSetUp} 
        className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg">
        <h2 className="text-2xl font-bold mb-6 text-center">
          Set-Up your Password
        </h2>
        <div className="mb-4">
         <Label>Password :</Label>
         <Input
         type="password"
         value={password}
         onChange={(e)=>setPassword(e.target.value)} />
         </div>
          <div className="mb-4">
         <Label>Confirm password :</Label>
         <Input
         type="password"
         value={confirmPassword}
         onChange={(e)=>setConfirmPassword(e.target.value)} />
         </div>
         
         <Button type="submit" className="w-full bg-black hover:bg-gray-600">Submit</Button>
        </form>
    </div>
 )
}
export default SetUpPassword;