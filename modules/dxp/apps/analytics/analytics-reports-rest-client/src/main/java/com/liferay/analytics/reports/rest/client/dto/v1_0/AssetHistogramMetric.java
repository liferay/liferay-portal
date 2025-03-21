/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.dto.v1_0;

import com.liferay.analytics.reports.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.reports.rest.client.serdes.v1_0.AssetHistogramMetricSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class AssetHistogramMetric implements Cloneable, Serializable {

	public static AssetHistogramMetric toDTO(String json) {
		return AssetHistogramMetricSerDes.toDTO(json);
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
	public AssetHistogramMetric clone() throws CloneNotSupportedException {
		return (AssetHistogramMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetHistogramMetric)) {
			return false;
		}

		AssetHistogramMetric assetHistogramMetric =
			(AssetHistogramMetric)object;

		return Objects.equals(toString(), assetHistogramMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AssetHistogramMetricSerDes.toJSON(this);
	}

}