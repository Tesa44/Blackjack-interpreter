interface SimulationStatsProps {
  data: {
    simulationData: Array<{
      balance: number;
      round: number;
    }>;
  };
}

export default function SimulationStats({ data }: SimulationStatsProps) {
  const simulationData = data.simulationData;
  const netProfit =
    simulationData[simulationData.length - 1].balance -
    simulationData[0].balance;

  return (
    <section className="rounded-[2rem] border border-white/10 bg-white/5 p-6 shadow-[0_30px_80px_rgba(2,6,23,0.35)]">
      <h2 className="mb-4 text-xl font-bold text-white">Simulation Stats</h2>
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <article className="rounded-xl border border-white/10 bg-slate-900/80 p-4 shadow-[0_20px_60px_rgba(15,23,42,0.2)]">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
            Total Rounds
          </p>
          <p className="mt-3 text-3xl font-bold text-white">
            {simulationData.length}
          </p>
        </article>
        <article className="rounded-xl border border-white/10 bg-slate-900/80 p-4 shadow-[0_20px_60px_rgba(15,23,42,0.2)]">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
            Starting Balance
          </p>
          <p className="mt-3 text-3xl font-bold text-white">
            ${simulationData[0].balance}
          </p>
        </article>
        <article className="rounded-xl border border-white/10 bg-slate-900/80 p-4 shadow-[0_20px_60px_rgba(15,23,42,0.2)]">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
            Final Balance
          </p>
          <p className="mt-3 text-3xl font-bold text-white">
            ${simulationData[simulationData.length - 1].balance}
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
    </section>
  );
}
