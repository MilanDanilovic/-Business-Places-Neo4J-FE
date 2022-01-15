package com.kaktus.application.views.pages;

import com.kaktus.application.data.model.Vlasnik;
import com.kaktus.application.data.model.Zaposleni;
import com.kaktus.application.feign_client.VlasnikFeignClient;
import com.kaktus.application.views.MainLayout;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.vaadin.klaudeta.PaginatedGrid;

import javax.annotation.PostConstruct;

@EnableFeignClients
@Route(value="vlasnik", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Vlasnik")
public class VlasnikView extends VerticalLayout {

    TextField imeVlasnik = new TextField();
    TextField prezimeVlasnik = new TextField();
    TextField datumRodjenjaVlasnik = new TextField();
    TextField jmbgVlasnik = new TextField();
    TextField brojTelefonaVlasnik = new TextField();

    Label upozorenjeUpdate = new Label();
    Label upozorenjeDelete = new Label();
    com.vaadin.flow.component.textfield.TextField nameFilter;

    private final PaginatedGrid<Vlasnik> vlasnikGrid =new PaginatedGrid<>();
    private Vlasnik vlasnikUpdate = new Vlasnik();
    private Vlasnik vlasnikDelete = new Vlasnik();

    private final VlasnikFeignClient vlasnikFeignClient;

    public VlasnikView(VlasnikFeignClient vlasnikFeignClient) {
        this.vlasnikFeignClient = vlasnikFeignClient;
    }

    @PostConstruct
    public void init(){
        setLabels();
        configureGrid();
    }

    private void configureGrid(){
        vlasnikGrid.addColumn(Vlasnik::getIme).setHeader("Ime").setSortable(true);
        vlasnikGrid.addColumn(Vlasnik::getPrezime).setHeader("Prezime").setSortable(true);
        vlasnikGrid.addColumn(Vlasnik::getDatum_rodjenja).setHeader("Datum rodjenja").setSortable(true);
        vlasnikGrid.addColumn(Vlasnik::getJmbg).setHeader("JMBG").setSortable(true);
        vlasnikGrid.addColumn(Vlasnik::getBroj_telefona).setHeader("Broj telefona").setSortable(true);

        vlasnikGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        vlasnikGrid.setWidthFull();
        vlasnikGrid.setItems(vlasnikFeignClient.findAllVlasnik());
        vlasnikGrid.setPageSize(10);
        vlasnikGrid.setPaginatorSize(3);
        vlasnikGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        vlasnikGrid.setPaginatorTexts("Strana", "od");

        Div sideBar = configureSideBar();

        vlasnikGrid.addSelectionListener(click -> {
            sideBar.removeClassName("hidden");
            if(click.getFirstSelectedItem().isPresent()){
                Vlasnik vlasnikPostojeci = click.getFirstSelectedItem().get();

                vlasnikUpdate.setId(vlasnikPostojeci.getId());
                vlasnikDelete.setId(vlasnikPostojeci.getId());

                if(vlasnikPostojeci.getIme() != null) {
                    imeVlasnik.setValue(vlasnikPostojeci.getIme());
                }
                if(vlasnikPostojeci.getPrezime() != null){
                    prezimeVlasnik.setValue(vlasnikPostojeci.getPrezime());
                }
                if(vlasnikPostojeci.getDatum_rodjenja() != null){
                    datumRodjenjaVlasnik.setValue(vlasnikPostojeci.getDatum_rodjenja());
                }
                if (vlasnikPostojeci.getJmbg() != null) {
                    jmbgVlasnik.setValue(String.valueOf(vlasnikPostojeci.getJmbg()));
                }
                if(vlasnikPostojeci.getBroj_telefona() != null){
                    brojTelefonaVlasnik.setValue(String.valueOf(vlasnikPostojeci.getBroj_telefona()));
                }

            }
            else {
                sideBar.addClassName("hidden");
            }
        });

        HorizontalLayout gridWithSideBar = new HorizontalLayout();
        VerticalLayout zaposleniGridWrapLayout = new VerticalLayout();

        zaposleniGridWrapLayout.add(vlasnikGrid);
        sideBar.addClassName("hidden");
        gridWithSideBar.add(zaposleniGridWrapLayout,sideBar);
        gridWithSideBar.setSizeFull();
        gridWithSideBar.setFlexGrow(5);

        add(gridWithSideBar);
    }

    private void onFilter(HasValue.ValueChangeEvent<String> event) {
        ListDataProvider<Vlasnik> dataProvider = (ListDataProvider<Vlasnik>) vlasnikGrid.getDataProvider();
        dataProvider.setFilter(Vlasnik::filterToString, s -> caseInsensitiveContains(s, event.getValue()));
    }

    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }



    private void refreshGrid(){
        vlasnikGrid.setItems(vlasnikFeignClient.findAllVlasnik());
    }

    private Dialog dialogUpdate(String text, Vlasnik vlasnik){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                vlasnikFeignClient.updateVlasnik(vlasnik);
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Promene uspesno sacuvane!");
                notification.setDuration(3000);
                notification.open();

                refreshGrid();
            } catch (Exception e) {
                Notification notification = new Notification("Greska prilikom cuvanja!", 3000);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
            }
        });

        potvrdiButton.getStyle().set("margin-right","10px");

        Button odustaniButton = new Button("Ne", event -> {
            dialog.close();
        });

        potvrdiButton.addClickShortcut(Key.ENTER);
        potvrdiButton.addClassName("m-5");
        odustaniButton.addClassName("m-5");

        dialog.add(new Div( potvrdiButton, odustaniButton));
        return dialog;
    }

    private Dialog dialogDelete(String text, Vlasnik vlasnik){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                vlasnikFeignClient.deleteVlasnik(vlasnik);
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Uspesno obrisan vlasnik!");
                notification.setDuration(3000);
                notification.open();

                refreshGrid();
            } catch (Exception e) {
                Notification notification = new Notification("Greska prilikom brisanja!", 3000);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.open();
            }
        });

        potvrdiButton.getStyle().set("margin-right","10px");

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

        sacuvajButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        odustaniButton.setIcon(VaadinIcon.CLOSE.create());

        imeVlasnik.setReadOnly(false);
        prezimeVlasnik.setReadOnly(false);
        datumRodjenjaVlasnik.setReadOnly(false);
        jmbgVlasnik.setReadOnly(false);
        brojTelefonaVlasnik.setReadOnly(false);

        FormLayout formLayoutSideBar = new FormLayout();
        formLayoutSideBar.add(imeVlasnik, prezimeVlasnik,
                datumRodjenjaVlasnik, jmbgVlasnik, brojTelefonaVlasnik
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
            Vlasnik vlasnikSave = new Vlasnik();
            vlasnikSave.setId(vlasnikUpdate.getId());
            vlasnikSave.setIme(imeVlasnik.getValue());
            vlasnikSave.setPrezime(prezimeVlasnik.getValue());
            vlasnikSave.setDatum_rodjenja(datumRodjenjaVlasnik.getValue());
            vlasnikSave.setJmbg(Long.valueOf(jmbgVlasnik.getValue()));
            vlasnikSave.setBroj_telefona(brojTelefonaVlasnik.getValue());

            Dialog dialog = dialogUpdate(upozorenjeUpdate.getText(), vlasnikSave);
            dialog.open();

        });

        sacuvajButton.getStyle().set("margin-right","10px");

        obrisiButton.addClickListener(click -> {
            Vlasnik vlasnikDel= new Vlasnik();
            vlasnikDel.setId(vlasnikDelete.getId());

            Dialog dialog = dialogDelete(upozorenjeDelete.getText(), vlasnikDel);
            dialog.open();
        });

        odustaniButton.addClickListener(click->{
            sideBarTmp.addClassName("hidden");
        });

        return sideBarTmp;
    }

    private void setLabels() {
        imeVlasnik.setLabel("Ime");
        prezimeVlasnik.setLabel("Prezime");
        datumRodjenjaVlasnik.setLabel("Datum rodjenja");
        jmbgVlasnik.setLabel("Jmbg");
        brojTelefonaVlasnik.setLabel("Broj telefona");
        upozorenjeUpdate.setText("Da li ste sigurni da zelite da izmenite podatke?");
        upozorenjeDelete.setText("Da li ste sigurni da zelite da obrisete vlasnika?");
    }
}
