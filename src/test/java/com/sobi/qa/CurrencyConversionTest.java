package com.sobi.qa;

import com.sobi.qa.model.CurrencyResponse;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class CurrencyConversionTest {

    private static final String BASE_URL = "https://api.frankfurter.app";

    @ParameterizedTest
    @CsvSource({
        "EUR, SEK",
        "USD, SEK",
        "GBP, SEK",
        "NOK, SEK",
        "DKK, SEK"
    })
    void konverterarPensionKorrekt(String baseValuta, String malValuta) {

        // 1. Anropa API:et
        Response response = given()
                .queryParam("from", baseValuta)
                .queryParam("to", malValuta)
                .when()
                .get(BASE_URL + "/latest");

        // 2. Kontrollera statuskod
        assertEquals(200, response.getStatusCode());

        // 3. Mappa JSON-svaret till vår modellklass
        CurrencyResponse body = response.as(CurrencyResponse.class);

        // 4. Kontrollera att växelkursen finns och är positiv
        Double rate = body.getRates().get(malValuta);
        assertNotNull(rate, "Växelkursen saknas i svaret");
        assertTrue(rate > 0, "Växelkursen måste vara positiv");

        // 5. Simulera en pensionsutbetalning, t.ex. 10 000 i basvaluta
        double pensionAmount = 10000;
        double converted = pensionAmount * rate;

        assertTrue(converted > 0);
        assertFalse(Double.isNaN(converted));
        assertFalse(Double.isInfinite(converted));
    }
}