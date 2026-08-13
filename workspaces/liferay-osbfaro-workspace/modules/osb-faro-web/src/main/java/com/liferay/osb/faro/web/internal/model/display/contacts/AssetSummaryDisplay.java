/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.osb.faro.engine.client.model.AssetSummary;
import com.liferay.osb.faro.engine.client.model.Metric;

import java.util.List;
import java.util.Map;

/**
 * @author Marcos Martins
 */
public class AssetSummaryDisplay {

	public AssetSummaryDisplay(AssetSummary assetSummary) {
		_assetCategories = assetSummary.getAssetCategories();
		_assetId = assetSummary.getAssetId();
		_assetTags = assetSummary.getAssetTags();
		_assetTitle = assetSummary.getAssetTitle();
		_assetType = assetSummary.getAssetType();
		_assetVocabularies = assetSummary.getAssetVocabularies();
		_downloadsMetric = assetSummary.getDownloadsMetric();
		_impressionsMetric = assetSummary.getImpressionsMetric();
		_mimeType = assetSummary.getMimeType();
		_objectType = assetSummary.getObjectType();
		_viewsMetric = assetSummary.getViewsMetric();
	}

	private final List<Map<String, Object>> _assetCategories;

	@JsonProperty("id")
	private final String _assetId;

	private final List<Map<String, Object>> _assetTags;
	private final String _assetTitle;
	private final String _assetType;
	private final List<Map<String, Object>> _assetVocabularies;
	private final Metric _downloadsMetric;
	private final Metric _impressionsMetric;
	private final String _mimeType;
	private final String _objectType;
	private final Metric _viewsMetric;

}