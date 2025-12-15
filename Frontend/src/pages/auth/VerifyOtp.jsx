import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
const VerifyOtp =({ mode })=>{
    const [otp,setOtp]=useState("");
    const location = useLocation();
    const { email } = location.state || {};
    const navigate = useNavigate();
    const handleOtpVerification =async (e)=>{
        e.preventDefault();
      if(!email || !otp){
        alert("Vous devez tapez votre email d'abord !")
        return;
        }
        try {
          const url = mode === "validate"
          ? `http://localhost:9090/api/v1/auth/validate-account/verify/${email}/${otp}`
          : `http://localhost:9090/api/v1/auth/forgot-password/verify/${email}/${otp}`;
          const res = await axios.post(url);
          alert(res.data);
         const dest =   mode === "validate" ? "/setUpPassword": "/resetPassword";
          navigate(dest, { state: { email } });
        } catch (error) {
          console.error(error);
          alert("Erreur lors de l’envoi");
        }
    };
 return(
    <div className="flex items-center justify-center min-h-screen ">
        <form onSubmit={handleOtpVerification} 
        className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg">
             <h2 className="text-2xl font-bold mb-6 text-center">
          Verify your Email
        </h2>
        <div className="mb-4">
         <Label>Otp:</Label>
         <Input
         type="Number"
         value={otp}
         onChange={(e)=>setOtp(e.target.value)} />
         </div>
         <Button type="submit" className="w-full bg-black hover:bg-gray-600">validate otp</Button>
        </form>
    </div>
 )
}
export default VerifyOtp;