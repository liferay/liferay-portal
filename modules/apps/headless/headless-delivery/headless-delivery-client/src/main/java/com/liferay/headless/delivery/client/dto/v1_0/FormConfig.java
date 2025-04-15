/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.FormConfigSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FormConfig implements Cloneable, Serializable {

	public static FormConfig toDTO(String json) {
		return FormConfigSerDes.toDTO(json);
	}

	public Object getFormReference() {
		return formReference;
	}

	public void setFormReference(Object formReference) {
		this.formReference = formReference;
	}

	public void setFormReference(
		UnsafeSupplier<Object, Exception> formReferenceUnsafeSupplier) {

		try {
			formReference = formReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object formReference;

	public Object getFormSuccessSubmissionResult() {
		return formSuccessSubmissionResult;
	}

	public void setFormSuccessSubmissionResult(
		Object formSuccessSubmissionResult) {

		this.formSuccessSubmissionResult = formSuccessSubmissionResult;
	}

	public void setFormSuccessSubmissionResult(
		UnsafeSupplier<Object, Exception>
			formSuccessSubmissionResultUnsafeSupplier) {

		try {
			formSuccessSubmissionResult =
				formSuccessSubmissionResultUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object formSuccessSubmissionResult;

	public FormType getFormType() {
		return formType;
	}

	public String getFormTypeAsString() {
		if (formType == null) {
			return null;
		}

		return formType.toString();
	}

	public void setFormType(FormType formType) {
		this.formType = formType;
	}

	public void setFormType(
		UnsafeSupplier<FormType, Exception> formTypeUnsafeSupplier) {

		try {
			formType = formTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FormType formType;

	public LocalizationConfig getLocalizationConfig() {
		return localizationConfig;
	}

	public void setLocalizationConfig(LocalizationConfig localizationConfig) {
		this.localizationConfig = localizationConfig;
	}

	public void setLocalizationConfig(
		UnsafeSupplier<LocalizationConfig, Exception>
			localizationConfigUnsafeSupplier) {

		try {
			localizationConfig = localizationConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected LocalizationConfig localizationConfig;

	public Integer getNumberOfSteps() {
		return numberOfSteps;
	}

	public void setNumberOfSteps(Integer numberOfSteps) {
		this.numberOfSteps = numberOfSteps;
	}

	public void setNumberOfSteps(
		UnsafeSupplier<Integer, Exception> numberOfStepsUnsafeSupplier) {

		try {
			numberOfSteps = numberOfStepsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfSteps;

	@Override
	public FormConfig clone() throws CloneNotSupportedException {
		return (FormConfig)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormConfig)) {
			return false;
		}

		FormConfig formConfig = (FormConfig)object;

		return Objects.equals(toString(), formConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FormConfigSerDes.toJSON(this);
	}

	public static enum FormType {

		SIMPLE("simple"), MULTISTEP("multistep");

		public static FormType create(String value) {
			for (FormType formType : values()) {
				if (Objects.equals(formType.getValue(), value) ||
					Objects.equals(formType.name(), value)) {

					return formType;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private FormType(String value) {
			_value = value;
		}

		private final String _value;

	}

}