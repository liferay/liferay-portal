/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.google.cloud.storage.Blob;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Hashimoto
 */
public class TestrayCloudObjectFactory {

	public static TestrayCloudObject newTestrayCloudObject(
		TestrayCloudBucket testrayCloudBucket, Blob blob) {

		if (blob == null) {
			return null;
		}

		String mapKey = JenkinsResultsParserUtil.combine(
			testrayCloudBucket.getName(), "/", blob.getName());

		TestrayCloudObject testrayCloudObject = _testrayCloudObjects.get(
			mapKey);

		if (testrayCloudObject != null) {
			testrayCloudObject.setBlob(blob);

			return testrayCloudObject;
		}

		testrayCloudObject = new TestrayCloudObject(testrayCloudBucket, blob);

		_testrayCloudObjects.put(mapKey, testrayCloudObject);

		return testrayCloudObject;
	}

	private static final Map<String, TestrayCloudObject> _testrayCloudObjects =
		new ConcurrentHashMap<>();

}