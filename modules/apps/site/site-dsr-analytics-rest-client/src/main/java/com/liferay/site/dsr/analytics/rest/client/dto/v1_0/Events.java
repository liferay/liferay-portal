/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.dto.v1_0;

import com.liferay.site.dsr.analytics.rest.client.function.UnsafeSupplier;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.EventsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class Events implements Cloneable, Serializable {

	public static Events toDTO(String json) {
		return EventsSerDes.toDTO(json);
	}

	public EventEntry[] getEventEntries() {
		return eventEntries;
	}

	public void setEventEntries(EventEntry[] eventEntries) {
		this.eventEntries = eventEntries;
	}

	public void setEventEntries(
		UnsafeSupplier<EventEntry[], Exception> eventEntriesUnsafeSupplier) {

		try {
			eventEntries = eventEntriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected EventEntry[] eventEntries;

	@Override
	public Events clone() throws CloneNotSupportedException {
		return (Events)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Events)) {
			return false;
		}

		Events events = (Events)object;

		return Objects.equals(toString(), events.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return EventsSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:85567716