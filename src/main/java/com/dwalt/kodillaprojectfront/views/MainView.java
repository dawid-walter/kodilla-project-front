package com.dwalt.kodillaprojectfront.views;

import com.dwalt.kodillaprojectfront.domain.Reservation;
import com.dwalt.kodillaprojectfront.fronclient.FrontEndClient;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Route("main")
public class MainView extends VerticalLayout {
    private LocalDate bookFrom;
    private LocalDate bookTo;
    private int guests = 0;
    private final String[] priceCurrencies = {"PLN", "USD", "EUR", "GBP", "AUD"};
    private final FrontEndClient frontEndClient;
    Dialog searchReservationDialog = new Dialog();
    MainMenu mainMenu = new MainMenu();

    public MainView(FrontEndClient frontEndClient) {
        this.frontEndClient = frontEndClient;

        setAlignItems(Alignment.CENTER);

        add(mainMenu.createMenu());

        createDatesSelections();
    }

    private void createDatesSelections() {
        Div value = new Div();
        DatePicker datePickerFrom = new DatePicker("Date From");
        DatePicker datePickerTo = new DatePicker("DateFrom");
        datePickerFrom.setMin(LocalDate.now());
        datePickerFrom.addValueChangeListener(event -> {
            bookFrom = event.getValue();
            datePickerTo.setMin(bookFrom.plusDays(1));
            datePickerTo.setInitialPosition(bookFrom);
        });
        datePickerTo.addValueChangeListener(event -> {
            bookTo = event.getValue();
        });

        ComboBox<String> placeHolderComboBox = new ComboBox<>();
        placeHolderComboBox.setItems(getAdultsList(30));
        placeHolderComboBox.setValue("1 Adult");

        Button searchButton = new Button("Search", event -> {
            Scanner scanner = new Scanner(placeHolderComboBox.getValue());
            guests = Integer.parseInt(scanner.next());
            createRoomsChoiceDialog();
        });

        HorizontalLayout datePickers = new HorizontalLayout();
        datePickers.add(datePickerFrom, datePickerTo);

        add(datePickers, placeHolderComboBox, searchButton, value);
    }

    private List<String> getAdultsList(int maxAdults) {
        List<String> list = new ArrayList<>();
        list.add("1 Adult");
        for (int i = 2; i < maxAdults - 1; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(i).append(" Adults");
            list.add(sb.toString());
        }
        return list;
    }

    private void createRoomsChoiceDialog() {
        searchReservationDialog.setWidth("800px");
        searchReservationDialog.setHeight("1000px");
        searchReservationDialog.removeAll();

        if (bookFrom == null || bookTo == null) {
            searchReservationDialog.removeAll();
            searchReservationDialog.add(new Paragraph("Please choose dates"));
            searchReservationDialog.setWidth("200px");
            searchReservationDialog.setHeight("200px");
            searchReservationDialog.open();
        }

        Map<String, Double> rates = frontEndClient.getCurrenciesRates();
        int bookedDays = Period.between(bookFrom, bookTo).getDays();

        frontEndClient.getAllRoomsInDates(bookFrom, bookTo).stream()
                .filter(room -> room.getCapacity() >= guests)
                .collect(Collectors.toList())
                .forEach(room -> {
                    double totalPrice = bookedDays * room.getPricePerDay();

                    TextField priceField = new TextField();

                    priceField.setLabel("Total price for: " + bookedDays + " days");
                    priceField.setValue(String.valueOf(totalPrice));
                    priceField.setReadOnly(true);

                    Select<String> currencySelection = new Select<>();
                    currencySelection.setLabel("Currency");
                    currencySelection.setItems(Arrays.asList(priceCurrencies));
                    currencySelection.setValue("PLN");

                    currencySelection.addValueChangeListener(event -> {
                        double rate = rates.get(currencySelection.getValue());
                        double currencyPrice = Math.round(totalPrice * rate);
                        priceField.setValue(String.valueOf(currencyPrice));
                    });

                    VerticalLayout roomsLayout = new VerticalLayout();
                    roomsLayout.add(new Paragraph(room.getTitle()));
                    roomsLayout.add(new Paragraph(room.getDescription()));
                    roomsLayout.add(new Image(room.getImageUrl(), "image"));
                    roomsLayout.add(new Paragraph("Reservation from: " + bookFrom + " to: " + bookTo));
                    roomsLayout.add(new Paragraph(priceField, currencySelection));

                    Button reservationButton = new Button("Book", buttonClickEvent -> {
                        roomsLayout.removeAll();

                        roomsLayout.add(new Paragraph("You just booked room: " + room.getTitle()));
                        roomsLayout.add(new Paragraph("Total days booked: " + bookedDays));
                        roomsLayout.add(new Paragraph("From: " + bookFrom + " to: " + bookTo));
                        roomsLayout.add(new Paragraph("Total price: " + priceField.getValue() + " " + currencySelection.getValue()));

                        searchReservationDialog.setHeight("400px");
                        searchReservationDialog.setWidth("400px");
                        searchReservationDialog.removeAll();
                        searchReservationDialog.add(roomsLayout);

                        Reservation reservation = Reservation.builder()
                                .fromDate(bookFrom)
                                .toDate(bookTo)
                                .roomId(frontEndClient.getRoom(room.getId()).getId())
                                .build();
                        frontEndClient.sendReservation(reservation);
                    });

                    searchReservationDialog.add(roomsLayout, reservationButton);
                });
        searchReservationDialog.open();
    }
}
