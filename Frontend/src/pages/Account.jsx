import { useState, useContext, useEffect } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Separator } from "@/components/ui/separator";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { AuthContext } from "@/context/AuthContext";
import axios from "axios";
import { User, Mail, Lock, Save, Edit2, X } from "lucide-react";
const API_URL =  "http://localhost:8080";
export default function Account() {
  const { user, setUser } = useContext(AuthContext);
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  
  // Form states
  const [formData, setFormData] = useState({
    firstname: user?.firstname || "",
    lastname: user?.lastname || "",
    email: user?.email || "",
  });

  // Update form data when user changes
  useEffect(() => {
    if (user) {
      setFormData({
        firstname: user.firstname || "",
        lastname: user.lastname || "",
        email: user.email || "",
      });
    }
  }, [user]);

  // Check if user can edit email (only ADMIN can)
  const canEditEmail = user?.role && !["STUDENT", "TEACHER"].includes(user.role.toUpperCase());
  
  const [passwordData, setPasswordData] = useState({
    password: "",
    repeatPassword: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSaveProfile = async () => {
    setLoading(true);
    try {
      // Prepare data - exclude email if user can't edit it
      const updateData = {
        firstname: formData.firstname,
        lastname: formData.lastname,
      };
      
      // Only include email if user has permission
      if (canEditEmail) {
        updateData.email = formData.email;
      }

      const response = await axios.put(
        `${API_URL}/api/users/profile`,
        updateData,
        { withCredentials: true }
      );
      setUser(response.data);
      setIsEditing(false);
      alert("Profil mis à jour avec succès");
    } catch (error) {
      console.error("Error updating profile:", error);
      alert(error.response?.data?.message || "Erreur lors de la mise à jour du profil");
    } finally {
      setLoading(false);
    }
  };

  const handleChangePassword = async () => {
    if (passwordData.password !== passwordData.repeatPassword) {
      alert("Les mots de passe ne correspondent pas");
      return;
    }
    
    if (passwordData.password.length < 8) {
      alert("Le mot de passe doit contenir au moins 8 caractères");
      return;
    }

    setLoading(true);
    try {
      await axios.put(
        `${API_URL}/api/users/change-password`,
        {
          password: passwordData.password,
          repeatPassword: passwordData.repeatPassword,
        },
        { withCredentials: true }
      );
      alert("Mot de passe modifié avec succès");
      setIsChangingPassword(false);
      setPasswordData({
        password: "",
        repeatPassword: "",
      });
    } catch (error) {
      console.error("Error changing password:", error);
      alert(error.response?.data?.message || "Erreur lors du changement de mot de passe");
    } finally {
      setLoading(false);
    }
  };

  const getInitials = () => {
    if (user?.firstname && user?.lastname) {
      return `${user.firstname[0]}${user.lastname[0]}`.toUpperCase();
    }
    if (user?.email) {
      return user.email[0].toUpperCase();
    }
    return "U";
  };

  return (
    <div className="container mx-auto p-6 max-w-4xl">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Mon Compte</h1>
        <p className="text-muted-foreground mt-2">
          Gérez vos informations personnelles et vos paramètres de compte
        </p>
      </div>

      <div className="grid gap-6">
        {/* Profile Information Card */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <User className="h-5 w-5" />
                  Informations Personnelles
                </CardTitle>
                <CardDescription>
                  Mettez à jour vos informations personnelles
                </CardDescription>
              </div>
              {!isEditing ? (
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setIsEditing(true)}
                >
                  <Edit2 className="h-4 w-4 mr-2" />
                  Modifier
                </Button>
              ) : (
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => {
                      setIsEditing(false);
                      setFormData({
                        firstname: user?.firstname || "",
                        lastname: user?.lastname || "",
                        email: user?.email || "",
                      });
                    }}
                  >
                    <X className="h-4 w-4 mr-2" />
                    Annuler
                  </Button>
                  <Button
                    size="sm"
                    onClick={handleSaveProfile}
                    disabled={loading}
                  >
                    <Save className="h-4 w-4 mr-2" />
                    Enregistrer
                  </Button>
                </div>
              )}
            </div>
          </CardHeader>
          <CardContent>
            <div className="flex items-center gap-6 mb-6">
              <Avatar className="h-20 w-20">
                <AvatarFallback className="text-2xl bg-primary text-primary-foreground">
                  {getInitials()}
                </AvatarFallback>
              </Avatar>
              <div>
                <h3 className="text-xl font-semibold">
                  {user?.firstname} {user?.lastname}
                </h3>
                <p className="text-muted-foreground">{user?.email}</p>
                {user?.role && (
                  <span className="inline-block mt-2 px-3 py-1 text-xs font-medium bg-primary/10 text-primary rounded-full">
                    {user.role}
                  </span>
                )}
              </div>
            </div>

            <Separator className="my-6" />

            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-2">
                <Label htmlFor="firstname">Prénom</Label>
                <Input
                  id="firstname"
                  name="firstname"
                  value={formData.firstname}
                  onChange={handleInputChange}
                  disabled={!isEditing}
                  placeholder="Votre prénom"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="lastname">Nom</Label>
                <Input
                  id="lastname"
                  name="lastname"
                  value={formData.lastname}
                  onChange={handleInputChange}
                  disabled={!isEditing}
                  placeholder="Votre nom"
                />
              </div>
              <div className="space-y-2 md:col-span-2">
                <Label htmlFor="email" className="flex items-center gap-2">
                  <Mail className="h-4 w-4" />
                  Email
                  {!canEditEmail && (
                    <span className="text-xs text-muted-foreground ml-2">
                      (Non modifiable)
                    </span>
                  )}
                </Label>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  disabled={!isEditing || !canEditEmail}
                  placeholder="votre.email@example.com"
                />
                {!canEditEmail && (
                  <p className="text-xs text-muted-foreground">
                    Les étudiants et enseignants ne peuvent pas modifier leur email
                  </p>
                )}
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Change Password Card */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <Lock className="h-5 w-5" />
                  Sécurité
                </CardTitle>
                <CardDescription>
                  Changez votre mot de passe pour sécuriser votre compte
                </CardDescription>
              </div>
              {!isChangingPassword && (
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setIsChangingPassword(true)}
                >
                  Changer le mot de passe
                </Button>
              )}
            </div>
          </CardHeader>
          {isChangingPassword && (
            <CardContent>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="password">Nouveau mot de passe</Label>
                  <Input
                    id="password"
                    name="password"
                    type="password"
                    value={passwordData.password}
                    onChange={handlePasswordChange}
                    placeholder="Entrez votre nouveau mot de passe"
                  />
                  <p className="text-xs text-muted-foreground">
                    Le mot de passe doit contenir au moins 8 caractères
                  </p>
                </div>
                <div className="space-y-2">
                  <Label htmlFor="repeatPassword">Confirmer le mot de passe</Label>
                  <Input
                    id="repeatPassword"
                    name="repeatPassword"
                    type="password"
                    value={passwordData.repeatPassword}
                    onChange={handlePasswordChange}
                    placeholder="Confirmez votre nouveau mot de passe"
                  />
                </div>
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    onClick={() => {
                      setIsChangingPassword(false);
                      setPasswordData({
                        password: "",
                        repeatPassword: "",
                      });
                    }}
                  >
                    Annuler
                  </Button>
                  <Button
                    onClick={handleChangePassword}
                    disabled={loading}
                  >
                    <Save className="h-4 w-4 mr-2" />
                    Enregistrer le nouveau mot de passe
                  </Button>
                </div>
              </div>
            </CardContent>
          )}
        </Card>

        {/* Account Information Card */}
        <Card>
          <CardHeader>
            <CardTitle>Informations du Compte</CardTitle>
            <CardDescription>
              Détails supplémentaires sur votre compte
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <div>
                  <p className="font-medium">Statut du compte</p>
                  <p className="text-sm text-muted-foreground">
                    {user?.enabled !== false ? "Actif" : "Inactif"}
                  </p>
                </div>
              </div>
              {user?.createdAt && (
                <>
                  <Separator />
                  <div className="flex justify-between items-center">
                    <div>
                      <p className="font-medium">Date de création</p>
                      <p className="text-sm text-muted-foreground">
                        {new Date(user.createdAt).toLocaleDateString('fr-FR', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric'
                        })}
                      </p>
                    </div>
                  </div>
                </>
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
