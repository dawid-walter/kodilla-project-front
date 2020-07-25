package com.dwalt.kodillaprojectfront.views;

import com.dwalt.kodillaprojectfront.domain.Reservation;
import com.dwalt.kodillaprojectfront.fronclient.FrontEndClient;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("reservations")
public class ReservationListView extends VerticalLayout {
    public ReservationListView(FrontEndClient frontEndClient) {
        List<Reservation> reservationList = frontEndClient.getAllReservations();
        Grid<Reservation> grid = new Grid<>();
        grid.setItems(reservationList);
        grid.addColumn(Reservation::getFromDate).setHeader("From");
        grid.addColumn(Reservation::getToDate).setHeader("To");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        add(grid);
    }
}
