/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.dto.v1_0;

import com.liferay.headless.form.client.function.UnsafeSupplier;
import com.liferay.headless.form.client.serdes.v1_0.ValidationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Validation implements Cloneable, Serializable {

	public static Validation toDTO(String json) {
		return ValidationSerDes.toDTO(json);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setErrorMessage(
		UnsafeSupplier<String, Exception> errorMessageUnsafeSupplier) {

		try {
			errorMessage = errorMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String errorMessage;

	public Map<String, String> getErrorMessage_i18n() {
		return errorMessage_i18n;
	}

	public void setErrorMessage_i18n(Map<String, String> errorMessage_i18n) {
		this.errorMessage_i18n = errorMessage_i18n;
	}

	public void setErrorMessage_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			errorMessage_i18nUnsafeSupplier) {

		try {
			errorMessage_i18n = errorMessage_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> errorMessage_i18n;

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setExpression(
		UnsafeSupplier<String, Exception> expressionUnsafeSupplier) {

		try {
			expression = expressionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String expression;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	@Override
	public Validation clone() throws CloneNotSupportedException {
		return (Validation)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Validation)) {
			return false;
		}

		Validation validation = (Validation)object;

		return Objects.equals(toString(), validation.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ValidationSerDes.toJSON(this);
	}

}