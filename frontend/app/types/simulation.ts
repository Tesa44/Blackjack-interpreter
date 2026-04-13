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

export interface SimulationPoint {
  round: number;
  balance: number;
}

export interface DashboardData {
  simulationData: SimulationPoint[];
  showResults: FilterResult[];
  timelineResults: TimelineResult[];
  statsResults: RawStatsResult[];
}
