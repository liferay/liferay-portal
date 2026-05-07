/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.dto.v1_0;

import com.liferay.site.dsr.analytics.rest.client.function.UnsafeSupplier;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.HistogramSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class Histogram implements Cloneable, Serializable {

	public static Histogram toDTO(String json) {
		return HistogramSerDes.toDTO(json);
	}

	public HistogramMetric[] getHistogramMetrics() {
		return histogramMetrics;
	}

	public void setHistogramMetrics(HistogramMetric[] histogramMetrics) {
		this.histogramMetrics = histogramMetrics;
	}

	public void setHistogramMetrics(
		UnsafeSupplier<HistogramMetric[], Exception>
			histogramMetricsUnsafeSupplier) {

		try {
			histogramMetrics = histogramMetricsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected HistogramMetric[] histogramMetrics;

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
// LIFERAY-REST-BUILDER-HASH:1327891809