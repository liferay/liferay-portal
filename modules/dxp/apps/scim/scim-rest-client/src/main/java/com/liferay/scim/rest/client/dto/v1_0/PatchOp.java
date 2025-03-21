/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.PatchOpSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class PatchOp implements Cloneable, Serializable {

	public static PatchOp toDTO(String json) {
		return PatchOpSerDes.toDTO(json);
	}

	public Operation[] getOperations() {
		return Operations;
	}

	public void setOperations(Operation[] Operations) {
		this.Operations = Operations;
	}

	public void setOperations(
		UnsafeSupplier<Operation[], Exception> OperationsUnsafeSupplier) {

		try {
			Operations = OperationsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Operation[] Operations;

	public String[] getSchemas() {
		return schemas;
	}

	public void setSchemas(String[] schemas) {
		this.schemas = schemas;
	}

	public void setSchemas(
		UnsafeSupplier<String[], Exception> schemasUnsafeSupplier) {

		try {
			schemas = schemasUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] schemas;

	@Override
	public PatchOp clone() throws CloneNotSupportedException {
		return (PatchOp)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatchOp)) {
			return false;
		}

		PatchOp patchOp = (PatchOp)object;

		return Objects.equals(toString(), patchOp.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PatchOpSerDes.toJSON(this);
	}

}