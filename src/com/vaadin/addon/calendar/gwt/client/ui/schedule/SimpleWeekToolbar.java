/**
 * 
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.addon.calendar.gwt.client.ui.VSchedule;

public class SimpleWeekToolbar extends FlexTable implements ClickHandler {
	private int height;
	private VSchedule schedule;

	public SimpleWeekToolbar(VSchedule parent) {
		this.schedule = parent;
		setCellSpacing(0);
		setCellPadding(0);
		setStyleName("v-schedule-weektoolbar");
	}

	public void addWeek(int week, int year) {
		WeekLabel l = new WeekLabel(week + "", week, year);
		l.addClickHandler(this);
		int rowCount = getRowCount();
		insertRow(rowCount);
		setWidget(rowCount, 0, l);
		updateCellHeights();
	}

	public void updateCellHeights() {
		int rowCount = getRowCount();
		if (rowCount == 0)
			return;
		int cellheight = (height / rowCount) - 1;
		for (int i = 0; i < rowCount; i++) {
			getCellFormatter().setHeight(i, 0, cellheight + "px");
		}
	}

	public void setHeightPX(int intHeight) {
		this.height = intHeight;
		updateCellHeights();
	}

	@Override
	protected void onLoad() {
		super.onLoad();
	}

	@Override
	protected void onUnload() {
		super.onUnload();
	}

	public void onClick(ClickEvent event) {
		WeekLabel wl = (WeekLabel) event.getSource();
		schedule.getClient().updateVariable(schedule.getPID(), "weekOpened", wl.getYear() + "w" + wl.getWeek(), true);
	}

	static class WeekLabel extends Label {
		private int week;

		public WeekLabel(String string, int week2, int year2) {
			super(string);
			this.week = week2;
			this.year = year2;
		}

		public int getWeek() {
			return week;
		}

		public void setWeek(int week) {
			this.week = week;
		}

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		private int year;
	}
}