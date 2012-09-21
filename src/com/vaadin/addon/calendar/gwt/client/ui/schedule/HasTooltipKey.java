package com.vaadin.addon.calendar.gwt.client.ui.schedule;

/**
 * For Calendar client-side internal use only.
 * 
 * @author Johannes
 * 
 */
public interface HasTooltipKey {
    /**
     * Gets the key associated for the Widget implementing this interface. This
     * key is used for getting a tooltip title identified by the key
     * 
     * @return the tooltip key
     */
    Object getTooltipKey();
}
