/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.model;

import org.json.JSONObject;

/**
 * @author Kiana Suetani
 */
public class Domain {

	public Domain(JSONObject jsonObject) {
		_hostname = jsonObject.optString("hostname", null);

		JSONObject seoStudioInstanceJSONObject = jsonObject.optJSONObject(
			"seoStudioInstance");

		if (seoStudioInstanceJSONObject != null) {
			_googlePageSpeedAPIKey = seoStudioInstanceJSONObject.optString(
				"googlePageSpeedAPIKey", null);
			_seoStudioInstanceId = seoStudioInstanceJSONObject.getLong("id");
		}
		else {
			_googlePageSpeedAPIKey = null;
			_seoStudioInstanceId = 0;
		}
	}

	public String getGooglePageSpeedAPIKey() {
		return _googlePageSpeedAPIKey;
	}

	public String getHostname() {
		return _hostname;
	}

	public long getSEOStudioInstanceId() {
		return _seoStudioInstanceId;
	}

	private final String _googlePageSpeedAPIKey;
	private final String _hostname;
	private final long _seoStudioInstanceId;

}