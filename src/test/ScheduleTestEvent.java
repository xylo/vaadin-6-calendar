package test;

import java.util.Date;

import com.vaadin.addon.calendar.ScheduleEvent;

/** Test ScheduleEvent implementation. */
public class ScheduleTestEvent implements ScheduleEvent {

    private Date whenFrom;
    private Date whenTo;
    private String caption;
    private String where;
    private String description;
    private Object data;
    private String styleName;

    public ScheduleTestEvent(String caption, Date whenFrom, Date whenTo) {
        this.caption = caption;
        this.whenFrom = whenFrom;
        this.whenTo = whenTo;
    }

    public Date getWhenFrom() {
        return whenFrom;
    }

    public void setWhenFrom(Date whenFrom) {
        this.whenFrom = whenFrom;
    }

    public Date getWhenTo() {
        return whenTo;
    }

    public void setWhenTo(Date whenTo) {
        this.whenTo = whenTo;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String what) {
        caption = what;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getStyleName() {
        return styleName;
    }
}
