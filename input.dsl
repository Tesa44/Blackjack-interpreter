strategy {
  when pair 11 against 2-11 then SPLIT;
  when pair 8 against 2-11 then SPLIT;
  when pair 10 against 2-11 then STAND;
  when pair 5 against 2-11 then DOUBLE;
  when pair 2-3 against 2-7 then SPLIT;
  when pair 4 against 5-6 then SPLIT;
  when pair 6 against 2-6 then SPLIT;
  when pair 7 against 2-7 then SPLIT;
  when pair 9 against 2-9 then SPLIT;

  when soft TWO,THREE against 5-6 then DOUBLE;
  when soft FOUR,FIVE against 4-6 then DOUBLE;
  when soft SIX against 3-6 then DOUBLE;
  when soft SEVEN against 3-6 then DOUBLE;
  when soft TWO,THREE,FOUR,FIVE against 2-11 then HIT;

  when total 11 against 2-10 then DOUBLE;
  when total 10 against 2-9 then DOUBLE;
  when total 9 against 3-6 then DOUBLE;

  when total 17-21 against 2-11 then STAND;
  when total 12-16 against 2-6 then STAND;
  when total 12-16 against 7-11 then HIT;
}
simulate 100 rounds;
stats games group by player.streaks;
timeline games;



