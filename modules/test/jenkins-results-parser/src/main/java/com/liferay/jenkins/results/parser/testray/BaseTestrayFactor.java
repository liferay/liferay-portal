/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestrayFactor implements TestrayFactor {

	@Override
	public Category getCategory() {
		return _category;
	}

	@Override
	public Long getId() {
		JSONObject jsonObject = getJSONObject();

		return jsonObject.getLong("id");
	}

	@Override
	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	@Override
	public Option getOption() {
		return _option;
	}

	@Override
	public TestrayServer getTestrayServer() {
		return _testrayServer;
	}

	protected BaseTestrayFactor(
		JSONObject jsonObject, TestrayServer testrayServer) {

		_jsonObject = jsonObject;
		_testrayServer = testrayServer;

		JSONObject categoryJSONObject = jsonObject.getJSONObject(
			"factorCategoryToFactors");

		_category = TestrayFactory.newTestrayFactorCategory(
			categoryJSONObject, _testrayServer);

		JSONObject optionJSONObject = jsonObject.optJSONObject(
			"factorOptionToFactors");

		if (optionJSONObject != null) {
			_option = TestrayFactory.newTestrayFactorOption(
				optionJSONObject, _testrayServer);
		}
		else {
			_option = null;
		}
	}

	protected BaseTestrayFactor(TestrayServer testrayServer) {
		_testrayServer = testrayServer;
	}

	private Category _category;
	private JSONObject _jsonObject;
	private Option _option;
	private final TestrayServer _testrayServer;

}