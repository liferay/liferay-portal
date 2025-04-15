/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.dto.v1_0;

import com.liferay.headless.form.client.function.UnsafeSupplier;
import com.liferay.headless.form.client.serdes.v1_0.FormPageSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FormPage implements Cloneable, Serializable {

	public static FormPage toDTO(String json) {
		return FormPageSerDes.toDTO(json);
	}

	public FormField[] getFormFields() {
		return formFields;
	}

	public void setFormFields(FormField[] formFields) {
		this.formFields = formFields;
	}

	public void setFormFields(
		UnsafeSupplier<FormField[], Exception> formFieldsUnsafeSupplier) {

		try {
			formFields = formFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FormField[] formFields;

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public void setHeadline(
		UnsafeSupplier<String, Exception> headlineUnsafeSupplier) {

		try {
			headline = headlineUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String headline;

	public Map<String, String> getHeadline_i18n() {
		return headline_i18n;
	}

	public void setHeadline_i18n(Map<String, String> headline_i18n) {
		this.headline_i18n = headline_i18n;
	}

	public void setHeadline_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			headline_i18nUnsafeSupplier) {

		try {
			headline_i18n = headline_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> headline_i18n;

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setText(UnsafeSupplier<String, Exception> textUnsafeSupplier) {
		try {
			text = textUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String text;

	public Map<String, String> getText_i18n() {
		return text_i18n;
	}

	public void setText_i18n(Map<String, String> text_i18n) {
		this.text_i18n = text_i18n;
	}

	public void setText_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			text_i18nUnsafeSupplier) {

		try {
			text_i18n = text_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> text_i18n;

	@Override
	public FormPage clone() throws CloneNotSupportedException {
		return (FormPage)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormPage)) {
			return false;
		}

		FormPage formPage = (FormPage)object;

		return Objects.equals(toString(), formPage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FormPageSerDes.toJSON(this);
	}

}