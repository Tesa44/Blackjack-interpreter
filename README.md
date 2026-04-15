## Blackjack Interpreter

A small DSL and simulation engine for running Blackjack experiments. The language
lets you define a player strategy, run simulations, and analyze the results with
a built-in query language, e.g.:

```text
strategy {
  when pair 8 against 2-11 then SPLIT;
  when total 17-21 against 2-11 then STAND;
}
set balance 250;
set bet 25;
simulate 1000 rounds;
stats games group by player.total;
```

The interpreter parses the input using ANTLR and runs the simulation with a simple
Blackjack engine. Results can be inspected either from the CLI or from the React
frontend that talks to the built-in HTTP API.

---

## How to run this project

### 1. Clone the repository

```bash
git clone https://github.com/Tesa44/Blackjack-interpreter.git
```

Open the project in **IntelliJ IDEA**.

### 2. Add ANTLR library in IntelliJ

1. In IntelliJ, **right-click** the project root folder.
2. Select **Open Module Settings**.
3. Go to the **Dependencies** tab.
4. Click **+** → **Library** and add the **ANTLR** library.
5. Apply and close the dialog.

### 3. Generate ANTLR recognizers

1. In IntelliJ, locate `ExprLexer.g4` and `ExprParser.g4` in the `src` folder.
2. Right-click **`ExprLexer.g4`** and choose **Generate ANTLR Recognizer**.
3. In the dialog, set the **Output directory** to a folder named `gen`
   (for example `.../Blackjack-interpreter/gen`) and confirm.
4. Repeat the same for **`ExprParser.g4`**, also outputting into the same `gen` folder.

You should now have a `gen` folder containing the generated ANTLR Java files.

### 4. Mark `gen` as a generated sources root

1. In IntelliJ's **Project** view, right-click the `gen` folder.
2. Select **Mark Directory As → Generated Sources Root**.

### 5. Build the project

In IntelliJ:

- Use **Build → Build Project** (or press `Ctrl+F9`).
- Ensure there are no compilation errors.

### 6. Run the interpreter

The project exposes a backend HTTP API that the frontend talks to.

1. Open `ApiServerMain.java` in `src`.
2. Right-click inside the file and choose **Run 'ApiServerMain.main()'**.

The server will start and accept DSL scripts sent from the frontend.

### 7. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Then open the printed local URL in your browser. You can type a DSL script in the
editor, run it, and inspect results as charts, tables, and timelines.


---

## DOKUMENTACJA
---

# 1. Wstęp

## 1.1 Cel projektu

Celem projektu jest zaprojektowanie oraz implementacja systemu symulacyjnego gry
w Blackjacka, którego kluczowym elementem jest **interpreter własnego języka
(DSL – Domain Specific Language)**. System umożliwia:

* definiowanie strategii gry w Blackjacka przy użyciu dedykowanej składni,
* sterowanie przebiegiem symulacji (budżet gracza, wysokość zakładu, liczba rund),
* przeprowadzanie symulacji wielu rozdań,
* analizę wyników przy pomocy wbudowanego języka zapytań
  (filtrowanie, agregacja, wykresy, oś czasu).

Projekt skupia się przede wszystkim na **warstwie językowej (interpreterze)** oraz
jej integracji z silnikiem symulacyjnym, a nie na samej implementacji gry.

---

## 1.2 Zakres prac

Zakres projektu obejmuje:

* implementację silnika symulacji Blackjacka,
* zaprojektowanie i implementację interpretera DSL dla strategii,
* zaprojektowanie i implementację języka zapytań do analizy wyników,
* stworzenie struktury danych reprezentującej strategie i wyniki rozdań,
* integrację interpretera z silnikiem gry,
* obsługę różnych akcji gracza (Hit, Stand, Split, Double),
* filtrowanie, agregację i wizualizację wyników symulacji,
* udostępnienie wyników przez API HTTP oraz interfejs graficzny (React).

---

# 2. Opis gry Blackjack

Blackjack to gra karciana, w której gracz rywalizuje z krupierem (dealerem).

## 2.1 Zasady podstawowe

* Celem gry jest uzyskanie wartości kart jak najbliższej 21, bez jej przekroczenia.
* Każdy gracz otrzymuje początkowo 2 karty.
* Krupier posiada jedną kartę odkrytą.
* Wartości kart:

  * karty 2–10 mają wartość nominalną,
  * walety, damy i króle mają wartość 10,
  * as ma wartość 1 lub 11.

## 2.2 Dostępne akcje

* **Hit** – dobranie kolejnej karty
* **Stand** – zakończenie tury
* **Split** – rozdzielenie pary kart na dwie ręce
* **Double** – podwojenie zakładu, dobranie jednej karty i zakończenie tury

---

# 3. Architektura systemu

System składa się z trzech głównych warstw:

1. **Silnik symulacyjny** – rozgrywa kolejne rozdania wg zasad Blackjacka.
2. **Interpreter DSL** – parsuje skrypt użytkownika (strategia + polecenia).
3. **Język zapytań** – filtruje i agreguje wyniki rozdań.

Na to wszystko nałożone jest **API HTTP** (`ApiServerMain`) i **frontend React**,
które pozwalają pisać i uruchamiać skrypty DSL z przeglądarki.

## 3.1 Przepływ działania

```
        Skrypt DSL
             ↓
        Interpreter (ANTLR)
             ↓
   Struktura Strategy + polecenia
             ↓
        Silnik symulacji
             ↓
    Lista wyników (RoundResult)
             ↓
         Język zapytań
             ↓
   Filtrowanie / agregacja / wykresy
             ↓
             API HTTP → Frontend
```

---

# 4. Interpreter strategii

## 4.1 Cel

Interpreter strategii umożliwia definiowanie zachowania gracza w Blackjacku przy
użyciu **języka domenowego (DSL) o składni zbliżonej do języka naturalnego**.

Cele takiego podejścia:

* zwiększenie czytelności i intuicyjności zapisu strategii,
* umożliwienie łatwego definiowania reguł decyzyjnych,
* oddzielenie logiki gry od sposobu jej konfiguracji.

---

## 4.2 Składnia strategii

Strategia definiowana jest jako blok zawierający zestaw reguł:

```text
strategy {
  when pair 8 against 2-11 then SPLIT;
  when pair 10 against 2-11 then STAND;
  when pair 2-3 against 2-7 then SPLIT;

  when soft TWO,THREE against 5-6 then DOUBLE;
  when soft FOUR,FIVE against 4-6 then DOUBLE;

  when total 11 against 2-10 then DOUBLE;
  when total 10 against 2-9 then DOUBLE;
  when total 9 against 3-6 then DOUBLE;

  when total 17-21 against 2-11 then STAND;
  when total 12-16 against 2-6 then STAND;
  when total 12-16 against 7-11 then HIT;
}
```

Każda linia reprezentuje pojedynczą regułę decyzyjną.

---

## 4.3 Struktura reguły

```text
when <warunek_gracza> against <warunek_krupiera> then <akcja>;
```

Elementy:

* **when** – początek reguły,
* **warunek_gracza** – opis sytuacji gracza,
* **against** – separator wskazujący kartę krupiera,
* **warunek_krupiera** – zakres wartości odkrytej karty krupiera,
* **then** – wprowadzenie decyzji,
* **akcja** – decyzja gracza.

---

## 4.4 Typy warunków gracza

### 4.4.1 Para (pair)

```text
when pair 8 against 2-11 then SPLIT;
```

Oznacza: gracz posiada dwie karty o tej samej wartości (tu: parę ósemek).

Zakres par:

```text
when pair 2-3 against 2-7 then SPLIT;
```

Oznacza: parę dwójek lub trójek.

---

### 4.4.2 Ręka typu soft

```text
when soft TWO,THREE against 5-6 then DOUBLE;
```

Oznacza: rękę zawierającą asa (liczonego jako 11) oraz drugą kartę
o wartości 2 lub 3, czyli A+2 lub A+3.

Wiele wartości:

```text
when soft TWO,THREE,FOUR,FIVE against 2-11 then HIT;
```

Oznacza dowolną z kombinacji A+2, A+3, A+4, A+5.

Dozwolone nazwy rang: `TWO`, `THREE`, `FOUR`, `FIVE`, `SIX`, `SEVEN`, `EIGHT`,
`NINE`, `TEN`, `JACK`, `QUEEN`, `KING`, `ACE`.

---

### 4.4.3 Suma kart (total)

```text
when total 12-16 against 2-6 then STAND;
```

Oznacza: suma kart gracza mieści się w przedziale 12–16.

Pojedyncza wartość:

```text
when total 11 against 2-10 then DOUBLE;
```

---

## 4.5 Warunki krupiera

Warunek krupiera określa zakres wartości odkrytej karty:

```text
against 2-6     # karta krupiera 2–6
against 2-11    # dowolna karta (2–10 lub as jako 11)
against 10      # dokładnie 10
```

---

## 4.6 Akcje

Możliwe decyzje: `HIT`, `STAND`, `SPLIT`, `DOUBLE`.

Semantyka:

* **HIT** – dobranie kolejnej karty,
* **STAND** – zakończenie tury,
* **SPLIT** – rozdzielenie pary na dwie ręce,
* **DOUBLE** – podwojenie zakładu, dobranie jednej karty i zakończenie tury.

---

## 4.7 Wybór decyzji

Podczas symulacji, dla aktualnej ręki gracza i karty krupiera:

1. interpreter przeszukuje reguły w kolejności ich zdefiniowania,
2. wybiera pierwszą pasującą regułę,
3. zwraca przypisaną akcję,
4. w przypadku braku dopasowania podejmowaną akcją jest **HIT**.

---

# 5. Polecenia sterujące symulacją

Poza blokiem `strategy { ... }` skrypt DSL zawiera polecenia sterujące.
Wszystkie polecenia kończą się średnikiem `;`.

## 5.1 Ustawienia gracza

```text
set balance 250;    # początkowy budżet gracza
set bet 25;         # stała wysokość zakładu
```

## 5.2 Uruchomienie symulacji

Istnieją trzy warianty:

```text
simulate 1000 rounds;   # rozegraj dokładnie 1000 rund
simulate until 1000;    # graj aż balans osiągnie 1000 (lub gracz zbankrutuje)
simulate;               # graj aż do bankructwa gracza
```

## 5.3 Kompletny przykład konfiguracji

```text
set balance 500;
set bet 50;
simulate until 1500;
```

---

# 6. Język zapytań do analizy wyników

Po uruchomieniu symulacji dostępne są cztery polecenia analityczne:

| Polecenie        | Zastosowanie                                      |
|------------------|---------------------------------------------------|
| `show games`     | listowanie filtrowanych rozdań                    |
| `stats games`    | agregacja / statystyki (z opcjonalnym `group by`) |
| `timeline games` | oś czasu rozdań                                   |
| `plot balance`   | wykres zmian budżetu gracza w czasie              |

Wszystkie polecenia oprócz `plot balance` przyjmują opcjonalną klauzulę
`where <warunek>`.

---

## 6.1 Składnia ogólna

```text
show games [where <warunek>] ;
stats games [where <warunek>] [group by <właściwość> [, <właściwość> ...]] ;
timeline games [where <warunek>] ;
plot balance ;
```

---

## 6.2 Właściwości dostępne w warunkach

| Właściwość           | Opis                                                    |
|----------------------|---------------------------------------------------------|
| `action`             | akcja podjęta przez gracza (`HIT`, `STAND`, `SPLIT`, `DOUBLE`) |
| `player.total`       | końcowa suma kart gracza                                |
| `player.initialTotal`| suma kart gracza po rozdaniu (2 karty)                  |
| `player.init`        | pierwsza karta gracza (ranga)                           |
| `player.cards`       | lista kart gracza (używane z `contains`)                |
| `player.isPair`      | czy początkowa ręka to para (`true` / `false`)          |
| `player.isSoft`      | czy początkowa ręka jest soft (`true` / `false`)        |
| `dealer.total`       | końcowa suma kart krupiera                              |
| `dealer.upcard`      | odkryta karta krupiera                                  |
| `dealer.init`        | pierwsza karta krupiera                                 |
| `dealer.cards`       | lista kart krupiera (używane z `contains`)              |

W klauzuli `group by` dodatkowo dostępne są:

* `player.streaks` – serie wygranych/przegranych gracza,
* `dealer.streaks` – serie wygranych/przegranych krupiera.

---

## 6.3 Operatory porównania

```text
=    >    <    >=    <=
```

Porównania mogą dotyczyć liczb, wartości `true` / `false` oraz akcji
(`HIT`, `STAND`, `SPLIT`, `DOUBLE`).

```text
player.total = 19
player.total >= 17
dealer.total <= 16
player.isPair = true
action = DOUBLE
```

---

## 6.4 Operatory logiczne i nawiasy

```text
player.total = 20 and dealer.total = 21
player.total = 21 or dealer.total > 21
(player.total >= 17 and action = HIT) or player.isSoft = true
```

---

## 6.5 Zakres wartości (`in`)

```text
player.total in 12..16
dealer.upcard in 2..6
```

---

## 6.6 Filtrowanie po kartach (`contains`)

Operator `contains` sprawdza, czy na ręce znajduje się karta o danej randze.
Rangi zapisuje się słowami (`ACE`, `TWO`, ..., `KING`):

```text
player.cards contains ACE
dealer.cards contains TEN
```

---

## 6.7 Przykłady pojedynczych zapytań

### Konkretne rozdania

```text
show games where player.total = 19 and dealer.total = 20;
```

### Wszystkie rozdania bez warunku

```text
show games;
```


### Statystyki z grupowaniem

```text
stats games group by player.total;
stats games group by player.isSoft, dealer.upcard;
stats games where action = DOUBLE group by player.total;
stats games group by player.streaks;
```

### Oś czasu rozdań

```text
timeline games;
timeline games where action = SPLIT;
```

### Wykres budżetu

```text
plot balance;
```

### Filtrowanie po akcji

```text
show games where action = HIT;
show games where action = SPLIT and player.isPair = true;
```

### Filtrowanie po kartach początkowych

```text
show games where dealer.upcard = 10;
show games where player.init in 9..11;
show games where player.initialTotal = 20;
```

### Filtrowanie po zawartości ręki

```text
show games where player.cards contains ACE;
show games where dealer.cards contains TEN and dealer.total > 21;
```

### Specjalne właściwości gracza

```text
show games where player.isPair = true;
show games where player.isSoft = true and action = DOUBLE;
```

---

# 7. Pełny przykład skryptu DSL

Poniżej znajduje się kompletny skrypt, który korzysta ze wszystkich
najważniejszych możliwości języka:

```text
strategy {
  when pair 8 against 2-11 then SPLIT;
  when pair 10 against 2-11 then STAND;
  when pair 2-3 against 2-7 then SPLIT;

  when soft TWO,THREE against 5-6 then DOUBLE;
  when soft FOUR,FIVE against 4-6 then DOUBLE;
  when soft TWO,THREE,FOUR,FIVE against 2-11 then HIT;

  when total 11 against 2-10 then DOUBLE;
  when total 10 against 2-9 then DOUBLE;
  when total 9 against 3-6 then DOUBLE;

  when total 17-21 against 2-11 then STAND;
  when total 12-16 against 2-6 then STAND;
  when total 12-16 against 7-11 then HIT;
}

set balance 500;
set bet 25;
simulate 100 rounds;

show games where player.total = 21 and dealer.total = 21;
stats games group by dealer.upcard;
timeline games where action = DOUBLE;
plot balance;
```

Drugi przykład – granie „do bankructwa” i analiza serii:

```text
strategy {
  when total 17-21 against 2-11 then STAND;
  when total 12-16 against 2-6 then STAND;
  when total 12-16 against 7-11 then HIT;
}

set balance 250;
set bet 25;
simulate until 1000;
show games;
stats games group by player.streaks;
plot balance;
```

---

# 8. Model danych wyników

Każde rozdanie (`RoundResult`) jest reprezentowane m.in. przez:

* rękę gracza (karty, suma, suma początkowa, `isPair`, `isSoft`),
* rękę krupiera (karty, upcard, suma),
* wykonaną akcję gracza,
* wynik (wygrana / przegrana / remis),
* zmianę budżetu gracza w wyniku rozdania.

Na podstawie listy rozdań budowane są:

* tabele dla `show games`,
* agregaty i grupowania dla `stats games`,
* oś czasu dla `timeline games`,
* wykres zmiany budżetu dla `plot balance`.

---

# 9. Podsumowanie

Projekt realizuje system symulacyjny oparty o interpreter DSL, który:

* umożliwia definiowanie strategii gry w Blackjacka,
* pozwala sterować parametrami symulacji (budżet, zakład, liczba rund),
* udostępnia język zapytań do filtrowania, agregacji i wizualizacji wyników,
* oddziela logikę gry od warstwy językowej,
* wprowadza elementy charakterystyczne dla języków programowania
  (warunki, reguły, operacje logiczne, grupowanie),
* integruje backend z frontendem przez API HTTP.

System stanowi przykład integracji interpretera języka, silnika symulacyjnego
oraz mechanizmów analizy i wizualizacji danych i wykracza poza prostą
implementację gry, stając się przykładem systemu językowego o realnym
zastosowaniu analitycznym.
