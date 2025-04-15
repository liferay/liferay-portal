/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.EmptyCollectionConfigSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class EmptyCollectionConfig implements Cloneable, Serializable {

	public static EmptyCollectionConfig toDTO(String json) {
		return EmptyCollectionConfigSerDes.toDTO(json);
	}

	public Boolean getDisplayMessage() {
		return displayMessage;
	}

	public void setDisplayMessage(Boolean displayMessage) {
		this.displayMessage = displayMessage;
	}

	public void setDisplayMessage(
		UnsafeSupplier<Boolean, Exception> displayMessageUnsafeSupplier) {

		try {
			displayMessage = displayMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean displayMessage;

	public Map<String, String> getMessage_i18n() {
		return message_i18n;
	}

	public void setMessage_i18n(Map<String, String> message_i18n) {
		this.message_i18n = message_i18n;
	}

	public void setMessage_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			message_i18nUnsafeSupplier) {

		try {
			message_i18n = message_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> message_i18n;

	@Override
	public EmptyCollectionConfig clone() throws CloneNotSupportedException {
		return (EmptyCollectionConfig)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EmptyCollectionConfig)) {
			return false;
		}

		EmptyCollectionConfig emptyCollectionConfig =
			(EmptyCollectionConfig)object;

		return Objects.equals(toString(), emptyCollectionConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return EmptyCollectionConfigSerDes.toJSON(this);
	}

}