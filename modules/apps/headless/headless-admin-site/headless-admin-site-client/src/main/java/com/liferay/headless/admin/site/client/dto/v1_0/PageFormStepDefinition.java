/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageFormStepDefinitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class PageFormStepDefinition implements Cloneable, Serializable {

	public static PageFormStepDefinition toDTO(String json) {
		return PageFormStepDefinitionSerDes.toDTO(json);
	}

	public Object getFormStepConfig() {
		return formStepConfig;
	}

	public void setFormStepConfig(Object formStepConfig) {
		this.formStepConfig = formStepConfig;
	}

	public void setFormStepConfig(
		UnsafeSupplier<Object, Exception> formStepConfigUnsafeSupplier) {

		try {
			formStepConfig = formStepConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object formStepConfig;

	@Override
	public PageFormStepDefinition clone() throws CloneNotSupportedException {
		return (PageFormStepDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageFormStepDefinition)) {
			return false;
		}

		PageFormStepDefinition pageFormStepDefinition =
			(PageFormStepDefinition)object;

		return Objects.equals(toString(), pageFormStepDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageFormStepDefinitionSerDes.toJSON(this);
	}

}