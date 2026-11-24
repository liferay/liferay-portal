/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.CampaignMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class CampaignMetric implements Cloneable, Serializable {

	public static CampaignMetric toDTO(String json) {
		return CampaignMetricSerDes.toDTO(json);
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

	public String getTrendClassification() {
		return trendClassification;
	}

	public void setTrendClassification(String trendClassification) {
		this.trendClassification = trendClassification;
	}

	public void setTrendClassification(
		UnsafeSupplier<String, Exception> trendClassificationUnsafeSupplier) {

		try {
			trendClassification = trendClassificationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String trendClassification;

	public Double getTrendPercentage() {
		return trendPercentage;
	}

	public void setTrendPercentage(Double trendPercentage) {
		this.trendPercentage = trendPercentage;
	}

	public void setTrendPercentage(
		UnsafeSupplier<Double, Exception> trendPercentageUnsafeSupplier) {

		try {
			trendPercentage = trendPercentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double trendPercentage;

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

	@Override
	public CampaignMetric clone() throws CloneNotSupportedException {
		return (CampaignMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CampaignMetric)) {
			return false;
		}

		CampaignMetric campaignMetric = (CampaignMetric)object;

		return Objects.equals(toString(), campaignMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CampaignMetricSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1595414326