/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.dto.v1_0;

import com.liferay.analytics.reports.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.reports.rest.client.serdes.v1_0.HistogramSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class Histogram implements Cloneable, Serializable {

	public static Histogram toDTO(String json) {
		return HistogramSerDes.toDTO(json);
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public void setMetricName(
		UnsafeSupplier<String, Exception> metricNameUnsafeSupplier) {

		try {
			metricName = metricNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String metricName;

	public Metric[] getMetrics() {
		return metrics;
	}

	public void setMetrics(Metric[] metrics) {
		this.metrics = metrics;
	}

	public void setMetrics(
		UnsafeSupplier<Metric[], Exception> metricsUnsafeSupplier) {

		try {
			metrics = metricsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Metric[] metrics;

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public void setTotal(
		UnsafeSupplier<Double, Exception> totalUnsafeSupplier) {

		try {
			total = totalUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double total;

	public Double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;
	}

	public void setTotalValue(
		UnsafeSupplier<Double, Exception> totalValueUnsafeSupplier) {

		try {
			totalValue = totalValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double totalValue;

	@Override
	public Histogram clone() throws CloneNotSupportedException {
		return (Histogram)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Histogram)) {
			return false;
		}

		Histogram histogram = (Histogram)object;

		return Objects.equals(toString(), histogram.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return HistogramSerDes.toJSON(this);
	}

}