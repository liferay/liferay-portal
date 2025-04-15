/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.dto.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.function.UnsafeSupplier;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.CalendarSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class Calendar implements Cloneable, Serializable {

	public static Calendar toDTO(String json) {
		return CalendarSerDes.toDTO(json);
	}

	public Boolean getDefaultCalendar() {
		return defaultCalendar;
	}

	public void setDefaultCalendar(Boolean defaultCalendar) {
		this.defaultCalendar = defaultCalendar;
	}

	public void setDefaultCalendar(
		UnsafeSupplier<Boolean, Exception> defaultCalendarUnsafeSupplier) {

		try {
			defaultCalendar = defaultCalendarUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean defaultCalendar;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String key;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	@Override
	public Calendar clone() throws CloneNotSupportedException {
		return (Calendar)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Calendar)) {
			return false;
		}

		Calendar calendar = (Calendar)object;

		return Objects.equals(toString(), calendar.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CalendarSerDes.toJSON(this);
	}

}