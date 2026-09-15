/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.MetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class Metric implements Cloneable, Serializable {

	public static Metric toDTO(String json) {
		return MetricSerDes.toDTO(json);
	}

	public HistogramBucket[] getHistogramBuckets() {
		return histogramBuckets;
	}

	public void setHistogramBuckets(HistogramBucket[] histogramBuckets) {
		this.histogramBuckets = histogramBuckets;
	}

	public void setHistogramBuckets(
		UnsafeSupplier<HistogramBucket[], Exception>
			histogramBucketsUnsafeSupplier) {

		try {
			histogramBuckets = histogramBucketsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected HistogramBucket[] histogramBuckets;

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
// LIFERAY-REST-BUILDER-HASH:1833204463