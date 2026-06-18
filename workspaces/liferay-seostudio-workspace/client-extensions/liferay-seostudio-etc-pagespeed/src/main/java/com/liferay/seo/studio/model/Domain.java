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
		_domainHostname = jsonObject.optString("domainHostname", null);

		JSONObject seoStudioInstanceJSONObject = jsonObject.optJSONObject(
			"seoStudioInstance");

		if (seoStudioInstanceJSONObject != null) {
			_googlePageSpeedAPIKey = seoStudioInstanceJSONObject.optString(
				"googlePageSpeedAPIKey", null);
		}
		else {
			_googlePageSpeedAPIKey = null;
		}
	}

	public String getDomainHostname() {
		return _domainHostname;
	}

	public String getGooglePageSpeedAPIKey() {
		return _googlePageSpeedAPIKey;
	}

	private final String _domainHostname;
	private final String _googlePageSpeedAPIKey;

}