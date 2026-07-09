/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Retryable;

import java.io.IOException;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class RunTestrayFactor extends BaseTestrayFactor {

	@Override
	public Category getCategory() {
		Option option = getOption();

		if (option == null) {
			return null;
		}

		return option.getCategory();
	}

	@Override
	public synchronized JSONObject getJSONObject() {
		if (_jsonObject != null) {
			return _jsonObject;
		}

		JSONObject baseJSONObject = super.getJSONObject();

		if (baseJSONObject != null) {
			_jsonObject = baseJSONObject;

			return _jsonObject;
		}

		Category category = getCategory();
		Option option = getOption();

		if ((category == null) || (option == null)) {
			return null;
		}

		TestrayRun testrayRun = getTestrayRun();

		final String filterString = JenkinsResultsParserUtil.combine(
			"r_factorCategoryToFactors_c_factorCategoryId eq '",
			String.valueOf(category.getId()),
			"' and r_factorOptionToFactors_c_factorOptionId eq '",
			String.valueOf(option.getId()), "' and r_runToFactors_c_runId eq '",
			String.valueOf(testrayRun.getId()), "'");

		final JSONObject postRequestJSONObject = new JSONObject();

		postRequestJSONObject.put(
			"r_factorCategoryToFactors_c_factorCategoryId", category.getId()
		).put(
			"r_factorOptionToFactors_c_factorOptionId", option.getId()
		).put(
			"r_runToFactors_c_runId", testrayRun.getId()
		);

		Retryable<JSONObject> retryable = new Retryable<JSONObject>(
			true, 3, 5, true) {

			@Override
			public JSONObject execute() {
				TestrayServer testrayServer = _testrayBuild.getTestrayServer();

				try {
					JSONObject existingJSONObject = new JSONObject(
						testrayServer.requestGet(
							"/o/c/factors?filter=" +
								URLEncoder.encode(filterString, "UTF-8")));

					JSONArray existingItemsJSONArray =
						existingJSONObject.optJSONArray("items");

					if ((existingItemsJSONArray != null) &&
						!existingItemsJSONArray.isEmpty()) {

						return existingItemsJSONArray.getJSONObject(0);
					}

					return new JSONObject(
						testrayServer.requestPost(
							"/o/c/factors", postRequestJSONObject.toString()));
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}

		};

		_jsonObject = retryable.executeWithRetries();

		return _jsonObject;
	}

	@Override
	public Option getOption() {
		if (_option != null) {
			return _option;
		}

		return super.getOption();
	}

	public TestrayRun getTestrayRun() {
		return _testrayRun;
	}

	protected RunTestrayFactor(JSONObject jsonObject, TestrayRun testrayRun) {
		super(jsonObject, testrayRun.getTestrayServer());

		_jsonObject = jsonObject;
		_testrayRun = testrayRun;

		_option = super.getOption();
		_testrayBuild = testrayRun.getTestrayBuild();
	}

	protected RunTestrayFactor(
		TestrayFactor.Option option, TestrayRun testrayRun) {

		super(testrayRun.getTestrayServer());

		_option = option;
		_testrayRun = testrayRun;

		_testrayBuild = testrayRun.getTestrayBuild();
	}

	private JSONObject _jsonObject;
	private final Option _option;
	private final TestrayBuild _testrayBuild;
	private final TestrayRun _testrayRun;

}