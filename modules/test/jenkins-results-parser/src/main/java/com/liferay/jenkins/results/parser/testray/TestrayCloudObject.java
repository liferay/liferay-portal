/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.google.cloud.storage.Blob;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.charset.StandardCharsets;

/**
 * @author Michael Hashimoto
 */
public class TestrayCloudObject {

	public void delete() {
		_blob.delete();
	}

	public void downloadTo(File file) {
		downloadTo(file, false);
	}

	public void downloadTo(File file, boolean replaceExisting) {
		if (replaceExisting || !file.exists()) {
			File parentDir = file.getParentFile();

			parentDir.mkdirs();

			System.out.println("Downloading " + getURL() + " to " + file);

			_blob.downloadTo(file.toPath());
		}
	}

	public boolean exists() {
		return _blob.exists();
	}

	public String getKey() {
		return _blob.getName();
	}

	public TestrayCloudBucket getTestrayCloudBucket() {
		return _testrayCloudBucket;
	}

	public URL getURL() {
		return _url;
	}

	public String getURLString() {
		return JenkinsResultsParserUtil.fixURL(String.valueOf(_url));
	}

	public String getValue() {
		if (!exists()) {
			return null;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			return new String(_blob.getContent(), StandardCharsets.UTF_8);
		}
		finally {
			long duration =
				JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

			System.out.println(
				JenkinsResultsParserUtil.combine(
					getURLString(), " in ",
					JenkinsResultsParserUtil.toDurationString(duration)));
		}
	}

	public void setBlob(Blob blob) {
		_blob = blob;
	}

	@Override
	public String toString() {
		return getURLString();
	}

	protected TestrayCloudObject(
		TestrayCloudBucket testrayCloudBucket, Blob blob) {

		_testrayCloudBucket = testrayCloudBucket;
		_blob = blob;

		try {
			_url = new URL(
				JenkinsResultsParserUtil.combine(
					testrayCloudBucket.getTestrayCloudBaseURL(), "/",
					getKey()));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private Blob _blob;
	private final TestrayCloudBucket _testrayCloudBucket;
	private final URL _url;

}