/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.FilterSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class Filter implements Cloneable, Serializable {

	public static Filter toDTO(String json) {
		return FilterSerDes.toDTO(json);
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public void setMaxResults(
		UnsafeSupplier<Integer, Exception> maxResultsUnsafeSupplier) {

		try {
			maxResults = maxResultsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer maxResults;

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
	public Filter clone() throws CloneNotSupportedException {
		return (Filter)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Filter)) {
			return false;
		}

		Filter filter = (Filter)object;

		return Objects.equals(toString(), filter.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FilterSerDes.toJSON(this);
	}

}