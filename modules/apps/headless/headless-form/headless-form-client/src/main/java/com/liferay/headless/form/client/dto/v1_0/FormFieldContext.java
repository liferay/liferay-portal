/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.dto.v1_0;

import com.liferay.headless.form.client.function.UnsafeSupplier;
import com.liferay.headless.form.client.serdes.v1_0.FormFieldContextSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FormFieldContext implements Cloneable, Serializable {

	public static FormFieldContext toDTO(String json) {
		return FormFieldContextSerDes.toDTO(json);
	}

	public Boolean getEvaluable() {
		return evaluable;
	}

	public void setEvaluable(Boolean evaluable) {
		this.evaluable = evaluable;
	}

	public void setEvaluable(
		UnsafeSupplier<Boolean, Exception> evaluableUnsafeSupplier) {

		try {
			evaluable = evaluableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean evaluable;

	public FormFieldOption[] getFormFieldOptions() {
		return formFieldOptions;
	}

	public void setFormFieldOptions(FormFieldOption[] formFieldOptions) {
		this.formFieldOptions = formFieldOptions;
	}

	public void setFormFieldOptions(
		UnsafeSupplier<FormFieldOption[], Exception>
			formFieldOptionsUnsafeSupplier) {

		try {
			formFieldOptions = formFieldOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FormFieldOption[] formFieldOptions;

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

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setReadOnly(
		UnsafeSupplier<Boolean, Exception> readOnlyUnsafeSupplier) {

		try {
			readOnly = readOnlyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean readOnly;

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		try {
			required = requiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean required;

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public void setValid(
		UnsafeSupplier<Boolean, Exception> validUnsafeSupplier) {

		try {
			valid = validUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean valid;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String value;

	public Boolean getValueChanged() {
		return valueChanged;
	}

	public void setValueChanged(Boolean valueChanged) {
		this.valueChanged = valueChanged;
	}

	public void setValueChanged(
		UnsafeSupplier<Boolean, Exception> valueChangedUnsafeSupplier) {

		try {
			valueChanged = valueChangedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean valueChanged;

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public void setVisible(
		UnsafeSupplier<Boolean, Exception> visibleUnsafeSupplier) {

		try {
			visible = visibleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean visible;

	@Override
	public FormFieldContext clone() throws CloneNotSupportedException {
		return (FormFieldContext)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormFieldContext)) {
			return false;
		}

		FormFieldContext formFieldContext = (FormFieldContext)object;

		return Objects.equals(toString(), formFieldContext.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FormFieldContextSerDes.toJSON(this);
	}

}