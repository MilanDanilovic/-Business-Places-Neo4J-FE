package com.kaktus.application.views.pages;

import com.kaktus.application.data.model.Firma;
import com.kaktus.application.data.model.PoslovniProstor;
import com.kaktus.application.data.model.Vlasnik;
import com.kaktus.application.data.model.Zaposleni;
import com.kaktus.application.feign_client.PoslovniProstorFeignClient;
import com.kaktus.application.feign_client.VlasnikFeignClient;
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
@Route(value="prostor", layout = MainLayout.class)
@PageTitle("Prostor")
public class PoslovniProstorView extends VerticalLayout {

    TextField kvadraturaProstor = new TextField();
    TextField adresaProstor = new TextField();

    Label upozorenjeUpdate = new Label();
    Label upozorenjeDelete = new Label();
    com.vaadin.flow.component.textfield.TextField nameFilter;
    Button createEntity = new Button();

    private final PaginatedGrid<PoslovniProstor> poslovniProstorGrid =new PaginatedGrid<>();
    private PoslovniProstor prostorUpdate = new PoslovniProstor();
    private PoslovniProstor prostorDelete = new PoslovniProstor();

    private final PoslovniProstorFeignClient poslovniProstorFeignClient;
    private final VlasnikFeignClient vlasnikFeignClient;

    public PoslovniProstorView(PoslovniProstorFeignClient poslovniProstorFeignClient, VlasnikFeignClient vlasnikFeignClient) {
        this.poslovniProstorFeignClient = poslovniProstorFeignClient;
        this.vlasnikFeignClient = vlasnikFeignClient;
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

        add(createToolsTab());
        add(gridWithSideBar);
    }

    private void onFilter(HasValue.ValueChangeEvent<String> event) {
        ListDataProvider<PoslovniProstor> dataProvider = (ListDataProvider<PoslovniProstor>) poslovniProstorGrid.getDataProvider();
        dataProvider.setFilter(PoslovniProstor::filterToString, s -> caseInsensitiveContains(s, event.getValue()));
    }

    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }

    private Dialog dialogCreate(String text,Long jmbg, String datum, PoslovniProstor poslovniProstor ){
        Dialog dialog = new Dialog();
        dialog.add(new Text(text));
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        Button potvrdiButton = new Button("Da", event -> {
            dialog.close();
            try {
                poslovniProstorFeignClient.addPoslovniProstor(jmbg,datum,poslovniProstor);
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

    private void addPoslovniProstorToDatabase(){
        Dialog createPoslovniProstorDialog = new Dialog();
        createPoslovniProstorDialog.open();

        Button save = new Button("Sacuvaj");
        Button odustani = new Button("Odustani");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setIcon(VaadinIcon.CHECK.create());
        odustani.setIcon(VaadinIcon.CLOSE.create());


        TextField kvadratura = new TextField("Kvadratura");
        TextField adresa = new TextField("Adresa");
        DatePicker datum_kupovine = new DatePicker("Datum kupovine");


        ComboBox<Vlasnik> vlasnik = new ComboBox<>("Vlasnik");
        vlasnik.setItems(vlasnikFeignClient.findAllVlasnik());
        vlasnik.setItemLabelGenerator(Vlasnik::getIme);

        save.addClickListener(click->{
            PoslovniProstor poslovniProstor = new PoslovniProstor();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY");

            poslovniProstor.setIdVlasnika(vlasnik.getValue().getId());
            poslovniProstor.setAdresa(adresa.getValue());
            poslovniProstor.setKvadratura(Double.valueOf(kvadratura.getValue()));

            Dialog dialog = dialogCreate(upozorenjeUpdate.getText(),vlasnik.getValue().getJmbg() ,formatter.format(datum_kupovine.getValue()),poslovniProstor);
            dialog.open();

        });

        odustani.addClickListener(click->{
            createPoslovniProstorDialog.close();
            UI.getCurrent().getPage().reload();
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(kvadratura, adresa, datum_kupovine, vlasnik);

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

        createPoslovniProstorDialog.add( formLayout,formLayoutControls);
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
            addPoslovniProstorToDatabase();
        });
        createEntity.setIcon(new Icon(VaadinIcon.PLUS));
        createEntity.setText("Dodaj novi poslovni prostor");

        toolBar.add(nameFilter,createEntity);
        toolBar.getStyle().set("margin-left","15px");
        return toolBar;

    }

    private void refreshGrid(){
        poslovniProstorGrid.setItems(poslovniProstorFeignClient.findAllPoslovniProstor());
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
