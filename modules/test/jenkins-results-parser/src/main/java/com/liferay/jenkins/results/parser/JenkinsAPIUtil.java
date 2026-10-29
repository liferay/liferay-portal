/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class JenkinsAPIUtil {

	public static JSONObject getAPIJSONObject(String jenkinsURL) {
		return getAPIJSONObject(jenkinsURL, null);
	}

	public static JSONObject getAPIJSONObject(String jenkinsURL, String tree) {
		if (jenkinsURL == null) {
			return null;
		}

		URIBuilder uriBuilder = JenkinsResultsParserUtil.newURIBuilder(
			JenkinsResultsParserUtil.combine(
				JenkinsResultsParserUtil.getLocalURL(jenkinsURL), "/api/json"));

		if (tree != null) {
			uriBuilder.addParameter("tree", tree);
		}

		final String jenkinsAPIURL = uriBuilder.toString();

		Retryable<JSONObject> retryable = new Retryable<JSONObject>() {

			@Override
			public JSONObject execute() {
				try {
					return JenkinsResultsParserUtil.toJSONObject(
						jenkinsAPIURL, false);
				}
				catch (IOException ioException) {
					ioException.printStackTrace();

					String errorMessage =
						"Unable to get Jenkins API JSON object from " +
							jenkinsAPIURL;

					throw new RuntimeException(errorMessage, ioException);
				}
			}

		};

		return retryable.executeWithRetries();
	}

	public static Map<String, String> getBuildParameters(
		JSONObject buildJSONObject) {

		Map<String, String> buildParameters = new HashMap<>();

		JSONArray actionsJSONArray = buildJSONObject.getJSONArray("actions");

		for (int i = 0; i < actionsJSONArray.length(); i++) {
			Object actions = actionsJSONArray.get(i);

			if (actions == JSONObject.NULL) {
				continue;
			}

			JSONObject actionJSONObject = actionsJSONArray.getJSONObject(i);

			if (!actionJSONObject.has("parameters")) {
				continue;
			}

			JSONArray parametersJSONArray = actionJSONObject.getJSONArray(
				"parameters");

			for (int j = 0; j < parametersJSONArray.length(); j++) {
				JSONObject parameterJSONObject =
					parametersJSONArray.getJSONObject(j);

				buildParameters.put(
					parameterJSONObject.getString("name"),
					parameterJSONObject.getString("value"));
			}
		}

		return buildParameters;
	}

	public static JSONObject getLastCompletedBuildJSONObject(
		String jobURL, String tree) {

		URIBuilder uriBuilder = JenkinsResultsParserUtil.newURIBuilder(
			JenkinsResultsParserUtil.combine(
				JenkinsResultsParserUtil.getLocalURL(jobURL),
				"/lastCompletedBuild/api/json"));

		if (tree != null) {
			uriBuilder.addParameter("tree", tree);
		}

		try {
			return JenkinsResultsParserUtil.toJSONObject(
				uriBuilder.toString(), false);
		}
		catch (IOException ioException) {
			throw new RuntimeException("Unable to get build JSON", ioException);
		}
	}

	public static int getLastCompletedBuildNumber(String jobURL) {
		JSONObject lastCompletedBuildJSONObject =
			getLastCompletedBuildJSONObject(jobURL, "number");

		return lastCompletedBuildJSONObject.getInt("number");
	}

}