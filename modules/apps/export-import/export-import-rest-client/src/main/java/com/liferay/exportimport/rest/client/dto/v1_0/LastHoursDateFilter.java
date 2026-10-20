/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.dto.v1_0;

import com.liferay.exportimport.rest.client.function.UnsafeSupplier;
import com.liferay.exportimport.rest.client.serdes.v1_0.LastHoursDateFilterSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class LastHoursDateFilter
	extends DateFilter implements Cloneable, Serializable {

	public static LastHoursDateFilter toDTO(String json) {
		return LastHoursDateFilterSerDes.toDTO(json);
	}

	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}

	public void setHours(
		UnsafeSupplier<Integer, Exception> hoursUnsafeSupplier) {

		try {
			hours = hoursUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer hours;

	@Override
	public LastHoursDateFilter clone() throws CloneNotSupportedException {
		return (LastHoursDateFilter)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LastHoursDateFilter)) {
			return false;
		}

		LastHoursDateFilter lastHoursDateFilter = (LastHoursDateFilter)object;

		return Objects.equals(toString(), lastHoursDateFilter.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return LastHoursDateFilterSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:540602519