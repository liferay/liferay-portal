/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.dto.v1_0;

import com.liferay.analytics.cms.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.cms.rest.client.serdes.v1_0.ObjectEntryTopPagesSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class ObjectEntryTopPages implements Cloneable, Serializable {

	public static ObjectEntryTopPages toDTO(String json) {
		return ObjectEntryTopPagesSerDes.toDTO(json);
	}

	public TopPage[] getTopPages() {
		return topPages;
	}

	public void setTopPages(TopPage[] topPages) {
		this.topPages = topPages;
	}

	public void setTopPages(
		UnsafeSupplier<TopPage[], Exception> topPagesUnsafeSupplier) {

		try {
			topPages = topPagesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TopPage[] topPages;

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public void setTotalCount(
		UnsafeSupplier<Long, Exception> totalCountUnsafeSupplier) {

		try {
			totalCount = totalCountUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long totalCount;

	@Override
	public ObjectEntryTopPages clone() throws CloneNotSupportedException {
		return (ObjectEntryTopPages)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectEntryTopPages)) {
			return false;
		}

		ObjectEntryTopPages objectEntryTopPages = (ObjectEntryTopPages)object;

		return Objects.equals(toString(), objectEntryTopPages.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectEntryTopPagesSerDes.toJSON(this);
	}

}