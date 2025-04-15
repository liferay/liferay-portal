/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentFieldActionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FragmentFieldAction implements Cloneable, Serializable {

	public static FragmentFieldAction toDTO(String json) {
		return FragmentFieldActionSerDes.toDTO(json);
	}

	public Object getAction() {
		return action;
	}

	public void setAction(Object action) {
		this.action = action;
	}

	public void setAction(
		UnsafeSupplier<Object, Exception> actionUnsafeSupplier) {

		try {
			action = actionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object action;

	public ActionExecutionResult getOnError() {
		return onError;
	}

	public void setOnError(ActionExecutionResult onError) {
		this.onError = onError;
	}

	public void setOnError(
		UnsafeSupplier<ActionExecutionResult, Exception>
			onErrorUnsafeSupplier) {

		try {
			onError = onErrorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ActionExecutionResult onError;

	public ActionExecutionResult getOnSuccess() {
		return onSuccess;
	}

	public void setOnSuccess(ActionExecutionResult onSuccess) {
		this.onSuccess = onSuccess;
	}

	public void setOnSuccess(
		UnsafeSupplier<ActionExecutionResult, Exception>
			onSuccessUnsafeSupplier) {

		try {
			onSuccess = onSuccessUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ActionExecutionResult onSuccess;

	public Object getText() {
		return text;
	}

	public void setText(Object text) {
		this.text = text;
	}

	public void setText(UnsafeSupplier<Object, Exception> textUnsafeSupplier) {
		try {
			text = textUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object text;

	@Override
	public FragmentFieldAction clone() throws CloneNotSupportedException {
		return (FragmentFieldAction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentFieldAction)) {
			return false;
		}

		FragmentFieldAction fragmentFieldAction = (FragmentFieldAction)object;

		return Objects.equals(toString(), fragmentFieldAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentFieldActionSerDes.toJSON(this);
	}

}