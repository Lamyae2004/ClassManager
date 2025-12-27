import { Card, CardContent } from "@/components/ui/card";

const colorClasses = {
  blue: { bg: "bg-blue-50", icon: "text-blue-600" },
  green: { bg: "bg-green-50", icon: "text-green-600" },
  red: { bg: "bg-red-50", icon: "text-red-600" },
  orange: { bg: "bg-orange-50", icon: "text-orange-600" },
  purple: { bg: "bg-purple-50", icon: "text-purple-600" },
};

function StatCard({ title, value, icon: Icon, color = "blue" }) {
  const colors = colorClasses[color] || colorClasses.blue;

  return (
    <Card className="border hover:border-gray-300 transition-colors">
      <CardContent className="p-5">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-gray-600 mb-1">{title}</p>
            <p className="text-2xl font-bold text-gray-800">{value}</p>
          </div>
          <div className={`p-3 rounded-lg ${colors.bg}`}>
            <Icon className={`w-6 h-6 ${colors.icon}`} />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

export default StatCard;