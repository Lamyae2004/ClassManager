import { useContext, useEffect, useState } from "react";
import { AuthContext } from "@/context/AuthContext";
import StatCard from "@/components/ui/StatCard";
import { Users, GraduationCap, School, UserX } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

// Recharts
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from "recharts";

export default function DashboardStats() {
const API_URL =  "http://localhost:8080";
  const { user } = useContext(AuthContext);
  const [stats, setStats] = useState({});
  const [studentsStatusByClass, setStudentsStatusByClass] = useState([]);


  const formatClassLabel = (value) => {
    const filieres = ["INFO", "MECA", "ELEC", "CIVIL", "INDUS", "RST"];
    const filiere = filieres.find(f => value.startsWith(f));
    if (!filiere) return value;

    const classe = value.replace(filiere, "");
    return `${classe}-${filiere}`;
  };

  useEffect(() => {
    if (!user) return;

    if (user.role === "ADMIN") {
      Promise.all([
        fetch(`${API_URL}/api/users/dashboard/stats`,{credentials: "include"}).then(res => res.json()),
        fetch(`${API_URL}/absences/classes-by-absence`).then(res => res.json())
      ])
        .then(([dashboardStats, absenceData]) => {
          const classesByAbsence = absenceData
            .map(d => ({
              class: `${d.classe}-${d.filiere}`,
              absenceRate: Number(d.absenceRate.toFixed(2))
            }))
            .sort((a, b) => b.absenceRate - a.absenceRate);

          setStats({
            ...dashboardStats,
            classesByAbsence
          });
        })
        .catch(console.error);
    }

    if (user.role === "TEACHER") {
      fetch(`${API_URL}/emploi/my-classes/${user.id}`)
        .then(res => res.json())
        .then(data => setStats(data))
        .catch(err => console.error(err));

      fetch(`${API_URL}/emploi/students-status/${user.id}`)
        .then(res => res.json())
        .then(data => {
          const formatted = data.map(d => ({
            ...d,
            classLabel: `${d.classe} - ${d.filiere}`
          }));
          setStudentsStatusByClass(formatted);
        })
        .catch(console.error);

      fetch(`${API_URL}/absences/dépassertaux/${user.id}`)
        .then(res => res.json())
        .then(data => {
          const formatted = data.map(d => ({
            classLabel: `${d.classe} - ${d.filiere}`,
            respectTaux: d.activeStudents,
            depasseTaux: d.inactiveStudents
          }));
          setStudentsStatusByClass(formatted);
        })
        .catch(console.error);
    }
  }, [user]);



  const defaultStats = {
    totalTeachers: 25,
    activeTeachers: 20,
    inactiveTeachers: 5,
    activeStudents: 320,
    inactiveStudents: 45,
    totalClasses: 20,
    studentsPerClass: [
      { class: "2GI1", students: 30 },
      { class: "2GI2", students: 28 },
      { class: "2GI3", students: 35 },
      { class: "2GI4", students: 25 },
    ],
    myClasses: 3,
    myStudents: 78,
    classesByAbsence: [
      { class: "2GI1", absenceRate: 12 },
      { class: "2GI2", absenceRate: 8 },
      { class: "2GI3", absenceRate: 15 },
      { class: "2GI4", absenceRate: 5 },
    ],
    studentsOverAbsence: [
      { name: "Dépasse taux", value: 5 },
      { name: "Respect taux", value: 18 },
    ],
  };

  const safeStats = {
    ...defaultStats,
    ...stats,
    classesByAbsence: stats.classesByAbsence ?? defaultStats.classesByAbsence
  };

  const chartColors = {
    blue: "#3B82F6",
    green: "#10B981",
    red: "#EF4444",
    purple: "#8B5CF6",
    orange: "#F59E0B",
    indigo: "#6366F1",
    violet: "#8B5CF6",      // violet principal
    lightBlue: "#60A5FA"
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-blue-50 space-y-6 p-6">
      {/* TITRE */}
     {/* <div>
       <h1 className="text-2xl font-bold text-gray-800">
          Dashboard {user.role === "ADMIN" ? "Administrateur" : "Professeur"}
        </h1>
        <p className="text-gray-600 text-sm mt-1">
          Statistiques et indicateurs de performance
        </p>
      </div>*/}

      {/* KPI CARDS */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {user.role === "ADMIN" && (
          <>
            <StatCard
              title="Professeurs actifs"
              value={safeStats.activeTeachers}
              icon={Users}
              color="blue"
            />
            <StatCard
              title="Professeurs inactifs"
              value={safeStats.inactiveTeachers}
              icon={UserX}
              color="red"
            />
            <StatCard
              title="Étudiants actifs"
              value={safeStats.activeStudents}
              icon={GraduationCap}
              color="green"
            />
            <StatCard
              title="Étudiants inactifs"
              value={safeStats.inactiveStudents}
              icon={UserX}
              color="orange"
            />
          </>
        )}

        {user.role === "TEACHER" && (
          <>
            <StatCard
              title="Mes classes"
              value={safeStats.myClasses}
              icon={School}
              color="blue"
            />
           
          </>
        )}
      </div>

      {/* GRAPHIQUES */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* ADMIN : Bar Chart étudiants par classe */}
        {user.role === "ADMIN" && (
          <>
            <Card className="border shadow-lg rounded-xl hover:shadow-2xl transition-shadow duration-300">
              <CardHeader className="pb-3">
                <CardTitle className="text-lg font-semibold">
                  Étudiants par classe
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="h-80">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                      data={safeStats.studentsPerClass}
                      margin={{ top: 20, right: 30, left: 0, bottom: 10 }}
                    >
                      <defs>
                        <linearGradient id="barGradient" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor={chartColors.violet} stopOpacity={0.8} />
                          <stop offset="100%" stopColor={chartColors.lightBlue} stopOpacity={0.4} />
                        </linearGradient>
                      </defs>
                      <XAxis
                        dataKey="class"
  tickFormatter={formatClassLabel}
                        tick={{ fill: '#374151', fontSize: 14, fontWeight: 500 }}
                        axisLine={{ stroke: '#E5E7EB' }}
                        tickLine={false}
                      />
                      <YAxis
                        tick={{ fill: '#374151', fontSize: 14 }}
                        axisLine={{ stroke: '#E5E7EB' }}
                        tickLine={false}
                      />
                      <Tooltip
                        cursor={{ fill: 'rgba(59,130,246,0.1)' }}
                        contentStyle={{
                          backgroundColor: 'white',
                          border: '1px solid #E5E7EB',
                          borderRadius: '8px',
                          fontSize: '13px',
                          boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
                        }}
                      />
                      <Bar
                        dataKey="students"
                        fill="url(#barGradient)"
                        radius={[6, 6, 0, 0]}
                        animationDuration={1000}
                        animationEasing="ease-out"
                        cursor="pointer"
                      />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </CardContent>
            </Card>


            {/* Classement des absences */}
            <Card className="border shadow-lg rounded-xl hover:shadow-2xl transition-shadow duration-300">
              <CardHeader className="pb-3">
                <CardTitle className="text-lg font-semibold">
                  Taux d'absences par classe
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="h-80">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                      data={safeStats.classesByAbsence}
                      margin={{ top: 20, right: 30, left: -10, bottom: 10 }}
                    >
                      <defs>
                        <linearGradient id="absenceGradient" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor={chartColors.red} stopOpacity={0.8} />
                          <stop offset="100%" stopColor={chartColors.red} stopOpacity={0.4} />
                        </linearGradient>
                      </defs>
                      <XAxis
                        dataKey="class"
                        tick={{ fill: "#374151", fontSize: 14, fontWeight: 500 }}
                        axisLine={{ stroke: "#E5E7EB" }}
                        tickLine={false}
                      />
                      <YAxis
                        domain={[0, 100]}
                        tickFormatter={(v) => `${v}%`}
                        tick={{ fill: "#374151", fontSize: 14 }}
                        axisLine={{ stroke: "#E5E7EB" }}
                        tickLine={false}
                      />
                      <Tooltip
                        formatter={(value) => [`${value}%`, "Taux"]}
                        cursor={{ fill: 'rgba(239,68,68,0.1)' }}
                        contentStyle={{
                          backgroundColor: "white",
                          borderRadius: 8,
                          border: "1px solid #E5E7EB",
                          fontSize: 13,
                          boxShadow: "0 4px 12px rgba(0,0,0,0.1)"
                        }}
                      />
                      <Bar
                        dataKey="absenceRate"
                        fill="url(#absenceGradient)"
                        radius={[6, 6, 0, 0]}
                        animationDuration={1000}
                        animationEasing="ease-out"
                        cursor="pointer"
                      />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </CardContent>
            </Card>

          </>
        )}

        {/* TEACHER : Graphiques */}
        {user.role === "TEACHER" && studentsStatusByClass.length > 0 && (
          <>
            <Card className="border shadow-lg rounded-xl hover:shadow-2xl transition-shadow duration-300">
              <CardHeader className="pb-3">
                <CardTitle className="text-lg font-semibold">
                  Étudiants actifs / non actifs par classe
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="h-80">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                      data={studentsStatusByClass}
                      margin={{ top: 20, right: 30, left: -10, bottom: 10 }}
                    >
                      <defs>
                        <linearGradient id="activeGradient" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor={chartColors.lightBlue} stopOpacity={0.8} />
                          <stop offset="100%" stopColor={chartColors.lightBlue} stopOpacity={0.4} />
                        </linearGradient>
                        <linearGradient id="inactiveGradient" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor={chartColors.violet} stopOpacity={0.8} />
                          <stop offset="100%" stopColor={chartColors.violet} stopOpacity={0.4} />
                        </linearGradient>
                      </defs>
                      <XAxis
                        dataKey="classLabel"
                        tick={{ fill: "#374151", fontSize: 14, fontWeight: 500 }}
                        axisLine={{ stroke: "#E5E7EB" }}
                        tickLine={false}
                      />
                      <YAxis
                        tick={{ fill: "#374151", fontSize: 14 }}
                        axisLine={{ stroke: "#E5E7EB" }}
                        tickLine={false}
                      />
                      <Tooltip
                        contentStyle={{
                          backgroundColor: "white",
                          borderRadius: 8,
                          border: "1px solid #E5E7EB",
                          fontSize: 13,
                          boxShadow: "0 4px 12px rgba(0,0,0,0.1)"
                        }}
                      />
                      <Legend />
                      <Bar
                        dataKey="activeStudents"
                        stackId="a"
                        fill="url(#activeGradient)"
                        radius={[6, 6, 0, 0]}
                        animationDuration={1000}
                        animationEasing="ease-out"
                        cursor="pointer"
                        name="Actifs"
                      />
                      <Bar
                        dataKey="inactiveStudents"
                        stackId="a"
                        fill="url(#inactiveGradient)"
                        radius={[6, 6, 0, 0]}
                        animationDuration={1000}
                        animationEasing="ease-out"
                        cursor="pointer"
                        name="Non actifs"
                      />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </CardContent>
            </Card>


            <Card className="border shadow-lg rounded-xl hover:shadow-2xl transition-shadow duration-300">
              <CardHeader className="pb-3">
                <CardTitle className="text-lg font-semibold">
                  Respect / dépassement du taux d'absence
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="h-80">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                      data={studentsStatusByClass}
                      margin={{ top: 20, right: 30, left: -10, bottom: 10 }}
                    >
                      <defs>
                        <linearGradient id="respectGradient" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor={chartColors.lightBlue} stopOpacity={0.8} />
                          <stop offset="100%" stopColor={chartColors.lightBlue} stopOpacity={0.4} />
                        </linearGradient>
                        <linearGradient id="depasseGradient" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="0%" stopColor={chartColors.violet} stopOpacity={0.8} />
                          <stop offset="100%" stopColor={chartColors.violet} stopOpacity={0.4} />
                        </linearGradient>
                      </defs>
                      <XAxis
                        dataKey="classLabel"
                        tick={{ fill: "#374151", fontSize: 14, fontWeight: 500 }}
                        axisLine={{ stroke: "#E5E7EB" }}
                        tickLine={false}
                      />
                      <YAxis
                        tick={{ fill: "#374151", fontSize: 14 }}
                        axisLine={{ stroke: "#E5E7EB" }}
                        tickLine={false}
                      />
                      <Tooltip
                        contentStyle={{
                          backgroundColor: "white",
                          borderRadius: 8,
                          border: "1px solid #E5E7EB",
                          fontSize: 13,
                          boxShadow: "0 4px 12px rgba(0,0,0,0.1)"
                        }}
                      />
                      <Legend />
                      <Bar
                        dataKey="respectTaux"
                        stackId="a"
                        fill="url(#respectGradient)"
                        radius={[6, 6, 0, 0]}
                        animationDuration={1000}
                        animationEasing="ease-out"
                        cursor="pointer"
                        name="Respect du taux"
                      />
                      <Bar
                        dataKey="depasseTaux"
                        stackId="a"
                        fill="url(#depasseGradient)"
                        radius={[6, 6, 0, 0]}
                        animationDuration={1000}
                        animationEasing="ease-out"
                        cursor="pointer"
                        name="Dépasse le taux"
                      />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </CardContent>
            </Card>

          </>
        )}
      </div>
    </div>
  );
}