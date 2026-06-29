/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.display.context;

import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Map;

/**
 * @author Kiana Suetani
 */
public class PageSpeedChartsDisplayContext {

	public PageSpeedChartsDisplayContext(
		ObjectEntry seoStudioPageSpeedResultObjectEntry) {

		_seoStudioPageSpeedResultObjectEntry =
			seoStudioPageSpeedResultObjectEntry;
	}

	public Map<String, Object> getViewProps() {
		return HashMapBuilder.<String, Object>put(
			"initialResult", _getInitialResultJSONObject()
		).build();
	}

	private JSONObject _getInitialResultJSONObject() {
		if (_seoStudioPageSpeedResultObjectEntry == null) {
			return null;
		}

		Map<String, Object> properties =
			_seoStudioPageSpeedResultObjectEntry.getProperties();

		return JSONUtil.put(
			"accessibilityScore", properties.get("accessibilityScore")
		).put(
			"bestPracticesScore", properties.get("bestPracticesScore")
		).put(
			"dateCreated",
			_toISOString(_seoStudioPageSpeedResultObjectEntry.getDateCreated())
		).put(
			"dateModified",
			_toISOString(_seoStudioPageSpeedResultObjectEntry.getDateModified())
		).put(
			"errorMessage", properties.get("errorMessage")
		).put(
			"pagesErrored", properties.get("pagesErrored")
		).put(
			"pagesScanned", properties.get("pagesScanned")
		).put(
			"pagesTotal", properties.get("pagesTotal")
		).put(
			"performanceScore", properties.get("performanceScore")
		).put(
			"seoScore", properties.get("seoScore")
		);
	}

	private String _toISOString(Date date) {
		if (date == null) {
			return null;
		}

		Instant instant = date.toInstant();

		return DateTimeFormatter.ISO_INSTANT.format(instant);
	}

	private final ObjectEntry _seoStudioPageSpeedResultObjectEntry;

}