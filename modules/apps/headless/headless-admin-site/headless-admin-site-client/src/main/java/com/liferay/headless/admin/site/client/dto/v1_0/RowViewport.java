/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.RowViewportSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class RowViewport implements Cloneable, Serializable {

	public static RowViewport toDTO(String json) {
		return RowViewportSerDes.toDTO(json);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String id;

	public RowViewportDefinition getRowViewportDefinition() {
		return rowViewportDefinition;
	}

	public void setRowViewportDefinition(
		RowViewportDefinition rowViewportDefinition) {

		this.rowViewportDefinition = rowViewportDefinition;
	}

	public void setRowViewportDefinition(
		UnsafeSupplier<RowViewportDefinition, Exception>
			rowViewportDefinitionUnsafeSupplier) {

		try {
			rowViewportDefinition = rowViewportDefinitionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RowViewportDefinition rowViewportDefinition;

	@Override
	public RowViewport clone() throws CloneNotSupportedException {
		return (RowViewport)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RowViewport)) {
			return false;
		}

		RowViewport rowViewport = (RowViewport)object;

		return Objects.equals(toString(), rowViewport.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return RowViewportSerDes.toJSON(this);
	}

}