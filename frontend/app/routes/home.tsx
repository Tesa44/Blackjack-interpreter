import type { Route } from "./+types/home";
import simulationData from "../data/blackjackSimulation.json";
import Chart from "../components/Chart";
import SimulationStats from "../components/SimulationStats";
import RoundResults from "../components/RoundResults";
import StatsSummary from "../components/StatsSummary";
import TimelineSummary from "~/components/TimelineSummary";
import { showResults } from "../data/showResults.json";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Blackjack Interpreter" },
    { name: "description", content: "Blackjack Game Interpreter" },
  ];
}

export default function Home() {
  return (
    <div className="flex min-h-screen w-full max-w-full flex-col overflow-x-hidden">
      {/* Top Section - Command + Chart/Stats */}
      <div className="flex flex-1 flex-col lg:flex-row">
        {/* Chart and Stats Section - 80% */}
        <div className="min-h-0 min-w-0 w-full flex flex-col lg:w-4/5">
          {/* Chart Section */}
          <section className="bg-blue-50 p-8 flex-1">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">
              Player Balance Simulation
            </h1>
            <Chart data={simulationData} />
          </section>

          {/* Simulation Stats Section */}
          <section className="bg-gray-100 p-6 border-t border-gray-300">
            <h2 className="text-xl font-bold text-gray-800 mb-4">
              Simulation Stats
            </h2>
            <SimulationStats data={simulationData} />
          </section>
        </div>

        {/* Command Section - 20% */}
        <section className="w-full min-w-0 bg-gray-100 p-6 border-r border-gray-300 flex flex-col lg:w-1/5">
          <h2 className="text-xl font-bold text-gray-800 mb-4">
            Command Input
          </h2>
          <div className="flex-1 flex flex-col">
            <textarea
              className="flex-1 p-3 border border-gray-300 rounded-md resize-none mb-4 text-gray-600"
              placeholder="Enter your command here..."
            />
            <button className="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-md">
              Send Command
            </button>
          </div>
        </section>
      </div>

      <StatsSummary />

      <RoundResults showResults={showResults} />

      <TimelineSummary />
    </div>
  );
}
