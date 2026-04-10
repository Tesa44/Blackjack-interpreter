import Chart from "./Chart";
import CommandBox from "./CommandBox";
import SimulationStats from "./SimulationStats";
import simulationData from "../data/blackjackSimulation.json";

export default function TopSection() {
  return (
    <section className="border-b border-white/10 bg-slate-950 px-6 py-8 text-slate-50 sm:px-8">
      <div className="mx-auto flex max-w-7xl flex-1 flex-col gap-6 lg:flex-row">
        <div className="min-h-0 min-w-0 w-full flex flex-col gap-6 lg:w-4/5">
          <Chart data={simulationData} />
          <SimulationStats data={simulationData} />
        </div>
        <CommandBox />
      </div>
    </section>
  );
}
