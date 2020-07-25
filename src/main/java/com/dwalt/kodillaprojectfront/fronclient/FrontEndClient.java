package com.dwalt.kodillaprojectfront.fronclient;

import com.dwalt.kodillaprojectfront.domain.Reservation;
import com.dwalt.kodillaprojectfront.domain.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class FrontEndClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontEndClient.class);
    RestTemplate restTemplate;


    public FrontEndClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Reservation> getAllReservations() {
        try {
            Optional<Reservation[]> boardsResponse = Optional.ofNullable(restTemplate.getForObject("http://localhost:8080/reservations", Reservation[].class));
            return Arrays.asList(boardsResponse.orElse(new Reservation[0]));
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Room> getAllRooms() {
        try {
            Optional<Room[]> boardsResponse = Optional.ofNullable(restTemplate.getForObject("http://localhost:8080/rooms", Room[].class));
            return Arrays.asList(boardsResponse.orElse(new Room[0]));
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public Room getRoom(Long id) {
        try {
            Optional<Room> boardsResponse = Optional.ofNullable(restTemplate.getForObject("http://localhost:8080/rooms/" + id, Room.class));
            return boardsResponse.orElse(new Room());
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return new Room();
        }
    }

    public Map getCurrenciesRates() {
        return restTemplate.getForObject("http://localhost:8080/currencies/", Map.class);
    }
    public void sendReservation(Reservation reservation) {
        restTemplate.postForObject("http://localhost:8080/reservations", reservation, Reservation.class);
    }

    /*public void sendReservation(Reservation reservation, Long roomId) {
        restTemplate.postForObject("http://localhost:8080/reservations?roomId=" + roomId, reservation, Reservation.class);
    }*/


}
