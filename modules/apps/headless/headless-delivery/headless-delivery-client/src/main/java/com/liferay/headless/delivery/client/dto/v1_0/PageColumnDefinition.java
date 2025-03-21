/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.PageColumnDefinitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageColumnDefinition implements Cloneable, Serializable {

	public static PageColumnDefinition toDTO(String json) {
		return PageColumnDefinitionSerDes.toDTO(json);
	}

	public ColumnViewportConfig getColumnViewportConfig() {
		return columnViewportConfig;
	}

	public void setColumnViewportConfig(
		ColumnViewportConfig columnViewportConfig) {

		this.columnViewportConfig = columnViewportConfig;
	}

	public void setColumnViewportConfig(
		UnsafeSupplier<ColumnViewportConfig, Exception>
			columnViewportConfigUnsafeSupplier) {

		try {
			columnViewportConfig = columnViewportConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ColumnViewportConfig columnViewportConfig;

	public ColumnViewport[] getColumnViewports() {
		return columnViewports;
	}

	public void setColumnViewports(ColumnViewport[] columnViewports) {
		this.columnViewports = columnViewports;
	}

	public void setColumnViewports(
		UnsafeSupplier<ColumnViewport[], Exception>
			columnViewportsUnsafeSupplier) {

		try {
			columnViewports = columnViewportsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ColumnViewport[] columnViewports;

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setSize(UnsafeSupplier<Integer, Exception> sizeUnsafeSupplier) {
		try {
			size = sizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer size;

	@Override
	public PageColumnDefinition clone() throws CloneNotSupportedException {
		return (PageColumnDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageColumnDefinition)) {
			return false;
		}

		PageColumnDefinition pageColumnDefinition =
			(PageColumnDefinition)object;

		return Objects.equals(toString(), pageColumnDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageColumnDefinitionSerDes.toJSON(this);
	}

}