package org.vaadin.anna.refreshcomponentrenderer.client;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.vaadin.anna.refreshcomponentrenderer.RefreshComponentRenderer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.connectors.grid.ComponentRendererConnector;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.renderers.WidgetRenderer;
import com.vaadin.client.ui.VRadioButtonGroup;
import com.vaadin.client.ui.VSlider;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.ListingJsonConstants;

import elemental.json.JsonObject;

/**
 * Connector for {@link RefreshComponentRenderer}.
 *
 * @author Anna Koskinen / Vaadin Ltd.
 *
 */
@Connect(RefreshComponentRenderer.class)
public class RefreshComponentRendererConnector
        extends ComponentRendererConnector {

    private HashSet<Widget> workaroundTriggered = new HashSet<>();

    @Override
    protected Renderer<String> createRenderer() {
        return new WidgetRenderer<String, SimplePanel>() {

            @Override
            public SimplePanel createWidget() {
                SimplePanel panel = GWT.create(SimplePanel.class);
                panel.setStyleName("component-wrap");
                return panel;
            }

            @Override
            public void render(RendererCellReference cell, String connectorId,
                    SimplePanel widget) {
                createConnectorHierarchyChangeHandler();
                Widget connectorWidget = null;
                if (connectorId != null) {
                    ComponentConnector connector = (ComponentConnector) ConnectorMap
                            .get(getConnection()).getConnector(connectorId);
                    if (connector != null) {
                        connectorWidget = connector.getWidget();
                        getKnownConnectors().add(connectorId);
                    }
                }
                if (connectorWidget != null) {
                    rendererWorkaround(connectorWidget);
                    widget.setWidget(connectorWidget);
                } else if (widget.getWidget() != null) {
                    widget.remove(widget.getWidget());
                    getKnownConnectors().remove(connectorId);
                }
            }
        };
    }

    private native void createConnectorHierarchyChangeHandler()
    /*-{
        this.@com.vaadin.client.connectors.grid.ComponentRendererConnector::createConnectorHierarchyChangeHandler()();
    }-*/;

    private native HashSet<String> getKnownConnectors()
    /*-{
        return this.@com.vaadin.client.connectors.grid.ComponentRendererConnector::knownConnectors;
    }-*/;

    private void rendererWorkaround(Widget connectorWidget) {
        if (workaroundTriggered.contains(connectorWidget)) {
            return;
        }
        workaroundTriggered.add(connectorWidget);
        Scheduler.get().scheduleFinally(() -> {
            if (connectorWidget instanceof VRadioButtonGroup) {
                for (Entry<RadioButton, JsonObject> entry : getOptionsToItems(
                        (VRadioButtonGroup) connectorWidget).entrySet()) {
                    refreshSelection((VRadioButtonGroup) connectorWidget,
                            entry.getKey(), entry.getValue().getBoolean(
                                    ListingJsonConstants.JSONKEY_ITEM_SELECTED));
                }
            } else if (connectorWidget instanceof VSlider) {
                ((VSlider) connectorWidget).setValue(
                        ((VSlider) connectorWidget).getValue(), false);
            }
            workaroundTriggered.remove(connectorWidget);
        });
    }

    private native Map<RadioButton, JsonObject> getOptionsToItems(
            VRadioButtonGroup group)
    /*-{
        return group.@com.vaadin.client.ui.VRadioButtonGroup::optionsToItems;
    }-*/;

    private native void refreshSelection(VRadioButtonGroup group,
            RadioButton button, Boolean value)
    /*-{
        group.@com.vaadin.client.ui.VRadioButtonGroup::updateItemSelection(*)(button, value);
    }-*/;

    @Override
    public void onUnregister() {
        workaroundTriggered.clear();
        super.onUnregister();
    }

    @Override
    public RefreshComponentRendererState getState() {
        return (RefreshComponentRendererState) super.getState();
    }
}
