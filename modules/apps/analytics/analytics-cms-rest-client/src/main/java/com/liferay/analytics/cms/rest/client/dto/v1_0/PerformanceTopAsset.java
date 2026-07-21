/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.dto.v1_0;

import com.liferay.analytics.cms.rest.client.function.UnsafeSupplier;
import com.liferay.analytics.cms.rest.client.serdes.v1_0.PerformanceTopAssetSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class PerformanceTopAsset implements Cloneable, Serializable {

	public static PerformanceTopAsset toDTO(String json) {
		return PerformanceTopAssetSerDes.toDTO(json);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setClassName(
		UnsafeSupplier<String, Exception> classNameUnsafeSupplier) {

		try {
			className = classNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String className;

	public Double getDownloads() {
		return downloads;
	}

	public void setDownloads(Double downloads) {
		this.downloads = downloads;
	}

	public void setDownloads(
		UnsafeSupplier<Double, Exception> downloadsUnsafeSupplier) {

		try {
			downloads = downloadsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double downloads;

	public Object getEmbedded() {
		return embedded;
	}

	public void setEmbedded(Object embedded) {
		this.embedded = embedded;
	}

	public void setEmbedded(
		UnsafeSupplier<Object, Exception> embeddedUnsafeSupplier) {

		try {
			embedded = embeddedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object embedded;

	public Double getEngagement() {
		return engagement;
	}

	public void setEngagement(Double engagement) {
		this.engagement = engagement;
	}

	public void setEngagement(
		UnsafeSupplier<Double, Exception> engagementUnsafeSupplier) {

		try {
			engagement = engagementUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double engagement;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Double getImpressions() {
		return impressions;
	}

	public void setImpressions(Double impressions) {
		this.impressions = impressions;
	}

	public void setImpressions(
		UnsafeSupplier<Double, Exception> impressionsUnsafeSupplier) {

		try {
			impressions = impressionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double impressions;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	public Trend getTrend() {
		return trend;
	}

	public void setTrend(Trend trend) {
		this.trend = trend;
	}

	public void setTrend(UnsafeSupplier<Trend, Exception> trendUnsafeSupplier) {
		try {
			trend = trendUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Trend trend;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String type;

	public Double getViews() {
		return views;
	}

	public void setViews(Double views) {
		this.views = views;
	}

	public void setViews(
		UnsafeSupplier<Double, Exception> viewsUnsafeSupplier) {

		try {
			views = viewsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double views;

	@Override
	public PerformanceTopAsset clone() throws CloneNotSupportedException {
		return (PerformanceTopAsset)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PerformanceTopAsset)) {
			return false;
		}

		PerformanceTopAsset performanceTopAsset = (PerformanceTopAsset)object;

		return Objects.equals(toString(), performanceTopAsset.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PerformanceTopAssetSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1614507002