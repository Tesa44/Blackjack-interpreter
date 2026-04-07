import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";

export default function Chart({ data }) {
    return (
        <div className="bg-white rounded-lg shadow-lg p-4">
                  <ResponsiveContainer width="100%" height={400}>
                    <LineChart data={data.simulationData}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis 
                        dataKey="round" 
                        label={{ value: "Rounds", position: "insideBottomRight", offset: -5 }}
                      />
                      <YAxis 
                        label={{ value: "Amount ($)", angle: -90, position: "insideLeft" }}
                      />
                      <Tooltip 
                        formatter={(value) => `$${value}`}
                        labelFormatter={(label) => `Round ${label}`}
                      />
                      <Legend />
                      <Line 
                        type="monotone" 
                        dataKey="balance" 
                        stroke="#3b82f6" 
                        dot={{ fill: "#3b82f6" }}
                        activeDot={{ r: 6 }}
                        name="Player Balance"
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>
    )
}
