package com.dwalt.kodillaprojectfront.domain;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class Reservation {
    private Long id;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long roomId;
}
