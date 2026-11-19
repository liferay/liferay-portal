/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.VisitorMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class VisitorMetric implements Cloneable, Serializable {

	public static VisitorMetric toDTO(String json) {
		return VisitorMetricSerDes.toDTO(json);
	}

	public Long getAverageSessionDuration() {
		return averageSessionDuration;
	}

	public void setAverageSessionDuration(Long averageSessionDuration) {
		this.averageSessionDuration = averageSessionDuration;
	}

	public void setAverageSessionDuration(
		UnsafeSupplier<Long, Exception> averageSessionDurationUnsafeSupplier) {

		try {
			averageSessionDuration = averageSessionDurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long averageSessionDuration;

	public Long getKnownVisitors() {
		return knownVisitors;
	}

	public void setKnownVisitors(Long knownVisitors) {
		this.knownVisitors = knownVisitors;
	}

	public void setKnownVisitors(
		UnsafeSupplier<Long, Exception> knownVisitorsUnsafeSupplier) {

		try {
			knownVisitors = knownVisitorsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long knownVisitors;

	public Long getTotalSessionDuration() {
		return totalSessionDuration;
	}

	public void setTotalSessionDuration(Long totalSessionDuration) {
		this.totalSessionDuration = totalSessionDuration;
	}

	public void setTotalSessionDuration(
		UnsafeSupplier<Long, Exception> totalSessionDurationUnsafeSupplier) {

		try {
			totalSessionDuration = totalSessionDurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long totalSessionDuration;

	public Long getVisitors() {
		return visitors;
	}

	public void setVisitors(Long visitors) {
		this.visitors = visitors;
	}

	public void setVisitors(
		UnsafeSupplier<Long, Exception> visitorsUnsafeSupplier) {

		try {
			visitors = visitorsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long visitors;

	@Override
	public VisitorMetric clone() throws CloneNotSupportedException {
		return (VisitorMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof VisitorMetric)) {
			return false;
		}

		VisitorMetric visitorMetric = (VisitorMetric)object;

		return Objects.equals(toString(), visitorMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return VisitorMetricSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:937787546