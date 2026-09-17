/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.UserSessionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class UserSession implements Cloneable, Serializable {

	public static UserSession toDTO(String json) {
		return UserSessionSerDes.toDTO(json);
	}

	public Boolean getBecameKnown() {
		return becameKnown;
	}

	public void setBecameKnown(Boolean becameKnown) {
		this.becameKnown = becameKnown;
	}

	public void setBecameKnown(
		UnsafeSupplier<Boolean, Exception> becameKnownUnsafeSupplier) {

		try {
			becameKnown = becameKnownUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean becameKnown;

	public String getBrowserName() {
		return browserName;
	}

	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	public void setBrowserName(
		UnsafeSupplier<String, Exception> browserNameUnsafeSupplier) {

		try {
			browserName = browserNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String browserName;

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public void setCompleteDate(
		UnsafeSupplier<Date, Exception> completeDateUnsafeSupplier) {

		try {
			completeDate = completeDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date completeDate;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		try {
			createDate = createDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date createDate;

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public void setDeviceType(
		UnsafeSupplier<String, Exception> deviceTypeUnsafeSupplier) {

		try {
			deviceType = deviceTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String deviceType;

	public Event[] getEvents() {
		return events;
	}

	public void setEvents(Event[] events) {
		this.events = events;
	}

	public void setEvents(
		UnsafeSupplier<Event[], Exception> eventsUnsafeSupplier) {

		try {
			events = eventsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Event[] events;

	@Override
	public UserSession clone() throws CloneNotSupportedException {
		return (UserSession)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserSession)) {
			return false;
		}

		UserSession userSession = (UserSession)object;

		return Objects.equals(toString(), userSession.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UserSessionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-111861528