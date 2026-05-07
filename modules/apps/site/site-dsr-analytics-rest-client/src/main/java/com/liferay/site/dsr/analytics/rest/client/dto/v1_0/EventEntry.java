/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.dto.v1_0;

import com.liferay.site.dsr.analytics.rest.client.function.UnsafeSupplier;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.EventEntrySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class EventEntry implements Cloneable, Serializable {

	public static EventEntry toDTO(String json) {
		return EventEntrySerDes.toDTO(json);
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public void setCreateDate(
		UnsafeSupplier<String, Exception> createDateUnsafeSupplier) {

		try {
			createDate = createDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String createDate;

	public String getEmailAddressHashed() {
		return emailAddressHashed;
	}

	public void setEmailAddressHashed(String emailAddressHashed) {
		this.emailAddressHashed = emailAddressHashed;
	}

	public void setEmailAddressHashed(
		UnsafeSupplier<String, Exception> emailAddressHashedUnsafeSupplier) {

		try {
			emailAddressHashed = emailAddressHashedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String emailAddressHashed;

	public String getIndividualName() {
		return individualName;
	}

	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}

	public void setIndividualName(
		UnsafeSupplier<String, Exception> individualNameUnsafeSupplier) {

		try {
			individualName = individualNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String individualName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	@Override
	public EventEntry clone() throws CloneNotSupportedException {
		return (EventEntry)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EventEntry)) {
			return false;
		}

		EventEntry eventEntry = (EventEntry)object;

		return Objects.equals(toString(), eventEntry.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return EventEntrySerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-2100009530