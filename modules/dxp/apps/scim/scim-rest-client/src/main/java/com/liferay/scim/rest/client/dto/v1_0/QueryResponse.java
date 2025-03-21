/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.QueryResponseSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class QueryResponse implements Cloneable, Serializable {

	public static QueryResponse toDTO(String json) {
		return QueryResponseSerDes.toDTO(json);
	}

	public Object getResources() {
		return Resources;
	}

	public void setResources(Object Resources) {
		this.Resources = Resources;
	}

	public void setResources(
		UnsafeSupplier<Object, Exception> ResourcesUnsafeSupplier) {

		try {
			Resources = ResourcesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object Resources;

	public Integer getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	public void setItemsPerPage(
		UnsafeSupplier<Integer, Exception> itemsPerPageUnsafeSupplier) {

		try {
			itemsPerPage = itemsPerPageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer itemsPerPage;

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

	public Integer getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}

	public void setTotalResults(
		UnsafeSupplier<Integer, Exception> totalResultsUnsafeSupplier) {

		try {
			totalResults = totalResultsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer totalResults;

	@Override
	public QueryResponse clone() throws CloneNotSupportedException {
		return (QueryResponse)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof QueryResponse)) {
			return false;
		}

		QueryResponse queryResponse = (QueryResponse)object;

		return Objects.equals(toString(), queryResponse.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return QueryResponseSerDes.toJSON(this);
	}

}