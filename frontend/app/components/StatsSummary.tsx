import statsData from "../data/statsResults.json";

type StatsEntry = {
  label: string;
  games: number;
  winRate: string;
  loseRate: string;
};

function parseStatsText(text: string): StatsEntry[] {
  return text
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter(Boolean)
    .map((line) => {
      const match = line.match(
        /^(.+?)\s*->\s*Games:\s*(\d+)\s*\|\s*Win:\s*([\d.]+%)\s*\|\s*Lose:\s*([\d.]+%)$/,
      );

      if (!match) {
        return null;
      }

      const [, label, games, winRate, loseRate] = match;
      return {
        label,
        games: Number(games),
        winRate,
        loseRate,
      };
    })
    .filter((entry): entry is StatsEntry => entry !== null);
}

export default function StatsSummary() {
  const summaries = statsData.statsResults.map((result) => ({
    ...result,
    entries: parseStatsText(result.text),
  }));

  return (
    <section className="bg-slate-950 px-6 py-8 text-slate-50 sm:px-8">
      <div className="mx-auto max-w-7xl">
        <div className="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <p className="text-sm font-medium uppercase tracking-[0.3em] text-cyan-300">
              Stats Summary
            </p>
            <h2 className="mt-2 text-3xl font-bold text-white">
              Filtered game breakdown
            </h2>
          </div>
          <p className="max-w-2xl text-sm text-slate-300">
            Parsed from the simulation statistics output and grouped into a
            compact comparison grid.
          </p>
        </div>

        <div className="space-y-6">
          {summaries.map((summary, index) => (
            <div
              key={`${summary.filter}-${index}`}
              className="overflow-hidden rounded-2xl border border-white/10 bg-white/5 shadow-[0_20px_60px_rgba(15,23,42,0.35)]"
            >
              <div className="border-b border-white/10 px-5 py-4 sm:px-6">
                <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                  <div>
                    <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
                      Filter
                    </p>
                    <h3 className="mt-1 text-xl font-semibold text-white">
                      {summary.filter}
                    </h3>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    {summary.groupBy.map((group) => (
                      <span
                        key={group}
                        className="rounded-full border border-cyan-400/30 bg-cyan-400/10 px-3 py-1 text-xs font-medium text-cyan-200"
                      >
                        Grouped by {group}
                      </span>
                    ))}
                  </div>
                </div>
              </div>

              <div className="grid gap-4 p-5 sm:grid-cols-2 xl:grid-cols-3 sm:p-6">
                {summary.entries.map((entry) => (
                  <article
                    key={entry.label}
                    className="rounded-xl border border-white/10 bg-slate-900/70 p-4"
                  >
                    <p className="text-sm font-medium text-slate-300">
                      {entry.label}
                    </p>
                    <p className="mt-3 text-3xl font-bold text-white">
                      {entry.games}
                      <span className="ml-2 text-sm font-medium text-slate-400">
                        games
                      </span>
                    </p>
                    <div className="mt-4 grid grid-cols-2 gap-3">
                      <div className="rounded-lg bg-emerald-500/10 px-3 py-3">
                        <p className="text-xs font-semibold uppercase tracking-wide text-emerald-300">
                          Win
                        </p>
                        <p className="mt-1 text-lg font-semibold text-emerald-200">
                          {entry.winRate}
                        </p>
                      </div>
                      <div className="rounded-lg bg-rose-500/10 px-3 py-3">
                        <p className="text-xs font-semibold uppercase tracking-wide text-rose-300">
                          Lose
                        </p>
                        <p className="mt-1 text-lg font-semibold text-rose-200">
                          {entry.loseRate}
                        </p>
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
