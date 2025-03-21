/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.dto.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.function.UnsafeSupplier;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.HistogramMetricSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class HistogramMetric implements Cloneable, Serializable {

	public static HistogramMetric toDTO(String json) {
		return HistogramMetricSerDes.toDTO(json);
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

	public Unit getUnit() {
		return unit;
	}

	public String getUnitAsString() {
		if (unit == null) {
			return null;
		}

		return unit.toString();
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public void setUnit(UnsafeSupplier<Unit, Exception> unitUnsafeSupplier) {
		try {
			unit = unitUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Unit unit;

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
	public HistogramMetric clone() throws CloneNotSupportedException {
		return (HistogramMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof HistogramMetric)) {
			return false;
		}

		HistogramMetric histogramMetric = (HistogramMetric)object;

		return Objects.equals(toString(), histogramMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return HistogramMetricSerDes.toJSON(this);
	}

	public static enum Unit {

		DAYS("Days"), HOURS("Hours"), MONTHS("Months"), WEEKS("Weeks"),
		YEARS("Years");

		public static Unit create(String value) {
			for (Unit unit : values()) {
				if (Objects.equals(unit.getValue(), value) ||
					Objects.equals(unit.name(), value)) {

					return unit;
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

		private Unit(String value) {
			_value = value;
		}

		private final String _value;

	}

}