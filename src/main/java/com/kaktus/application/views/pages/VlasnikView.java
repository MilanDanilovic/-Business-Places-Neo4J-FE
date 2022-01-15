package com.kaktus.application.views.pages;

import com.kaktus.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@Route(value="vlasnik", layout = MainLayout.class)
@PageTitle("Vlasnik")
public class VlasnikView extends VerticalLayout {
    
    TextField imeVlasnik = new TextField();
    TextField prezimeVlasnik = new TextField();
    TextField datumRodjenjaVlasnik = new TextField();
    TextField jmbgVlasnik = new TextField();
    TextField brojTelefonaVlasnik = new TextField();

}
