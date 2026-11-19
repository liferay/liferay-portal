/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.VisitFrequencyMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class VisitFrequencyMetric implements Cloneable, Serializable {

	public static VisitFrequencyMetric toDTO(String json) {
		return VisitFrequencyMetricSerDes.toDTO(json);
	}

	public Long getBiWeeklyVisitors() {
		return biWeeklyVisitors;
	}

	public void setBiWeeklyVisitors(Long biWeeklyVisitors) {
		this.biWeeklyVisitors = biWeeklyVisitors;
	}

	public void setBiWeeklyVisitors(
		UnsafeSupplier<Long, Exception> biWeeklyVisitorsUnsafeSupplier) {

		try {
			biWeeklyVisitors = biWeeklyVisitorsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long biWeeklyVisitors;

	public Long getDailyVisitors() {
		return dailyVisitors;
	}

	public void setDailyVisitors(Long dailyVisitors) {
		this.dailyVisitors = dailyVisitors;
	}

	public void setDailyVisitors(
		UnsafeSupplier<Long, Exception> dailyVisitorsUnsafeSupplier) {

		try {
			dailyVisitors = dailyVisitorsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long dailyVisitors;

	public Long getMonthlyVisitors() {
		return monthlyVisitors;
	}

	public void setMonthlyVisitors(Long monthlyVisitors) {
		this.monthlyVisitors = monthlyVisitors;
	}

	public void setMonthlyVisitors(
		UnsafeSupplier<Long, Exception> monthlyVisitorsUnsafeSupplier) {

		try {
			monthlyVisitors = monthlyVisitorsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long monthlyVisitors;

	public Long getOccasionalVisitors() {
		return occasionalVisitors;
	}

	public void setOccasionalVisitors(Long occasionalVisitors) {
		this.occasionalVisitors = occasionalVisitors;
	}

	public void setOccasionalVisitors(
		UnsafeSupplier<Long, Exception> occasionalVisitorsUnsafeSupplier) {

		try {
			occasionalVisitors = occasionalVisitorsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long occasionalVisitors;

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

	public Long getWeeklyVisitors() {
		return weeklyVisitors;
	}

	public void setWeeklyVisitors(Long weeklyVisitors) {
		this.weeklyVisitors = weeklyVisitors;
	}

	public void setWeeklyVisitors(
		UnsafeSupplier<Long, Exception> weeklyVisitorsUnsafeSupplier) {

		try {
			weeklyVisitors = weeklyVisitorsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long weeklyVisitors;

	@Override
	public VisitFrequencyMetric clone() throws CloneNotSupportedException {
		return (VisitFrequencyMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof VisitFrequencyMetric)) {
			return false;
		}

		VisitFrequencyMetric visitFrequencyMetric =
			(VisitFrequencyMetric)object;

		return Objects.equals(toString(), visitFrequencyMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return VisitFrequencyMetricSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1242708710