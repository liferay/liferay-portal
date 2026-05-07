/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.dto.v1_0;

import com.liferay.site.dsr.analytics.rest.client.function.UnsafeSupplier;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.MostActiveVisitorsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class MostActiveVisitors implements Cloneable, Serializable {

	public static MostActiveVisitors toDTO(String json) {
		return MostActiveVisitorsSerDes.toDTO(json);
	}

	public MostActiveVisitor[] getMostActiveVisitors() {
		return mostActiveVisitors;
	}

	public void setMostActiveVisitors(MostActiveVisitor[] mostActiveVisitors) {
		this.mostActiveVisitors = mostActiveVisitors;
	}

	public void setMostActiveVisitors(
		UnsafeSupplier<MostActiveVisitor[], Exception>
			mostActiveVisitorsUnsafeSupplier) {

		try {
			mostActiveVisitors = mostActiveVisitorsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MostActiveVisitor[] mostActiveVisitors;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public void setTotal(UnsafeSupplier<Long, Exception> totalUnsafeSupplier) {
		try {
			total = totalUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long total;

	@Override
	public MostActiveVisitors clone() throws CloneNotSupportedException {
		return (MostActiveVisitors)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MostActiveVisitors)) {
			return false;
		}

		MostActiveVisitors mostActiveVisitors = (MostActiveVisitors)object;

		return Objects.equals(toString(), mostActiveVisitors.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return MostActiveVisitorsSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-222317883