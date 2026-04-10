import type { Route } from "./+types/home";
import CommandBox from "../components/CommandBox";
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
    <div className="min-h-screen w-full max-w-full bg-slate-950 relative">
      <div className="mx-auto max-w-[1800px] gap-6 px-4 py-6 sm:px-6 lg:flex lg:items-start lg:gap-6 lg:px-8">
        <main className="min-w-0 space-y-6 lg:w-[calc(80%-0.75rem)]">
          <TopSection />
          <StatsSummary />
          <RoundResults showResults={showResults} />
          <TimelineSummary />
        </main>

        <aside className="hidden min-w-0 lg:block lg:shrink-0">
          <div className="fixed lg:top-6 lg:right-8 w-[calc(20%-0.75rem)]">
            <CommandBox />
          </div>
        </aside>
      </div>
    </div>
  );
}
