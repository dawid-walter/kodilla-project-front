package com.dwalt.kodillaprojectfront.domain;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class Room {
    private Long id;
    private Color color;
    private String title;
    private String description;
    private int capacity;
    private String imageUrl;
    private double pricePerDay;

    private List<Reservation> reservations;
}

