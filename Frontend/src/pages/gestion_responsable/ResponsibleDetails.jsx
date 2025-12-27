import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import React from 'react'
import { useLocation, useNavigate } from 'react-router-dom';

function ResponsibleDetails() {
  const { state } = useLocation();
  const navigate = useNavigate();

  // Sécurité si accès direct à l'URL
  if (!state?.responsable) {
    return (
      <p className="text-center mt-10 text-red-500">
        Aucun responsable sélectionné
      </p>
    );
  }

  const { firstname, lastname, email } = state.responsable;
  return (
    <Card className="w-full max-w-xl mx-auto mt-10">
      <CardHeader>
        <CardTitle className="text-xl text-center">
          Responsable Assigné
        </CardTitle>
      </CardHeader>

      <CardContent className="space-y-4">
        <p><strong>Nom :</strong> {firstname}</p>
        <p><strong>Prénom :</strong> {lastname}</p>
        <p><strong>Email :</strong> {email}</p>
    
        <Button
          className="w-full mt-4"
          onClick={() => navigate(-1)}
        >
          Retour
        </Button>
      </CardContent>
    </Card>
  );
}

export default ResponsibleDetails
