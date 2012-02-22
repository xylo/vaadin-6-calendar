package com.vaadin.addon.calendar.test;

import java.util.Arrays;
import java.util.Date;

import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.ContainerEventProvider;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class BeanItemContainerTestApp extends VerticalSplitPanel {

    private Calendar calendar;

    private Table table;

    private BeanItemContainer<BasicEvent> events = new BeanItemContainer<BasicEvent>(
            BasicEvent.class);

    private void test() {

        BeanItemContainer<BasicEvent> events = new BeanItemContainer<BasicEvent>(
                BasicEvent.class);
        ContainerEventProvider eventProvider = new ContainerEventProvider(
                events);
        Calendar calendar = new Calendar();
        calendar.setEventProvider(eventProvider);

    }

    public BeanItemContainerTestApp() {
        setSizeFull();

        // Create Calendar
        calendar = new Calendar();
        calendar.setImmediate(true);
        calendar.setSizeFull();
        calendar.setContainerDataSource(events);
        calendar.setStartDate(new Date(100, 1, 1));
        calendar.setEndDate(new Date(100, 2, 1));


        addComponent(calendar);

        // Add event table connected to same data source
        table = createTable();
        table.setContainerDataSource(events);
        table.setVisibleColumns(new Object[] { "caption", "description",
                "start", "end" });
        addComponent(table);
    }

    /**
     * Creates a table with some actions
     * 
     * @return
     */
    private Table createTable() {
        Table table = new Table();
        table.setSizeFull();
        table.addActionHandler(new Action.Handler() {

            private final Action ADD = new Action("Add event");
            private final Action EDIT = new Action("Edit event");
            private final Action REMOVE = new Action("Remove event");

            public void handleAction(Action action, Object sender, Object target) {
                if (action == ADD) {
                    BasicEvent event = new BasicEvent();
                    event.setStart(new Date(100, 1, 1));
                    event.setEnd(new Date(100, 1, 1));
                    editEvent(event);
                } else if (action == EDIT) {
                    editEvent((BasicEvent) target);
                } else if (action == REMOVE) {
                    events.removeItem(target);
                }
            }

            public Action[] getActions(Object target, Object sender) {
                if (target == null) {
                    return new Action[] { ADD };
                } else {
                    return new Action[] { EDIT, REMOVE };
                }
            }
        });
        return table;
    }

    /**
     * Opens up a modal dialog window where an event can be modified
     * 
     * @param event
     *            The event to modify
     */
    private void editEvent(final BasicEvent event) {
        Window modal = new Window("Add event");
        modal.setModal(true);
        modal.setResizable(false);
        modal.setDraggable(false);
        modal.setWidth("300px");
        final Form form = new Form();
        form.setFormFieldFactory(new FormFieldFactory() {
            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                if (ContainerEventProvider.STARTDATE_PROPERTY
                        .equals(propertyId)
                        || ContainerEventProvider.ENDDATE_PROPERTY
                        .equals(propertyId)) {
                    Class<?> type = item.getItemProperty(propertyId).getType();
                    DateField field = (DateField) DefaultFieldFactory
                            .createFieldByPropertyType(type);
                    field.setResolution(DateField.RESOLUTION_MIN);
                    return field;
                } else {
                    return DefaultFieldFactory.get().createField(item,
                            propertyId, uiContext);
                }
            }
        });

        form.setItemDataSource(new BeanItem<BasicEvent>(event, Arrays.asList(
                ContainerEventProvider.CAPTION_PROPERTY,
                ContainerEventProvider.DESCRIPTION_PROPERTY,
                ContainerEventProvider.STARTDATE_PROPERTY,
                ContainerEventProvider.ENDDATE_PROPERTY)));
        modal.addComponent(form);
        modal.addListener(new Window.CloseListener() {
            public void windowClose(CloseEvent e) {
                if (events.containsId(event)) {

                    // Commit changes to bean
                    form.commit();

                    /*
                     * BeanItemContainer does not notify container listeners
                     * when the bean changes so we need to trigger a
                     * ItemSetChange event
                     */
                    BasicEvent dummy = new BasicEvent();
                    events.addBean(dummy);
                    events.removeItem(dummy);

                } else {
                    events.addBean(event);
                }
            }
        });
        getWindow().addWindow(modal);
    }
}
