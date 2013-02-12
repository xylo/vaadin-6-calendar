/**
 * Copyright 2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.addon.calendar.test;

/*
 * #%L
 * Vaadin Calendar
 * %%
 * Copyright (C) 2010 - 2013 Vaadin Ltd
 * %%
 * This program is available under GNU Affero General Public License (version
 * 3 or later at your option).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
