package com.dwalt.kodillaprojectfront.views;

import com.dwalt.kodillaprojectfront.fronclient.FrontEndClient;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import java.time.format.DateTimeFormatter;

@Route("calendar")
public class CalendarView extends VerticalLayout {

    private FullCalendar calendar = FullCalendarBuilder.create().build();

    private FrontEndClient frontEndClient;

    public CalendarView(FrontEndClient frontEndClient) {
        this.frontEndClient = frontEndClient;

        getStyle().set("flex-grow", "1");
        setFlexGrow(1, calendar);
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);

        createCalendarToolbar();
        addEntriesToCalendarFromDataBase();
        calendar.addEntryClickedListener(event -> new Dialog(new Label(event.getEntry().getId())).open());

        add(calendar);
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
        frontEndClient.getAllReservations().forEach(e -> {
            Entry entry = new Entry(String.valueOf(e.getId()));
            entry.setTitle("Rezerwacja" + e.getId());
            entry.setStart(e.getFromDate().atStartOfDay());
            entry.setEnd(e.getToDate().atStartOfDay().plusDays(1));
            entry.setAllDay(true);
            calendar.addEntry(entry);
        });
    }
}
