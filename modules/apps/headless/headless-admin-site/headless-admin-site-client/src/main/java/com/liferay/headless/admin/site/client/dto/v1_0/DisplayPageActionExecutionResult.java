/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.DisplayPageActionExecutionResultSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class DisplayPageActionExecutionResult
	implements Cloneable, Serializable {

	public static DisplayPageActionExecutionResult toDTO(String json) {
		return DisplayPageActionExecutionResultSerDes.toDTO(json);
	}

	public Mapping getMapping() {
		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public void setMapping(
		UnsafeSupplier<Mapping, Exception> mappingUnsafeSupplier) {

		try {
			mapping = mappingUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Mapping mapping;

	@Override
	public DisplayPageActionExecutionResult clone()
		throws CloneNotSupportedException {

		return (DisplayPageActionExecutionResult)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DisplayPageActionExecutionResult)) {
			return false;
		}

		DisplayPageActionExecutionResult displayPageActionExecutionResult =
			(DisplayPageActionExecutionResult)object;

		return Objects.equals(
			toString(), displayPageActionExecutionResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DisplayPageActionExecutionResultSerDes.toJSON(this);
	}

}