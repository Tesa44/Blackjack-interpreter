import Chart from "./Chart";
import SimulationStats from "./SimulationStats";
import LayoutSection from "../layouts/LayoutSection";
import type { SimulationPoint } from "~/types/simulation";

interface TopSectionProps {
  simulationData: SimulationPoint[];
}

export default function TopSection({ simulationData }: TopSectionProps) {
  const chartData = { simulationData };

  return (
    <div className="min-h-0 min-w-0 space-y-6">
      <LayoutSection
        eyebrow="Simulation"
        title="Player Balance Trajectory"
        description="Visualize how the bankroll evolves round by round with a stronger, dashboard-style presentation."
        titleTag="h1"
        className="bg-[radial-gradient(circle_at_top_left,_rgba(34,211,238,0.14),_transparent_40%),linear-gradient(180deg,rgba(15,23,42,0.96),rgba(2,6,23,0.98))] sm:p-8"
      >
        <Chart data={chartData} />

        <SimulationStats data={chartData} />
      </LayoutSection>
    </div>
  );
}
