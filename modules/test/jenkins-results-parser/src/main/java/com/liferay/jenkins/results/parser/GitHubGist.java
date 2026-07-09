/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class GitHubGist {

	public GitHubGist(String gistId) {
		try {
			_jsonObject = JenkinsResultsParserUtil.toJSONObject(
				"https://api.github.com/gists/" + gistId);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Could not find gist https://gist.github.com/" + gistId,
				ioException);
		}
	}

	public String getContent() {
		JSONObject filesJSONObject = _jsonObject.getJSONObject("files");

		for (String key : filesJSONObject.keySet()) {
			JSONObject fileJSONObject = filesJSONObject.getJSONObject(key);

			return fileJSONObject.getString("content");
		}

		return null;
	}

	public Map<String, String> getEnvironmentVariables() {
		Map<String, String> environmentVariables = new HashMap<>();

		String content = getContent();

		for (String line : content.split("\\n")) {
			if (JenkinsResultsParserUtil.isNullOrEmpty(line) ||
				!line.contains("=")) {

				continue;
			}

			int x = line.indexOf("=");

			environmentVariables.put(
				line.substring(0, x), line.substring(x + 1));
		}

		return environmentVariables;
	}

	private final JSONObject _jsonObject;

}