import type { Route } from "./+types/home";
import RoundResults from "../components/RoundResults";
import StatsSummary from "../components/StatsSummary";
import TimelineSummary from "~/components/TimelineSummary";
import { showResults } from "../data/showResults.json";
import TopSection from "~/components/TopSection";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Blackjack Interpreter" },
    { name: "description", content: "Blackjack Game Interpreter" },
  ];
}

export default function Home() {
  return (
    <div className="flex min-h-screen w-full max-w-full flex-col overflow-x-hidden">
      <TopSection />
      <StatsSummary />
      <RoundResults showResults={showResults} />
      <TimelineSummary />
    </div>
  );
}
