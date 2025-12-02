import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Page from "./pages/dashboard";
import { TimetableUpload } from './pages/gestion_emplois/TimetableUpload.jsx';
import { TimetableList } from './pages/gestion_emplois/TimetableList.jsx';
import TimetableExtractor from './pages/gestion_emplois/TimetableExtractor.jsx';
import AbsencePage from './pages/gestion_absences/AbsencePage.jsx';
import HistoriqueAbsences from "./pages/gestion_absences/HistoriqueAbsences.jsx";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Page />}>         
          <Route index element={<div>Bienvenue sur le dashboard</div>} />        
         <Route path="upload" element={<TimetableUpload />} />
          <Route path="extract" element={<TimetableExtractor />} />
          <Route path="timetable" element={<TimetableList />} />
          <Route path="upload/edit/:id" element={<TimetableUpload />} />
           <Route path="createabsence" element={<AbsencePage />} />
            <Route path="absence" element={<HistoriqueAbsences />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
