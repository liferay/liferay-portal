/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.dto.v1_0;

import com.liferay.analytics.reports.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.reports.rest.client.serdes.v1_0.AssetDeviceMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class AssetDeviceMetric implements Cloneable, Serializable {

	public static AssetDeviceMetric toDTO(String json) {
		return AssetDeviceMetricSerDes.toDTO(json);
	}

	public DeviceMetric[] getDeviceMetrics() {
		return deviceMetrics;
	}

	public void setDeviceMetrics(DeviceMetric[] deviceMetrics) {
		this.deviceMetrics = deviceMetrics;
	}

	public void setDeviceMetrics(
		UnsafeSupplier<DeviceMetric[], Exception> deviceMetricsUnsafeSupplier) {

		try {
			deviceMetrics = deviceMetricsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DeviceMetric[] deviceMetrics;

	@Override
	public AssetDeviceMetric clone() throws CloneNotSupportedException {
		return (AssetDeviceMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetDeviceMetric)) {
			return false;
		}

		AssetDeviceMetric assetDeviceMetric = (AssetDeviceMetric)object;

		return Objects.equals(toString(), assetDeviceMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AssetDeviceMetricSerDes.toJSON(this);
	}

}