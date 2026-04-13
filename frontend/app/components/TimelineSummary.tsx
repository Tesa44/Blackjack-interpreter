import React from "react";
import LayoutSection from "../layouts/LayoutSection";
import type { TimelineResult } from "~/types/simulation";

interface TimelineSummaryProps {
  timelineResults: TimelineResult[];
}

const TimelineSummary: React.FC<TimelineSummaryProps> = ({ timelineResults }) => {
  const rounds = timelineResults[0]?.rounds ?? [];
  const timeline = rounds.map((round) => {
    if (round.result === "PLAYER_WIN") {
      return { label: "W", color: "text-emerald-300" };
    }
    if (round.result === "DEALER_WIN") {
      return { label: "L", color: "text-rose-300" };
    }
    return { label: "D", color: "text-amber-300" };
  });

  return (
    <LayoutSection
      eyebrow="Timeline"
      title="Game Flow Summary"
      description="A round-by-round snapshot of wins, losses, and draws across the simulation."
      headerAside={
        <div className="flex flex-wrap gap-2 text-xs font-semibold uppercase tracking-wide">
          <span className="rounded-full border border-emerald-400/30 bg-emerald-500/10 px-3 py-1 text-emerald-200">
            W = Win
          </span>
          <span className="rounded-full border border-rose-400/30 bg-rose-500/10 px-3 py-1 text-rose-200">
            L = Loss
          </span>
          <span className="rounded-full border border-amber-400/30 bg-amber-500/10 px-3 py-1 text-amber-200">
            D = Draw
          </span>
        </div>
      }
    >
      {timeline.length > 0 ? (
        <div className="rounded-2xl border border-white/10 bg-white/5 p-5">
          <div className="mb-4 flex items-center justify-between">
            <p className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-400">
              Sequence
            </p>
            <p className="text-sm text-slate-400">{timeline.length} rounds</p>
          </div>
          <div className="flex flex-wrap gap-x-3 gap-y-3 font-mono text-lg font-semibold tracking-wide">
            {timeline.map(({ label, color }, index) => (
              <span
                key={`${label}-${index}`}
                className={`inline-flex h-11 w-11 items-center justify-center rounded-xl border border-white/10 bg-slate-900/80 text-2xl shadow-[0_12px_30px_rgba(15,23,42,0.25)] ${color}`}
              >
                {label}
              </span>
            ))}
          </div>
        </div>
      ) : (
        <div className="rounded-2xl border border-white/10 bg-white/5 p-6 text-sm text-slate-300">
          No timeline data yet. Run a command to generate round flow.
        </div>
      )}
    </LayoutSection>
  );
};

export default TimelineSummary;
