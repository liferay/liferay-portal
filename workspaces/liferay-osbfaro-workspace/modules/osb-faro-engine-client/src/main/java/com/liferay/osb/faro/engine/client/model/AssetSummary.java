/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marcos Martins
 */
public class AssetSummary {

	public List<Map<String, Object>> getAssetCategories() {
		return _assetCategories;
	}

	public String getAssetId() {
		return _assetId;
	}

	public List<Map<String, Object>> getAssetTags() {
		return _assetTags;
	}

	public String getAssetTitle() {
		return _assetTitle;
	}

	public String getAssetType() {
		return _assetType;
	}

	public List<Map<String, Object>> getAssetVocabularies() {
		return _assetVocabularies;
	}

	public Metric getDownloadsMetric() {
		return _downloadsMetric;
	}

	@JsonProperty("_embedded")
	public Map<String, Object> getEmbeddedResources() {
		return _embeddedResources;
	}

	public Metric getImpressionsMetric() {
		return _impressionsMetric;
	}

	public String getMimeType() {
		return _mimeType;
	}

	public String getObjectType() {
		return _objectType;
	}

	public Metric getReadsMetric() {
		return _readsMetric;
	}

	public Metric getViewsMetric() {
		return _viewsMetric;
	}

	public void setAssetCategories(List<Map<String, Object>> assetCategories) {
		_assetCategories = assetCategories;
	}

	public void setAssetId(String assetId) {
		_assetId = assetId;
	}

	public void setAssetTags(List<Map<String, Object>> assetTags) {
		_assetTags = assetTags;
	}

	public void setAssetTitle(String assetTitle) {
		_assetTitle = assetTitle;
	}

	public void setAssetType(String assetType) {
		_assetType = assetType;
	}

	public void setAssetVocabularies(
		List<Map<String, Object>> assetVocabularies) {

		_assetVocabularies = assetVocabularies;
	}

	public void setDownloadsMetric(Metric downloadsMetric) {
		_downloadsMetric = downloadsMetric;
	}

	public void setEmbeddedResources(Map<String, Object> embeddedResources) {
		_embeddedResources = embeddedResources;
	}

	public void setImpressionsMetric(Metric impressionsMetric) {
		_impressionsMetric = impressionsMetric;
	}

	public void setMimeType(String mimeType) {
		_mimeType = mimeType;
	}

	public void setObjectType(String objectType) {
		_objectType = objectType;
	}

	public void setReadsMetric(Metric readsMetric) {
		_readsMetric = readsMetric;
	}

	public void setViewsMetric(Metric viewsMetric) {
		_viewsMetric = viewsMetric;
	}

	private List<Map<String, Object>> _assetCategories;
	private String _assetId;
	private List<Map<String, Object>> _assetTags;
	private String _assetTitle;
	private String _assetType;
	private List<Map<String, Object>> _assetVocabularies;
	private Metric _downloadsMetric;
	private Map<String, Object> _embeddedResources = new HashMap<>();
	private Metric _impressionsMetric;
	private String _mimeType;
	private String _objectType;
	private Metric _readsMetric;
	private Metric _viewsMetric;

}