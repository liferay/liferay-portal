/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.dto.v1_0;

import com.liferay.analytics.reports.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.reports.rest.client.serdes.v1_0.MetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class Metric implements Cloneable, Serializable {

	public static Metric toDTO(String json) {
		return MetricSerDes.toDTO(json);
	}

	public String getMetricType() {
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

	public void setMetricType(
		UnsafeSupplier<String, Exception> metricTypeUnsafeSupplier) {

		try {
			metricType = metricTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String metricType;

	public Double getPreviousValue() {
		return previousValue;
	}

	public void setPreviousValue(Double previousValue) {
		this.previousValue = previousValue;
	}

	public void setPreviousValue(
		UnsafeSupplier<Double, Exception> previousValueUnsafeSupplier) {

		try {
			previousValue = previousValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double previousValue;

	public String getPreviousValueKey() {
		return previousValueKey;
	}

	public void setPreviousValueKey(String previousValueKey) {
		this.previousValueKey = previousValueKey;
	}

	public void setPreviousValueKey(
		UnsafeSupplier<String, Exception> previousValueKeyUnsafeSupplier) {

		try {
			previousValueKey = previousValueKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String previousValueKey;

	public Trend getTrend() {
		return trend;
	}

	public void setTrend(Trend trend) {
		this.trend = trend;
	}

	public void setTrend(UnsafeSupplier<Trend, Exception> trendUnsafeSupplier) {
		try {
			trend = trendUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Trend trend;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<Double, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double value;

	public String getValueKey() {
		return valueKey;
	}

	public void setValueKey(String valueKey) {
		this.valueKey = valueKey;
	}

	public void setValueKey(
		UnsafeSupplier<String, Exception> valueKeyUnsafeSupplier) {

		try {
			valueKey = valueKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String valueKey;

	@Override
	public Metric clone() throws CloneNotSupportedException {
		return (Metric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Metric)) {
			return false;
		}

		Metric metric = (Metric)object;

		return Objects.equals(toString(), metric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return MetricSerDes.toJSON(this);
	}

}