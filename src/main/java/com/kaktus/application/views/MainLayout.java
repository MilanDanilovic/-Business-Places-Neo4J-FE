package com.kaktus.application.views;

import com.kaktus.application.views.about.AboutView;
import com.kaktus.application.views.pregled.PregledView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import java.util.ArrayList;

/**
 * The main view is a top-level placeholder for other views.
 */
@PWA(name = "vaadin-neo4j", shortName = "vaadin-neo4j", enableInstallPrompt = false)
@Theme(themeFolder = "vaadin-neo4j", variant = Lumo.DARK)
@PageTitle("Main")
@CssImport("./themes/vaadin-neo4j/shared-styles.css")
public class MainLayout extends AppLayout {
    RouterLink vlasnikLink, firmaLink, poslovniProstorLink, kancelarijaLink, zaposleniLink;
    ArrayList<RouterLink> routerLinks;

    public MainLayout(){
        routerLinks = new ArrayList<>();

        createHeader();
        createDrawer();
    }
    private void createHeader() {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);


        Image logoKaktus = new Image("icons/icon.png","logo");
        logoKaktus.getStyle().set("width","60px");
        logoKaktus.getStyle().set("height","50px");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(),logoKaktus);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("header");

        Div div = new Div();
        div.getElement().getStyle().set("display","flex");
        div.getElement().getStyle().set("align-items","center");

        addToNavbar(header);
    }

    private void linkCreator(RouterLink routerLink,String text, Class c){
        routerLink = new RouterLink(text,c);
        routerLink.setHighlightCondition(HighlightConditions.sameLocation());
        routerLink.addClassName("layout-routes");
        routerLinks.add(routerLink);
    }

    private void createDrawer() {

        vlasnikLink = new RouterLink("Vlasnik",PregledView.class);
        vlasnikLink.setHighlightCondition(HighlightConditions.sameLocation());
        vlasnikLink.addClassName("layout-routes");
        routerLinks.add(vlasnikLink);

        firmaLink = new RouterLink("Firma", PregledView.class);
        firmaLink.setHighlightCondition(HighlightConditions.sameLocation());
        firmaLink.addClassName("layout-routes");
        routerLinks.add(firmaLink);

        poslovniProstorLink = new RouterLink("Poslovni Prostor", AboutView.class);
        poslovniProstorLink.setHighlightCondition(HighlightConditions.sameLocation());
        poslovniProstorLink.addClassName("layout-routes");
        routerLinks.add(poslovniProstorLink);

        kancelarijaLink = new RouterLink("Kancelarija", AboutView.class);
        kancelarijaLink.setHighlightCondition(HighlightConditions.sameLocation());
        kancelarijaLink.addClassName("layout-routes");
        routerLinks.add(kancelarijaLink);

        zaposleniLink = new RouterLink("Zaposleni", AboutView.class);
        zaposleniLink.setHighlightCondition(HighlightConditions.sameLocation());
        zaposleniLink.addClassName("layout-routes");
        routerLinks.add(zaposleniLink);

        routerLinks.forEach(el -> {
            el.addFocusListener(listener -> {
                routerLinks.forEach(ch->{ch.removeClassName("main-layout-item");});
                el.addClassName("main-layout-item");
            });
        });

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(true);

        verticalLayout.setWidth("max-content");
        verticalLayout.add(vlasnikLink, firmaLink, poslovniProstorLink, kancelarijaLink, zaposleniLink);

        verticalLayout.setWidth("max-content");
        verticalLayout.setSizeFull();
        verticalLayout.getStyle().set("overflow-y", "auto");
        verticalLayout.getStyle().set("overflow-x","hidden");

        addToDrawer(verticalLayout);
    }


}
