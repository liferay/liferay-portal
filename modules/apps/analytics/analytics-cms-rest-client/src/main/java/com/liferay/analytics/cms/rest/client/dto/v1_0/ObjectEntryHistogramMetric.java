/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.dto.v1_0;

import com.liferay.analytics.cms.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.cms.rest.client.serdes.v1_0.ObjectEntryHistogramMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class ObjectEntryHistogramMetric implements Cloneable, Serializable {

	public static ObjectEntryHistogramMetric toDTO(String json) {
		return ObjectEntryHistogramMetricSerDes.toDTO(json);
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
	public ObjectEntryHistogramMetric clone()
		throws CloneNotSupportedException {

		return (ObjectEntryHistogramMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectEntryHistogramMetric)) {
			return false;
		}

		ObjectEntryHistogramMetric objectEntryHistogramMetric =
			(ObjectEntryHistogramMetric)object;

		return Objects.equals(
			toString(), objectEntryHistogramMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectEntryHistogramMetricSerDes.toJSON(this);
	}

}