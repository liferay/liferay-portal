/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.dto.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.function.UnsafeSupplier;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.NodeKeySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class NodeKey implements Cloneable, Serializable {

	public static NodeKey toDTO(String json) {
		return NodeKeySerDes.toDTO(json);
	}

	public String getExecutionType() {
		return executionType;
	}

	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}

	public void setExecutionType(
		UnsafeSupplier<String, Exception> executionTypeUnsafeSupplier) {

		try {
			executionType = executionTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String executionType;

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
	public NodeKey clone() throws CloneNotSupportedException {
		return (NodeKey)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NodeKey)) {
			return false;
		}

		NodeKey nodeKey = (NodeKey)object;

		return Objects.equals(toString(), nodeKey.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return NodeKeySerDes.toJSON(this);
	}

}