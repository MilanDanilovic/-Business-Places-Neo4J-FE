package com.kaktus.application.views.pages;

import com.kaktus.application.data.model.PoslovniProstor;
import com.kaktus.application.data.model.Zaposleni;
import com.kaktus.application.feign_client.PoslovniProstorFeignClient;
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
@Route(value="prostor", layout = MainLayout.class)
@PageTitle("Prostor")
public class PoslovniProstorView extends VerticalLayout {
    /*private Long id;
    private Double kvadratura;
    private String adresa;*/
    TextField kvadraturaProstor = new TextField();
    TextField adresaProstor = new TextField();

    Label upozorenjeUpdate = new Label();
    Label upozorenjeDelete = new Label();

    private final PaginatedGrid<PoslovniProstor> poslovniProstorGrid =new PaginatedGrid<>();
    private PoslovniProstor prostorUpdate = new PoslovniProstor();
    private PoslovniProstor prostorDelete = new PoslovniProstor();

    private final PoslovniProstorFeignClient poslovniProstorFeignClient;

    public PoslovniProstorView(PoslovniProstorFeignClient poslovniProstorFeignClient) {
        this.poslovniProstorFeignClient = poslovniProstorFeignClient;
    }

    @PostConstruct
    public void init(){
        setLabels();
        configureGrid();
    }

    private void configureGrid(){
        poslovniProstorGrid.addColumn(PoslovniProstor::getKvadratura).setHeader("Kvadratura").setSortable(true);
        poslovniProstorGrid.addColumn(PoslovniProstor::getAdresa).setHeader("Adresa").setSortable(true);

        poslovniProstorGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        poslovniProstorGrid.setWidthFull();
        poslovniProstorGrid.setItems(poslovniProstorFeignClient.findAllPoslovniProstor());
        poslovniProstorGrid.setPageSize(10);
        poslovniProstorGrid.setPaginatorSize(3);
        poslovniProstorGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        poslovniProstorGrid.setPaginatorTexts("Strana", "od");

        Div sideBar = configureSideBar();

        poslovniProstorGrid.addSelectionListener(click -> {
            sideBar.removeClassName("hidden");
            if(click.getFirstSelectedItem().isPresent()){
                PoslovniProstor prostorPostojeci = click.getFirstSelectedItem().get();

                prostorUpdate.setId(prostorPostojeci.getId());
                prostorDelete.setId(prostorPostojeci.getId());

                if(prostorPostojeci.getKvadratura() != null) {
                    kvadraturaProstor.setValue(String.valueOf(prostorPostojeci.getKvadratura()));
                }
                if(prostorPostojeci.getAdresa() != null){
                    adresaProstor.setValue(prostorPostojeci.getAdresa());
                }
            }
            else {
                sideBar.addClassName("hidden");
            }
        });

        HorizontalLayout gridWithSideBar = new HorizontalLayout();
        VerticalLayout zaposleniGridWrapLayout = new VerticalLayout();

        zaposleniGridWrapLayout.add(poslovniProstorGrid);
        sideBar.addClassName("hidden");
        gridWithSideBar.add(zaposleniGridWrapLayout,sideBar);
        gridWithSideBar.setSizeFull();
        gridWithSideBar.setFlexGrow(5);

        add(gridWithSideBar);
    }

    private Dialog dialogUpdate(String text, PoslovniProstor poslovniProstor){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                poslovniProstorFeignClient.updatePoslovniProstor(poslovniProstor);
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

    private Dialog dialogDelete(String text, PoslovniProstor poslovniProstor){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                poslovniProstorFeignClient.deletePoslovniProstor(poslovniProstor);
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Uspesno obrisan poslovni prostor!");
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

        kvadraturaProstor.setReadOnly(false);
        adresaProstor.setReadOnly(false);

        FormLayout formLayoutSideBar = new FormLayout();
        formLayoutSideBar.add(kvadraturaProstor, adresaProstor
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
            PoslovniProstor prostorSave = new PoslovniProstor();
            prostorSave.setId(prostorUpdate.getId());
            prostorSave.setKvadratura(Double.valueOf(kvadraturaProstor.getValue()));
            prostorSave.setAdresa(adresaProstor.getValue());

            Dialog dialog = dialogUpdate(upozorenjeUpdate.getText(), prostorSave);
            dialog.open();

        });

        obrisiButton.addClickListener(click -> {
            PoslovniProstor prostorDel= new PoslovniProstor();
            prostorDel.setId(prostorDelete.getId());

            Dialog dialog = dialogDelete(upozorenjeDelete.getText(), prostorDel);
            dialog.open();
        });

        odustaniButton.addClickListener(click->{
            sideBarTmp.addClassName("hidden");
        });

        return sideBarTmp;
    }

    private void setLabels() {
        kvadraturaProstor.setLabel("Kvadratura");
        adresaProstor.setLabel("Adresa");
        upozorenjeUpdate.setText("Da li ste sigurni da zelite da izmenite podatke?");
        upozorenjeDelete.setText("Da li ste sigurni da zelite da obrisete poslovni prostor?");
    }
}
