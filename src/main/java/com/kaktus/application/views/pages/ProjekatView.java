package com.kaktus.application.views.pages;

import com.kaktus.application.data.model.Projekat;
import com.kaktus.application.data.model.Zaposleni;
import com.kaktus.application.feign_client.ProjektiFeignClient;
import com.kaktus.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.vaadin.klaudeta.PaginatedGrid;

import javax.annotation.PostConstruct;

@EnableFeignClients
@Route(value="projekat", layout = MainLayout.class)
@PageTitle("Projekat")
public class ProjekatView extends VerticalLayout {
    /* private Long id;
    private String naziv;
    private Long sifra_projekta;*/
    TextField nazivProjekat = new TextField();
    TextField sifraProjekat = new TextField();

    Label upozorenjeUpdate = new Label();
    Label upozorenjeDelete = new Label();

    private final PaginatedGrid<Projekat> projekatGrid =new PaginatedGrid<>();
    private Projekat projekatUpdate = new Projekat();
    private Projekat projekatDelete = new Projekat();

    private final ProjektiFeignClient projektiFeignClient;

    public ProjekatView(ProjektiFeignClient projektiFeignClient) {
        this.projektiFeignClient = projektiFeignClient;
    }

    @PostConstruct
    public void init(){
        setLabels();
        configureGrid();
    }

    private void configureGrid(){
        projekatGrid.addColumn(Projekat::getNaziv).setHeader("Naziv").setSortable(true);
        projekatGrid.addColumn(Projekat::getSifra_projekta).setHeader("Sifra projekta").setSortable(true);

        projekatGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        projekatGrid.setWidthFull();
        projekatGrid.setItems(projektiFeignClient.findAllProjekat());
        projekatGrid.setPageSize(10);
        projekatGrid.setPaginatorSize(3);
        projekatGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        projekatGrid.setPaginatorTexts("Strana", "od");

        Div sideBar = configureSideBar();

        projekatGrid.addSelectionListener(click -> {
            sideBar.removeClassName("hidden");
            if(click.getFirstSelectedItem().isPresent()){
                Projekat projekatPostojeci = click.getFirstSelectedItem().get();

                projekatUpdate.setId(projekatPostojeci.getId());
                projekatDelete.setId(projekatPostojeci.getId());

                if(projekatPostojeci.getNaziv() != null) {
                    nazivProjekat.setValue(projekatPostojeci.getNaziv());
                }
                if(projekatPostojeci.getSifra_projekta() != null){
                    sifraProjekat.setValue(String.valueOf(projekatPostojeci.getSifra_projekta()));
                }
            }
            else {
                sideBar.addClassName("hidden");
            }
        });

        HorizontalLayout gridWithSideBar = new HorizontalLayout();
        VerticalLayout zaposleniGridWrapLayout = new VerticalLayout();

        zaposleniGridWrapLayout.add(projekatGrid);
        sideBar.addClassName("hidden");
        gridWithSideBar.add(zaposleniGridWrapLayout,sideBar);
        gridWithSideBar.setSizeFull();
        gridWithSideBar.setFlexGrow(5);

        add(gridWithSideBar);
    }

    private Dialog dialogUpdate(String text, Projekat projekat){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                projektiFeignClient.updateProjekat(projekat);
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Promene uspesno sacuvane!");
                notification.setDuration(3000);
                notification.open();
            } catch (Exception e) {
                Notification notification = new Notification("Greska prilikom cuvanja!", 3000);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
            }
        });

        Button odustaniButton = new Button("Ne", event -> {
            dialog.close();
        });

        potvrdiButton.addClickShortcut(Key.ENTER);
        potvrdiButton.addClassName("m-5");
        odustaniButton.addClassName("m-5");

        dialog.add(new Div( potvrdiButton, odustaniButton));
        return dialog;
    }

    private Dialog dialogDelete(String text, Projekat projekat){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                projektiFeignClient.deleteProjekat(projekat);
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Uspesno obrisan projekat!");
                notification.setDuration(3000);
                notification.open();
            } catch (Exception e) {
                Notification notification = new Notification("Greska prilikom brisanja!", 3000);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
            }
        });

        Button odustaniButton = new Button("Ne", event -> {
            dialog.close();
        });

        potvrdiButton.addClickShortcut(Key.ENTER);
        potvrdiButton.addClassName("m-5");
        odustaniButton.addClassName("m-5");

        dialog.add(new Div( potvrdiButton, odustaniButton));
        return dialog;
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

        nazivProjekat.setReadOnly(false);
        sifraProjekat.setReadOnly(false);

        FormLayout formLayoutSideBar = new FormLayout();
        formLayoutSideBar.add(nazivProjekat, sifraProjekat
        );

        formLayoutSideBar.setResponsiveSteps(

                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        HorizontalLayout buttonsWrap = new HorizontalLayout(sacuvajButton, obrisiButton, odustaniButton);
        buttonsWrap.getStyle().set("justify-content","center");

        sideBarTmp.add(formLayoutSideBar, buttonsWrap);
        sideBarTmp.addClassName("view-animation");
        sideBarTmp.addClassName("mw-30");

        sacuvajButton.addClickListener(click -> {
            Projekat projekatSave = new Projekat();
            projekatSave.setId(projekatUpdate.getId());
            projekatSave.setNaziv(nazivProjekat.getValue());
            projekatSave.setSifra_projekta(Long.valueOf(sifraProjekat.getValue()));

            Dialog dialog = dialogUpdate(upozorenjeUpdate.getText(), projekatSave);
            dialog.open();

        });

        obrisiButton.addClickListener(click -> {
            Projekat projekatDel= new Projekat();
            projekatDel.setId(projekatDelete.getId());

            Dialog dialog = dialogDelete(upozorenjeDelete.getText(), projekatDel);
            dialog.open();
        });

        odustaniButton.addClickListener(click->{
            sideBarTmp.addClassName("hidden");
        });

        return sideBarTmp;
    }

    private void setLabels() {
        nazivProjekat.setLabel("Naziv");
        sifraProjekat.setLabel("Sifra projekta");
        upozorenjeUpdate.setText("Da li ste sigurni da zelite da izmenite podatke?");
        upozorenjeDelete.setText("Da li ste sigurni da zelite da obrisete projekat?");
    }
}
