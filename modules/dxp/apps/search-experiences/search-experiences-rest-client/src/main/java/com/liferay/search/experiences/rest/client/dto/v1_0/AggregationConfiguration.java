/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.AggregationConfigurationSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class AggregationConfiguration implements Cloneable, Serializable {

	public static AggregationConfiguration toDTO(String json) {
		return AggregationConfigurationSerDes.toDTO(json);
	}

	public Object getAggs() {
		return aggs;
	}

	public void setAggs(Object aggs) {
		this.aggs = aggs;
	}

	public void setAggs(UnsafeSupplier<Object, Exception> aggsUnsafeSupplier) {
		try {
			aggs = aggsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object aggs;

	@Override
	public AggregationConfiguration clone() throws CloneNotSupportedException {
		return (AggregationConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AggregationConfiguration)) {
			return false;
		}

		AggregationConfiguration aggregationConfiguration =
			(AggregationConfiguration)object;

		return Objects.equals(toString(), aggregationConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AggregationConfigurationSerDes.toJSON(this);
	}

}