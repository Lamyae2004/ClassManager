import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Page from "./pages/dashboard";
import { TimetableUpload } from './pages/gestion_emplois/TimetableUpload.jsx';
import { TimetableList } from './pages/gestion_emplois/TimetableList.jsx';
import Login from "./pages/auth/Login";
import VerifyEmail from "./pages/auth/VerifyEmail";
import VerifyOtp from "./pages/auth/VerifyOtp";
import SetUpPassword from "./pages/auth/SetUpPassword";
import AddStudents from "./pages/gestion_users/AddStudents";
import AddTeachers from "./pages/gestion_users/AddTeachers";
import { AuthProvider } from "./context/AuthContext";


function App() {
  return (
  <Router>
    <AuthProvider>
      <Routes>
          <Route path="login" element={<Login />} />
          <Route path="verifyEmail" element={<VerifyEmail mode="validate"/>} />
          <Route path="verifyOtp" element={<VerifyOtp mode="validate"/>} />
          <Route path="setUpPassword" element={<SetUpPassword mode="validate"/>} />
          <Route path="forgotPassword" element={<VerifyEmail mode="forgot" />} />
          <Route path="forgotOtp" element={<VerifyOtp mode="forgot" />}/>
          <Route path="resetPassword" element={<SetUpPassword mode="forgot" />}/>
          <Route path="/" element={<Page />}>         
          <Route index element={<div>Bienvenue sur le dashboard</div>} />        
          <Route path="upload" element={<TimetableUpload />} />
          <Route path="timetable" element={<TimetableList />} />
          <Route path="upload/edit/:id" element={<TimetableUpload />} />
          <Route path="add-Students" element={<AddStudents />} />
          <Route path="add-Teachers" element={<AddTeachers />} />
        </Route>
      </Routes>
    </AuthProvider>
  </Router>
  );
}

export default App;