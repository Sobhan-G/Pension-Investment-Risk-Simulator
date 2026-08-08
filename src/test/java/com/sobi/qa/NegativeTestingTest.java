package com.sobi.qa;

import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class NegativeTestingTest {

    private static final String BASE_URL = "https://api.frankfurter.app";

    @ParameterizedTest
    @ValueSource(strings = {"XXX", "", "123", "ABCDE", "!!!"})
    void avvisarOgiltigValutaMed404(String ogiltigValuta) {

        Response response = given()
                .queryParam("from", ogiltigValuta)
                .queryParam("to", "SEK")
                .when()
                .get(BASE_URL + "/latest");

        assertEquals(404, response.getStatusCode(),
                "Förväntade 404 för ogiltig valuta: '" + ogiltigValuta + "'");

        String messageField = response.jsonPath().getString("message");
        assertNotNull(messageField, "Felmeddelande saknas i svaret");
        assertFalse(messageField.isBlank(), "Felmeddelandet är tomt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"SEK", "USD", "NOK"})
    void accepterarGiltigaValutorMed200(String giltigValuta) {

        Response response = given()
                .queryParam("from", "EUR")
                .queryParam("to", giltigValuta)
                .when()
                .get(BASE_URL + "/latest");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void avvisarKonverteringTillSammaValutaMed422() {

        // Edge case: API:et ska INTE tillåta att konvertera en valuta till sig själv
        Response response = given()
                .queryParam("from", "EUR")
                .queryParam("to", "EUR")
                .when()
                .get(BASE_URL + "/latest");

        assertEquals(422, response.getStatusCode(),
                "Förväntade 422 när from och to är samma valuta (EUR -> EUR)");
    }
}