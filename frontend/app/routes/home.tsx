import type { Route } from "./+types/home";
import simulationData from "../data/blackjackSimulation.json";
import Chart from "../components/Chart";
import SimulationStats from "../components/SimulationStats";
import RoundResults from "../components/RoundResults";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Blackjack Interpreter" },
    { name: "description", content: "Blackjack Game Interpreter" },
  ];
}

export default function Home() {
  // Sample data for round results - replace with actual data from simulator
  const sampleRoundResults = [
    {
      "filter": "player.total in 20..21",
      "rounds": [
        {
          "roundNumber": 2,
          "result": "DEALER_WIN",
          "action": "HIT",
          "dealerTotal": 21,
          "dealerUpcard": 11,
          "playerInitialTotal": 20,
          "netBetUnits": -1,
          "dealerCards": [{"rank": "ACE", "suit": "SPADES", "value": 11}, {"rank": "TEN", "suit": "CLUBS", "value": 10}],
          "playerHands": [
            {
              "bestValue": 20,
              "betMultiplier": 1,
              "isPair": true,
              "isSoft": false,
              "cards": [{"rank": "TWO", "suit": "CLUBS", "value": 2}, {"rank": "TWO", "suit": "DIAMONDS", "value": 2}, {"rank": "EIGHT", "suit": "CLUBS", "value": 8}, {"rank": "EIGHT", "suit": "SPADES", "value": 8}]
            }
          ]
        },
        {
          "roundNumber": 3,
          "result": "PUSH",
          "action": "STAND",
          "dealerTotal": 20,
          "dealerUpcard": 10,
          "playerInitialTotal": 20,
          "netBetUnits": 0,
          "dealerCards": [{"rank": "KING", "suit": "SPADES", "value": 10}, {"rank": "JACK", "suit": "CLUBS", "value": 10}],
          "playerHands": [
            {
              "bestValue": 20,
              "betMultiplier": 1,
              "isPair": false,
              "isSoft": false,
              "cards": [{"rank": "TEN", "suit": "HEARTS", "value": 10}, {"rank": "JACK", "suit": "DIAMONDS", "value": 10}]
            }
          ]
        },
        {
          "roundNumber": 3,
          "result": "PUSH",
          "action": "STAND",
          "dealerTotal": 20,
          "dealerUpcard": 10,
          "playerInitialTotal": 20,
          "netBetUnits": 0,
          "dealerCards": [{"rank": "KING", "suit": "SPADES", "value": 10}, {"rank": "JACK", "suit": "CLUBS", "value": 10}],
          "playerHands": [
            {
              "bestValue": 20,
              "betMultiplier": 1,
              "isPair": false,
              "isSoft": false,
              "cards": [{"rank": "TEN", "suit": "HEARTS", "value": 10}, {"rank": "JACK", "suit": "DIAMONDS", "value": 10}]
            }
          ]
        },
        {
          "roundNumber": 3,
          "result": "PUSH",
          "action": "STAND",
          "dealerTotal": 20,
          "dealerUpcard": 10,
          "playerInitialTotal": 20,
          "netBetUnits": 0,
          "dealerCards": [{"rank": "KING", "suit": "SPADES", "value": 10}, {"rank": "JACK", "suit": "CLUBS", "value": 10}],
          "playerHands": [
            {
              "bestValue": 20,
              "betMultiplier": 1,
              "isPair": false,
              "isSoft": false,
              "cards": [{"rank": "TEN", "suit": "HEARTS", "value": 10}, {"rank": "JACK", "suit": "DIAMONDS", "value": 10}]
            }
          ]
        }
      ]
    }
  ];

  return (
    <div className="flex flex-col h-screen w-screen">
      {/* Top Section - Command + Chart/Stats */}
      <div className="flex flex-1">

        {/* Chart and Stats Section - 80% */}
        <div className="w-4/5 min-h-0 flex flex-col">
          {/* Chart Section */}
          <section className="bg-blue-50 p-8 flex-1 overflow-auto">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">Player Balance Simulation</h1>
            <Chart data={simulationData} />
          </section>

           {/* Simulation Stats Section */}
          <section className="bg-gray-100 p-6 border-t border-gray-300">
            <h2 className="text-xl font-bold text-gray-800 mb-4">Simulation Stats</h2>
            <SimulationStats data={simulationData} />
          </section>
        </div>

           {/* Command Section - 20% */}
        <section className="w-1/5 bg-gray-100 p-6 border-r border-gray-300 flex flex-col">
          <h2 className="text-xl font-bold text-gray-800 mb-4">Command Input</h2>
          <div className="flex-1 flex flex-col">
            <textarea
              className="flex-1 p-3 border border-gray-300 rounded-md resize-none mb-4"
              placeholder="Enter your command here..."
            />
            <button className="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-md">
              Send Command
            </button>
          </div>
        </section>
      </div>

      {/* Bottom Section - Round Results */}
      <section className="bg-white p-8 overflow-auto min-h-96 border-t border-gray-300">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Detailed Round Results</h2>
        <RoundResults showResults={sampleRoundResults} />
      </section>
    </div>
  );
}
