/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v2_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v2_0.AggregationConfigurationSerDes;

import java.io.Serializable;

import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class AggregationConfiguration implements Cloneable, Serializable {

	public static AggregationConfiguration toDTO(String json) {
		return AggregationConfigurationSerDes.toDTO(json);
	}

	public Object getAggregations() {
		return aggregations;
	}

	public void setAggregations(Object aggregations) {
		this.aggregations = aggregations;
	}

	public void setAggregations(
		UnsafeSupplier<Object, Exception> aggregationsUnsafeSupplier) {

		try {
			aggregations = aggregationsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object aggregations;

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