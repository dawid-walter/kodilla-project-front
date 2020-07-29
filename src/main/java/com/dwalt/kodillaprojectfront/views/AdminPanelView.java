package com.dwalt.kodillaprojectfront.views;

import com.dwalt.kodillaprojectfront.domain.Color;
import com.dwalt.kodillaprojectfront.domain.Reservation;
import com.dwalt.kodillaprojectfront.domain.Room;
import com.dwalt.kodillaprojectfront.fronclient.FrontEndClient;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Route("adminPanel")
public class AdminPanelView extends VerticalLayout {
    private FullCalendar calendar = FullCalendarBuilder.create().build();
    MainMenu mainMenu = new MainMenu();

    private FrontEndClient frontEndClient;

    public AdminPanelView(FrontEndClient frontEndClient) {
        this.frontEndClient = frontEndClient;
        getStyle().set("flex-grow", "1");
        setFlexGrow(1, calendar);
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        add(mainMenu.createMenu());

        createCalendarToolbar();

        addEntriesToCalendarFromDataBase();
        createCalendarEntryEdit();

        add(calendar);

        createAddRoom();

        createRoomList();
    }

    private void createCalendarToolbar() {
        Button buttonToday = new Button("Today", VaadinIcon.HOME.create(), e -> calendar.today());
        Button buttonPrevious = new Button("Previous", VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
        Button buttonNext = new Button("Next", VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
        buttonNext.setIconAfterText(true);

        DatePicker gotoDate = new DatePicker();
        gotoDate.addValueChangeListener(event1 -> calendar.gotoDate(event1.getValue()));
        gotoDate.getElement().getStyle().set("visibility", "hidden");
        gotoDate.getElement().getStyle().set("position", "fixed");
        gotoDate.setWidth("0px");
        gotoDate.setHeight("0px");
        gotoDate.setWeekNumbersVisible(true);
        Button buttonDatePicker = new Button(VaadinIcon.CALENDAR.create());
        buttonDatePicker.getElement().appendChild(gotoDate.getElement());
        buttonDatePicker.addClickListener(event -> gotoDate.open());

        calendar.addDatesRenderedListener(event -> buttonDatePicker.setText(event.getIntervalStart().format(DateTimeFormatter.ofPattern("MMMM yyyy"))));

        HorizontalLayout toolbar = new HorizontalLayout(buttonToday, buttonPrevious, buttonNext, buttonDatePicker, gotoDate);

        add(toolbar);
    }

    private void addEntriesToCalendarFromDataBase() {
        frontEndClient.getAllReservations().forEach(reservation -> {
            Entry entry = new Entry(String.valueOf(reservation.getId()));
            entry.setColor(frontEndClient.getRoom(reservation.getRoomId()).getColor().name().toLowerCase());
            entry.setTitle("Reservation for: " + frontEndClient.getRoom(reservation.getRoomId()).getTitle());
            entry.setStart(reservation.getFromDate().atStartOfDay());
            entry.setEnd(reservation.getToDate().atStartOfDay());
            entry.setAllDay(true);
            calendar.addEntry(entry);
        });
    }

    private void createAddRoom() {
        add(
                new Button("Add new room", VaadinIcon.PLUS.create(), buttonClickEvent -> {
                    Dialog dialog = new Dialog();
                    dialog.removeAll();

                    Select<String> colorSelection = new Select<>();
                    colorSelection.setItems("red", "blue", "green", "orange", "purple", "black");
                    colorSelection.setLabel("Color in calendar");

                    TextField roomTitleField = new TextField("Room title");
                    TextField roomDescriptionField = new TextField("Room description");
                    TextField roomCapacityField = new TextField("Capacity");
                    TextField roomImgField = new TextField("Img url");
                    TextField roomPriceField = new TextField("Price per day");

                    VerticalLayout addRoomFields = new VerticalLayout();
                    addRoomFields.add(colorSelection, roomTitleField, roomDescriptionField, roomCapacityField, roomImgField, roomPriceField);

                    Button addButton = new Button("Add room", VaadinIcon.PLUS.create(), buttonClickEvent1 -> {
                        frontEndClient.sendRoom(
                                Room.builder()
                                        .color(Color.valueOf(colorSelection.getValue().toUpperCase()))
                                        .title(roomTitleField.getValue())
                                        .description(roomDescriptionField.getValue())
                                        .capacity(Integer.parseInt(roomCapacityField.getValue()))
                                        .imageUrl(roomImgField.getValue())
                                        .pricePerDay(Double.parseDouble(roomPriceField.getValue()))
                                        .build());
                        dialog.close();
                        Dialog confirmationDialog = new Dialog();
                        confirmationDialog.add(new Paragraph("Room added"));
                        confirmationDialog.open();
                        refreshPage();
                    });
                    Button cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create(), buttonClickEvent1 -> dialog.close());

                    HorizontalLayout addRoomButtons = new HorizontalLayout();
                    addRoomButtons.add(addButton, cancelButton);

                    dialog.add(addRoomFields, addRoomButtons);
                    dialog.open();
                }));
    }

    private void createRoomList() {
        frontEndClient.getAllRooms().forEach(room -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout(
                    new Paragraph(room.getTitle()),
                    new VerticalLayout(
                            new Button("Edit room", VaadinIcon.PENCIL.create(), buttonClickEvent -> {
                                Dialog dialog = new Dialog();
                                dialog.removeAll();
                                Select<String> colorSelection = new Select<>();
                                colorSelection.setItems("red", "blue", "green", "orange", "purple", "black");
                                colorSelection.setLabel("Color in calendar");

                                TextField roomTitleField = new TextField("Room title");
                                TextField roomDescriptionField = new TextField("Room description");
                                TextField roomCapacityField = new TextField("Capacity");
                                TextField roomImgField = new TextField("Img url");
                                TextField roomPriceField = new TextField("Price per day");

                                colorSelection.setValue(room.getColor().name().toLowerCase());
                                roomTitleField.setValue(room.getTitle());
                                roomDescriptionField.setValue(room.getDescription());
                                roomCapacityField.setValue(String.valueOf(room.getCapacity()));
                                roomImgField.setValue(room.getImageUrl());
                                roomPriceField.setValue(String.valueOf(room.getPricePerDay()));

                                VerticalLayout editRoomFields = new VerticalLayout();
                                editRoomFields.add(colorSelection, roomTitleField, roomDescriptionField, roomCapacityField, roomImgField, roomPriceField);

                                Button updateButton = new Button("Update room", VaadinIcon.REFRESH.create(), buttonClickEvent1 -> {
                                    frontEndClient.updateRoom(
                                            Room.builder()
                                                    .id(room.getId())
                                                    .color(Color.valueOf(colorSelection.getValue().toUpperCase()))
                                                    .title(roomTitleField.getValue())
                                                    .description(roomDescriptionField.getValue())
                                                    .capacity(Integer.parseInt(roomCapacityField.getValue()))
                                                    .imageUrl(roomImgField.getValue())
                                                    .pricePerDay(Double.parseDouble(roomPriceField.getValue()))
                                                    .build());
                                    dialog.close();
                                    Dialog confirmationDialog = new Dialog();
                                    confirmationDialog.add(new Paragraph("Room updated"));
                                    confirmationDialog.open();
                                    refreshPage();
                                });
                                Button cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create(), buttonClickEvent1 -> dialog.close());

                                HorizontalLayout editRoomButtons = new HorizontalLayout();
                                editRoomButtons.add(updateButton, cancelButton);

                                dialog.add(editRoomFields, editRoomButtons);
                                dialog.open();
                            }),
                            new Button("Details", VaadinIcon.TABS.create(), buttonClickEvent -> {
                                Dialog dialog = new Dialog();
                                dialog.removeAll();
                                VerticalLayout roomsLayout = new VerticalLayout();
                                roomsLayout.add(new Paragraph(room.getTitle()));
                                roomsLayout.add(new Paragraph(room.getDescription()));
                                roomsLayout.add(new Image(room.getImageUrl(), "image"));
                                roomsLayout.add(new Paragraph("Max guest: " + room.getCapacity()));
                                roomsLayout.add(new Paragraph("Price per day: " + room.getPricePerDay()));
                                dialog.add(roomsLayout);
                                dialog.open();
                            }),
                            new Button("Delete room", VaadinIcon.TRASH.create(), buttonClickEvent -> {
                                Dialog dialog = new Dialog();
                                dialog.removeAll();
                                dialog.add(new Paragraph("This will delete room: " + room.getTitle() + " are you sure"),
                                        new HorizontalLayout(
                                                new Button("Yes", VaadinIcon.CHECK.create(), buttonClickEvent1 -> {
                                                    frontEndClient.deleteRoom(room.getId());
                                                    refreshPage();
                                                }),
                                                new Button("Cancel", VaadinIcon.CLOSE.create(), buttonClickEvent1 -> dialog.close())));
                                dialog.open();
                            })
                    ));
            horizontalLayout.setMinWidth("400");
            add(horizontalLayout);
        });

    }

    private String[] getRoomsTable() {
        List<String> rooms = new ArrayList<>();
        frontEndClient.getAllRooms().forEach(room -> rooms.add(room.getTitle()));
        return rooms.toArray(new String[0]);
    }

    private void createCalendarEntryEdit() {
        calendar.addEntryClickedListener(calendarEntry -> {
            Long calendarEntryReservationId = Long.parseLong(calendarEntry.getEntry().getId());
            Dialog dialog = new Dialog();
            dialog.removeAll();

            VerticalLayout fieldsLayout = new VerticalLayout();
            fieldsLayout.setAlignItems(Alignment.CENTER);

            TextField reservationId = new TextField("Reservation id");
            reservationId.setValue(calendarEntry.getEntry().getId());
            reservationId.setReadOnly(true);

            DatePicker dateFrom = new DatePicker("Booked from:");
            dateFrom.setValue(calendarEntry.getEntry().getStart().toLocalDate());
            DatePicker dateTo = new DatePicker("Booked to:");
            dateTo.setValue(calendarEntry.getEntry().getEnd().toLocalDate());

            Long roomId = frontEndClient.getReservationById(calendarEntry.getEntry().getId()).getRoomId();
            Select<String> roomSelect = new Select<>(getRoomsTable());
            roomSelect.setValue(frontEndClient.getRoom(roomId).getTitle());
            roomSelect.setLabel("Room in reservation");

            fieldsLayout.add(reservationId, dateFrom, dateTo, roomSelect);

            HorizontalLayout buttonLayout = new HorizontalLayout();

            Button cancelReservationButton = new Button("Cancel", VaadinIcon.CLOSE_CIRCLE.create());
            cancelReservationButton.addClickListener(cancelEvent -> {
                dialog.removeAll();
                dialog.close();
            });

            Button deleteReservationButton = new Button("Delete", VaadinIcon.TRASH.create());
            deleteReservationButton.addClickListener(cancelEvent -> {
                frontEndClient.deleteReservation(Long.parseLong(reservationId.getValue()));
                dialog.close();
                refreshPage();
            });

            Button updateReservationButton = new Button("Update", VaadinIcon.REFRESH.create());
            updateReservationButton.addClickListener(cancelEvent -> {
                Long newRoomId = frontEndClient.getRoomByTitle(roomSelect.getValue()).getId();
                frontEndClient.updateReservation(Reservation.builder()
                        .id(calendarEntryReservationId)
                        .toDate(dateTo.getValue())
                        .fromDate(dateFrom.getValue())
                        .roomId(newRoomId)
                        .build());
                dialog.close();
                Dialog confirmationDialog = new Dialog();
                confirmationDialog.add(new Paragraph("Reservation updated"));
                confirmationDialog.open();
                refreshPage();
            });

            buttonLayout.add(cancelReservationButton, deleteReservationButton, updateReservationButton);

            dialog.add(new Paragraph("Edit reservation"), fieldsLayout, buttonLayout);
            dialog.open();
        });
    }

    private void refreshPage() {
        UI.getCurrent().getPage().reload();
    }
}
