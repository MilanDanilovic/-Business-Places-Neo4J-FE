package com.kaktus.application.views.pages;

import com.kaktus.application.data.model.Firma;
import com.kaktus.application.data.model.Kancelarija;
import com.kaktus.application.data.model.PoslovniProstor;
import com.kaktus.application.data.model.Zaposleni;
import com.kaktus.application.feign_client.FirmaFeignClient;
import com.kaktus.application.feign_client.KancelarijaFeignClient;
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
@Route(value="firma", layout = MainLayout.class)
@PageTitle("Firma")
public class FirmaView extends VerticalLayout {

    TextField nazivFirma = new TextField();
    TextField godisnjaZaradaFirma = new TextField();
    TextField pibFirma = new TextField();
    TextField datumOsnivanjaFirma = new TextField();

    Label upozorenjeUpdate = new Label();
    Label upozorenjeDelete = new Label();
    com.vaadin.flow.component.textfield.TextField nameFilter;
    Button createEntity = new Button();

    private final PaginatedGrid<Firma> firmaGrid =new PaginatedGrid<>();
    private Firma firmaUpdate = new Firma();
    private Firma firmaDelete = new Firma();

    private final FirmaFeignClient firmaFeignClient;
    private final ZaposleniFeignClient zaposleniFeignClient;
    private final KancelarijaFeignClient kancelarijaFeignClient;

    public FirmaView(FirmaFeignClient firmaFeignClient
                     ,KancelarijaFeignClient kancelarijaFeignClient ,ZaposleniFeignClient zaposleniFeignClient) {
        this.firmaFeignClient = firmaFeignClient;
        this.zaposleniFeignClient = zaposleniFeignClient;
        this.kancelarijaFeignClient = kancelarijaFeignClient;
    }

    @PostConstruct
    public void init(){
        setLabels();
        configureGrid();
    }

    private void configureGrid(){

        firmaGrid.addColumn(Firma::getNaziv).setHeader("Naziv").setSortable(true);
        firmaGrid.addColumn(Firma::getGodisnja_zarada).setHeader("Godisnja zarada").setSortable(true);
        firmaGrid.addColumn(Firma::getPib).setHeader("Pib firme").setSortable(true);
        firmaGrid.addColumn(Firma::getDatum_osnivanja).setHeader("Datum osnivanja firme").setSortable(true);

        firmaGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        firmaGrid.setWidthFull();
        firmaGrid.setItems(firmaFeignClient.findAllFirma());
        firmaGrid.setPageSize(10);
        firmaGrid.setPaginatorSize(3);
        firmaGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        firmaGrid.setPaginatorTexts("Strana", "od");

        Div sideBar = configureSideBar();

        firmaGrid.addSelectionListener(click -> {
            sideBar.removeClassName("hidden");
            if(click.getFirstSelectedItem().isPresent()){
                Firma firmaPostojeci = click.getFirstSelectedItem().get();

                firmaUpdate.setId(firmaPostojeci.getId());
                firmaDelete.setId(firmaPostojeci.getId());

                if(firmaPostojeci.getNaziv() != null) {
                    nazivFirma.setValue(firmaPostojeci.getNaziv());
                }
                if(firmaPostojeci.getGodisnja_zarada() != null){
                    godisnjaZaradaFirma.setValue(String.valueOf(firmaPostojeci.getGodisnja_zarada()));
                }
                if(firmaPostojeci.getPib() != null){
                    pibFirma.setValue(String.valueOf(firmaPostojeci.getPib()));
                }
                if(firmaPostojeci.getDatum_osnivanja() != null) {
                    datumOsnivanjaFirma.setValue(firmaPostojeci.getDatum_osnivanja());
                }
            }
            else {
                sideBar.addClassName("hidden");
            }
        });

        HorizontalLayout gridWithSideBar = new HorizontalLayout();
        VerticalLayout zaposleniGridWrapLayout = new VerticalLayout();

        zaposleniGridWrapLayout.add(firmaGrid);
        sideBar.addClassName("hidden");
        gridWithSideBar.add(zaposleniGridWrapLayout,sideBar);
        gridWithSideBar.setSizeFull();
        gridWithSideBar.setFlexGrow(5);

        add(createToolsTab());
        add(gridWithSideBar);
    }

    private void onFilter(HasValue.ValueChangeEvent<String> event) {
        ListDataProvider<Firma> dataProvider = (ListDataProvider<Firma>) firmaGrid.getDataProvider();
        dataProvider.setFilter(Firma::filterToString, s -> caseInsensitiveContains(s, event.getValue()));
    }

    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }
    private Dialog dialogCreate(String text, Firma firma,String datum_od, String datum_do, String broj_kancelarije){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                firmaFeignClient.addFirma(datum_od,datum_do,Long.parseLong(broj_kancelarije),firma);
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

    private void addFirmaToDatabase(){
        Dialog createFirmaDialog = new Dialog();
        createFirmaDialog.open();

        Button save = new Button("Sacuvaj");
        Button odustani = new Button("Odustani");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setIcon(VaadinIcon.CHECK.create());
        odustani.setIcon(VaadinIcon.CLOSE.create());


        TextField naziv = new TextField("Naziv");
        TextField godisnja_zarada = new TextField("Godisnja zarada");
        TextField pib = new TextField("PIB");
        DatePicker datum_osnivanja = new DatePicker("Datum osnivanja");

        DatePicker datum_od = new DatePicker("Datum od kada se iznajmljuje");
        DatePicker datum_do = new DatePicker("Datum do kada se iznajmljuje");



        ComboBox<Kancelarija> kancelarije = new ComboBox<>("Kancelarija");
        kancelarije.setItems(kancelarijaFeignClient.findAllKancelarija());
        kancelarije.setItemLabelGenerator(Kancelarija::brojKancelarije);


        save.addClickListener(click->{
            Firma firma = new Firma();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY");


            firma.setNaziv(naziv.getValue());
            firma.setGodisnja_zarada(Double.valueOf(godisnja_zarada.getValue()));
            firma.setPib(Long.parseLong(pib.getValue()));
            firma.setDatum_osnivanja(formatter.format(datum_osnivanja.getValue()));
            firma.setIdKancelarije(kancelarije.getValue().getId());

            Dialog dialog = dialogCreate(upozorenjeUpdate.getText(),firma,formatter.format(datum_od.getValue()),formatter.format(datum_do.getValue()), kancelarije.getValue().brojKancelarije());
            dialog.open();

        });

        odustani.addClickListener(click->{
            createFirmaDialog.close();
            UI.getCurrent().getPage().reload();
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(naziv, godisnja_zarada, pib, datum_osnivanja, kancelarije, datum_od, datum_do);

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

        createFirmaDialog.add(formLayout,formLayoutControls);
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
            addFirmaToDatabase();
        });
        createEntity.setIcon(new Icon(VaadinIcon.PLUS));
        createEntity.setText("Dodaj novu firmu");

        toolBar.add(nameFilter,createEntity);
        toolBar.getStyle().set("margin-left","15px");
        return toolBar;

    }

    private void refreshGrid(){
        firmaGrid.setItems(firmaFeignClient.findAllFirma());
    }

    private Dialog dialogUpdate(String text, Firma firma){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                firmaFeignClient.updateFirma(firma);
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

    private Dialog dialogDelete(String text, Firma firma){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                firmaFeignClient.deleteFirma(firma);
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Uspesno obrisana firma!");
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

        nazivFirma.setReadOnly(false);
        godisnjaZaradaFirma.setReadOnly(false);
        pibFirma.setReadOnly(false);
        datumOsnivanjaFirma.setReadOnly(false);

        FormLayout formLayoutSideBar = new FormLayout();
        formLayoutSideBar.add(nazivFirma, godisnjaZaradaFirma,
                pibFirma, datumOsnivanjaFirma
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
            Firma firmaSave = new Firma();
            firmaSave.setId(firmaUpdate.getId());
            firmaSave.setNaziv(nazivFirma.getValue());
            firmaSave.setGodisnja_zarada(Double.valueOf(godisnjaZaradaFirma.getValue()));
            firmaSave.setPib(Long.valueOf(pibFirma.getValue()));
            firmaSave.setDatum_osnivanja(datumOsnivanjaFirma.getValue());

            Dialog dialog = dialogUpdate(upozorenjeUpdate.getText(), firmaSave);
            dialog.open();

        });

        obrisiButton.addClickListener(click -> {
            Firma firmaDel= new Firma();
            firmaDel.setId(firmaDelete.getId());

            Dialog dialog = dialogDelete(upozorenjeDelete.getText(), firmaDel);
            dialog.open();
        });

        odustaniButton.addClickListener(click->{
            sideBarTmp.addClassName("hidden");
        });

        return sideBarTmp;
    }

    private void setLabels() {
        nazivFirma.setLabel("Naziv");
        godisnjaZaradaFirma.setLabel("Godisnja zarada");
        pibFirma.setLabel("Pib firme");
        datumOsnivanjaFirma.setLabel("Datum osnivanja firme");
        upozorenjeUpdate.setText("Da li ste sigurni da zelite da izmenite podatke?");
        upozorenjeDelete.setText("Da li ste sigurni da zelite da obrisete firmu?");
    }
}
