import LayoutSection from "~/layouts/LayoutSection";
import type { SimulationSummary } from "~/types/simulation";

interface SimulationStatsProps {
  summary: SimulationSummary;
}

export default function SimulationStats({ summary }: SimulationStatsProps) {
  const netProfit = summary.finalBalance - summary.initialBalance;

  return (
    <LayoutSection
      eyebrow="Betting summary"
      title="Balance view and total net profit"
    >
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <article className="rounded-xl border border-white/10 bg-slate-900/80 p-4 shadow-[0_20px_60px_rgba(15,23,42,0.2)]">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
            Total Rounds
          </p>
          <p className="mt-3 text-3xl font-bold text-white">
            {summary.roundsPlayed}
          </p>
        </article>
        <article className="rounded-xl border border-white/10 bg-slate-900/80 p-4 shadow-[0_20px_60px_rgba(15,23,42,0.2)]">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
            Starting Balance
          </p>
          <p className="mt-3 text-3xl font-bold text-white">
            ${summary.initialBalance}
          </p>
        </article>
        <article className="rounded-xl border border-white/10 bg-slate-900/80 p-4 shadow-[0_20px_60px_rgba(15,23,42,0.2)]">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
            Final Balance
          </p>
          <p className="mt-3 text-3xl font-bold text-white">
            ${summary.finalBalance}
          </p>
        </article>
        <article className="rounded-xl border border-white/10 bg-slate-900/80 p-4 shadow-[0_20px_60px_rgba(15,23,42,0.2)]">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
            Net Profit
          </p>
          <p
            className={`mt-3 text-3xl font-bold ${netProfit >= 0 ? "text-emerald-300" : "text-rose-300"}`}
          >
            {netProfit >= 0 ? "+" : ""}${netProfit}
          </p>
        </article>
      </div>
    </LayoutSection>
  );
}
