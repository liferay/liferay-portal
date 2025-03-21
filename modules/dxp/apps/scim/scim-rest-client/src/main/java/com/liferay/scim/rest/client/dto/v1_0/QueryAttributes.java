/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.QueryAttributesSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class QueryAttributes implements Cloneable, Serializable {

	public static QueryAttributes toDTO(String json) {
		return QueryAttributesSerDes.toDTO(json);
	}

	public String[] getAttributes() {
		return attributes;
	}

	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}

	public void setAttributes(
		UnsafeSupplier<String[], Exception> attributesUnsafeSupplier) {

		try {
			attributes = attributesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] attributes;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public void setCount(
		UnsafeSupplier<Integer, Exception> countUnsafeSupplier) {

		try {
			count = countUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer count;

	public String[] getExcludedAttributes() {
		return excludedAttributes;
	}

	public void setExcludedAttributes(String[] excludedAttributes) {
		this.excludedAttributes = excludedAttributes;
	}

	public void setExcludedAttributes(
		UnsafeSupplier<String[], Exception> excludedAttributesUnsafeSupplier) {

		try {
			excludedAttributes = excludedAttributesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] excludedAttributes;

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setFilter(
		UnsafeSupplier<String, Exception> filterUnsafeSupplier) {

		try {
			filter = filterUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String filter;

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public void setSortBy(
		UnsafeSupplier<String, Exception> sortByUnsafeSupplier) {

		try {
			sortBy = sortByUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sortBy;

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setSortOrder(
		UnsafeSupplier<String, Exception> sortOrderUnsafeSupplier) {

		try {
			sortOrder = sortOrderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sortOrder;

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public void setStartIndex(
		UnsafeSupplier<Integer, Exception> startIndexUnsafeSupplier) {

		try {
			startIndex = startIndexUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer startIndex;

	@Override
	public QueryAttributes clone() throws CloneNotSupportedException {
		return (QueryAttributes)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof QueryAttributes)) {
			return false;
		}

		QueryAttributes queryAttributes = (QueryAttributes)object;

		return Objects.equals(toString(), queryAttributes.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return QueryAttributesSerDes.toJSON(this);
	}

}