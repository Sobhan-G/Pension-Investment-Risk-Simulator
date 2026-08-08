# Pension & Investment Risk Simulator

A QA automation portfolio project simulating real-world test scenarios found in banking and pension systems — combining API testing, UI automation, data-driven testing, and negative/edge-case testing.

## Overview

This project demonstrates a practical QA test suite built around a real, public finance API (Frankfurter — currency exchange rates), covering three core testing disciplines commonly required in fintech and banking QA roles:

1. **Data-driven API testing** — validating currency conversion logic across multiple inputs
2. **UI/API integration testing** — simulating a pension forecast calculator with mocked API responses, including failure scenarios
3. **Negative testing & edge cases** — verifying the API fails safely and predictably on invalid input

## Tech Stack

- **Java 21** (LTS)
- **Maven** — build & dependency management
- **Playwright for Java** — UI automation and network mocking
- **REST Assured** — API testing
- **JUnit 5** — test framework (parameterized tests)
- **Jackson** — JSON deserialization

## Test Suite

| Test Class | What it covers | Tests |
|---|---|---|
| `CurrencyConversionTest` | Data-driven API test converting 5 currencies (EUR, USD, GBP, NOK, DKK) to SEK. Validates status code, response schema, and calculation correctness. | 5 |
| `PensionForecastUiTest` | UI test using a local mock calculator page. Uses Playwright's `page.route()` to mock API responses — one normal-return scenario, one simulated failure ("market crash") scenario — and verifies the UI handles both gracefully. | 2 |
| `NegativeTestingTest` | Sends invalid currency codes, empty values, and malformed input to the API and verifies it consistently fails with `404` and a meaningful error message. Includes a dedicated edge-case test confirming the API correctly rejects converting a currency to itself (`422`). | 9 |

**Total: 16/16 tests passing**

## Project Structure
src/
├── main/
│ ├── java/com/sobi/qa/
│ │ ├── model/ # Jackson model classes (API response mapping)
│ │ └── server/ # Lightweight local HTTP server for the mock UI
│ └── resources/
│ └── calculator.html # Mock pension forecast calculator page
└── test/
└── java/com/sobi/qa/
├── CurrencyConversionTest.java
├── PensionForecastUiTest.java
└── NegativeTestingTest.java


## Running the Tests

**Prerequisites:** Java 17+ and Maven installed.

```bash
mvn clean test
```

This will run all 16 tests across the three test classes and print a summary of results.

## Key Design Decisions

- **Real, public API** (Frankfurter) used for API tests instead of a fully mocked backend — gives the suite real-world reliability characteristics.
- **`page.route()` network mocking** used for UI tests, allowing deterministic testing of both success and failure scenarios without depending on a live backend.
- **Local HTTP server** (Java's built-in `HttpServer`, no extra dependencies) serves the mock calculator page, keeping the UI test setup realistic and dependency-light.
- **Edge case discovery**: while writing negative tests, the suite uncovered that the Frankfurter API returns `422` when converting a currency to itself — this was turned into its own explicit, documented test rather than being treated as a bug.

## About

Built as a QA portfolio project to demonstrate practical automation skills relevant to fintech/banking test environments: complex business logic validation, strict data/schema validation, and multi-layer (API + UI) test flows.