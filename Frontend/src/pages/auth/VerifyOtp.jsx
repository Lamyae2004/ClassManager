import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
const API_URL =  "http://localhost:8080";
const VerifyOtp =({ mode })=>{
    const [otp,setOtp]=useState("");
    const [message,setMessage]=useState("");
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
          ? `${API_URL}/api/v1/auth/validate-account/verify/${email}/${otp}`
          : `${API_URL}/api/v1/auth/forgot-password/verify/${email}/${otp}`;
          const res = await axios.post(url);
          alert(res.data);
         const dest =   mode === "validate" ? "/setUpPassword": "/resetPassword";
          navigate(dest, { state: { email } });
        } catch (error) {
            let msg;
            if (typeof error.response?.data === "string") {
              msg = error.response.data;
            } else if (error.response?.data?.message) {
              msg = error.response.data.message;
            } else {
              msg = "OTP incorrect, veuillez réessayer.";
            }
            setMessage(msg);
        
        }
    };
 return(
    <div className="flex items-center justify-center min-h-screen ">
        <form onSubmit={handleOtpVerification} 
        className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg">
           {message && (
          <p className="mb-4 text-center text-red-600">{message}</p>
        )}
             <h2 className="text-2xl font-bold mb-6 text-blue-700 text-center">
          Verify your Email
        </h2>
        <div className="mb-4 text-gray-600">
         <Label>Otp:</Label>
         <Input
         type="Number"
         value={otp}
         onChange={(e)=>setOtp(e.target.value)} />
         </div>
         <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700">validate otp</Button>
        </form>
    </div>
 )
}
export default VerifyOtp;