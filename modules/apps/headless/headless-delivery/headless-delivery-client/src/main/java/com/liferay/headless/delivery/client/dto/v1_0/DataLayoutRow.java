/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.DataLayoutRowSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class DataLayoutRow implements Cloneable, Serializable {

	public static DataLayoutRow toDTO(String json) {
		return DataLayoutRowSerDes.toDTO(json);
	}

	public DataLayoutColumn[] getDataLayoutColumns() {
		return dataLayoutColumns;
	}

	public void setDataLayoutColumns(DataLayoutColumn[] dataLayoutColumns) {
		this.dataLayoutColumns = dataLayoutColumns;
	}

	public void setDataLayoutColumns(
		UnsafeSupplier<DataLayoutColumn[], Exception>
			dataLayoutColumnsUnsafeSupplier) {

		try {
			dataLayoutColumns = dataLayoutColumnsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DataLayoutColumn[] dataLayoutColumns;

	@Override
	public DataLayoutRow clone() throws CloneNotSupportedException {
		return (DataLayoutRow)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataLayoutRow)) {
			return false;
		}

		DataLayoutRow dataLayoutRow = (DataLayoutRow)object;

		return Objects.equals(toString(), dataLayoutRow.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DataLayoutRowSerDes.toJSON(this);
	}

}