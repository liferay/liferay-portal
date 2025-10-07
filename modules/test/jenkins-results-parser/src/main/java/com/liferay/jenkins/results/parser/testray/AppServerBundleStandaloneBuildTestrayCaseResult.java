/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.JenkinsConsoleTextLoader;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Michael Hashimoto
 */
public class AppServerBundleStandaloneBuildTestrayCaseResult
	extends BaseStandaloneBuildTestrayCaseResult {

	public AppServerBundleStandaloneBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport,
		String appServerType) {

		super(testrayBuild, topLevelBuildReport);

		_appServerType = appServerType;
	}

	@Override
	public BuildReport getBuildReport() {
		TopLevelBuildReport topLevelBuildReport = getTopLevelBuildReport();

		return topLevelBuildReport.getDownstreamBuildReport(_getAxisName());
	}

	@Override
	public String getComponentName() {
		String componentName = super.getComponentName();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(componentName)) {
			return componentName;
		}

		return "App Server Bundle Builder";
	}

	@Override
	public String getName() {
		return "App Server Bundle Builder > " + _appServerType;
	}

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments =
			super.getTestrayAttachments();

		testrayAttachments.add(_getJenkinsConsoleTestrayAttachment());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	@Override
	protected String getBatchName() {
		return "app-server-bundle-builder";
	}

	@Override
	protected String getFileName() {
		TopLevelBuildReport topLevelBuildReport = getTopLevelBuildReport();

		JenkinsMaster jenkinsMaster = topLevelBuildReport.getJenkinsMaster();

		return JenkinsResultsParserUtil.combine(
			"TESTS-", jenkinsMaster.getName(), "_",
			topLevelBuildReport.getJobName(), "_",
			String.valueOf(topLevelBuildReport.getBuildNumber()), "_",
			getBatchName(), "_", _appServerType, ".xml");
	}

	private String _getAxisName() {
		return getBatchName() + "/" + _appServerType;
	}

	private TestrayAttachment _getJenkinsConsoleTestrayAttachment() {
		String name = "Jenkins Console";
		String key = _getAxisName() + "/jenkins-console.txt.gz";

		TestrayAttachment testrayAttachment = getTestrayAttachment(
			getBuildReport(), name, key);

		if (testrayAttachment != null) {
			return testrayAttachment;
		}

		final BuildReport buildReport = getBuildReport();

		if (buildReport == null) {
			return null;
		}

		TestrayCloudBucket testrayCloudBucket =
			TestrayCloudBucket.getInstance();

		String testrayCloudObjectPath = getTopLevelBuildURLPath() + "/" + key;

		TestrayCloudObject testrayCloudObject =
			testrayCloudBucket.getTestrayCloudObject(testrayCloudObjectPath);

		if (testrayCloudObject != null) {
			return new DefaultTestrayAttachment(
				this, name, testrayCloudObjectPath,
				testrayCloudObject.getURL());
		}

		return uploadTestrayAttachment(
			name, testrayCloudObjectPath,
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File jenkinsConsoleFile = new File(
						getTestrayUploadBaseDir(), "jenkins-console.txt");
					File jenkinsConsoleGzFile = new File(
						getTestrayUploadBaseDir(), "jenkins-console.txt.gz");

					try {
						String buildURL = String.valueOf(
							buildReport.getBuildURL());

						JenkinsConsoleTextLoader jenkinsConsoleTextLoader =
							JenkinsConsoleTextLoader.getInstance(buildURL);

						JenkinsResultsParserUtil.write(
							jenkinsConsoleFile,
							jenkinsConsoleTextLoader.getConsoleText());

						JenkinsResultsParserUtil.gzip(
							jenkinsConsoleFile, jenkinsConsoleGzFile);
					}
					catch (IOException ioException) {
						throw new RuntimeException(ioException);
					}
					finally {
						JenkinsResultsParserUtil.delete(jenkinsConsoleFile);
					}

					if (jenkinsConsoleGzFile.exists()) {
						return jenkinsConsoleGzFile;
					}

					return null;
				}

			});
	}

	private final String _appServerType;

}