# Major Equivalent Mutants and EMS Statistics

## Scope

- Input folders: `mutants-without-ems` and `mutants-with-ems`.
- Totals verified from mutant directories: **176** without EMS, **172** with EMS.
- Data sources: `mutants.log` in both folders and `suppression.log` in `mutants-with-ems`.

## Equivalent mutants detected by EMS (Major)

EMS explicitly suppresses **4 equivalent mutants**:

| # | Without-EMS ID | EMS compact ID | Kind | Method | Line | Mutant (original -> mutant) |
|---:|---:|---:|---|---|---:|---|
| 1 | 99 | 99 | `LOOP_BOUNDS` | `com.example.RPNCalculator@slowEvaluate(java.lang.String[])` | 12 | `i < list.size() |==> i != list.size()` |
| 2 | 141 | 140 | `MUTUALLY_EXCLUSIVE` | `com.example.RPNCalculator@isOperator(java.lang.String)` | 75 | `token.equals("+") || token.equals("-") |==> token.equals("+") != token.equals("-")` |
| 3 | 145 | 143 | `MUTUALLY_EXCLUSIVE` | `com.example.RPNCalculator@isOperator(java.lang.String)` | 75 | `token.equals("+") || token.equals("-") || token.equals("*") |==> (token.equals("+") || token.equals("-")) != token.equals("*")` |
| 4 | 149 | 146 | `MUTUALLY_EXCLUSIVE` | `com.example.RPNCalculator@isOperator(java.lang.String)` | 75 | `token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") |==> (token.equals("+") || token.equals("-") || token.equals("*")) != token.equals("/")` |

### Equivalence rationale

1. **Loop bounds equivalence** (`ID 99`): `i < list.size()` -> `i != list.size()` keeps the same loop stopping point for monotonic `i++`.
2. **Mutually exclusive predicate equivalence** (`IDs 141, 145, 149`): in `isOperator`, each operand checks the same token against different literals (`"+"`, `"-"`, `"*"`, `"/"`), so OR and XOR (`!=`) are behaviorally identical.

## High-level statistics

- Total mutants without EMS: **176**
- Total mutants with EMS: **172**
- Equivalent mutants suppressed: **4**
- Absolute reduction: **4 mutants**
- Reduction rate: **2.27%**
- Retained ratio: **172/176 = 97.73%**

### Distribution by mutation group

| Category | Without EMS | With EMS | Delta |
|---|---:|---:|---:|
| `STD` | 83 (47.16%) | 83 (48.26%) | +0 |
| `COR` | 34 (19.32%) | 31 (18.02%) | -3 |
| `AOR` | 32 (18.18%) | 32 (18.60%) | +0 |
| `ROR` | 27 (15.34%) | 26 (15.12%) | -1 |

### Distribution by class

| Category | Without EMS | With EMS | Delta |
|---|---:|---:|---:|
| `com.example.Console` | 86 (48.86%) | 86 (50.00%) | +0 |
| `com.example.RPNCalculator` | 80 (45.45%) | 76 (44.19%) | -4 |
| `com.example.FileService` | 9 (5.11%) | 9 (5.23%) | +0 |
| `com.example.Main` | 1 (0.57%) | 1 (0.58%) | +0 |

### Distribution by method

| Method | Without EMS | With EMS | Delta |
|---|---:|---:|---:|
| `com.example.RPNCalculator@slowEvaluate(java.lang.String[])` | 35 (19.89%) | 34 (19.77%) | -1 |
| `com.example.Console@showHelp()` | 29 (16.48%) | 29 (16.86%) | +0 |
| `com.example.RPNCalculator@applyOperator(double,double,java.lang.String)` | 23 (13.07%) | 23 (13.37%) | +0 |
| `com.example.Console@processJsonFile(java.util.Scanner)` | 18 (10.23%) | 18 (10.47%) | +0 |
| `com.example.Console@manualInput(java.util.Scanner)` | 17 (9.66%) | 17 (9.88%) | +0 |
| `com.example.Console@run()` | 11 (6.25%) | 11 (6.40%) | +0 |
| `com.example.RPNCalculator@isOperator(java.lang.String)` | 12 (6.82%) | 9 (5.23%) | -3 |
| `com.example.RPNCalculator@fastEvaluate(java.lang.String[])` | 10 (5.68%) | 10 (5.81%) | +0 |
| `com.example.FileService@writeResultToJson(java.lang.String,java.lang.String,double)` | 7 (3.98%) | 7 (4.07%) | +0 |
| `com.example.Console@printMenu()` | 6 (3.41%) | 6 (3.49%) | +0 |
| `com.example.Console@formatResult(double)` | 3 (1.70%) | 3 (1.74%) | +0 |
| `com.example.Console@pause(java.util.Scanner)` | 2 (1.14%) | 2 (1.16%) | +0 |
| `com.example.FileService@readExpressionFromJson(java.lang.String)` | 2 (1.14%) | 2 (1.16%) | +0 |
| `com.example.Main@main(java.lang.String[])` | 1 (0.57%) | 1 (0.58%) | +0 |

## Suppression-specific statistics

### Suppression reason kinds

| Kind | Count | Share of suppressed |
|---|---:|---:|
| `MUTUALLY_EXCLUSIVE` | 3 | 75.00% |
| `LOOP_BOUNDS` | 1 | 25.00% |

### Suppressed mutants by mutation group

| Group | Count | Share of suppressed |
|---|---:|---:|
| `COR` | 3 | 75.00% |
| `ROR` | 1 | 25.00% |

### Suppressed mutants by method

| Method | Count |
|---|---:|
| `com.example.RPNCalculator@isOperator(java.lang.String)` | 3 |
| `com.example.RPNCalculator@slowEvaluate(java.lang.String[])` | 1 |

## Consistency checks

- Folder-level count difference is **4** (176 -> 172), equal to suppression-log size.
- `suppression.log` entries mapped one-to-one to without-EMS mutants: **4**.
- Full signature diff (`without` minus `with`) yields **18** changed signatures ({'COR': 17, 'ROR': 1}).
- The extra 14 signature differences are not EMS suppressions; they are representation/serialization differences in `mutants.log` between the two generation modes.

