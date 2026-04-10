import streakStatsData from "../data/statsResults-streak.json";

type RawStatsResult = {
  filter: string;
  groupBy: string[];
  text: string;
};

type GroupedEntry = {
  type: "grouped";
  label: string;
  games: number;
  winRate: string;
  loseRate: string;
};

type StreakEntry = {
  type: "streak";
  label: string;
  count: number;
  percentage: string;
};

type ParsedStatsBlock = {
  title: string;
  entries: Array<GroupedEntry | StreakEntry>;
};

function parseGroupedEntries(lines: string[]): GroupedEntry[] {
  return lines
    .map((line) =>
      line.match(
        /^(.+?)\s*->\s*Games:\s*(\d+)\s*\|\s*Win:\s*([\d.]+%)\s*\|\s*Lose:\s*([\d.]+%)$/
      )
    )
    .filter((match): match is RegExpMatchArray => match !== null)
    .map(([, label, games, winRate, loseRate]) => ({
      type: "grouped",
      label,
      games: Number(games),
      winRate,
      loseRate,
    }));
}

function parseStreakEntries(lines: string[]): ParsedStatsBlock | null {
  const [header, ...body] = lines;

  if (!header.endsWith(":")) {
    return null;
  }

  const entries = body
    .map((line) => line.match(/^(.+?):\s*(\d+)\s+times\s+\(([\d.]+%)\)$/))
    .filter((match): match is RegExpMatchArray => match !== null)
    .map(([, label, count, percentage]) => ({
      type: "streak" as const,
      label,
      count: Number(count),
      percentage,
    }));

  if (entries.length === 0) {
    return null;
  }

  return {
    title: header.slice(0, -1),
    entries,
  };
}

function parseStatsBlock(result: RawStatsResult): ParsedStatsBlock {
  const lines = result.text
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter(Boolean);

  const groupedEntries = parseGroupedEntries(lines);
  if (groupedEntries.length > 0) {
    return {
      title: "Grouped Results",
      entries: groupedEntries,
    };
  }

  const streakBlock = parseStreakEntries(lines);
  if (streakBlock) {
    return streakBlock;
  }

  return {
    title: "Raw Stats",
    entries: [],
  };
}

function renderGroupedEntry(entry: GroupedEntry) {
  return (
    <article
      key={entry.label}
      className="rounded-xl border border-white/10 bg-slate-900/70 p-4"
    >
      <p className="text-sm font-medium text-slate-300">{entry.label}</p>
      <p className="mt-3 text-3xl font-bold text-white">
        {entry.games}
        <span className="ml-2 text-sm font-medium text-slate-400">games</span>
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
  );
}

function renderStreakEntry(entry: StreakEntry) {
  return (
    <article
      key={entry.label}
      className="rounded-xl border border-white/10 bg-slate-900/70 p-4"
    >
      <p className="text-sm font-medium text-slate-300">{entry.label}</p>
      <p className="mt-3 text-3xl font-bold text-white">
        {entry.count}
        <span className="ml-2 text-sm font-medium text-slate-400">times</span>
      </p>
      <div className="mt-4 rounded-lg bg-amber-500/10 px-3 py-3">
        <p className="text-xs font-semibold uppercase tracking-wide text-amber-300">
          Share
        </p>
        <p className="mt-1 text-lg font-semibold text-amber-200">
          {entry.percentage}
        </p>
      </div>
    </article>
  );
}

export default function StatsSummary() {
  const allStatsResults: RawStatsResult[] = [
    // ...groupedStatsData.statsResults,
    ...streakStatsData.statsResults,
  ];

  const summaries = allStatsResults.map((result) => ({
    ...result,
    parsed: parseStatsBlock(result),
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
            Grouped rates and streak distributions rendered from multiple stats
            result formats.
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
                    <p className="mt-2 text-sm text-slate-300">
                      {summary.parsed.title}
                    </p>
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

              {summary.parsed.entries.length > 0 ? (
                <div className="grid gap-4 p-5 sm:grid-cols-2 xl:grid-cols-3 sm:p-6">
                  {summary.parsed.entries.map((entry) =>
                    entry.type === "grouped"
                      ? renderGroupedEntry(entry)
                      : renderStreakEntry(entry)
                  )}
                </div>
              ) : (
                <div className="p-6 text-sm text-slate-400">
                  No supported stats rows were found in this result block.
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
