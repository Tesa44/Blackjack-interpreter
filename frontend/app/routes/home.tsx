import type { Route } from "./+types/home";
import { useState } from "react";
import CommandBox from "../components/CommandBox";
import RoundResults from "../components/RoundResults";
import StatsSummary from "../components/StatsSummary";
import TimelineSummary from "~/components/TimelineSummary";
import TopSection from "~/components/TopSection";
import CommandBoxLayout from "../layouts/CommandBoxLayout";
import dataFromApi from "../data/data-from-api.json";
import type { ApiSimulationResponse } from "~/types/simulation";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Blackjack Interpreter" },
    { name: "description", content: "Blackjack Game Interpreter" },
  ];
}

export default function Home() {
  const [dashboardData, setDashboardData] = useState<ApiSimulationResponse>(dataFromApi);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const sendCommand = async (command: string) => {
    setIsLoading(true);
    setErrorMessage(null);

    try {
      const response = await fetch("http://localhost:8080/api/execute", {
        method: "POST",
        headers: {
          "Content-Type": "text/plain",
        },
        body: command,
      });

      if (!response.ok) {
        throw new Error(`Request failed: ${response.status} ${response.statusText}`);
      }

      const data: ApiSimulationResponse = await response.json();
      setDashboardData(data);
    } catch (error) {
      const fallbackMessage =
        error instanceof Error
          ? error.message
          : "Failed to execute command. Please try again.";
      setErrorMessage(fallbackMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full max-w-full bg-slate-950 relative">
      <div className="mx-auto max-w-[1800px] gap-6 px-4 py-6 sm:px-6 lg:flex lg:items-start lg:gap-6 lg:px-8">
        <main className="min-w-0 space-y-6 lg:w-[calc(80%-0.75rem)]">
          {(dashboardData.plot || dashboardData.summary) ? (
            <TopSection plot={dashboardData.plot} summary={dashboardData.summary} />
          ) : null}
          {dashboardData.statsResults ? (
            <StatsSummary statsResults={dashboardData.statsResults} />
          ) : null}
          {dashboardData.showResults ? (
            <RoundResults showResults={dashboardData.showResults} />
          ) : null}
          {dashboardData.timelineResults ? (
            <TimelineSummary timelineResults={dashboardData.timelineResults} />
          ) : null}
        </main>

        <CommandBoxLayout>
          <CommandBox
            onSendCommand={sendCommand}
            isLoading={isLoading}
            errorMessage={errorMessage}
          />
        </CommandBoxLayout>
      </div>
    </div>
  );
}
