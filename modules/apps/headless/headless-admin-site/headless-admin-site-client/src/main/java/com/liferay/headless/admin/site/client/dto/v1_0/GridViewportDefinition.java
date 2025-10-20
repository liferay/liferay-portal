/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.GridViewportDefinitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class GridViewportDefinition implements Cloneable, Serializable {

	public static GridViewportDefinition toDTO(String json) {
		return GridViewportDefinitionSerDes.toDTO(json);
	}

	public Integer getModulesPerRow() {
		return modulesPerRow;
	}

	public void setModulesPerRow(Integer modulesPerRow) {
		this.modulesPerRow = modulesPerRow;
	}

	public void setModulesPerRow(
		UnsafeSupplier<Integer, Exception> modulesPerRowUnsafeSupplier) {

		try {
			modulesPerRow = modulesPerRowUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer modulesPerRow;

	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	public String getVerticalAlignmentAsString() {
		if (verticalAlignment == null) {
			return null;
		}

		return verticalAlignment.toString();
	}

	public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}

	public void setVerticalAlignment(
		UnsafeSupplier<VerticalAlignment, Exception>
			verticalAlignmentUnsafeSupplier) {

		try {
			verticalAlignment = verticalAlignmentUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected VerticalAlignment verticalAlignment;

	@Override
	public GridViewportDefinition clone() throws CloneNotSupportedException {
		return (GridViewportDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GridViewportDefinition)) {
			return false;
		}

		GridViewportDefinition gridViewportDefinition =
			(GridViewportDefinition)object;

		return Objects.equals(toString(), gridViewportDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return GridViewportDefinitionSerDes.toJSON(this);
	}

	public static enum VerticalAlignment {

		BOTTOM("Bottom"), MIDDLE("Middle"), TOP("Top");

		public static VerticalAlignment create(String value) {
			for (VerticalAlignment verticalAlignment : values()) {
				if (Objects.equals(verticalAlignment.getValue(), value) ||
					Objects.equals(verticalAlignment.name(), value)) {

					return verticalAlignment;
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

		private VerticalAlignment(String value) {
			_value = value;
		}

		private final String _value;

	}

}