package com.dwalt.kodillaprojectfront.views;

import com.dwalt.kodillaprojectfront.domain.Reservation;
import com.dwalt.kodillaprojectfront.domain.Room;
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
import com.vaadin.flow.router.RouterLink;

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

    public MainView(FrontEndClient frontEndClient) {
        this.frontEndClient = frontEndClient;

        setAlignItems(Alignment.CENTER);

        createNavMenu();
        createDatesSelections();
        testCreate();
    }

    private void createNavMenu() {
        Div menu = new Div();
        menu.add(new RouterLink(" Home ", MainView.class));
        menu.add(new RouterLink(" Reservation List ", ReservationListView.class));
        menu.add(new RouterLink(" Calendar ", CalendarView.class));
        HorizontalLayout mainMenu = new HorizontalLayout(menu);
        mainMenu.setSpacing(true);
        add(mainMenu);
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
            roomsChoiceDialog();

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

    private void roomsChoiceDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("800px");
        dialog.setHeight("1000px");
        dialog.removeAll();

        Map<String, Double> rates = frontEndClient.getCurrenciesRates();
        int bookedDays = Period.between(bookFrom, bookTo).getDays();
        List<Room> chosenRooms = new ArrayList<>();

        frontEndClient.getAllRooms().stream()
                .filter(room -> room.getCapacity() >= guests)
                .collect(Collectors.toList())
                .forEach(room -> {
                    double totalPrice = bookedDays * room.getPricePerDay();

                    TextField priceField = new TextField();

                    priceField.setLabel("Total Price");
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
                    roomsLayout.add(new Paragraph(priceField, currencySelection));

                    Button reservationButton = new Button("Book", buttonClickEvent -> {
                        roomsLayout.removeAll();

                        roomsLayout.add(new Paragraph("You just booked room: " + room.getTitle()));
                        roomsLayout.add(new Paragraph("Total days booked: " + bookedDays));
                        roomsLayout.add(new Paragraph("From: " + bookFrom + " to: " + bookTo));
                        roomsLayout.add(new Paragraph("Total price: " + priceField.getValue() + " " + currencySelection.getValue()));

                        dialog.setHeight("400px");
                        dialog.setWidth("400px");
                        dialog.removeAll();
                        dialog.add(roomsLayout);

                        Reservation reservation = Reservation.builder()
                                .fromDate(bookFrom)
                                .toDate(bookTo)
                                .room(frontEndClient.getRoom(room.getId()))
                                .build();
                        frontEndClient.sendReservation(reservation);
                    });

                    dialog.add(roomsLayout, reservationButton);
                });
        dialog.open();
    }

    private void testCreate() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.getStyle().set("border", "1px solid #9E9E9E");

        Div component1 = new Div();
        component1.add("ijfis sjfisdjf  safjisdjf  afjs fja f f ajf j fdjsf s afjs fi");
        Div component2 = new Div();
        component1.add("ijfis sjfisdjf  safjisdjf  afjs fja f f ajf j fdjsf s afjs fi");
        Div component3 = new Div();
        component1.add("ijfis sjfisdjf  safjisdjf  afjs fja f f ajf j fdjsf s afjs fi");

        layout.addAndExpand(component1, component2, component3);
        add(layout);
    }
}
