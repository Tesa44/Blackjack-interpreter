import type {
  DashboardData,
  FilterResult,
  RawStatsResult,
  TimelineResult,
} from "~/types/simulation";

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null;
}

function parseUnknownJson(value: unknown): unknown {
  if (typeof value !== "string") {
    return value;
  }

  try {
    return JSON.parse(value);
  } catch {
    const firstBrace = value.indexOf("{");
    const lastBrace = value.lastIndexOf("}");

    if (firstBrace >= 0 && lastBrace > firstBrace) {
      const candidate = value.slice(firstBrace, lastBrace + 1);
      try {
        return JSON.parse(candidate);
      } catch {
        return value;
      }
    }

    return value;
  }
}

function pickObjectPayload(value: unknown): Record<string, unknown> | null {
  const parsed = parseUnknownJson(value);
  if (!isRecord(parsed)) {
    return null;
  }

  if (isRecord(parsed.data)) {
    return parsed.data;
  }

  const parsedMessage = parseUnknownJson(parsed.message);
  if (isRecord(parsedMessage)) {
    return parsedMessage;
  }

  return parsed;
}

function asFilterResults(value: unknown): FilterResult[] {
  return Array.isArray(value) ? (value as FilterResult[]) : [];
}

function asTimelineResults(value: unknown): TimelineResult[] {
  return Array.isArray(value) ? (value as TimelineResult[]) : [];
}

function asStatsResults(value: unknown): RawStatsResult[] {
  return Array.isArray(value) ? (value as RawStatsResult[]) : [];
}

function buildTimelineFromRounds(showResults: FilterResult[]): TimelineResult[] {
  const [first] = showResults;
  if (!first) {
    return [];
  }

  return [
    {
      filter: first.filter || "all games",
      rounds: first.rounds.map((round) => ({
        roundNumber: round.roundNumber,
        result: round.result,
        action: round.action,
        netBetUnits: round.netBetUnits,
      })),
    },
  ];
}

export function normalizeSimulationResponse(
  responseBody: unknown,
  fallback: DashboardData
): DashboardData {
  const payload = pickObjectPayload(responseBody);
  if (!payload) {
    throw new Error("Server response does not contain a valid JSON object.");
  }

  const simulationData = Array.isArray(payload.simulationData)
    ? payload.simulationData
    : fallback.simulationData;

  const showResults = asFilterResults(
    payload.showResults ?? payload.roundResults ?? payload.filteredResults
  );

  const timelineResults = asTimelineResults(payload.timelineResults);
  const statsResults = asStatsResults(
    payload.statsResults ?? payload.statisticsResults
  );

  return {
    simulationData,
    showResults: showResults.length > 0 ? showResults : fallback.showResults,
    timelineResults:
      timelineResults.length > 0
        ? timelineResults
        : showResults.length > 0
          ? buildTimelineFromRounds(showResults)
          : fallback.timelineResults,
    statsResults: statsResults.length > 0 ? statsResults : fallback.statsResults,
  };
}
