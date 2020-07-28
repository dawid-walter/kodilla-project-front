package com.dwalt.kodillaprojectfront.fronclient;

import com.dwalt.kodillaprojectfront.configuration.FrontEndConfiguration;
import com.dwalt.kodillaprojectfront.domain.Reservation;
import com.dwalt.kodillaprojectfront.domain.Room;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FrontEndClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FrontEndClient.class);
    private final RestTemplate restTemplate;
    private final FrontEndConfiguration configuration;


    public List<Reservation> getAllReservations() {
        try {
            Optional<Reservation[]> boardsResponse = Optional.ofNullable(restTemplate.getForObject(configuration.getBackApiEndpoint() + "/reservations", Reservation[].class));
            return Arrays.asList(boardsResponse.orElse(new Reservation[0]));
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public Reservation getReservationById(String id) {
        Optional<Reservation> boardsResponse = Optional.ofNullable(restTemplate.getForObject(configuration.getBackApiEndpoint() + "/reservations/" + id, Reservation.class));
        return boardsResponse.orElse(new Reservation());
    }

    public List<Room> getAllRooms() {
        try {
            Optional<Room[]> boardsResponse = Optional.ofNullable(restTemplate.getForObject(configuration.getBackApiEndpoint() + "/rooms/", Room[].class));
            return Arrays.asList(boardsResponse.orElse(new Room[0]));
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Room> getAllRoomsInDates(LocalDate from, LocalDate to) {
        try {
            Optional<Room[]> boardsResponse = Optional.ofNullable(restTemplate.getForObject(createGetRoomsInDatesUrl(from, to), Room[].class));
            return Arrays.asList(boardsResponse.orElse(new Room[0]));
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private URI createGetRoomsInDatesUrl(LocalDate from, LocalDate to) {
        return UriComponentsBuilder.fromHttpUrl(configuration.getBackApiEndpoint() + "/rooms/inDates")
                .queryParam("from", from)
                .queryParam("to", to)
                .build().encode().toUri();
    }

    public Room getRoom(Long id) {
        try {
            Optional<Room> boardsResponse = Optional.ofNullable(restTemplate.getForObject(configuration.getBackApiEndpoint() + "/rooms/" + id, Room.class));
            return boardsResponse.orElse(new Room());
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return new Room();
        }
    }

    public Map getCurrenciesRates() {
        return restTemplate.getForObject(configuration.getBackApiEndpoint() + "/currencies/", Map.class);
    }

    public void sendReservation(Reservation reservation) {
        restTemplate.postForObject(configuration.getBackApiEndpoint() + "/reservations/", reservation, Reservation.class);
    }

    public void deleteRoom(Long id) {
        restTemplate.delete(configuration.getBackApiEndpoint() + "/rooms/" + id);
    }

    public void sendRoom(Room room) {
        restTemplate.postForObject(configuration.getBackApiEndpoint() + "/rooms/", room, Room.class);
    }

    public void updateRoom(Room room) {
        restTemplate.put(configuration.getBackApiEndpoint() + "/rooms/", room, Room.class);
    }

    public void deleteReservation(Long id) {
        restTemplate.delete(configuration.getBackApiEndpoint() + "/reservations/" + id);
    }

    public void updateReservation(Reservation reservation) {
        restTemplate.put(configuration.getBackApiEndpoint() + "/reservations/", reservation, Reservation.class);
    }
}
