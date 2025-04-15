/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.dto.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.function.UnsafeSupplier;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.StartNodeKeysSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class StartNodeKeys implements Cloneable, Serializable {

	public static StartNodeKeys toDTO(String json) {
		return StartNodeKeysSerDes.toDTO(json);
	}

	public NodeKey[] getNodeKeys() {
		return nodeKeys;
	}

	public void setNodeKeys(NodeKey[] nodeKeys) {
		this.nodeKeys = nodeKeys;
	}

	public void setNodeKeys(
		UnsafeSupplier<NodeKey[], Exception> nodeKeysUnsafeSupplier) {

		try {
			nodeKeys = nodeKeysUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected NodeKey[] nodeKeys;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<Integer, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer status;

	@Override
	public StartNodeKeys clone() throws CloneNotSupportedException {
		return (StartNodeKeys)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof StartNodeKeys)) {
			return false;
		}

		StartNodeKeys startNodeKeys = (StartNodeKeys)object;

		return Objects.equals(toString(), startNodeKeys.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return StartNodeKeysSerDes.toJSON(this);
	}

}