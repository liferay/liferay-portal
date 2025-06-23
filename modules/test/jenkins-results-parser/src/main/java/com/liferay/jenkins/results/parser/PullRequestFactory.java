/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PullRequestFactory {

	public static PullRequest newPullRequest(JSONObject jsonObject) {
		String gitHubURL = jsonObject.optString("html_url");

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitHubURL)) {
			throw new RuntimeException("Invalid Pull Request JSONObject");
		}

		PullRequest pullRequest = _pullRequests.get(gitHubURL);

		if (pullRequest != null) {
			return pullRequest;
		}

		pullRequest = new PullRequest(jsonObject);

		_pullRequests.put(gitHubURL, pullRequest);

		return pullRequest;
	}

	public static PullRequest newPullRequest(String gitHubURL) {
		return newPullRequest(gitHubURL, null);
	}

	public static PullRequest newPullRequest(String gitHubURL, Build build) {
		PullRequest pullRequest = _pullRequests.get(gitHubURL);

		if (pullRequest != null) {
			return pullRequest;
		}

		BuildDatabase buildDatabase = null;

		if (build != null) {
			buildDatabase = build.getBuildDatabase();
		}
		else {
			buildDatabase = BuildDatabaseUtil.getBuildDatabase();
		}

		if (buildDatabase.hasPullRequest(gitHubURL)) {
			pullRequest = buildDatabase.getPullRequest(gitHubURL);

			_pullRequests.put(gitHubURL, pullRequest);

			return pullRequest;
		}

		pullRequest = new PullRequest(gitHubURL);

		_pullRequests.put(gitHubURL, pullRequest);

		if (buildDatabase != null) {
			buildDatabase.putPullRequest(gitHubURL, pullRequest);
		}

		return pullRequest;
	}

	private static final int _MAX_CACHED_PULL_REQUESTS = 25;

	private static final Map<String, PullRequest> _pullRequests =
		new LinkedHashMap<String, PullRequest>() {

			@Override
			protected boolean removeEldestEntry(
				Map.Entry<String, PullRequest> eldest) {

				if (size() > _MAX_CACHED_PULL_REQUESTS) {
					return true;
				}

				return false;
			}

		};

}