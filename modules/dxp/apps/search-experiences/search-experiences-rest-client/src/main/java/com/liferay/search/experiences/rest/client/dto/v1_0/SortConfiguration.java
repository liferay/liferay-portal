/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.SortConfigurationSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class SortConfiguration implements Cloneable, Serializable {

	public static SortConfiguration toDTO(String json) {
		return SortConfigurationSerDes.toDTO(json);
	}

	public Object getSorts() {
		return sorts;
	}

	public void setSorts(Object sorts) {
		this.sorts = sorts;
	}

	public void setSorts(
		UnsafeSupplier<Object, Exception> sortsUnsafeSupplier) {

		try {
			sorts = sortsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object sorts;

	@Override
	public SortConfiguration clone() throws CloneNotSupportedException {
		return (SortConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SortConfiguration)) {
			return false;
		}

		SortConfiguration sortConfiguration = (SortConfiguration)object;

		return Objects.equals(toString(), sortConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SortConfigurationSerDes.toJSON(this);
	}

}