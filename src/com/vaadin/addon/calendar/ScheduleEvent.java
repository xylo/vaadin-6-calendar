package com.vaadin.addon.calendar;

import java.util.Date;

/**
 * One event in schedule.<br/>
 * <li>whenFrom, whenTo and caption fields are mandatory. <li>In "allDay"
 * events, starting and ending clocktimes are omitted in UI and only dates are
 * shown.
 */
public interface ScheduleEvent {

    public Date getWhenFrom();

    public void setWhenFrom(Date date);

    public Date getWhenTo();

    public void setWhenTo(Date date);

    public String getCaption();

    public void setCaption(String caption);

    public String getWhere();

    public void setWhere(String where);

    public String getDescription();

    public void setDescription(String description);

    public Object getData();

    public void setData(Object data);

    public String getStyleName();

    public void setStyleName(String styleName);
}
