import Chart from "./Chart";
import SimulationStats from "./SimulationStats";
import simulationData from "../data/blackjackSimulation.json";

export default function TopSection() {
  return (
    <div className="min-h-0 min-w-0 space-y-6">
      <section className="rounded-[2rem] border border-white/10 bg-[radial-gradient(circle_at_top_left,_rgba(34,211,238,0.14),_transparent_40%),linear-gradient(180deg,rgba(15,23,42,0.96),rgba(2,6,23,0.98))] p-6 text-slate-50 shadow-[0_30px_80px_rgba(2,6,23,0.45)] sm:p-8">
        <div className="mb-6 flex flex-col gap-3">
          <p className="text-sm font-medium uppercase tracking-[0.3em] text-cyan-300">
            Simulation
          </p>
          <h1 className="text-3xl font-bold text-white sm:text-4xl">
            Player Balance Trajectory
          </h1>
          <p className="max-w-2xl text-sm text-slate-300">
            Visualize how the bankroll evolves round by round with a stronger,
            dashboard-style presentation.
          </p>
        </div>
        <Chart data={simulationData} />
      </section>

      <section className="rounded-[2rem] border border-white/10 bg-white/5 p-6 text-slate-50 shadow-[0_30px_80px_rgba(2,6,23,0.35)]">
        <h2 className="mb-4 text-xl font-bold text-white">Simulation Stats</h2>
        <SimulationStats data={simulationData} />
      </section>
    </div>
  );
}
