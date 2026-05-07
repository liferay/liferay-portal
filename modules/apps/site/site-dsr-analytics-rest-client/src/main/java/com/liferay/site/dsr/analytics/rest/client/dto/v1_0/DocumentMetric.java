/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.dto.v1_0;

import com.liferay.site.dsr.analytics.rest.client.function.UnsafeSupplier;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.DocumentMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class DocumentMetric implements Cloneable, Serializable {

	public static DocumentMetric toDTO(String json) {
		return DocumentMetricSerDes.toDTO(json);
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public void setAssetId(
		UnsafeSupplier<String, Exception> assetIdUnsafeSupplier) {

		try {
			assetId = assetIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String assetId;

	public String getAssetTitle() {
		return assetTitle;
	}

	public void setAssetTitle(String assetTitle) {
		this.assetTitle = assetTitle;
	}

	public void setAssetTitle(
		UnsafeSupplier<String, Exception> assetTitleUnsafeSupplier) {

		try {
			assetTitle = assetTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String assetTitle;

	public MetricValue getCommentsMetric() {
		return commentsMetric;
	}

	public void setCommentsMetric(MetricValue commentsMetric) {
		this.commentsMetric = commentsMetric;
	}

	public void setCommentsMetric(
		UnsafeSupplier<MetricValue, Exception> commentsMetricUnsafeSupplier) {

		try {
			commentsMetric = commentsMetricUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MetricValue commentsMetric;

	public MetricValue getDownloadsMetric() {
		return downloadsMetric;
	}

	public void setDownloadsMetric(MetricValue downloadsMetric) {
		this.downloadsMetric = downloadsMetric;
	}

	public void setDownloadsMetric(
		UnsafeSupplier<MetricValue, Exception> downloadsMetricUnsafeSupplier) {

		try {
			downloadsMetric = downloadsMetricUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MetricValue downloadsMetric;

	public MetricValue getImpressionMadeMetric() {
		return impressionMadeMetric;
	}

	public void setImpressionMadeMetric(MetricValue impressionMadeMetric) {
		this.impressionMadeMetric = impressionMadeMetric;
	}

	public void setImpressionMadeMetric(
		UnsafeSupplier<MetricValue, Exception>
			impressionMadeMetricUnsafeSupplier) {

		try {
			impressionMadeMetric = impressionMadeMetricUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MetricValue impressionMadeMetric;

	public MetricValue getLastViewedMetric() {
		return lastViewedMetric;
	}

	public void setLastViewedMetric(MetricValue lastViewedMetric) {
		this.lastViewedMetric = lastViewedMetric;
	}

	public void setLastViewedMetric(
		UnsafeSupplier<MetricValue, Exception> lastViewedMetricUnsafeSupplier) {

		try {
			lastViewedMetric = lastViewedMetricUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MetricValue lastViewedMetric;

	public MetricValue getRatingsMetric() {
		return ratingsMetric;
	}

	public void setRatingsMetric(MetricValue ratingsMetric) {
		this.ratingsMetric = ratingsMetric;
	}

	public void setRatingsMetric(
		UnsafeSupplier<MetricValue, Exception> ratingsMetricUnsafeSupplier) {

		try {
			ratingsMetric = ratingsMetricUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MetricValue ratingsMetric;

	public String[] getUrls() {
		return urls;
	}

	public void setUrls(String[] urls) {
		this.urls = urls;
	}

	public void setUrls(
		UnsafeSupplier<String[], Exception> urlsUnsafeSupplier) {

		try {
			urls = urlsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] urls;

	public MetricValue getUsersInvolvedMetric() {
		return usersInvolvedMetric;
	}

	public void setUsersInvolvedMetric(MetricValue usersInvolvedMetric) {
		this.usersInvolvedMetric = usersInvolvedMetric;
	}

	public void setUsersInvolvedMetric(
		UnsafeSupplier<MetricValue, Exception>
			usersInvolvedMetricUnsafeSupplier) {

		try {
			usersInvolvedMetric = usersInvolvedMetricUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MetricValue usersInvolvedMetric;

	@Override
	public DocumentMetric clone() throws CloneNotSupportedException {
		return (DocumentMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DocumentMetric)) {
			return false;
		}

		DocumentMetric documentMetric = (DocumentMetric)object;

		return Objects.equals(toString(), documentMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DocumentMetricSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-862070494