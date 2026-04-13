import React from "react";
import LayoutSection from "../layouts/LayoutSection";
import type { Card, FilterResult, PlayerHand, Round } from "~/types/simulation";

interface RoundResultsProps {
  showResults: FilterResult[];
}

const CardDisplay: React.FC<{ cards: Card[] }> = ({ cards }) => (
  <div className="flex flex-wrap gap-2">
    {cards.map((card, index) => (
      <span
        key={index}
        className="inline-block rounded-lg border border-white/10 bg-white/5 px-3 py-1.5 text-sm font-mono text-slate-200"
      >
        {card.rank} of {card.suit}
      </span>
    ))}
  </div>
);

const RoundCard: React.FC<{ round: Round }> = ({ round }) => {
  const resultStyles =
    round.result === "PLAYER_WIN"
      ? {
          badge: "border-emerald-400/30 bg-emerald-500/10 text-emerald-200",
          accent: "from-emerald-500/30 via-emerald-400/10 to-transparent",
        }
      : round.result === "DEALER_WIN"
        ? {
            badge: "border-rose-400/30 bg-rose-500/10 text-rose-200",
            accent: "from-rose-500/30 via-rose-400/10 to-transparent",
          }
        : {
            badge: "border-amber-400/30 bg-amber-500/10 text-amber-200",
            accent: "from-amber-500/30 via-amber-400/10 to-transparent",
          };

  return (
    <article className="overflow-hidden rounded-2xl border border-white/10 bg-slate-900/80 shadow-[0_20px_60px_rgba(15,23,42,0.35)]">
      <div className={`h-1.5 w-full bg-gradient-to-r ${resultStyles.accent}`} />
      <div className="p-5 sm:p-6">
        <div className="mb-5 flex flex-col gap-3 border-b border-white/10 pb-4 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
              Round
            </p>
            <h4 className="mt-1 text-2xl font-bold text-white">
              #{round.roundNumber}
            </h4>
          </div>
          <div className="flex flex-wrap gap-2">
            <span
              className={`rounded-full border px-3 py-1 text-xs font-semibold uppercase tracking-wide ${resultStyles.badge}`}
            >
              {round.result.replace("_", " ")}
            </span>
            <span className="rounded-full border border-cyan-400/30 bg-cyan-500/10 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-cyan-200">
              {round.action}
            </span>
          </div>
        </div>

        <div className="grid gap-4 lg:grid-cols-3">
          <section className="rounded-xl border border-white/10 bg-white/5 p-4">
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
              Summary
            </p>
            <div className="mt-4 grid gap-3 sm:grid-cols-3 lg:grid-cols-1">
              <div>
                <p className="text-sm text-slate-400">Dealer Total</p>
                <p className="text-2xl font-bold text-white">
                  {round.dealerTotal}
                </p>
              </div>
              <div>
                <p className="text-sm text-slate-400">Dealer Upcard</p>
                <p className="text-2xl font-bold text-white">
                  {round.dealerUpcard}
                </p>
              </div>
              <div>
                <p className="text-sm text-slate-400">Initial Total</p>
                <p className="text-2xl font-bold text-white">
                  {round.playerInitialTotal}
                </p>
              </div>
            </div>
          </section>

          <section className="rounded-xl border border-white/10 bg-white/5 p-4">
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
              Dealer Cards
            </p>
            <div className="mt-4">
              <CardDisplay cards={round.dealerCards} />
            </div>
          </section>

          <section className="rounded-xl border border-white/10 bg-white/5 p-4">
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
              Player Hands
            </p>
            <div className="mt-4 space-y-4">
              {round.playerHands.map((hand: PlayerHand, handIndex: number) => (
                <div
                  key={handIndex}
                  className="rounded-lg border border-white/10 bg-slate-950/60 p-4"
                >
                  <p className="text-sm font-semibold text-white">
                    Hand {handIndex + 1}
                  </p>
                  <p className="mt-1 text-sm text-slate-300">
                    Value: {hand.bestValue}
                    {hand.isSoft ? " | Soft" : ""}
                    {hand.isPair ? " | Pair" : ""}
                  </p>
                  <div className="mt-3">
                    <CardDisplay cards={hand.cards} />
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>
      </div>
    </article>
  );
};

const RoundResults: React.FC<RoundResultsProps> = ({ showResults }) => {
  if (!showResults || showResults.length === 0) {
    return (
      <div className="rounded-2xl border border-white/10 bg-slate-900/80 py-8 text-center text-slate-400">
        No round results to display
      </div>
    );
  }

  return (
    <LayoutSection
      eyebrow="Round Results"
      title="Detailed Round Results"
      description="Use the page scrollbar to skip this section, or the internal scrollbar to inspect every round."
    >
      <div className="space-y-6">
        {showResults.map((filterResult: FilterResult, filterIndex: number) => (
          <section
            key={filterIndex}
            className="overflow-hidden rounded-2xl border border-white/10 bg-slate-950 text-slate-50 shadow-[0_20px_60px_rgba(15,23,42,0.35)]"
          >
            <div className="border-b border-white/10 px-6 py-5">
              <p className="text-xs font-semibold uppercase tracking-[0.2em] text-cyan-300">
                Filter
              </p>
              <h3 className="mt-2 text-2xl font-bold text-white">
                {filterResult.filter}
              </h3>
            </div>
            <div className="max-h-256 overflow-y-auto space-y-4 p-6 pr-2">
              {filterResult.rounds.map((round: Round) => (
                <RoundCard key={round.roundNumber} round={round} />
              ))}
            </div>
          </section>
        ))}
      </div>
    </LayoutSection>
  );
};

export default RoundResults;
