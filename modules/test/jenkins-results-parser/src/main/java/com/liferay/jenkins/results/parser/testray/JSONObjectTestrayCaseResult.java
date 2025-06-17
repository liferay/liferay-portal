/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSONObjectTestrayCaseResult extends TestrayCaseResult {

	protected JSONObjectTestrayCaseResult(
		TestrayBuild testrayBuild, JSONObject jsonObject) {

		super(testrayBuild, jsonObject);
	}

	protected JSONObjectTestrayCaseResult(
		TestrayServer testrayServer, JSONObject jsonObject) {

		super(testrayServer, jsonObject);
	}

}