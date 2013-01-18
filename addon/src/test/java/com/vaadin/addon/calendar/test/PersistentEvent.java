package com.vaadin.addon.calendar.test;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vaadin.addon.calendar.event.CalendarEventEditor;

@Entity
public class PersistentEvent implements CalendarEventEditor {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventStart;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventEnd;

    @Column(length = 256)
    private String caption;

    @Column(length = 256)
    private String description;

    @Column(length = 256)
    private String styleName;

    @Column
    private boolean allDay;

    public Date getStart() {
        return eventStart;
    }

    public Date getEnd() {
        return eventEnd;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public String getStyleName() {
        return styleName;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnd(Date end) {
        this.eventEnd = end;
    }

    public void setStart(Date start) {
        this.eventStart = start;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public void setAllDay(boolean isAllDay) {
        this.allDay = isAllDay;
    }
}
