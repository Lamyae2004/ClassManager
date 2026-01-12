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
    const [error, setError] = useState("");
    const location = useLocation();
    const { email } = location.state || {};
    const navigate = useNavigate();
    const handleOtpVerification =async (e)=>{
        e.preventDefault();
        if (!email || !otp) {
          setError("Vous devez taper votre email et le OTP !");
          return;
        }
        try {
          const url = mode === "validate"
          ? `${API_URL}/api/v1/auth/validate-account/verify/${email}/${otp}`
          : `${API_URL}/api/v1/auth/forgot-password/verify/${email}/${otp}`;

          const res = await axios.post(url);
          setMessage(res.data);

          const dest =   mode === "validate" ? "/setUpPassword": "/resetPassword";
          setTimeout(() => {
          navigate(dest, { state: { email } });
          }, 2000);

        } catch (error) {
            let err;
            if (typeof error.response?.data === "string") {
              err = error.response.data;
            } else if (error.response?.data?.message) {
              err = error.response.data.message;
            } else {
              err = "OTP incorrect, veuillez réessayer.";
            }
            setError(err);
        
        }
    };
 return(
    <div className="flex items-center justify-center min-h-screen ">
        <form onSubmit={handleOtpVerification} 
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