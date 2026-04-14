import LayoutSection from "../layouts/LayoutSection";
import type {
  GroupedStatsEntry,
  RawStatsResult,
  StreakEntry,
  StreakStats,
  SummaryActionStats,
  SummaryStats,
} from "~/types/simulation";

function formatPercent(value: number) {
  return `${(value * 100).toFixed(1)}%`;
}

function renderGroupedEntry(entry: GroupedStatsEntry) {
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
            {formatPercent(entry.winRate)}
          </p>
        </div>
        <div className="rounded-lg bg-rose-500/10 px-3 py-3">
          <p className="text-xs font-semibold uppercase tracking-wide text-rose-300">
            Lose
          </p>
          <p className="mt-1 text-lg font-semibold text-rose-200">
            {formatPercent(entry.loseRate)}
          </p>
        </div>
      </div>
    </article>
  );
}

function renderStreakEntry(entry: StreakEntry) {
  return (
    <article
      key={entry.length}
      className="rounded-xl border border-white/10 bg-slate-900/70 p-4"
    >
      <p className="text-sm font-medium text-slate-300">Streak {entry.length}</p>
      <p className="mt-3 text-3xl font-bold text-white">
        {entry.count}
        <span className="ml-2 text-sm font-medium text-slate-400">times</span>
      </p>
      <div className="mt-4 rounded-lg bg-amber-500/10 px-3 py-3">
        <p className="text-xs font-semibold uppercase tracking-wide text-amber-300">
          Share
        </p>
        <p className="mt-1 text-lg font-semibold text-amber-200">
          {formatPercent(entry.percentage)}
        </p>
      </div>
    </article>
  );
}

function renderSummaryCard(label: string, value: number, rate?: number) {
  return (
    <article className="rounded-xl border border-white/10 bg-slate-900/70 p-4">
      <p className="text-sm font-medium text-slate-300">{label}</p>
      <p className="mt-3 text-3xl font-bold text-white">{value}</p>
      {typeof rate === "number" ? (
        <p className="mt-2 text-sm text-slate-400">{formatPercent(rate)}</p>
      ) : null}
    </article>
  );
}

function renderActionStats(actionStats: SummaryActionStats[]) {
  return (
    <div className="grid gap-4 xl:grid-cols-2">
      {actionStats.map((actionStat) => (
        <article
          key={actionStat.action}
          className="rounded-xl border border-white/10 bg-slate-900/70 p-4"
        >
          <div className="flex items-start justify-between gap-4">
            <div>
              <p className="text-sm font-medium text-slate-300">{actionStat.action}</p>
              <p className="mt-3 text-3xl font-bold text-white">{actionStat.count}</p>
              <p className="mt-1 text-sm text-slate-400">actions taken</p>
            </div>
            <div className="rounded-lg bg-cyan-500/10 px-3 py-2 text-right">
              <p className="text-xs font-semibold uppercase tracking-wide text-cyan-300">
                Win Rate
              </p>
              <p className="mt-1 text-lg font-semibold text-cyan-200">
                {formatPercent(actionStat.winRate)}
              </p>
            </div>
          </div>
          <p className="mt-4 text-sm text-slate-400">Wins: {actionStat.wins}</p>
        </article>
      ))}
    </div>
  );
}

function parseLegacyGroupedEntries(text?: string): GroupedStatsEntry[] | null {
  if (!text) {
    return null;
  }

  const entries = text
    .split(/\r?\n/)
    .map((line) =>
      line.match(
        /^(.+?)\s*->\s*Games:\s*(\d+)\s*\|\s*Win:\s*([\d.]+)%\s*\|\s*Lose:\s*([\d.]+)%$/
      )
    )
    .filter((match): match is RegExpMatchArray => match !== null)
    .map(([, label, games, winRate, loseRate]) => ({
      label,
      games: Number(games),
      winRate: Number(winRate) / 100,
      loseRate: Number(loseRate) / 100,
    }));

  return entries.length > 0 ? entries : null;
}

function parseLegacyStreakStats(text?: string): StreakStats | null {
  if (!text) {
    return null;
  }

  const [header, ...body] = text
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter(Boolean);

  if (!header?.endsWith(":")) {
    return null;
  }

  const sideLabel = header.replace(/\s+streaks:$/, "");
  const entries = body
    .map((line) => line.match(/^Streak\s+(\d+):\s*(\d+)\s+times\s+\(([\d.]+)%\)$/))
    .filter((match): match is RegExpMatchArray => match !== null)
    .map(([, length, count, percentage]) => ({
      length: Number(length),
      count: Number(count),
      percentage: Number(percentage) / 100,
    }));

  if (entries.length === 0) {
    return null;
  }

  return {
    sideLabel,
    totalStreaks: entries.reduce((sum, entry) => sum + entry.count, 0),
    entries,
  };
}

function normalizeStatsResult(result: RawStatsResult) {
  return {
    ...result,
    summary: result.summary ?? null,
    groupedEntries: result.groupedEntries ?? parseLegacyGroupedEntries(result.text),
    streakStats: result.streakStats ?? parseLegacyStreakStats(result.text),
  };
}

function renderSummary(summary: SummaryStats) {
  return (
    <div className="space-y-4">
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
        {renderSummaryCard("Total games", summary.totalGames)}
        {renderSummaryCard("Player wins", summary.playerWins, summary.playerWinRate)}
        {renderSummaryCard("Dealer wins", summary.dealerWins, summary.dealerWinRate)}
        {renderSummaryCard("Draws", summary.draws, summary.drawRate)}
        {renderSummaryCard("Player busts", Math.round(summary.playerBustRate * summary.totalGames), summary.playerBustRate)}
      </div>
      <div className="grid gap-4 sm:grid-cols-2">
        <article className="rounded-xl border border-white/10 bg-slate-900/70 p-4">
          <p className="text-sm font-medium text-slate-300">Player bust rate</p>
          <p className="mt-3 text-3xl font-bold text-white">
            {formatPercent(summary.playerBustRate)}
          </p>
        </article>
        <article className="rounded-xl border border-white/10 bg-slate-900/70 p-4">
          <p className="text-sm font-medium text-slate-300">Dealer bust rate</p>
          <p className="mt-3 text-3xl font-bold text-white">
            {formatPercent(summary.dealerBustRate)}
          </p>
        </article>
      </div>
      {renderActionStats(summary.actionStats)}
    </div>
  );
}

interface StatsSummaryProps {
  statsResults: RawStatsResult[];
}

export default function StatsSummary({ statsResults }: StatsSummaryProps) {
  const normalizedResults = statsResults.map(normalizeStatsResult);

  return (
    <LayoutSection
      eyebrow="Stats Summary"
      title="Filtered game breakdown"
      description="Grouped rates, streak distributions, and aggregate stats rendered directly from structured backend data."
      headerClassName="lg:items-end"
    >
      <div className="space-y-6">
        {normalizedResults.length > 0 ? (
          normalizedResults.map((summary, index) => (
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
                      {summary.streakStats
                        ? `${summary.streakStats.sideLabel} streak distribution`
                        : summary.groupedEntries
                          ? "Grouped Results"
                          : "Aggregate Results"}
                    </p>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    {(summary.groupBy ?? []).map((group) => (
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

              {summary.summary ? (
                <div className="p-5 sm:p-6">{renderSummary(summary.summary)}</div>
              ) : null}

              {summary.groupedEntries ? (
                <div className="grid gap-4 p-5 sm:grid-cols-2 xl:grid-cols-3 sm:p-6">
                  {summary.groupedEntries.map((entry) => renderGroupedEntry(entry))}
                </div>
              ) : null}

              {summary.streakStats ? (
                <div className="space-y-4 p-5 sm:p-6">
                  <div className="rounded-xl border border-white/10 bg-slate-900/70 p-4">
                    <p className="text-sm font-medium text-slate-300">Total streaks</p>
                    <p className="mt-3 text-3xl font-bold text-white">
                      {summary.streakStats.totalStreaks}
                    </p>
                  </div>
                  <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
                    {summary.streakStats.entries.map((entry) => renderStreakEntry(entry))}
                  </div>
                </div>
              ) : null}
            </div>
          ))
        ) : (
          <div className="rounded-2xl border border-white/10 bg-white/5 p-6 text-sm text-slate-300">
            No stats results yet. Send a command to load grouped and streak data.
          </div>
        )}
      </div>
    </LayoutSection>
  );
}
