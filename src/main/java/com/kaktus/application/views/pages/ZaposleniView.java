package com.kaktus.application.views.pages;

import com.kaktus.application.data.model.Zaposleni;
import com.kaktus.application.feign_client.ZaposleniFeignClient;
import com.kaktus.application.views.MainLayout;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.vaadin.klaudeta.PaginatedGrid;

import javax.annotation.PostConstruct;

@EnableFeignClients
@Route(value="zaposleni", layout = MainLayout.class)
@PageTitle("Zaposleni")
public class ZaposleniView extends VerticalLayout {


    private final PaginatedGrid<Zaposleni> zaposleniGrid =new PaginatedGrid<>();

    private final ZaposleniFeignClient zaposleniFeignClient;

    public ZaposleniView(ZaposleniFeignClient zaposleniFeignClient) {
        this.zaposleniFeignClient = zaposleniFeignClient;
    }

    @PostConstruct
    public void init(){
        configureGrid();
    }

    private void configureGrid(){
        zaposleniGrid.addColumn(Zaposleni::getIme).setHeader("Ime").setSortable(true);
        zaposleniGrid.addColumn(Zaposleni::getPrezime).setHeader("Prezime").setSortable(true);
        zaposleniGrid.addColumn(Zaposleni::getPol).setHeader("Pol").setSortable(true);
        zaposleniGrid.addColumn(Zaposleni::getDatum_rodjenja).setHeader("Datum rodjenja").setSortable(true);
        zaposleniGrid.addColumn(Zaposleni::getJmbg).setHeader("JMBG").setSortable(true);
        zaposleniGrid.addColumn(Zaposleni::getKartica).setHeader("Status kartice").setSortable(true);

        GridContextMenu<Zaposleni> gridMenuZaposleni =zaposleniGrid.addContextMenu();

        zaposleniGrid.setItems(zaposleniFeignClient.findAllZaposleni());
        zaposleniGrid.setPageSize(10);
        zaposleniGrid.setPaginatorSize(3);
        zaposleniGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        zaposleniGrid.setPaginatorTexts("Strana", "od");

        add(zaposleniGrid);
    }

}
