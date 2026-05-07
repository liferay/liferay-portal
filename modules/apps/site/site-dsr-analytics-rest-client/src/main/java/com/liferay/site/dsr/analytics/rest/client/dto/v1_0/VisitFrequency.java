/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.dto.v1_0;

import com.liferay.site.dsr.analytics.rest.client.function.UnsafeSupplier;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.VisitFrequencySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class VisitFrequency implements Cloneable, Serializable {

	public static VisitFrequency toDTO(String json) {
		return VisitFrequencySerDes.toDTO(json);
	}

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

	public VisitFrequencyItem[] getVisitFrequencyItems() {
		return visitFrequencyItems;
	}

	public void setVisitFrequencyItems(
		VisitFrequencyItem[] visitFrequencyItems) {

		this.visitFrequencyItems = visitFrequencyItems;
	}

	public void setVisitFrequencyItems(
		UnsafeSupplier<VisitFrequencyItem[], Exception>
			visitFrequencyItemsUnsafeSupplier) {

		try {
			visitFrequencyItems = visitFrequencyItemsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected VisitFrequencyItem[] visitFrequencyItems;

	@Override
	public VisitFrequency clone() throws CloneNotSupportedException {
		return (VisitFrequency)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof VisitFrequency)) {
			return false;
		}

		VisitFrequency visitFrequency = (VisitFrequency)object;

		return Objects.equals(toString(), visitFrequency.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return VisitFrequencySerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-518750449