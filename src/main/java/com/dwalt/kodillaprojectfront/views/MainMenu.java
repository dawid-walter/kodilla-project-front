package com.dwalt.kodillaprojectfront.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainMenu extends VerticalLayout {
    public HorizontalLayout createMenu() {
        setAlignItems(Alignment.CENTER);
        HorizontalLayout layout = new HorizontalLayout();
        layout.add(new H1(new RouterLink("Home", MainView.class)), new H1(new RouterLink("Admin Panel", AdminPanelView.class)));
        return layout;
    }
}
