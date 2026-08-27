# Kas Kelas

Offline-first Android app for class-treasurer (bendahara) cash management: PIN lock,
transaction records, real-time balance, monthly reports, local backup/restore.
Product spec lives in `PRD.md` (Indonesian) — read it before implementing features;
feature priorities and acceptance criteria are defined there and must not be re-decided ad hoc.

## Project snapshot

- **Package / app ID:** `id.kaskelas.kas`
- **Sources:** `app/src/main/java/id/kaskelas/kas/`
- **Module structure:** single Gradle module `app/`, package-by-feature
- **Architecture:** MVVM + **Hilt** DI, Room + Flow/StateFlow, Jetpack Compose, single Activity + Navigation Compose
- **Source layout:**
  - `core/` — database, util (e.g. `Hasher`)
  - `data/` — Room Entity + Dao + RepositoryImpl, mappers (subpackages: `transaction/`, `category/`, `settings/`)
  - `domain/` — domain models, repository interfaces, `usecase/BalanceCalculator` (pure money logic — the only usecase; must be unit-tested)
  - `ui/` — Compose screens, ViewModels, theme
  - `di/` — Hilt modules
- **minSdk:** 26 **targetSdk:** 35 **compileSdk:** 36
- **UI language:** fully Indonesian
- **Theme:** LIGHT only — Bone White `#F6F1E8` base, navy text/cards `#0F172A`, green `#1F7A5C` income, coral `#D95D5D` expense (PRD §11 palette)

## Current state

All phases F0–F6 are complete. The project is production-ready (release APK builds,
44 unit tests passing, zero TODO debt). Execution is driven by the agreed phase plan
below — do not add features out of band.

### Security (Lock feature — complete)

- 4-digit PIN stored hashed via `androidx.security:security-crypto` + custom `Hasher`
- Lock screen on app cold start; PIN keypad in `ui/lock/`
- Wrong-PIN attempt delay + `isVerifying` race-condition guard
- Security-question reset flow (5 presets + free-text option, answer hashed)
- Account setup for first run (set PIN + security question)

### Data layer (complete)

- Room database `KasDatabase` (v3) with `TransactionDao`, `TransactionEntity`
- `TransactionRepositoryImpl` implementing the domain transaction repository interface
- `CategoryRepository` / `CategoryDao` / `CategoryEntity` — DB-backed categories
- `MIGRATION_2_3` seeds 8 default categories (4 MASUK + 4 KELUAR)
- PIN/lock state stored via `androidx.datastore:preferences`
- Database is seeded nothing by default — first-run data comes from the user

### Features (all complete)

- **Transaction CRUD**: add/edit/delete with confirmation dialog
- **Transaction list**: search by description/category, date-range filter (DatePickerDialog)
- **Category management**: DB-backed, settings screen to add/edit/delete categories
- **Dashboard**: balance card, monthly in/out summary, last transaction
- **Monthly reports**: month picker, totals, ending balance
- **Settings**: change PIN, JSON backup/restore via SAF, app version
- **Release hardening**: R8/minify (20.22 MB → 1.66 MB), conditional signing, ProGuard rules
- **Compose Previews**: 20+ @Preview functions across 4 files for Android Studio preview

### Tests (complete)

- 44 unit tests: BalanceCalculator (5), TransactionDao (9), FormatRupiah (9), Converters (3), + others
- Stack: JUnit 4 + Robolectric 4.16 + AndroidX Test Core + Coroutines Test + Turbine + Room Testing

## Dev environment (verified)

- **JDK 17 & 21:** `C:/Program Files/Eclipse Adoptium/jdk-{17,21}.0.x-hotspot`
  - **Robolectric requires JAVA_HOME → JDK 21** (SDK 36 needs Java 21)
  - `assembleDebug` alone works on JDK 17, but set JDK 21 for anything touching tests
- **Android SDK:** `C:/Users/SAGU/AppData/Local/Android/Sdk`
- **local.properties:** must use forward slashes —
  `sdk.dir=C\:/Users/SAGU/AppData/Local/Android/Sdk` (backslash escaping causes `IOException: filename syntax incorrect`)
- **Gradle:** use `./gradlew` via git-bash (MSYS shell), never the system gradle
- **AGP / Kotlin / deps (pinned in `gradle/libs.versions.toml`):**
  - AGP 9.1.1, Gradle 9.7.1 (wrapper), Kotlin 2.2.20, KSP 2.3.6
  - Hilt 2.60.1, Room 2.8.4, Compose BOM 2025.10.01, Navigation 2.9.5
  - Coroutines 1.10.2, Datastore 1.1.7, Security-Crypto 1.1.0

## Build & test

```bash
# Debug build
./gradlew assembleDebug

# Install debug APK on device/emulator
./gradlew installDebug

# Unit tests (Robolectric — requires JAVA_HOME=JDK 21)
./gradlew testDebugUnitTest

# Release build
export JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-21.0.x-hotspot"
./gradlew assembleRelease
```

- Test results: XML in `app/build/test-results/`, HTML report in `app/build/reports/tests/`
- Release: `isMinifyEnabled = true`, `isShrinkResources = true` already configured in `app/build.gradle.kts`
- R8/minify active on release → save `app/build/outputs/mapping/release/mapping.txt` per release for crash deobfuscation
- Keystore lives OUTSIDE git (gitignored); never commit it or its passwords
- **Debug vs release signatures differ** — switching variants on the same device fails; uninstall the old variant first

## Conventions

- UI strings in Indonesian; user-facing copy matches PRD wording
- Offline-only: **no `INTERNET` permission**, no backend, all data local Room
- Domain models separate from Room entities; UI never touches DAOs directly —
  ViewModels depend on **domain repository interfaces** only
- `BalanceCalculator` usecase must be unit-tested — it is pure money logic with no Android dependencies
- Test stack: JUnit 4 + Robolectric 4.16 + AndroidX Test Core + Coroutines Test + Turbine + Room Testing
- Database seeds nothing by default: first-run data comes from the user

## Pitfalls

- Forgetting `JAVA_HOME=JDK 21` silently breaks Robolectric tests (not the assemble step)
- `local.properties` backslashes → `IOException`; keep forward slashes
- Compose BOM version drift: pin the BOM, not individual compose library versions
- Debug vs release signatures differ: switching variants on a device fails — uninstall first
- Room entity `id` is `Int` but DAO insert returns `Long`; in tests compare with `.toLong()`
- If AGP says "Minimum supported Gradle version is X", bump `gradle/wrapper/gradle-wrapper.properties` `distributionUrl` accordingly

## Execution phases (agreed)

F0 scaffold+theme+nav skeleton+Lock →
F1 data layer+tests →
F2 transaction CRUD+history →
F3 dashboard →
F4 reports →
F5 settings+backup/restore →
F6 hardening/release build.

**Commit per phase ONLY when the user explicitly asks.**

## Feature scope (from PRD)

### MVP (Priority 1-2)

1. Local PIN/password login
2. Dashboard — current balance, monthly income/expense totals, last transaction
3. Add income transaction
4. Add expense transaction
5. Edit transaction
6. Delete transaction (with confirmation)
7. Transaction history list
8. Filter by date / search by description or category
9. Monthly summary report (total income, total expense, ending balance)
10. Backup data to local file
11. Restore data from backup file
12. Basic settings (change PIN, app version, category management)

### Later (Priority 3 — not for MVP)

- Custom transaction categories
- Income/expense charts
- PDF/CSV export
- Automatic backup history
- Iuran/billing reminders
- Multi-user (future phase)

### Transaction model

Each transaction carries: date, nominal, type (income/expense), category, description.
Balance is computed automatically from stored transactions — never typed in by the user.

## Files of record

| File | Purpose |
|---|---|
| `PRD.md` | Product spec (Indonesian) — authority on features & acceptance criteria |
| `app/build.gradle.kts` | App module: SDK targets, deps, minify config |
| `build.gradle.kts` | Root: plugin declarations |
| `settings.gradle.kts` | Project name + include |
| `gradle/libs.versions.toml` | Version catalog — single source of truth for all deps |
| `local.properties` | SDK path (machine-specific, gitignored) |
| `proguard-rules.pro` | Custom R8 rules (if any beyond defaults) |
