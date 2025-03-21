/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.dto.v1_0;

import com.liferay.analytics.reports.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.reports.rest.client.serdes.v1_0.AssetAppearsOnHistogramSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class AssetAppearsOnHistogram implements Cloneable, Serializable {

	public static AssetAppearsOnHistogram toDTO(String json) {
		return AssetAppearsOnHistogramSerDes.toDTO(json);
	}

	public AppearsOnHistogram[] getAppearsOnHistograms() {
		return appearsOnHistograms;
	}

	public void setAppearsOnHistograms(
		AppearsOnHistogram[] appearsOnHistograms) {

		this.appearsOnHistograms = appearsOnHistograms;
	}

	public void setAppearsOnHistograms(
		UnsafeSupplier<AppearsOnHistogram[], Exception>
			appearsOnHistogramsUnsafeSupplier) {

		try {
			appearsOnHistograms = appearsOnHistogramsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AppearsOnHistogram[] appearsOnHistograms;

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

	@Override
	public AssetAppearsOnHistogram clone() throws CloneNotSupportedException {
		return (AssetAppearsOnHistogram)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetAppearsOnHistogram)) {
			return false;
		}

		AssetAppearsOnHistogram assetAppearsOnHistogram =
			(AssetAppearsOnHistogram)object;

		return Objects.equals(toString(), assetAppearsOnHistogram.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AssetAppearsOnHistogramSerDes.toJSON(this);
	}

}