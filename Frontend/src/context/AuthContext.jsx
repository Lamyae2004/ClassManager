import axios from 'axios';
import React, {  createContext, useEffect } from 'react'
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
export  const AuthContext = createContext();
export const AuthProvider = ({children})=>{
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true); 
    const navigate = useNavigate();
    const API_URL =  "http://localhost:8080";

    useEffect(()=>{
         axios.get(`${API_URL}/api/users/profile`, { withCredentials: true })
      .then(res => setUser(res.data))
      .catch(err => {
        setUser(null);
      })
      .finally(() => setLoading(false));
    },[navigate]);
     return (
    <AuthContext.Provider value={{ user, setUser, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

