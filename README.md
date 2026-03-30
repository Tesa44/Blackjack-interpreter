## Blackjack Interpreter



A small DSL and simulation engine for running Blackjack experiments, e.g.:

```text
simulate 1000 rounds;
```

The interpreter parses the input using ANTLR and runs the simulation with a simple Blackjack engine.

---

## How to run this project

### 1. Clone the repository

```bash
git clone https://github.com/Tesa44/Blackjack-interpreter.git
```

Open the project in **IntelliJ IDEA**.


### 2. Add ANTLR library in IntelliJ

To make sure ANTLR is available in the project:

1. In IntelliJ, **right‑click** the project root folder.
2. Select **Open Module Settings**.
3. Go to the **Dependencies** tab.
4. Click **+** → **Library** and add the **ANTLR** library.
5. Apply and close the dialog.


### 3. Generate ANTLR recognizers

1. In IntelliJ, locate `ExprLexer.g4` and `ExprParser.g4` in the `src` folder.
2. Right‑click **`ExprLexer.g4`** and choose **Generate ANTLR Recognizer**.
3. In the dialog, set the **Output directory** to a folder named `gen` (for example `.../Blackjack-interpreter/gen`) and confirm.
4. Repeat the same for **`ExprParser.g4`**, also outputting into the same `gen` folder.

You should now have a `gen` folder containing the generated ANTLR Java files.

### 4. Mark `gen` as a generated sources root

1. In IntelliJ’s **Project** view, right‑click the `gen` folder.
2. Select **Mark Directory As → Generated Sources Root**.

This tells IntelliJ to treat the ANTLR output as source code for compilation.

### 5. Build the project

In IntelliJ:

- Use **Build → Build Project** (or press `Ctrl+F9`).
- Ensure there are no compilation errors.

### 6. Run the interpreter

1. Open `Main.java` in `src`.
2. Right‑click inside the file and choose **Run 'Main.main()'**.

You should see output similar to:

```text
Simulated 1000 rounds -> playerWins=..., dealerWins=..., pushes=...
```

That’s it – you now have a working Blackjack simulation interpreter.


## DOKUMENTACJA
---

# 1. Wstęp

## 1.1 Cel projektu

Celem projektu jest zaprojektowanie oraz implementacja systemu symulacyjnego gry w Blackjacka, którego kluczowym elementem jest **interpreter własnego języka (DSL – Domain Specific Language)**. System umożliwia:

* definiowanie strategii gry w Blackjacka przy użyciu dedykowanej składni,
* przeprowadzanie symulacji wielu rozdań,
* analizę wyników przy pomocy języka zapytań.

Projekt skupia się przede wszystkim na **warstwie językowej (interpreterze)** oraz jej integracji z silnikiem symulacyjnym, a nie na samej implementacji gry.

---

## 1.2 Zakres prac

Zakres projektu obejmuje:

* implementację silnika symulacji Blackjacka,
* zaprojektowanie i implementację interpretera DSL dla strategii,
* zaprojektowanie i implementację języka zapytań do analizy wyników,
* stworzenie struktury danych reprezentującej strategie i wyniki,
* integrację interpretera z silnikiem gry,
* obsługę różnych akcji gracza (Hit, Stand, Split, Double),
* filtrowanie i analizę wyników symulacji.

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
* **Double** – dobranie jednej karty i zakończenie tury

---

# 3. Architektura systemu

System składa się z dwóch głównych komponentów:

1. **Silnik symulacyjny**
2. **Interpreter DSL**

## 3.1 Przepływ działania

```
Strategia DSL
      ↓
Interpreter
      ↓
Struktura Strategy
      ↓
Silnik symulacji
      ↓
Lista wyników (GameResult)
      ↓
Język zapytań (DSL)
      ↓
Filtrowanie / analiza
```

---

# 4. Interpreter strategii (nowa składnia DSL)

## 4.1 Cel

Interpreter strategii umożliwia definiowanie zachowania gracza w Blackjacku przy użyciu **języka domenowego (DSL) o składni zbliżonej do języka naturalnego**.

Celem takiego podejścia jest:

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

Ogólny schemat reguły:

```text
when <warunek_gracza> against <warunek_krupiera> then <akcja>;
```

Elementy:

* **when** – początek reguły,
* **warunek_gracza** – opis sytuacji gracza,
* **against** – separator wskazujący kartę krupiera,
* **warunek_krupiera** – zakres kart krupiera,
* **then** – wprowadzenie decyzji,
* **akcja** – decyzja gracza.

---

## 4.4 Typy warunków gracza

Interpreter obsługuje trzy podstawowe typy warunków:

---

### 4.4.1 Para (pair)

```text
when pair 8 against 2-11 then SPLIT;
```

Oznacza:

* gracz posiada dwie karty o tej samej wartości,
* w tym przypadku parę ósemek.

---

#### Zakres par

```text
when pair 2-3 against 2-7 then SPLIT;
```

Oznacza:

* para dwójek lub trójek.

---

---

### 4.4.2 Ręka typu soft (soft)

```text
when soft TWO,THREE against 5-6 then DOUBLE;
```

Oznacza:

* rękę zawierającą asa (liczonego jako 11),
* drugą kartę o wartości 2 lub 3,
* czyli np. A+2 lub A+3.

---

#### Wiele wartości

```text
when soft TWO,THREE,FOUR,FIVE against 2-11 then HIT;
```

Oznacza:

* dowolną z kombinacji:

  * A+2
  * A+3
  * A+4
  * A+5

---

### 4.4.3 Suma kart (total)

```text
when total 12-16 against 2-6 then STAND;
```

Oznacza:

* suma kart gracza mieści się w przedziale 12–16.

---

#### Pojedyncza wartość

```text
when total 11 against 2-10 then DOUBLE;
```

Oznacza dokładnie sumę 11.

---

---

## 4.5 Warunki krupiera

Warunek krupiera określa zakres wartości odkrytej karty:

```text
against 2-6
```

Oznacza:

* karta krupiera ma wartość od 2 do 6.

---

```text
against 2-11
```

Oznacza:

* dowolną kartę (2–10 oraz as jako 11).

---

---

## 4.6 Akcje

Możliwe decyzje:

```text
HIT
STAND
SPLIT
DOUBLE
```

---

### Semantyka akcji

* **HIT** – dobranie kolejnej karty
* **STAND** – zakończenie tury
* **SPLIT** – rozdzielenie pary na dwie ręce
* **DOUBLE** – dobranie jednej karty i zakończenie tury

---

## 4.7 Mechanizm działania interpretera

Interpreter przetwarza strategię w następujących krokach:

1. Parsowanie składni DSL
2. Utworzenie listy reguł
3. Zamiana reguł na strukturę obiektową
4. Przekazanie strategii do silnika symulacji

---

## 4.8 Model danych

Każda reguła reprezentowana jest jako obiekt zawierający:

* warunek gracza,
* warunek krupiera,
* akcję.

Warunki gracza są modelowane jako różne typy obiektów:

* warunek pary,
* warunek soft,
* warunek sumy.

---

## 4.9 Wybór decyzji

Podczas symulacji:

1. dla aktualnej ręki gracza i karty krupiera,
2. interpreter przeszukuje reguły w kolejności ich zdefiniowania,
3. wybiera pierwszą pasującą regułę,
4. zwraca przypisaną akcję.
5. W przypadku braku reguły podejmową akcją jest HIT

---

# 5. Język zapytań do analizy wyników

## 5.1 Cel

Język zapytań umożliwia analizę wyników symulacji poprzez filtrowanie i agregację danych.

---

## 5.2 Składnia podstawowa

### Wyświetlanie wyników

```
show games where <warunek>
```

### Zliczanie wyników

```
count games where <warunek>
```

---

## 5.3 Warunki (WHERE)

### Porównania

```
player.total = 19
dealer.total = 20
player.total >= 17
dealer.total <= 16
```

---

### Operatory logiczne

```
player.total = 19 and dealer.total = 20
```

```
dealer.total = 21 or player.total = 20
```

---

### Zakres

```
player.total in 12..16
```

---

## 5.4 Filtrowanie po kartach

```
player.cards contains A+8
dealer.cards contains 10
```

---

## 5.5 Specjalne właściwości

```
player.isPair = true
player.isSoft = true
```

---

## 5.6 Filtrowanie po akcji

```
action = HIT
action = SPLIT
action = DOUBLE
```

---

## 5.7 Sortowanie

```
sort by player.total desc
sort by dealer.total asc
```

---

## 5.8 Limit wyników

```
limit 10
```

---

# 6. Przykłady użycia

## 6.1 Konkretne rozdania

```
show games where player.total = 19 and dealer.total = 20
```

---

## 6.2 Zliczanie przypadków

```
count games where dealer.total = 21
```

---

## 6.3 Analiza strategii

```
show games where player.total >= 17 and action = HIT
sort by player.total desc
limit 20
```

---

## 6.4 Analiza kart

```
show games where player.cards contains A+7 or A+8
```

---

# 7. Model danych wyników

Każde rozdanie jest reprezentowane jako:

* ręka gracza,
* ręka krupiera,
* wykonana akcja,
* wynik (wygrana/przegrana/remis).

---

# 8. Podsumowanie

Projekt realizuje zaawansowany system symulacyjny oparty o interpreter DSL, który:

* umożliwia definiowanie strategii gry,
* pozwala analizować wyniki symulacji,
* oddziela logikę gry od warstwy językowej,
* wprowadza elementy charakterystyczne dla języków programowania (warunki, reguły, operacje logiczne).

System stanowi przykład integracji:

* interpretera języka,
* silnika symulacyjnego,
* mechanizmów analizy danych.

Dzięki temu projekt wykracza poza prostą implementację gry i staje się przykładem systemu językowego o realnym zastosowaniu analitycznym.
