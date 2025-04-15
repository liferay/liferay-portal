/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.ColumnViewportSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ColumnViewport implements Cloneable, Serializable {

	public static ColumnViewport toDTO(String json) {
		return ColumnViewportSerDes.toDTO(json);
	}

	public ColumnViewportDefinition getColumnViewportDefinition() {
		return columnViewportDefinition;
	}

	public void setColumnViewportDefinition(
		ColumnViewportDefinition columnViewportDefinition) {

		this.columnViewportDefinition = columnViewportDefinition;
	}

	public void setColumnViewportDefinition(
		UnsafeSupplier<ColumnViewportDefinition, Exception>
			columnViewportDefinitionUnsafeSupplier) {

		try {
			columnViewportDefinition =
				columnViewportDefinitionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ColumnViewportDefinition columnViewportDefinition;

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

	@Override
	public ColumnViewport clone() throws CloneNotSupportedException {
		return (ColumnViewport)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ColumnViewport)) {
			return false;
		}

		ColumnViewport columnViewport = (ColumnViewport)object;

		return Objects.equals(toString(), columnViewport.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ColumnViewportSerDes.toJSON(this);
	}

}