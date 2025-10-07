/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.scancode;

import com.liferay.jenkins.results.parser.CloudBucketUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public class MapDevelopToDeployScanCodePipeline extends BaseScanCodePipeline {

	@Override
	public void execute() throws IOException, TimeoutException {
		invokeScan(getJSONObject());

		waitForScan(_pipelineName);

		checkComplianceAlerts("ERROR");

		checkComplianceAlerts("WARNING");

		downloadResultFiles();

		sendSlackNotification(getCloudBucketURL());
	}

	public JSONObject getJSONObject() throws IOException {
		String tomcatURL = getTomcatURL();

		if (JenkinsResultsParserUtil.isNullOrEmpty(tomcatURL)) {
			throw new NullPointerException("Tomcat URL is null");
		}

		List<String> inputURLs = new ArrayList<>();

		inputURLs.add(tomcatURL + "#to");
		inputURLs.add(getReleaseTarballLink());
		inputURLs.add(
			JenkinsResultsParserUtil.getBuildProperty("scancode.tar.gz.url"));
		inputURLs.add(
			JenkinsResultsParserUtil.getBuildProperty(
				"scancode.config.file.url"));
		inputURLs.add(
			JenkinsResultsParserUtil.getBuildProperty(
				"scancode.policies.file.url"));

		String portalReleaseVersion =
			JenkinsResultsParserUtil.getBuildParameter(
				_buildURL, "TEST_PORTAL_RELEASE_VERSION");
		SimpleDateFormat simpleDateFormat = getSimpleDateFormat();

		return new JSONObject(
		).put(
			"execute_now", true
		).put(
			"input_urls", inputURLs
		).put(
			"labels", getLabels(new String[] {portalReleaseVersion})
		).put(
			"name",
			JenkinsResultsParserUtil.combine(
				portalReleaseVersion, " Scan-",
				simpleDateFormat.format(new Date()))
		).put(
			"pipeline", "map_deploy_to_develop:Java,Javascript"
		);
	}

	public String getReleaseTarballLink() {
		StringBuilder sb = new StringBuilder();

		sb.append("https://github.com/");
		sb.append(
			JenkinsResultsParserUtil.getBuildParameter(
				_buildURL, "TEST_PORTAL_USER_NAME"));
		sb.append("/liferay-portal-ee/archive/");
		sb.append(
			JenkinsResultsParserUtil.getBuildParameter(
				_buildURL, "TEST_PORTAL_RELEASE_GIT_ID"));
		sb.append(".tar.gz");
		sb.append("#from");

		return sb.toString();
	}

	public String getTomcatURL() {
		String tomcatURL = JenkinsResultsParserUtil.getBuildParameter(
			_buildURL, "TEST_PORTAL_RELEASE_TOMCAT_URL");

		if (!tomcatURL.matches(_GCP_URL_REGEX + ".*")) {
			return tomcatURL;
		}

		tomcatURL = tomcatURL.replaceAll(_GCP_URL_REGEX, "gs://");

		try {
			String credentialsFile = JenkinsResultsParserUtil.getBuildProperty(
				"google.application.crendential.file[jenkins]");

			return CloudBucketUtil.getSignedURL(15, credentialsFile, tomcatURL);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	protected MapDevelopToDeployScanCodePipeline(
		String buildURL, String pipelineName, String releaseBuildURL) {

		super(buildURL, pipelineName, releaseBuildURL);

		_buildURL = buildURL;
		_pipelineName = pipelineName;
		_releaseBuildURL = releaseBuildURL;
	}

	private static final String _GCP_URL_REGEX =
		"https:\\/\\/storage.(cloud\\.google\\.com|googleapis\\.com)\\/";

	private final String _buildURL;
	private final String _pipelineName;
	private final String _releaseBuildURL;

}