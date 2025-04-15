/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.dto.v1_0;

import com.liferay.headless.form.client.function.UnsafeSupplier;
import com.liferay.headless.form.client.serdes.v1_0.GridSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Grid implements Cloneable, Serializable {

	public static Grid toDTO(String json) {
		return GridSerDes.toDTO(json);
	}

	public FormFieldOption[] getColumns() {
		return columns;
	}

	public void setColumns(FormFieldOption[] columns) {
		this.columns = columns;
	}

	public void setColumns(
		UnsafeSupplier<FormFieldOption[], Exception> columnsUnsafeSupplier) {

		try {
			columns = columnsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FormFieldOption[] columns;

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

	public FormFieldOption[] getRows() {
		return rows;
	}

	public void setRows(FormFieldOption[] rows) {
		this.rows = rows;
	}

	public void setRows(
		UnsafeSupplier<FormFieldOption[], Exception> rowsUnsafeSupplier) {

		try {
			rows = rowsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FormFieldOption[] rows;

	@Override
	public Grid clone() throws CloneNotSupportedException {
		return (Grid)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Grid)) {
			return false;
		}

		Grid grid = (Grid)object;

		return Objects.equals(toString(), grid.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return GridSerDes.toJSON(this);
	}

}