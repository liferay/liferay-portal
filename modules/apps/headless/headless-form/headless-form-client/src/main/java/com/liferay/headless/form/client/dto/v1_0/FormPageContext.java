/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.dto.v1_0;

import com.liferay.headless.form.client.function.UnsafeSupplier;
import com.liferay.headless.form.client.serdes.v1_0.FormPageContextSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FormPageContext implements Cloneable, Serializable {

	public static FormPageContext toDTO(String json) {
		return FormPageContextSerDes.toDTO(json);
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnabled(
		UnsafeSupplier<Boolean, Exception> enabledUnsafeSupplier) {

		try {
			enabled = enabledUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enabled;

	public FormFieldContext[] getFormFieldContexts() {
		return formFieldContexts;
	}

	public void setFormFieldContexts(FormFieldContext[] formFieldContexts) {
		this.formFieldContexts = formFieldContexts;
	}

	public void setFormFieldContexts(
		UnsafeSupplier<FormFieldContext[], Exception>
			formFieldContextsUnsafeSupplier) {

		try {
			formFieldContexts = formFieldContextsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FormFieldContext[] formFieldContexts;

	public Boolean getShowRequiredFieldsWarning() {
		return showRequiredFieldsWarning;
	}

	public void setShowRequiredFieldsWarning(
		Boolean showRequiredFieldsWarning) {

		this.showRequiredFieldsWarning = showRequiredFieldsWarning;
	}

	public void setShowRequiredFieldsWarning(
		UnsafeSupplier<Boolean, Exception>
			showRequiredFieldsWarningUnsafeSupplier) {

		try {
			showRequiredFieldsWarning =
				showRequiredFieldsWarningUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean showRequiredFieldsWarning;

	@Override
	public FormPageContext clone() throws CloneNotSupportedException {
		return (FormPageContext)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormPageContext)) {
			return false;
		}

		FormPageContext formPageContext = (FormPageContext)object;

		return Objects.equals(toString(), formPageContext.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FormPageContextSerDes.toJSON(this);
	}

}