/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.dto.v1_0;

import com.liferay.analytics.cms.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.cms.rest.client.serdes.v1_0.PerformanceHistogramMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class PerformanceHistogramMetric implements Cloneable, Serializable {

	public static PerformanceHistogramMetric toDTO(String json) {
		return PerformanceHistogramMetricSerDes.toDTO(json);
	}

	public Histogram[] getHistograms() {
		return histograms;
	}

	public void setHistograms(Histogram[] histograms) {
		this.histograms = histograms;
	}

	public void setHistograms(
		UnsafeSupplier<Histogram[], Exception> histogramsUnsafeSupplier) {

		try {
			histograms = histogramsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Histogram[] histograms;

	@Override
	public PerformanceHistogramMetric clone()
		throws CloneNotSupportedException {

		return (PerformanceHistogramMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PerformanceHistogramMetric)) {
			return false;
		}

		PerformanceHistogramMetric performanceHistogramMetric =
			(PerformanceHistogramMetric)object;

		return Objects.equals(
			toString(), performanceHistogramMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PerformanceHistogramMetricSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1609087599