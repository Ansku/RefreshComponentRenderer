package org.vaadin.anna.refreshcomponentrenderer;

import org.vaadin.anna.refreshcomponentrenderer.client.RefreshComponentRendererState;

import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.Slider;
import com.vaadin.ui.renderers.ComponentRenderer;

/**
 * Extended {@link ComponentRenderer} for components such as
 * {@link RadioButtonGroup} and {@link Slider} that don't get rendered properly
 * after resizing or scrolling.
 *
 * @author Anna Koskinen / Vaadin Ltd.
 *
 */
public class RefreshComponentRenderer extends ComponentRenderer {

    @Override
    protected RefreshComponentRendererState getState() {
        return (RefreshComponentRendererState) super.getState();
    }
}
