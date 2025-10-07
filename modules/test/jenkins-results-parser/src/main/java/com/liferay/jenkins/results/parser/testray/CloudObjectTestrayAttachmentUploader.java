/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Michael Hashimoto
 */
public class CloudObjectTestrayAttachmentUploader
	extends BaseTestrayAttachmentUploader {

	@Override
	public File getPreparedFilesBaseDir() {
		String workspace = System.getenv("WORKSPACE");

		if (JenkinsResultsParserUtil.isNullOrEmpty(workspace)) {
			throw new RuntimeException("Please set WORKSPACE");
		}

		return new File(workspace, "testray/prepared_cloud_logs");
	}

	@Override
	public URL getTestrayServerLogsURL() {
		try {
			return new URL("https://storage.cloud.google.com/testray-results");
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	@Override
	public void upload() {
		if (_uploaded) {
			return;
		}

		prepareFiles();

		TopLevelBuildReport topLevelBuildReport = getTopLevelBuildReport();

		File preparedFilesBaseDir = getPreparedFilesBaseDir();

		TestrayCloudBucket testrayCloudBucket =
			TestrayCloudBucket.getInstance();

		for (File preparedFile : getPreparedFiles()) {
			TestrayCloudObject testrayCloudObject =
				testrayCloudBucket.createTestrayCloudObject(
					JenkinsResultsParserUtil.getPathRelativeTo(
						preparedFile, preparedFilesBaseDir),
					preparedFile);

			if (topLevelBuildReport != null) {
				topLevelBuildReport.addTestrayAttachmentURL(
					testrayCloudObject.getURL());
			}
		}

		uploadBuildReportTestrayAttachment();

		_uploaded = true;
	}

	protected CloudObjectTestrayAttachmentUploader(
		Build build, URL testrayServerURL) {

		super(build, testrayServerURL);
	}

	private boolean _uploaded;

}