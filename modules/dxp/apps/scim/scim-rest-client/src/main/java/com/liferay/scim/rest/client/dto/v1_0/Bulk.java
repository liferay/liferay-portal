/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.BulkSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class Bulk implements Cloneable, Serializable {

	public static Bulk toDTO(String json) {
		return BulkSerDes.toDTO(json);
	}

	public Integer getMaxOperations() {
		return maxOperations;
	}

	public void setMaxOperations(Integer maxOperations) {
		this.maxOperations = maxOperations;
	}

	public void setMaxOperations(
		UnsafeSupplier<Integer, Exception> maxOperationsUnsafeSupplier) {

		try {
			maxOperations = maxOperationsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer maxOperations;

	public Integer getMaxPayloadSize() {
		return maxPayloadSize;
	}

	public void setMaxPayloadSize(Integer maxPayloadSize) {
		this.maxPayloadSize = maxPayloadSize;
	}

	public void setMaxPayloadSize(
		UnsafeSupplier<Integer, Exception> maxPayloadSizeUnsafeSupplier) {

		try {
			maxPayloadSize = maxPayloadSizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer maxPayloadSize;

	public Boolean getSupported() {
		return supported;
	}

	public void setSupported(Boolean supported) {
		this.supported = supported;
	}

	public void setSupported(
		UnsafeSupplier<Boolean, Exception> supportedUnsafeSupplier) {

		try {
			supported = supportedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean supported;

	@Override
	public Bulk clone() throws CloneNotSupportedException {
		return (Bulk)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Bulk)) {
			return false;
		}

		Bulk bulk = (Bulk)object;

		return Objects.equals(toString(), bulk.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return BulkSerDes.toJSON(this);
	}

}