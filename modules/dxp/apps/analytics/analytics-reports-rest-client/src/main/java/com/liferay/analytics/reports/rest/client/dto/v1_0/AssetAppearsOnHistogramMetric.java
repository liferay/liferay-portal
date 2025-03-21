/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.dto.v1_0;

import com.liferay.analytics.reports.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.reports.rest.client.serdes.v1_0.AssetAppearsOnHistogramMetricSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class AssetAppearsOnHistogramMetric implements Cloneable, Serializable {

	public static AssetAppearsOnHistogramMetric toDTO(String json) {
		return AssetAppearsOnHistogramMetricSerDes.toDTO(json);
	}

	public AssetAppearsOnHistogram[] getAssetAppearsOnHistograms() {
		return assetAppearsOnHistograms;
	}

	public void setAssetAppearsOnHistograms(
		AssetAppearsOnHistogram[] assetAppearsOnHistograms) {

		this.assetAppearsOnHistograms = assetAppearsOnHistograms;
	}

	public void setAssetAppearsOnHistograms(
		UnsafeSupplier<AssetAppearsOnHistogram[], Exception>
			assetAppearsOnHistogramsUnsafeSupplier) {

		try {
			assetAppearsOnHistograms =
				assetAppearsOnHistogramsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected AssetAppearsOnHistogram[] assetAppearsOnHistograms;

	@Override
	public AssetAppearsOnHistogramMetric clone()
		throws CloneNotSupportedException {

		return (AssetAppearsOnHistogramMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetAppearsOnHistogramMetric)) {
			return false;
		}

		AssetAppearsOnHistogramMetric assetAppearsOnHistogramMetric =
			(AssetAppearsOnHistogramMetric)object;

		return Objects.equals(
			toString(), assetAppearsOnHistogramMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AssetAppearsOnHistogramMetricSerDes.toJSON(this);
	}

}