/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.dto.v1_0;

import com.liferay.analytics.cms.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.cms.rest.client.serdes.v1_0.ObjectEntryMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class ObjectEntryMetric implements Cloneable, Serializable {

	public static ObjectEntryMetric toDTO(String json) {
		return ObjectEntryMetricSerDes.toDTO(json);
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public void setDataSourceId(
		UnsafeSupplier<String, Exception> dataSourceIdUnsafeSupplier) {

		try {
			dataSourceId = dataSourceIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String dataSourceId;

	public Metric getDefaultMetric() {
		return defaultMetric;
	}

	public void setDefaultMetric(Metric defaultMetric) {
		this.defaultMetric = defaultMetric;
	}

	public void setDefaultMetric(
		UnsafeSupplier<Metric, Exception> defaultMetricUnsafeSupplier) {

		try {
			defaultMetric = defaultMetricUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Metric defaultMetric;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Metric[] getSelectedMetrics() {
		return selectedMetrics;
	}

	public void setSelectedMetrics(Metric[] selectedMetrics) {
		this.selectedMetrics = selectedMetrics;
	}

	public void setSelectedMetrics(
		UnsafeSupplier<Metric[], Exception> selectedMetricsUnsafeSupplier) {

		try {
			selectedMetrics = selectedMetricsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Metric[] selectedMetrics;

	@Override
	public ObjectEntryMetric clone() throws CloneNotSupportedException {
		return (ObjectEntryMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectEntryMetric)) {
			return false;
		}

		ObjectEntryMetric objectEntryMetric = (ObjectEntryMetric)object;

		return Objects.equals(toString(), objectEntryMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectEntryMetricSerDes.toJSON(this);
	}

}