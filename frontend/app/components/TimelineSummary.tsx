import React from "react";
import { timelineResults } from "../data/timelineSimulation.json";

const TimelineSummary: React.FC = () => {
  const [{ rounds }] = timelineResults;
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
    <section className="rounded-[2rem] border border-white/10 bg-slate-950 p-6 text-slate-50 shadow-[0_30px_80px_rgba(2,6,23,0.4)]">
      <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <p className="text-sm font-medium uppercase tracking-[0.3em] text-cyan-300">
            Timeline
          </p>
          <h2 className="mt-2 text-3xl font-bold text-white">
            Game Flow Summary
          </h2>
          <p className="mt-2 max-w-2xl text-sm text-slate-300">
            A round-by-round snapshot of wins, losses, and draws across the simulation.
          </p>
        </div>
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
      </div>

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
    </section>
  );
};

export default TimelineSummary;
