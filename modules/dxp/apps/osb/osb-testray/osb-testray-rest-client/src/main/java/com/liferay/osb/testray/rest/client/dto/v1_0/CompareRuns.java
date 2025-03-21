/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.testray.rest.client.dto.v1_0;

import com.liferay.osb.testray.rest.client.function.UnsafeSupplier;
import com.liferay.osb.testray.rest.client.serdes.v1_0.CompareRunsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class CompareRuns implements Cloneable, Serializable {

	public static CompareRuns toDTO(String json) {
		return CompareRunsSerDes.toDTO(json);
	}

	public String[] getDueStatuses() {
		return dueStatuses;
	}

	public void setDueStatuses(String[] dueStatuses) {
		this.dueStatuses = dueStatuses;
	}

	public void setDueStatuses(
		UnsafeSupplier<String[], Exception> dueStatusesUnsafeSupplier) {

		try {
			dueStatuses = dueStatusesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] dueStatuses;

	public Object getValues() {
		return values;
	}

	public void setValues(Object values) {
		this.values = values;
	}

	public void setValues(
		UnsafeSupplier<Object, Exception> valuesUnsafeSupplier) {

		try {
			values = valuesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object values;

	@Override
	public CompareRuns clone() throws CloneNotSupportedException {
		return (CompareRuns)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CompareRuns)) {
			return false;
		}

		CompareRuns compareRuns = (CompareRuns)object;

		return Objects.equals(toString(), compareRuns.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CompareRunsSerDes.toJSON(this);
	}

}