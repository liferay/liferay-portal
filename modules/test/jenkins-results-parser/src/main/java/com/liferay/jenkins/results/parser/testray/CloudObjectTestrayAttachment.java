/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import java.net.URL;

/**
 * @author Michael Hashimoto
 */
public class CloudObjectTestrayAttachment extends BaseTestrayAttachment {

	public CloudObjectTestrayAttachment(
		TestrayCaseResult testrayCaseResult, String name, String key) {

		super(testrayCaseResult, name, key);

		TestrayCloudBucket testrayCloudBucket =
			TestrayCloudBucket.getInstance();

		_testrayCloudObject = testrayCloudBucket.getTestrayCloudObject(key);
	}

	@Override
	public URL getURL() {
		if (_testrayCloudObject == null) {
			return null;
		}

		return _testrayCloudObject.getURL();
	}

	@Override
	public String getValue() {
		if (_testrayCloudObject == null) {
			return null;
		}

		return _testrayCloudObject.getValue();
	}

	private final TestrayCloudObject _testrayCloudObject;

}