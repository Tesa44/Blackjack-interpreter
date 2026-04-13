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

export interface RawStatsResult {
  filter: string;
  groupBy: string[];
  text: string;
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
