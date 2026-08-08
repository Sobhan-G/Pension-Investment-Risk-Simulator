package com.sobi.qa;

import com.microsoft.playwright.*;
import com.sobi.qa.server.LocalServer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PensionForecastUiTest {

    private static LocalServer server;
    private static final int PORT = 8089;

    private Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeAll
    static void startServer() throws Exception {
        server = new LocalServer();
        server.start(PORT);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        page = browser.newPage();
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    void visarNormalPrognosKorrekt() {
        page.route("**/api/forecast", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setContentType("application/json")
                    .setBody("{\"projectedValue\": 1250000, \"annualReturn\": 6.5}")
            );
        });

        page.navigate("http://localhost:" + PORT);

        page.fill("#age", "30");
        page.fill("#monthlySavings", "2000");
        page.selectOption("#riskLevel", "medium");
        page.click("#submitBtn");

        String resultText = page.locator("#result").innerText();
        assertTrue(resultText.contains("1250000"));
        assertTrue(resultText.contains("6.5"));
    }

    @Test
    void hanterarBorskraschScenarioUtanAttKrascha() {
        page.route("**/api/forecast", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(500)
                    .setContentType("application/json")
                    .setBody("{\"error\": \"Beräkning misslyckades\"}")
            );
        });

        page.navigate("http://localhost:" + PORT);

        page.fill("#age", "30");
        page.fill("#monthlySavings", "2000");
        page.selectOption("#riskLevel", "high");
        page.click("#submitBtn");

        Locator errorMessage = page.locator("#errorMessage");
        assertTrue(errorMessage.isVisible());
        assertFalse(errorMessage.innerText().isEmpty());
    }
}