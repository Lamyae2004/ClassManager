import React, { useContext, useState } from "react";
import { Input } from "@/components/ui/Input";
import { Button } from "@/components/ui/Button";
import { Label } from "@/components/ui/Label";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "@/context/AuthContext";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { setUser } = useContext(AuthContext);

  const navigate = useNavigate();
  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    if (!email || !password) {
      setError("Tous les champs sont obligatoires !");
    } else {

          try{
            const res = await axios.post("http://localhost:8080/api/v1/auth/authenticate",{email,password}, {
          headers: {
            "Content-Type": "application/json" 
          }, withCredentials: true 
        });
          const res2 = await axios.get("http://localhost:8080/api/users/profile", {
        withCredentials: true,
      });

      setUser(res2.data); // 
      navigate("/");
          alert("Connexion réussie !"); 
          navigate("/");
          }
          catch(err){
            console.error(error);
             if (err.response && err.response.data && err.response.data.error) {
                setError(err.response.data.error);
                
    } else {
                setError("Erreur inconnue lors de la connexion");
                alert("Erreur inconnue lors de la connexion");
    }
  }
   }
  };

  return (
    <div className="flex items-center justify-center min-h-screen ">
      <form onSubmit={handleLogin}
        className="bg-white p-8 rounded-lg shadow-lg w-full max-w-lg"
      >
        <h2 className="text-2xl font-bold mb-6 text-center">
          Sign-In
        </h2>
        {error && <p className="text-red-600 mb-4 text-center">{error}</p>}
        <div className="mb-4">
          <Label>Email</Label>
          <Input
            type="email"
            placeholder="Votre email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="mb-4">
          <Label>Mot de passe</Label>
          <Input
            type="password"
            placeholder="Votre mot de passe"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <Button type="submit" className="w-full bg-black hover:bg-gray-600">
          Se connecter
        </Button>
        <p className="mt-2 text-sm text-center">
         <a href="/verifyEmail" className="text-blue-600 hover:underline font-medium">
          First login? Validate your account
         </a>
        </p>
        <p className="mt-2 text-sm text-center">
          <a href="/forgotPassword" className="text-gray-400 hover:underline">
            Mot de passe oublié ?
          </a>
        </p>
        
      </form>
    </div>
  );
};

export default Login;
