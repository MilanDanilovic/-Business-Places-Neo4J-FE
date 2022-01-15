package com.kaktus.application.views.pages;

import com.kaktus.application.data.model.*;
import com.kaktus.application.feign_client.KancelarijaFeignClient;
import com.kaktus.application.feign_client.PoslovniProstorFeignClient;
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
@Route(value="kancelarija", layout = MainLayout.class)
@PageTitle("Kancelarija")
public class KancelarijaView extends VerticalLayout {

    TextField brojKancelarija = new TextField();
    TextField kvadraturaKancelarija = new TextField();
    TextField brojRadnikaKancelarija = new TextField();
    ComboBox<Boolean> statusKancelarija = new ComboBox<>("Status kancelarija");//statusKancelarija

    Label upozorenjeUpdate = new Label();
    Label upozorenjeDelete = new Label();
    com.vaadin.flow.component.textfield.TextField nameFilter;
    Button createEntity = new Button();

    private final PaginatedGrid<Kancelarija> kancelarijaGrid =new PaginatedGrid<>();
    private Kancelarija kancelarijaUpdate = new Kancelarija();
    private Kancelarija kancelarijaDelete = new Kancelarija();


    private final KancelarijaFeignClient kancelarijaFeignClient;
    private final PoslovniProstorFeignClient poslovniProstorFeignClient;

    public KancelarijaView(KancelarijaFeignClient kancelarijaFeignClient, PoslovniProstorFeignClient poslovniProstorFeignClient) {
        this.kancelarijaFeignClient = kancelarijaFeignClient;
        this.poslovniProstorFeignClient = poslovniProstorFeignClient;
    }

    @PostConstruct
    public void init(){
        setLabels();
        configureGrid();
    }

    private void configureGrid(){

        kancelarijaGrid.addColumn(Kancelarija::getBroj_kancelarije).setHeader("Broj kancelarije").setSortable(true);
        kancelarijaGrid.addColumn(Kancelarija::getKvadratura).setHeader("Kvadratura").setSortable(true);
        kancelarijaGrid.addColumn(Kancelarija::getBroj_radnika).setHeader("Broj radnika").setSortable(true);
        kancelarijaGrid.addColumn(Kancelarija::getStatus).setHeader("Status kancelarije").setSortable(true);

        kancelarijaGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        kancelarijaGrid.setWidthFull();
        kancelarijaGrid.setItems(kancelarijaFeignClient.findAllKancelarija());
        kancelarijaGrid.setPageSize(10);
        kancelarijaGrid.setPaginatorSize(3);
        kancelarijaGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        kancelarijaGrid.setPaginatorTexts("Strana", "od");

        Div sideBar = configureSideBar();

        kancelarijaGrid.addSelectionListener(click -> {
            sideBar.removeClassName("hidden");
            if(click.getFirstSelectedItem().isPresent()){
                Kancelarija kancelarijaPostojeci = click.getFirstSelectedItem().get();

                kancelarijaUpdate.setId(kancelarijaPostojeci.getId());
                kancelarijaDelete.setId(kancelarijaPostojeci.getId());

                if(kancelarijaPostojeci.getBroj_kancelarije() != null) {
                    brojKancelarija.setValue(String.valueOf(kancelarijaPostojeci.getBroj_kancelarije()));
                }
                if(kancelarijaPostojeci.getKvadratura() != null){
                    kvadraturaKancelarija.setValue(String.valueOf(kancelarijaPostojeci.getKvadratura()));
                }
                if(kancelarijaPostojeci.getBroj_radnika() != null){
                    brojRadnikaKancelarija.setValue(String.valueOf(kancelarijaPostojeci.getBroj_radnika()));
                }
                if(kancelarijaPostojeci.getStatus() != null) {
                    statusKancelarija.setValue(kancelarijaPostojeci.getStatus());
                }
            }
            else {
                sideBar.addClassName("hidden");
            }
        });

        HorizontalLayout gridWithSideBar = new HorizontalLayout();
        VerticalLayout zaposleniGridWrapLayout = new VerticalLayout();

        zaposleniGridWrapLayout.add(kancelarijaGrid);
        sideBar.addClassName("hidden");
        gridWithSideBar.add(zaposleniGridWrapLayout,sideBar);
        gridWithSideBar.setSizeFull();
        gridWithSideBar.setFlexGrow(5);

        add(createToolsTab());
        add(gridWithSideBar);
    }

    private void onFilter(HasValue.ValueChangeEvent<String> event) {
        ListDataProvider<Kancelarija> dataProvider = (ListDataProvider< Kancelarija>) kancelarijaGrid.getDataProvider();
        dataProvider.setFilter(Kancelarija::filterToString, s -> caseInsensitiveContains(s, event.getValue()));
    }

    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }

    private Dialog dialogCreate(String text,String adresa, Kancelarija kancelarija){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                kancelarijaFeignClient.addKancelarija(adresa,kancelarija);
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

    private void addKancelarijaToDatabase(){
        Dialog createKancelarijaDialog = new Dialog();
        createKancelarijaDialog.open();

        Button save = new Button("Sacuvaj");
        Button odustani = new Button("Odustani");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setIcon(VaadinIcon.CHECK.create());
        odustani.setIcon(VaadinIcon.CLOSE.create());


        TextField broj_kancelarija = new TextField("Broj kancelarije");
        TextField kvadratura = new TextField("Kvadratura");
        TextField broj_radnika = new TextField("Broj radnika");


        ComboBox<Boolean> status = new ComboBox<>("Status kancelarije");
        status.setItems(true, false);


        ComboBox<PoslovniProstor> poslovniProstori = new ComboBox<>("Poslovni prostor");
        poslovniProstori.setItems(poslovniProstorFeignClient.findAllPoslovniProstor());
        poslovniProstori.setItemLabelGenerator(PoslovniProstor::getAdresa);


        save.addClickListener(click->{
            Kancelarija kancelarija = new Kancelarija();

            kancelarija.setBroj_kancelarije(Long.parseLong(broj_kancelarija.getValue()));
            kancelarija.setKvadratura(Double.valueOf(kvadratura.getValue()));
            kancelarija.setBroj_radnika(Long.parseLong(broj_radnika.getValue()));
            kancelarija.setStatus(status.getValue());

            Dialog dialog = dialogCreate(upozorenjeUpdate.getText(),poslovniProstori.getValue().getAdresa(),kancelarija);
            dialog.open();

        });

        odustani.addClickListener(click->{
            createKancelarijaDialog.close();
            UI.getCurrent().getPage().reload();
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(broj_kancelarija, kvadratura, broj_radnika, status, poslovniProstori);

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

        createKancelarijaDialog.add(formLayout,formLayoutControls);
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
            addKancelarijaToDatabase();
        });
        createEntity.setIcon(new Icon(VaadinIcon.PLUS));
        createEntity.setText("Dodaj novu kancelariju");

        toolBar.add(nameFilter,createEntity);
        toolBar.getStyle().set("margin-left","15px");
        return toolBar;

    }

    private void refreshGrid(){
        kancelarijaGrid.setItems(kancelarijaFeignClient.findAllKancelarija());
    }

    private Dialog dialogUpdate(String text, Kancelarija kancelarija){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                kancelarijaFeignClient.updateKancelarija(kancelarija);
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

    private Dialog dialogDelete(String text, Kancelarija kancelarija){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                kancelarijaFeignClient.deleteKancelarija(kancelarija);
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setText("Uspesno obrisana kancelarija!");
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

        brojKancelarija.setReadOnly(false);
        kvadraturaKancelarija.setReadOnly(false);
        brojRadnikaKancelarija.setReadOnly(false);
        statusKancelarija.setReadOnly(false);

        statusKancelarija.setItems(true,false);

        FormLayout formLayoutSideBar = new FormLayout();
        formLayoutSideBar.add(brojKancelarija, kvadraturaKancelarija,
                brojRadnikaKancelarija, statusKancelarija
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
            Kancelarija kancelarijaSave = new Kancelarija();
            kancelarijaSave.setId(kancelarijaUpdate.getId());
            kancelarijaSave.setBroj_kancelarije(Long.valueOf(brojKancelarija.getValue()));
            kancelarijaSave.setKvadratura(Double.valueOf(kvadraturaKancelarija.getValue()));
            kancelarijaSave.setBroj_radnika(Long.valueOf(brojRadnikaKancelarija.getValue()));
            kancelarijaSave.setStatus(statusKancelarija.getValue());

            Dialog dialog = dialogUpdate(upozorenjeUpdate.getText(), kancelarijaSave);
            dialog.open();

        });

        sacuvajButton.getStyle().set("margin-right","10px");

        obrisiButton.addClickListener(click -> {
            Kancelarija kancelarijeDel= new Kancelarija();
            kancelarijeDel.setId(kancelarijaDelete.getId());

            Dialog dialog = dialogDelete(upozorenjeDelete.getText(), kancelarijeDel);
            dialog.open();
        });

        odustaniButton.addClickListener(click->{
            sideBarTmp.addClassName("hidden");
        });

        return sideBarTmp;
    }

    private void setLabels() {
        brojKancelarija.setLabel("Broj kancelarije");
        kvadraturaKancelarija.setLabel("Kvadratura");
        brojRadnikaKancelarija.setLabel("Broj radnika");
        statusKancelarija.setLabel("Status kancelarije");
        upozorenjeUpdate.setText("Da li ste sigurni da zelite da izmenite podatke?");
        upozorenjeDelete.setText("Da li ste sigurni da zelite da obrisete kancelariju?");
    }
}
