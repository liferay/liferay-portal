/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.dto.v1_0;

import com.liferay.analytics.reports.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.reports.rest.client.serdes.v1_0.TrendSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class Trend implements Cloneable, Serializable {

	public static Trend toDTO(String json) {
		return TrendSerDes.toDTO(json);
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public void setPercentage(
		UnsafeSupplier<Double, Exception> percentageUnsafeSupplier) {

		try {
			percentage = percentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double percentage;

	public TrendClassification getTrendClassification() {
		return trendClassification;
	}

	public String getTrendClassificationAsString() {
		if (trendClassification == null) {
			return null;
		}

		return trendClassification.toString();
	}

	public void setTrendClassification(
		TrendClassification trendClassification) {

		this.trendClassification = trendClassification;
	}

	public void setTrendClassification(
		UnsafeSupplier<TrendClassification, Exception>
			trendClassificationUnsafeSupplier) {

		try {
			trendClassification = trendClassificationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TrendClassification trendClassification;

	@Override
	public Trend clone() throws CloneNotSupportedException {
		return (Trend)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Trend)) {
			return false;
		}

		Trend trend = (Trend)object;

		return Objects.equals(toString(), trend.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TrendSerDes.toJSON(this);
	}

	public static enum TrendClassification {

		NEGATIVE("NEGATIVE"), NEUTRAL("NEUTRAL"), POSITIVE("POSITIVE");

		public static TrendClassification create(String value) {
			for (TrendClassification trendClassification : values()) {
				if (Objects.equals(trendClassification.getValue(), value) ||
					Objects.equals(trendClassification.name(), value)) {

					return trendClassification;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private TrendClassification(String value) {
			_value = value;
		}

		private final String _value;

	}

}