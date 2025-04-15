/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.NotificationActionExecutionResultSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class NotificationActionExecutionResult
	implements Cloneable, Serializable {

	public static NotificationActionExecutionResult toDTO(String json) {
		return NotificationActionExecutionResultSerDes.toDTO(json);
	}

	public Boolean getReload() {
		return reload;
	}

	public void setReload(Boolean reload) {
		this.reload = reload;
	}

	public void setReload(
		UnsafeSupplier<Boolean, Exception> reloadUnsafeSupplier) {

		try {
			reload = reloadUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean reload;

	public FragmentInlineValue getText() {
		return text;
	}

	public void setText(FragmentInlineValue text) {
		this.text = text;
	}

	public void setText(
		UnsafeSupplier<FragmentInlineValue, Exception> textUnsafeSupplier) {

		try {
			text = textUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentInlineValue text;

	@Override
	public NotificationActionExecutionResult clone()
		throws CloneNotSupportedException {

		return (NotificationActionExecutionResult)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NotificationActionExecutionResult)) {
			return false;
		}

		NotificationActionExecutionResult notificationActionExecutionResult =
			(NotificationActionExecutionResult)object;

		return Objects.equals(
			toString(), notificationActionExecutionResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return NotificationActionExecutionResultSerDes.toJSON(this);
	}

}