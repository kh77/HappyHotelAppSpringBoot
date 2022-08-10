package com.sm.service;

import com.sm.integration.MailSender;
import com.sm.integration.PaymentService;
import com.sm.repo.BookingRepository;
import com.sm.resource.request.BookingRequest;
import com.sm.util.CurrencyConverter;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
public class BookingService {

    private final PaymentService paymentService;
    private final RoomService roomService;
    private final BookingRepository bookingRepository;
    private final MailSender mailSender;

    private final static double BASE_PRICE_USD = 50.0;

    public int getAvailablePlaceCount() {
        return roomService.getAvailableRooms()
                .stream()
                .map(room -> room.getCapacity())
                .reduce(0, Integer::sum);
    }

    public double calculatePrice(BookingRequest bookingRequest) {
        long nights = ChronoUnit.DAYS.between(bookingRequest.getDateFrom(), bookingRequest.getDateTo());
        return BASE_PRICE_USD * bookingRequest.getGuestCount() * nights;
    }

    public double calculatePriceEuro(BookingRequest bookingRequest) {
        return CurrencyConverter.toEuro(calculatePrice(bookingRequest));
    }

    public String makeBooking(BookingRequest bookingRequest) {
        String roomId = roomService.findAvailableRoomId(bookingRequest);
        double price = calculatePrice(bookingRequest);

        if (bookingRequest.isPrepaid()) {
            paymentService.pay(bookingRequest, price);
        }

        bookingRequest.setRoomId(roomId);
        String bookingId = bookingRepository.save(bookingRequest);
        roomService.bookRoom(roomId);
        mailSender.sendBookingConfirmation(bookingId);
        return bookingId;
    }

    public void cancelBooking(String id) {
        BookingRequest request = bookingRepository.get(id);
        roomService.unbookRoom(request.getRoomId());
        bookingRepository.delete(id);
    }

    public BookingService(PaymentService paymentService, RoomService roomService, BookingRepository bookingRepository,
                          MailSender mailSender) {
        this.paymentService = paymentService;
        this.roomService = roomService;
        this.bookingRepository = bookingRepository;
        this.mailSender = mailSender;
    }

}
