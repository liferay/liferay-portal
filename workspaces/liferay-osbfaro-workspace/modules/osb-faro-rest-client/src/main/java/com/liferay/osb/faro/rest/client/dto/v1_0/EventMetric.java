/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.EventMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class EventMetric implements Cloneable, Serializable {

	public static EventMetric toDTO(String json) {
		return EventMetricSerDes.toDTO(json);
	}

	public Metric getTotalEvents() {
		return totalEvents;
	}

	public void setTotalEvents(Metric totalEvents) {
		this.totalEvents = totalEvents;
	}

	public void setTotalEvents(
		UnsafeSupplier<Metric, Exception> totalEventsUnsafeSupplier) {

		try {
			totalEvents = totalEventsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Metric totalEvents;

	public Metric getTotalSessions() {
		return totalSessions;
	}

	public void setTotalSessions(Metric totalSessions) {
		this.totalSessions = totalSessions;
	}

	public void setTotalSessions(
		UnsafeSupplier<Metric, Exception> totalSessionsUnsafeSupplier) {

		try {
			totalSessions = totalSessionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Metric totalSessions;

	@Override
	public EventMetric clone() throws CloneNotSupportedException {
		return (EventMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EventMetric)) {
			return false;
		}

		EventMetric eventMetric = (EventMetric)object;

		return Objects.equals(toString(), eventMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return EventMetricSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1384782448