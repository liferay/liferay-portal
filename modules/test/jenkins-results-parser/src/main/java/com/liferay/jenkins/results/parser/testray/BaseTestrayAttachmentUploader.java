/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.BuildReportFactory;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TopLevelBuild;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestrayAttachmentUploader
	implements TestrayAttachmentUploader {

	@Override
	public TestrayAttachmentRecorder getTestrayAttachmentRecorder() {
		return _testrayAttachmentRecorder;
	}

	@Override
	public URL getTestrayServerURL() {
		return _testrayServerURL;
	}

	@Override
	public void prepareFiles() {
		if (_prepared) {
			return;
		}

		File preparedFilesBaseDir = getPreparedFilesBaseDir();

		JenkinsResultsParserUtil.delete(preparedFilesBaseDir);

		TestrayAttachmentRecorder testrayAttachmentRecorder =
			getTestrayAttachmentRecorder();

		testrayAttachmentRecorder.record();

		try {
			JenkinsResultsParserUtil.copy(
				testrayAttachmentRecorder.getRecordedFilesBaseDir(),
				preparedFilesBaseDir);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		for (File preparedFile : getPreparedFiles()) {
			String preparedFilePath = preparedFile.toString();

			if (preparedFilePath.contains("playwright-report") ||
				preparedFilePath.contains("trace.zip")) {

				continue;
			}

			String preparedFileName = preparedFile.getName();

			if (preparedFileName.endsWith(".html")) {
				try {
					String preparedFileContent = JenkinsResultsParserUtil.read(
						preparedFile);

					File preparedParentFile = preparedFile.getParentFile();

					preparedFileContent = preparedFileContent.replaceAll(
						"(screenshots/(?:after|before|screenshot)\\d+)\\.jpg",
						JenkinsResultsParserUtil.combine(
							String.valueOf(getTestrayServerLogsURL()), "/",
							testrayAttachmentRecorder.getRelativeBuildDirPath(),
							"/", preparedParentFile.getName(), "/$1.jpg.gz"));

					preparedFileContent = preparedFileContent.replace(
						"https://cdn.alloyui.com/3.1.0/",
						"https://cdn.jsdelivr.net/npm/alloy-ui@3.1.0/build/");

					JenkinsResultsParserUtil.write(
						preparedFile, preparedFileContent);
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}

			_convertToGzipFile(preparedFile);
		}

		_prepared = true;
	}

	protected BaseTestrayAttachmentUploader(Build build, URL testrayServerURL) {
		if (build == null) {
			throw new RuntimeException("Please set a build");
		}

		_build = build;

		_testrayAttachmentRecorder =
			TestrayFactory.newTestrayAttachmentRecorder(build);

		_testrayServerURL = testrayServerURL;
	}

	protected Build getBuild() {
		return _build;
	}

	protected URL getBuildReportTestrayAttachmentURL() {
		TestrayCloudBucket testrayCloudBucket =
			TestrayCloudBucket.getInstance();

		TestrayAttachmentRecorder testrayAttachmentRecorder =
			getTestrayAttachmentRecorder();

		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					testrayCloudBucket.getTestrayCloudBaseURL(), "/",
					testrayAttachmentRecorder.getRelativeBuildDirPath(), "/",
					"build-report.json.gz"));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	protected List<File> getPreparedFiles() {
		return JenkinsResultsParserUtil.findFiles(
			getPreparedFilesBaseDir(), ".*");
	}

	protected TopLevelBuildReport getTopLevelBuildReport() {
		if (_topLevelBuildReport != null) {
			return _topLevelBuildReport;
		}

		Build build = getBuild();

		if (!(build instanceof TopLevelBuild)) {
			return null;
		}

		TestrayCloudObject testrayCloudObject =
			_getBuildReportTestrayCloudObject();

		if (testrayCloudObject != null) {
			_topLevelBuildReport = BuildReportFactory.newTopLevelBuildReport(
				new JSONObject(testrayCloudObject.getValue()));

			return _topLevelBuildReport;
		}

		_topLevelBuildReport = BuildReportFactory.newTopLevelBuildReport(
			(TopLevelBuild)build);

		return _topLevelBuildReport;
	}

	protected void uploadBuildReportTestrayAttachment() {
		TopLevelBuildReport topLevelBuildReport = getTopLevelBuildReport();

		if (topLevelBuildReport == null) {
			return;
		}

		topLevelBuildReport.addTestrayAttachmentURL(
			getBuildReportTestrayAttachmentURL());

		JSONObject buildReportJSONObject =
			topLevelBuildReport.getBuildReportJSONObject();

		TestrayAttachmentRecorder testrayAttachmentRecorder =
			getTestrayAttachmentRecorder();

		String relativeBuildDirPath =
			testrayAttachmentRecorder.getRelativeBuildDirPath();

		File buildReportFile = new File(
			getPreparedFilesBaseDir(),
			relativeBuildDirPath + "/build-report.json");

		try {
			JenkinsResultsParserUtil.write(
				buildReportFile, buildReportJSONObject.toString());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		File buildReportGzipFile = _convertToGzipFile(buildReportFile);

		TestrayCloudBucket testrayCloudBucket =
			TestrayCloudBucket.getInstance();

		testrayCloudBucket.createTestrayCloudObject(
			relativeBuildDirPath + "/" + buildReportGzipFile.getName(),
			buildReportGzipFile);
	}

	private File _convertToGzipFile(File file) {
		File gzipFile = new File(file.getParent(), file.getName() + ".gz");

		JenkinsResultsParserUtil.gzip(file, gzipFile);

		JenkinsResultsParserUtil.delete(file);

		return gzipFile;
	}

	private TestrayCloudObject _getBuildReportTestrayCloudObject() {
		Build build = getBuild();

		if (!(build instanceof TopLevelBuild)) {
			return null;
		}

		TopLevelBuild topLevelBuild = (TopLevelBuild)build;

		StringBuilder sb = new StringBuilder();

		sb.append(
			JenkinsResultsParserUtil.toDateString(
				new Date(topLevelBuild.getStartTime()), "yyyy-MM",
				"America/Los_Angeles"));
		sb.append("/");

		JenkinsMaster jenkinsMaster = topLevelBuild.getJenkinsMaster();

		sb.append(jenkinsMaster.getName());

		sb.append("/");
		sb.append(topLevelBuild.getJobName());
		sb.append("/");
		sb.append(topLevelBuild.getBuildNumber());
		sb.append("/build-report.json.gz");

		TestrayCloudBucket testrayCloudBucket =
			TestrayCloudBucket.getInstance();

		return testrayCloudBucket.getTestrayCloudObject(sb.toString());
	}

	private final Build _build;
	private boolean _prepared;
	private final TestrayAttachmentRecorder _testrayAttachmentRecorder;
	private final URL _testrayServerURL;
	private TopLevelBuildReport _topLevelBuildReport;

}