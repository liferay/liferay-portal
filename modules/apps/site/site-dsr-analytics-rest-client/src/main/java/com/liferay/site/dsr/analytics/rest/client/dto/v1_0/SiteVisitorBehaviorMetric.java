/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.dto.v1_0;

import com.liferay.site.dsr.analytics.rest.client.function.UnsafeSupplier;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.SiteVisitorBehaviorMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class SiteVisitorBehaviorMetric implements Cloneable, Serializable {

	public static SiteVisitorBehaviorMetric toDTO(String json) {
		return SiteVisitorBehaviorMetricSerDes.toDTO(json);
	}

	public Double getAverageSessionDuration() {
		return averageSessionDuration;
	}

	public void setAverageSessionDuration(Double averageSessionDuration) {
		this.averageSessionDuration = averageSessionDuration;
	}

	public void setAverageSessionDuration(
		UnsafeSupplier<Double, Exception>
			averageSessionDurationUnsafeSupplier) {

		try {
			averageSessionDuration = averageSessionDurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double averageSessionDuration;

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

	public Double getTotalSessionDuration() {
		return totalSessionDuration;
	}

	public void setTotalSessionDuration(Double totalSessionDuration) {
		this.totalSessionDuration = totalSessionDuration;
	}

	public void setTotalSessionDuration(
		UnsafeSupplier<Double, Exception> totalSessionDurationUnsafeSupplier) {

		try {
			totalSessionDuration = totalSessionDurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double totalSessionDuration;

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
	public SiteVisitorBehaviorMetric clone() throws CloneNotSupportedException {
		return (SiteVisitorBehaviorMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SiteVisitorBehaviorMetric)) {
			return false;
		}

		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric =
			(SiteVisitorBehaviorMetric)object;

		return Objects.equals(toString(), siteVisitorBehaviorMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SiteVisitorBehaviorMetricSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:418129371