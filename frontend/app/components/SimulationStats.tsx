interface SimulationStatsProps {
  data: {
    simulationData: Array<{
      balance: number;
      round: number;
    }>;
  };
}

export default function SimulationStats({ data }: SimulationStatsProps) {
  const simulationData = data.simulationData;
  return (
    <div className="grid grid-cols-2 gap-4 text-gray-700">
      <div>
        <p className="font-semibold">Total Rounds:</p>
        <p className="text-lg">{simulationData.length}</p>
      </div>
      <div>
        <p className="font-semibold">Starting Balance:</p>
        <p className="text-lg">${simulationData[0].balance}</p>
      </div>
      <div>
        <p className="font-semibold">Final Balance:</p>
        <p className="text-lg">${simulationData[simulationData.length - 1].balance}</p>
      </div>
      <div>
        <p className="font-semibold">Net Profit:</p>
        <p className="text-lg text-green-600">
          ${simulationData[simulationData.length - 1].balance - simulationData[0].balance}
        </p>
      </div>
    </div>
  );
}