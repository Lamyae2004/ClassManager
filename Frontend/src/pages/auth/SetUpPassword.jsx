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
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const location = useLocation();
    const { email } = location.state || {};
    const navigate = useNavigate();
   
    const handlePasswordSetUp =async (e)=>{
      e.preventDefault();
      if(!email){
        setError("Vous devez taper votre email d'abord !");
         return;
        }
       try {
           const url = mode === "validate"
          ? `${API_URL}/api/v1/auth/validate-account/set-password/${email}`
          : `${API_URL}/api/v1/auth/forgot-password/change/${email}`;
          const res = await axios.post(url,{password,repeatPassword: confirmPassword});
          setMessage(res.data);
          setTimeout(() => {
           navigate("/login");
          }, 2000);   
        } catch (error) {
          console.error(error);
          setError(error)
        }
      

    };
   return(
    <div className="flex items-center justify-center min-h-screen ">
        <form onSubmit={handlePasswordSetUp} 
        className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg">
        <h2 className="text-2xl font-bold mb-6 text-blue-700 text-center">
          Set-Up your Password
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
         <Label>Password :</Label>
         <Input
         type="password"
         value={password}
         onChange={(e)=>setPassword(e.target.value)} />
         </div>
          <div className="mb-4 text-gray-600">
         <Label>Confirm password :</Label>
         <Input
         type="password"
         value={confirmPassword}
         onChange={(e)=>setConfirmPassword(e.target.value)} />
         </div>
         
         <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700">Submit</Button>
        </form>
    </div>
 )
}
export default SetUpPassword;