/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.dto.v1_0;

import com.liferay.analytics.reports.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.reports.rest.client.serdes.v1_0.AppearsOnHistogramSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class AppearsOnHistogram implements Cloneable, Serializable {

	public static AppearsOnHistogram toDTO(String json) {
		return AppearsOnHistogramSerDes.toDTO(json);
	}

	public String getCanonicalUrl() {
		return canonicalUrl;
	}

	public void setCanonicalUrl(String canonicalUrl) {
		this.canonicalUrl = canonicalUrl;
	}

	public void setCanonicalUrl(
		UnsafeSupplier<String, Exception> canonicalUrlUnsafeSupplier) {

		try {
			canonicalUrl = canonicalUrlUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String canonicalUrl;

	public Metric[] getMetrics() {
		return metrics;
	}

	public void setMetrics(Metric[] metrics) {
		this.metrics = metrics;
	}

	public void setMetrics(
		UnsafeSupplier<Metric[], Exception> metricsUnsafeSupplier) {

		try {
			metrics = metricsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Metric[] metrics;

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public void setPageTitle(
		UnsafeSupplier<String, Exception> pageTitleUnsafeSupplier) {

		try {
			pageTitle = pageTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String pageTitle;

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public void setTotal(
		UnsafeSupplier<Double, Exception> totalUnsafeSupplier) {

		try {
			total = totalUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double total;

	public Double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;
	}

	public void setTotalValue(
		UnsafeSupplier<Double, Exception> totalValueUnsafeSupplier) {

		try {
			totalValue = totalValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double totalValue;

	@Override
	public AppearsOnHistogram clone() throws CloneNotSupportedException {
		return (AppearsOnHistogram)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AppearsOnHistogram)) {
			return false;
		}

		AppearsOnHistogram appearsOnHistogram = (AppearsOnHistogram)object;

		return Objects.equals(toString(), appearsOnHistogram.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AppearsOnHistogramSerDes.toJSON(this);
	}

}