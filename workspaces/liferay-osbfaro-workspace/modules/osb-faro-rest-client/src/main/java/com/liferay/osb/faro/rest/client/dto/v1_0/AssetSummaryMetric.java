/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AssetSummaryMetricSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class AssetSummaryMetric implements Cloneable, Serializable {

	public static AssetSummaryMetric toDTO(String json) {
		return AssetSummaryMetricSerDes.toDTO(json);
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

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public void setAssetType(
		UnsafeSupplier<String, Exception> assetTypeUnsafeSupplier) {

		try {
			assetType = assetTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String assetType;

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

	public Double getDownloadsTrendPercentage() {
		return downloadsTrendPercentage;
	}

	public void setDownloadsTrendPercentage(Double downloadsTrendPercentage) {
		this.downloadsTrendPercentage = downloadsTrendPercentage;
	}

	public void setDownloadsTrendPercentage(
		UnsafeSupplier<Double, Exception>
			downloadsTrendPercentageUnsafeSupplier) {

		try {
			downloadsTrendPercentage =
				downloadsTrendPercentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double downloadsTrendPercentage;

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

	public Double getImpressionsTrendPercentage() {
		return impressionsTrendPercentage;
	}

	public void setImpressionsTrendPercentage(
		Double impressionsTrendPercentage) {

		this.impressionsTrendPercentage = impressionsTrendPercentage;
	}

	public void setImpressionsTrendPercentage(
		UnsafeSupplier<Double, Exception>
			impressionsTrendPercentageUnsafeSupplier) {

		try {
			impressionsTrendPercentage =
				impressionsTrendPercentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double impressionsTrendPercentage;

	public Double getReads() {
		return reads;
	}

	public void setReads(Double reads) {
		this.reads = reads;
	}

	public void setReads(
		UnsafeSupplier<Double, Exception> readsUnsafeSupplier) {

		try {
			reads = readsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double reads;

	public Double getReadsTrendPercentage() {
		return readsTrendPercentage;
	}

	public void setReadsTrendPercentage(Double readsTrendPercentage) {
		this.readsTrendPercentage = readsTrendPercentage;
	}

	public void setReadsTrendPercentage(
		UnsafeSupplier<Double, Exception> readsTrendPercentageUnsafeSupplier) {

		try {
			readsTrendPercentage = readsTrendPercentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double readsTrendPercentage;

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

	public Double getViewsTrendPercentage() {
		return viewsTrendPercentage;
	}

	public void setViewsTrendPercentage(Double viewsTrendPercentage) {
		this.viewsTrendPercentage = viewsTrendPercentage;
	}

	public void setViewsTrendPercentage(
		UnsafeSupplier<Double, Exception> viewsTrendPercentageUnsafeSupplier) {

		try {
			viewsTrendPercentage = viewsTrendPercentageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Double viewsTrendPercentage;

	@Override
	public AssetSummaryMetric clone() throws CloneNotSupportedException {
		return (AssetSummaryMetric)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetSummaryMetric)) {
			return false;
		}

		AssetSummaryMetric assetSummaryMetric = (AssetSummaryMetric)object;

		return Objects.equals(toString(), assetSummaryMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AssetSummaryMetricSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-2092426612