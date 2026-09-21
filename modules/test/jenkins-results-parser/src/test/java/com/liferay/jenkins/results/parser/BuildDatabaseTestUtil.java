/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

/**
 * @author Calum Ragan
 */
public class BuildDatabaseTestUtil {

	public static BuildDatabase newBuildDatabaseWithPullRequest() {
		File buildDir = _newBuildDir();

		File buildDatabaseFile = new File(
			buildDir, BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON);

		JSONObject buildDatabaseJSONObject = new JSONObject();

		buildDatabaseJSONObject.put(
			"pull_requests",
			_newPullRequestsJSONObject(_index.getAndIncrement()));

		try {
			JenkinsResultsParserUtil.write(
				buildDatabaseFile, buildDatabaseJSONObject.toString());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return new DefaultBuildDatabase(buildDir);
	}

	private static File _newBuildDir() {
		try {
			File buildDir = File.createTempFile("build-database-", null);

			buildDir.delete();

			buildDir.mkdir();

			return buildDir;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static JSONObject _newPullRequestsJSONObject(int index) {
		String htmlURL =
			"https://github.com/test-owner-" + index + "/test-repository-" +
				index + "/pull/" + index;

		return new JSONObject(
		).put(
			htmlURL,
			new JSONObject(
			).put(
				"base",
				new JSONObject(
				).put(
					"ref", "master"
				).put(
					"repo",
					new JSONObject(
					).put(
						"name", "test-repository-" + index
					).put(
						"owner",
						new JSONObject(
						).put(
							"login", "test-owner-" + index
						)
					)
				).put(
					"user",
					new JSONObject(
					).put(
						"login", "test-owner-" + index
					)
				)
			).put(
				"html_url", htmlURL
			).put(
				"number", index
			).put(
				"statuses_url", htmlURL + "/statuses"
			)
		);
	}

	private static final AtomicInteger _index = new AtomicInteger(1);

}