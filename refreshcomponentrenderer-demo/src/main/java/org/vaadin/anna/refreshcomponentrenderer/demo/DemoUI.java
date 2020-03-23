package org.vaadin.anna.refreshcomponentrenderer.demo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.vaadin.anna.refreshcomponentrenderer.RefreshComponentRenderer;

import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.Slider;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Title("RefreshComponentRenderer Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        List<Pojo> content = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            content.add(new Pojo(i % 3 == 0, i));
        }

        Grid<Pojo> grid = new Grid<>(DataProvider.ofCollection(content));
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setBodyRowHeight(70);
        grid.addComponentColumn(this::radioButtonResponse)
                .setCaption("Boolean");
        grid.addColumn(this::radioButtonResponse,
                new RefreshComponentRenderer()).setCaption("Boolean refreshed");
        grid.addComponentColumn(this::sliderResponse).setCaption("Integer");
        grid.addColumn(this::sliderResponse, new RefreshComponentRenderer())
                .setCaption("Integer refreshed");

        final VerticalLayout layout = new VerticalLayout(grid);
        layout.setSizeFull();
        setContent(layout);
    }

    private RadioButtonGroup<Boolean> radioButtonResponse(Pojo item) {
        RadioButtonGroup<Boolean> yesNoRadioButtonGroup = new RadioButtonGroup<>();
        yesNoRadioButtonGroup.setItems(Boolean.TRUE, Boolean.FALSE);
        yesNoRadioButtonGroup.setSelectedItem(item.isBoolean());
        return yesNoRadioButtonGroup;
    }

    private Slider sliderResponse(Pojo item) {
        Slider s = new Slider(0, 4);
        s.setValue((double) ((item.getInt() % 4) + 1));
        return s;
    }

    private class Pojo {
        boolean b;
        int i;

        public Pojo(boolean b, int i) {
            this.b = b;
            this.i = i;
        }

        public boolean isBoolean() {
            return b;
        }

        public int getInt() {
            return i;
        }
    }

}
