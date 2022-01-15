package com.kaktus.application.views.pages;

import com.kaktus.application.data.model.Zaposleni;
import com.kaktus.application.feign_client.ZaposleniFeignClient;
import com.kaktus.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.vaadin.klaudeta.PaginatedGrid;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.time.format.DateTimeFormatter;

@EnableFeignClients
@Route(value="zaposleni", layout = MainLayout.class)
@PageTitle("Zaposleni")
public class ZaposleniView extends VerticalLayout {

    com.vaadin.flow.component.textfield.TextField imeZaposleni = new com.vaadin.flow.component.textfield.TextField();
    com.vaadin.flow.component.textfield.TextField prezimeZaposleni = new com.vaadin.flow.component.textfield.TextField();
    com.vaadin.flow.component.textfield.TextField datumRodjenjaZaposleni = new com.vaadin.flow.component.textfield.TextField();
    com.vaadin.flow.component.textfield.TextField polZaposleni = new com.vaadin.flow.component.textfield.TextField();
    com.vaadin.flow.component.textfield.TextField jmbgZaposleni = new com.vaadin.flow.component.textfield.TextField();
    com.vaadin.flow.component.textfield.TextField karticaZaposleni = new com.vaadin.flow.component.textfield.TextField();

    private final PaginatedGrid<Zaposleni> zaposleniGrid =new PaginatedGrid<>();

    private final ZaposleniFeignClient zaposleniFeignClient;

    public ZaposleniView(ZaposleniFeignClient zaposleniFeignClient) {
        this.zaposleniFeignClient = zaposleniFeignClient;
    }

    @PostConstruct
    public void init(){
        setLabels();
        configureGrid();
    }

    private void configureGrid(){
        zaposleniGrid.addColumn(Zaposleni::getIme).setHeader("Ime").setSortable(true);
        zaposleniGrid.addColumn(Zaposleni::getPrezime).setHeader("Prezime").setSortable(true);
        zaposleniGrid.addColumn(Zaposleni::getPol).setHeader("Pol").setSortable(true);
        zaposleniGrid.addColumn(Zaposleni::getDatum_rodjenja).setHeader("Datum rodjenja").setSortable(true);
        zaposleniGrid.addColumn(Zaposleni::getJmbg).setHeader("JMBG").setSortable(true);
        zaposleniGrid.addColumn(Zaposleni::getKartica).setHeader("Status kartice").setSortable(true);

        zaposleniGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        zaposleniGrid.setWidthFull();
        zaposleniGrid.setItems(zaposleniFeignClient.findAllZaposleni());
        zaposleniGrid.setPageSize(10);
        zaposleniGrid.setPaginatorSize(3);
        zaposleniGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        zaposleniGrid.setPaginatorTexts("Strana", "od");

        Div sideBar = configureSideBar();

        zaposleniGrid.addSelectionListener(click -> {
            sideBar.removeClassName("hidden");
            if(click.getFirstSelectedItem().isPresent()){
                Zaposleni zaposleni = click.getFirstSelectedItem().get();

                if(zaposleni.getIme() != null) {
                    imeZaposleni.setValue(zaposleni.getIme());
                }
                if(zaposleni.getPrezime() != null){
                    prezimeZaposleni.setValue(zaposleni.getPrezime());
                }
                if(zaposleni.getDatum_rodjenja() != null){
                    datumRodjenjaZaposleni.setValue(zaposleni.getDatum_rodjenja());
                }
                if(zaposleni.getPol() != null) {
                    polZaposleni.setValue(zaposleni.getPol());
                }
                if (zaposleni.getJmbg() != null) {
                    jmbgZaposleni.setValue(String.valueOf(zaposleni.getJmbg()));
                }
                if(zaposleni.getKartica() != null){
                    karticaZaposleni.setValue(String.valueOf(zaposleni.getKartica()));
                }
            }
            else {
                sideBar.addClassName("hidden");
            }
        });

        HorizontalLayout gridWithSideBar = new HorizontalLayout();
        VerticalLayout zaposleniGridWrapLayout = new VerticalLayout();

        zaposleniGridWrapLayout.add(zaposleniGrid);
        sideBar.addClassName("hidden");
        gridWithSideBar.add(zaposleniGridWrapLayout,sideBar);
        gridWithSideBar.setSizeFull();
        gridWithSideBar.setFlexGrow(5);

        add(gridWithSideBar);
    }

    private Div configureSideBar() {
        Div sideBarTmp = new Div();

        Button sacuvajButton = new Button("Sacuvaj");
        Button obrisiButton = new Button("Obrisi");
        Button odustaniButton = new Button("Odustani");

        sacuvajButton.addClassName("form-buttons");
        obrisiButton.addClassName("form-buttons");
        odustaniButton.addClassName("form-buttons");

        odustaniButton.setIcon(VaadinIcon.CLOSE.create());

        imeZaposleni.setReadOnly(false);
        prezimeZaposleni.setReadOnly(false);
        datumRodjenjaZaposleni.setReadOnly(false);
        polZaposleni.setReadOnly(false);
        jmbgZaposleni.setReadOnly(false);
        karticaZaposleni.setReadOnly(false);

        FormLayout formLayoutSideBar = new FormLayout();
        formLayoutSideBar.add(imeZaposleni, prezimeZaposleni,
                datumRodjenjaZaposleni, polZaposleni, jmbgZaposleni, karticaZaposleni
        );

        formLayoutSideBar.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2)
        );

        HorizontalLayout buttonsWrap = new HorizontalLayout(sacuvajButton, obrisiButton, odustaniButton);
        buttonsWrap.getStyle().set("justify-content","center");

        sideBarTmp.add(formLayoutSideBar, buttonsWrap);
        sideBarTmp.addClassName("view-animation");
        sideBarTmp.addClassName("mw-30");

        odustaniButton.addClickListener(click->{
            sideBarTmp.addClassName("hidden");
        });

        return sideBarTmp;
    }

    private void setLabels() {
        imeZaposleni.setLabel("Ime");
        prezimeZaposleni.setLabel("Prezime");
        datumRodjenjaZaposleni.setLabel("Datum rodjenja");
        polZaposleni.setLabel("Pol");
        jmbgZaposleni.setLabel("Jmbg");
        karticaZaposleni.setLabel("Kartica");
    }

}
