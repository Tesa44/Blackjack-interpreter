import React from "react";
import { timelineResults } from "../data/timelineSimulation.json";

const TimelineSummary: React.FC = () => {
  const [{ rounds }] = timelineResults;
  const timeline = rounds.map((round) => {
    if (round.result === "PLAYER_WIN") {
      return { label: "W", color: "text-green-600" };
    }
    if (round.result === "DEALER_WIN") {
      return { label: "L", color: "text-red-600" };
    }
    return { label: "D", color: "text-yellow-500" };
  });

  return (
    <div className="mt-4 p-4 bg-gray-100 rounded-lg">
      <h4 className="text-lg font-semibold text-gray-800 mb-2">
        Game Timeline
      </h4>
      <div className="flex flex-wrap gap-x-2 gap-y-1 font-mono text-lg font-semibold tracking-wide">
        {timeline.map(({ label, color }, index) => (
          <span key={`${label}-${index}`} className={`${color} text-3xl`}>
            {label}
          </span>
        ))}
      </div>
    </div>
  );
};

export default TimelineSummary;
