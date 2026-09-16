/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSUnitTestClassResult extends BaseTestClassResult {

	@Override
	public String getTestClassResultKey() {
		JSONObject suiteJSONObject = getSuiteJSONObject();

		JSONArray casesJSONArray = suiteJSONObject.optJSONArray("cases");

		if ((casesJSONArray == null) || casesJSONArray.isEmpty()) {
			return suiteJSONObject.getString("name");
		}

		JSONObject caseJSONObject = casesJSONArray.getJSONObject(0);

		return caseJSONObject.getString("className");
	}

	protected JSUnitTestClassResult(Build build, JSONObject suiteJSONObject) {
		super(build, suiteJSONObject);
	}

}