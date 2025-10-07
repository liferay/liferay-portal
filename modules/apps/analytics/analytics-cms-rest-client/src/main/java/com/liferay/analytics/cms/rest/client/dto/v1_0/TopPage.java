/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.dto.v1_0;

import com.liferay.analytics.cms.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.cms.rest.client.serdes.v1_0.TopPageSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class TopPage implements Cloneable, Serializable {

	public static TopPage toDTO(String json) {
		return TopPageSerDes.toDTO(json);
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

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public void setSiteName(
		UnsafeSupplier<String, Exception> siteNameUnsafeSupplier) {

		try {
			siteName = siteNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String siteName;

	@Override
	public TopPage clone() throws CloneNotSupportedException {
		return (TopPage)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TopPage)) {
			return false;
		}

		TopPage topPage = (TopPage)object;

		return Objects.equals(toString(), topPage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TopPageSerDes.toJSON(this);
	}

}