/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayCaseType {

	public static final String[] FIELD_NAMES = {
		"dateCreated", "dateModified", "id", "name"
	};

	public Long getId() {
		return _jsonObject.getLong("id");
	}

	public String getName() {
		return _jsonObject.getString("name");
	}

	public TestrayServer getTestrayServer() {
		return _testrayServer;
	}

	protected TestrayCaseType(
		TestrayServer testrayServer, JSONObject jsonObject) {

		_testrayServer = testrayServer;
		_jsonObject = jsonObject;
	}

	private final JSONObject _jsonObject;
	private final TestrayServer _testrayServer;

}