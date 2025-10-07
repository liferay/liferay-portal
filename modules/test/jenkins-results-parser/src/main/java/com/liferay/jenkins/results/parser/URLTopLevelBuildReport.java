/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.testray.TestrayCloudObject;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.Date;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class URLTopLevelBuildReport extends BaseTopLevelBuildReport {

	@Override
	public JSONObject getBuildReportJSONObject() {
		if (_buildReportJSONObject != null) {
			return _buildReportJSONObject;
		}

		TestrayCloudObject buildReportTestrayCloudObject =
			getBuildReportTestrayCloudObject();

		if (buildReportTestrayCloudObject != null) {
			_buildReportJSONObject = new JSONObject(
				buildReportTestrayCloudObject.getValue());
		}

		if (_buildReportJSONObject == null) {
			_buildReportJSONObject = getJSONObjectFromURL(
				getBuildReportJSONUserContentURL());
		}

		if (_buildReportJSONObject == null) {
			_buildReportJSONObject = getJSONObjectFromURL(
				getBuildReportJSONTestrayURL());
		}

		initialize(_buildReportJSONObject);

		return _buildReportJSONObject;
	}

	@Override
	public Date getStartDate() {
		return new Date(_buildJSONObject.getLong("timestamp"));
	}

	protected URLTopLevelBuildReport(
		JSONObject buildJSONObject, JobReport jobReport) {

		super(buildJSONObject.getString("url"), jobReport);

		_buildJSONObject = buildJSONObject;
	}

	protected URLTopLevelBuildReport(String buildURLString) {
		super(buildURLString);

		_buildJSONObject = JenkinsAPIUtil.getAPIJSONObject(
			String.valueOf(getBuildURL()));
	}

	protected JSONObject getJSONObjectFromURL(URL url) {
		if (!JenkinsResultsParserUtil.exists(url)) {
			return null;
		}

		String urlString = String.valueOf(url);

		if (!urlString.endsWith(".gz")) {
			try {
				return JenkinsResultsParserUtil.toJSONObject(urlString);
			}
			catch (IOException ioException) {
				return null;
			}
		}

		File file = new File(
			System.getenv("WORKSPACE"),
			JenkinsResultsParserUtil.getDistinctTimeStamp() + ".gz");

		try {
			JenkinsResultsParserUtil.toFile(url, file);

			String fileContent = JenkinsResultsParserUtil.read(file);

			if (JenkinsResultsParserUtil.isNullOrEmpty(fileContent)) {
				return null;
			}

			return new JSONObject(fileContent);
		}
		catch (Exception exception) {
			return null;
		}
		finally {
			if (file.exists()) {
				JenkinsResultsParserUtil.delete(file);
			}
		}
	}

	private final JSONObject _buildJSONObject;
	private JSONObject _buildReportJSONObject;

}