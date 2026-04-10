import React from "react";

interface Card {
  rank: string;
  suit: string;
  value: number;
}

interface PlayerHand {
  bestValue: number;
  betMultiplier: number;
  isPair: boolean;
  isSoft: boolean;
  cards: Card[];
}

interface Round {
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

interface FilterResult {
  filter: string;
  rounds: Round[];
}

interface RoundResultsProps {
  showResults: FilterResult[];
}

const CardDisplay: React.FC<{ cards: Card[] }> = ({ cards }) => (
  <div className="flex flex-wrap gap-1">
    {cards.map((card, index) => (
      <span
        key={index}
        className="inline-block bg-white border border-gray-300 rounded px-2 py-1 text-sm font-mono text-gray-600"
      >
        {card.rank} of {card.suit}
      </span>
    ))}
  </div>
);

const RoundCard: React.FC<{ round: Round }> = ({ round }) => (
  <div className="bg-white rounded-lg shadow-md p-4 mb-4 border-l-4 border-blue-500">
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div>
        <h4 className="font-semibold text-gray-800">
          Round {round.roundNumber}
        </h4>
        <p className="text-sm text-gray-600">
          Result:{" "}
          <span
            className={`font-medium ${round.result === "PLAYER_WIN" ? "text-green-600" : round.result === "DEALER_WIN" ? "text-red-600" : "text-yellow-600"}`}
          >
            {round.result}
          </span>
        </p>
        <p className="text-sm text-gray-600">Action: {round.action}</p>
        <p className="text-sm text-gray-600">
          Net Bet: {round.netBetUnits} units
        </p>
      </div>
      <div>
        <h5 className="font-medium text-gray-700">Dealer</h5>
        <p className="text-sm text-gray-600">Total: {round.dealerTotal}</p>
        <p className="text-sm text-gray-600">Upcard: {round.dealerUpcard}</p>
        <CardDisplay cards={round.dealerCards} />
      </div>
      <div>
        <h5 className="font-medium text-gray-700">Player</h5>
        <p className="text-sm text-gray-600">
          Initial Total: {round.playerInitialTotal}
        </p>
        {round.playerHands.map((hand: PlayerHand, handIndex: number) => (
          <div key={handIndex} className="mt-2">
            <p className="text-sm text-gray-600">
              Hand {handIndex + 1}: {hand.bestValue}{" "}
              {hand.isSoft ? "(Soft)" : ""} {hand.isPair ? "(Pair)" : ""}
            </p>
            <CardDisplay cards={hand.cards} />
          </div>
        ))}
      </div>
    </div>
  </div>
);

const RoundResults: React.FC<RoundResultsProps> = ({ showResults }) => {
  if (!showResults || showResults.length === 0) {
    return (
      <div className="text-center text-gray-500 py-8">
        No round results to display
      </div>
    );
  }

  return (
    <div className="space-y-6 overflow-auto">
      {showResults.map((filterResult: FilterResult, filterIndex: number) => (
        <div key={filterIndex} className="bg-gray-50 rounded-lg p-6">
          <h3 className="text-xl font-bold text-gray-800 mb-4">
            Filter: {filterResult.filter}
          </h3>
          <div className="space-y-4">
            {filterResult.rounds.map((round: Round) => (
              <RoundCard key={round.roundNumber} round={round} />
            ))}
          </div>
        </div>
      ))}
    </div>
  );
};

export default RoundResults;
