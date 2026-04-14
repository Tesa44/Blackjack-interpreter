export interface Card {
  rank: string;
  suit: string;
  value: number;
}

export interface PlayerHand {
  bestValue: number;
  betMultiplier: number;
  isPair: boolean;
  isSoft: boolean;
  cards: Card[];
}

export interface Round {
  roundNumber: number;
  result: string;
  action: string;
  dealerTotal: number;
  dealerUpcard: number;
  playerInitialTotal: number;
  netBetUnits: number;
  dealerCards: Card[];
  playerHands: PlayerHand[];
}

export interface FilterResult {
  filter: string;
  rounds: Round[];
}

export interface TimelineRound {
  roundNumber: number;
  result: string;
  action: string;
  netBetUnits: number;
}

export interface TimelineResult {
  filter: string;
  rounds: TimelineRound[];
}

export interface SummaryActionStats {
  action: string;
  count: number;
  wins: number;
  winRate: number;
}

export interface SummaryStats {
  totalGames: number;
  playerWins: number;
  dealerWins: number;
  draws: number;
  playerWinRate: number;
  dealerWinRate: number;
  drawRate: number;
  playerBustRate: number;
  dealerBustRate: number;
  actionStats: SummaryActionStats[];
}

export interface GroupedStatsEntry {
  label: string;
  games: number;
  winRate: number;
  loseRate: number;
}

export interface StreakEntry {
  length: number;
  count: number;
  percentage: number;
}

export interface StreakStats {
  sideLabel: string;
  totalStreaks: number;
  entries: StreakEntry[];
}

export interface RawStatsResult {
  filter: string;
  groupBy: string[];
  summary?: SummaryStats | null;
  groupedEntries?: GroupedStatsEntry[] | null;
  streakStats?: StreakStats | null;
  text?: string;
}

export interface SimulationSummary {
  simulationMode: string;
  roundsPlayed: number;
  playerWins: number;
  dealerWins: number;
  pushes: number;
  initialBalance: number;
  betPerGame: number;
  finalBalance: number;
}

export interface SimulationPlot {
  balanceHistory: number[];
}

export interface ApiSimulationResponse {
  summary?: SimulationSummary;
  showResults?: FilterResult[];
  statsResults?: RawStatsResult[];
  timelineResults?: TimelineResult[];
  plot?: SimulationPlot;
}
