package com.kaktus.application.views.pages;

import com.kaktus.application.data.model.Firma;
import com.kaktus.application.data.model.Zaposleni;
import com.kaktus.application.feign_client.FirmaFeignClient;
import com.kaktus.application.feign_client.ZaposleniFeignClient;
import com.kaktus.application.views.MainLayout;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.vaadin.klaudeta.PaginatedGrid;

import javax.annotation.PostConstruct;
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

    Label upozorenjeUpdate = new Label();
    Label upozorenjeDelete = new Label();
    com.vaadin.flow.component.textfield.TextField nameFilter;
    Button createEntity = new Button();

    private final PaginatedGrid<Zaposleni> zaposleniGrid =new PaginatedGrid<>();
    private Zaposleni zaposleniUpdate = new Zaposleni();
    private Zaposleni zaposleniDelete = new Zaposleni();


    private final ZaposleniFeignClient zaposleniFeignClient;
    private final FirmaFeignClient firmaFeignClient;

    public ZaposleniView(ZaposleniFeignClient zaposleniFeignClient, FirmaFeignClient firmaFeignClient) {
        this.zaposleniFeignClient = zaposleniFeignClient;
        this.firmaFeignClient = firmaFeignClient;
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
                Zaposleni zaposleniPostojeci = click.getFirstSelectedItem().get();

                zaposleniUpdate.setId(zaposleniPostojeci.getId());
                zaposleniDelete.setId(zaposleniPostojeci.getId());

                if(zaposleniPostojeci.getIme() != null) {
                    imeZaposleni.setValue(zaposleniPostojeci.getIme());
                }
                if(zaposleniPostojeci.getPrezime() != null){
                    prezimeZaposleni.setValue(zaposleniPostojeci.getPrezime());
                }
                if(zaposleniPostojeci.getDatum_rodjenja() != null){
                    datumRodjenjaZaposleni.setValue(zaposleniPostojeci.getDatum_rodjenja());
                }
                if(zaposleniPostojeci.getPol() != null) {
                    polZaposleni.setValue(zaposleniPostojeci.getPol());
                }
                if (zaposleniPostojeci.getJmbg() != null) {
                    jmbgZaposleni.setValue(String.valueOf(zaposleniPostojeci.getJmbg()));
                }
                if(zaposleniPostojeci.getKartica() != null){
                    karticaZaposleni.setValue(String.valueOf(zaposleniPostojeci.getKartica()));
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

        add(createToolsTab());
        add(gridWithSideBar);
    }

    private void onFilter(HasValue.ValueChangeEvent<String> event) {
        ListDataProvider<Zaposleni> dataProvider = (ListDataProvider<Zaposleni>) zaposleniGrid.getDataProvider();
        dataProvider.setFilter(Zaposleni::filterToString, s -> caseInsensitiveContains(s, event.getValue()));
    }

    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }

    private void addZaposleniToDatabase(){
        Dialog createZaposleniDialog = new Dialog();
        createZaposleniDialog.open();

        Button save = new Button("Sacuvaj");
        Button odustani = new Button("Odustani");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setIcon(VaadinIcon.CHECK.create());
        odustani.setIcon(VaadinIcon.CLOSE.create());


        TextField ime = new TextField("Ime");
        TextField prezime = new TextField("Prezime");
        DatePicker datum_rodjenja = new DatePicker("Datum rodjenja");
        TextField jmbg = new TextField("JMBG");

        DatePicker datum_od = new DatePicker("Datum pocetka rada");
        DatePicker datum_do = new DatePicker("Datum do koga radi");
        TextField pozicija = new TextField("Pozicija");

        ComboBox<Firma> firme = new ComboBox<>("Firma");
        firme.setItems(firmaFeignClient.findAllFirma());
        firme.setItemLabelGenerator(Firma::getNaziv);

        ComboBox<String>  polIzbor = new ComboBox<>("Pol");
        polIzbor.setItems("muski","zenski");

        ComboBox<Long>  kartica = new ComboBox<>("Kartica");
        kartica.setItems(0L,1L);

        save.addClickListener(click->{
            Zaposleni zaposleni = new Zaposleni();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY");

            zaposleni.setIme(ime.getValue());
            zaposleni.setPrezime(prezime.getValue());
            zaposleni.setDatum_rodjenja(formatter.format(datum_rodjenja.getValue()));
            zaposleni.setJmbg(Long.parseLong(jmbg.getValue()));
            zaposleni.setPol(polIzbor.getValue());
            zaposleni.setIdFirme(firme.getValue().getId());
            zaposleni.setKartica(kartica.getValue());

            Dialog dialog = dialogCreate(upozorenjeUpdate.getText(), zaposleni, formatter.format(datum_od.getValue()), formatter.format(datum_do.getValue()), pozicija.getValue(), firme.getValue().getPib());
            dialog.open();

        });

        odustani.addClickListener(click->{
            createZaposleniDialog.close();
            UI.getCurrent().getPage().reload();
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(ime, prezime, datum_rodjenja, jmbg, datum_od, datum_do, pozicija, firme, polIzbor, kartica);

        FormLayout formLayoutControls = new FormLayout();
        formLayoutControls.add(
                save, odustani
        );
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("320", 2),
                new FormLayout.ResponsiveStep("400px", 3),
                new FormLayout.ResponsiveStep("500px", 4)
        );

        createZaposleniDialog.add( formLayout,formLayoutControls);
    }

    private HorizontalLayout createToolsTab(){

        HorizontalLayout toolBar = new HorizontalLayout();
        nameFilter = new TextField();
        nameFilter.focus();
        nameFilter.setPlaceholder("Pretrazi..");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        nameFilter.addValueChangeListener(this::onFilter);

        createEntity.addClickListener(click -> {
            addZaposleniToDatabase();
        });
        createEntity.setIcon(new Icon(VaadinIcon.PLUS));
        createEntity.setText("Dodaj novog zaposlenog");

        toolBar.add(nameFilter,createEntity);
        toolBar.getStyle().set("margin-left","15px");
        return toolBar;

    }

    private void refreshGrid(){
        zaposleniGrid.setItems(zaposleniFeignClient.findAllZaposleni());
    }

    private Dialog dialogUpdate(String text, Zaposleni zaposleni){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                zaposleniFeignClient.updateZaposleni(zaposleni);
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

    private Dialog dialogCreate(String text, Zaposleni zaposleni,String datum_od, String datum_do, String pozicija, Long pib){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                zaposleniFeignClient.addZaposleni(datum_od,datum_do,pozicija,pib,zaposleni);
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Promene uspesno sacuvane!");
                notification.setDuration(3000);
                notification.open();

                refreshGrid();
                dialog.close();
                UI.getCurrent().getPage().reload();
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

    private Dialog dialogDelete(String text, Zaposleni zaposleni){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                zaposleniFeignClient.deleteZaposleni(zaposleni);
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Uspesno obrisan zaposleni!");
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

        sacuvajButton.addClickListener(click -> {
            Zaposleni zaposleniSave = new Zaposleni();
            zaposleniSave.setId(zaposleniUpdate.getId());
            zaposleniSave.setIme(imeZaposleni.getValue());
            zaposleniSave.setPrezime(prezimeZaposleni.getValue());
            zaposleniSave.setDatum_rodjenja(datumRodjenjaZaposleni.getValue());
            zaposleniSave.setPol(polZaposleni.getValue());
            zaposleniSave.setJmbg(Long.valueOf(jmbgZaposleni.getValue()));
            zaposleniSave.setKartica(Long.valueOf(karticaZaposleni.getValue()));

            Dialog dialog = dialogUpdate(upozorenjeUpdate.getText(), zaposleniSave);
            dialog.open();

        });

        sacuvajButton.getStyle().set("margin-right","10px");

        obrisiButton.addClickListener(click -> {
            Zaposleni zaposleniDel= new Zaposleni();
            zaposleniDel.setId(zaposleniDelete.getId());

            Dialog dialog = dialogDelete(upozorenjeDelete.getText(), zaposleniDel);
            dialog.open();
        });

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
        upozorenjeUpdate.setText("Da li ste sigurni da zelite da izmenite podatke?");
        upozorenjeDelete.setText("Da li ste sigurni da zelite da obrisete zaposlenog?");
    }

}
