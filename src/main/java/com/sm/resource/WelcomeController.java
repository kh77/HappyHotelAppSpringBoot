package com.sm.resource;

import com.sm.service.BookingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    private BookingService bookingService;

    @RequestMapping("/available")
    public String index() {
        return "Available: " + bookingService.getAvailablePlaceCount();
    }

    public WelcomeController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

}
