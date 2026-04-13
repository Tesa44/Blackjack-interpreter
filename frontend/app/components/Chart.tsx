import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import type { SimulationPlot } from "~/types/simulation";

interface ChartProps {
  plot: SimulationPlot;
}

export default function Chart({ plot }: ChartProps) {
  const chartData = plot.balanceHistory.map((balance, index) => ({
    round: index,
    balance,
  }));
  const hasData = chartData.length > 0;

  return (
    <section className="rounded-[2rem] border border-white/10 bg-[radial-gradient(circle_at_top_left,_rgba(34,211,238,0.14),_transparent_40%),linear-gradient(180deg,rgba(15,23,42,0.96),rgba(2,6,23,0.98))] p-6 shadow-[0_30px_80px_rgba(2,6,23,0.45)] sm:p-8">
      <div className="mb-6 flex flex-col gap-3">
        <p className="text-sm font-medium uppercase tracking-[0.3em] text-cyan-300">
          Simulation
        </p>
        <h1 className="text-3xl font-bold text-white sm:text-4xl">
          Player Balance Trajectory
        </h1>
      </div>
      {hasData ? (
        <div className="rounded-2xl border border-white/10 bg-slate-900/80 p-4 shadow-[0_20px_60px_rgba(15,23,42,0.35)] sm:p-6">
          <ResponsiveContainer width="100%" height={400}>
            <LineChart
              data={chartData}
              margin={{ top: 12, right: 20, left: 8, bottom: 12 }}
            >
              <CartesianGrid
                stroke="rgba(148, 163, 184, 0.16)"
                strokeDasharray="3 3"
              />
              <XAxis
                dataKey="round"
                tick={{ fill: "#cbd5e1", fontSize: 12 }}
                axisLine={{ stroke: "rgba(148, 163, 184, 0.3)" }}
                tickLine={{ stroke: "rgba(148, 163, 184, 0.3)" }}
                label={{
                  value: "Rounds",
                  position: "insideBottomRight",
                  offset: -5,
                  fill: "#94a3b8",
                }}
              />
              <YAxis
                tick={{ fill: "#cbd5e1", fontSize: 12 }}
                axisLine={{ stroke: "rgba(148, 163, 184, 0.3)" }}
                tickLine={{ stroke: "rgba(148, 163, 184, 0.3)" }}
                label={{
                  value: "Amount ($)",
                  angle: -90,
                  position: "insideLeft",
                  fill: "#94a3b8",
                }}
              />
              <Tooltip
                contentStyle={{
                  backgroundColor: "#0f172a",
                  border: "1px solid rgba(34, 211, 238, 0.2)",
                  borderRadius: "12px",
                  color: "#e2e8f0",
                }}
                formatter={(value) => `$${value}`}
                labelFormatter={(label) => `Round ${label}`}
              />
              <Legend wrapperStyle={{ color: "#cbd5e1" }} />
              <Line
                type="monotone"
                dataKey="balance"
                stroke="#22d3ee"
                strokeWidth={3}
                dot={{ fill: "#22d3ee", stroke: "#0f172a", strokeWidth: 2 }}
                activeDot={{
                  r: 7,
                  fill: "#67e8f9",
                  stroke: "#082f49",
                  strokeWidth: 2,
                }}
                name="Player Balance"
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      ) : (
        <div className="rounded-2xl border border-white/10 bg-slate-900/80 px-6 py-10 text-center text-slate-300">
          No simulation data yet. Send a command to run the simulator.
        </div>
      )}
    </section>
  );
}
