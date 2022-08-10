package com.sm;

import com.sm.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SampleControllerSampleTest {

    @LocalServerPort
    private int port;

    private URL base;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private TestRestTemplate template;

    @BeforeEach
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/available");
    }

    @Test
    @DisplayName("Test Success: Return available quantity")
    public void should_return_available_quantity() throws Exception {
        String expected = "Available: 10";
        when(bookingService.getAvailablePlaceCount()).thenReturn(10);
        ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
        String actual = response.getBody();
        assertEquals(expected, actual);
    }

}
