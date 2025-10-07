/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.scancode;

import com.google.cloud.storage.Blob;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brittney Nguyen
 */
public class ScanCodeCloudObjectFactory {

	public static ScanCodeCloudObject newScanCodeCloudObject(
		Blob blob, ScanCodeCloudBucket scanCodeCloudBucket) {

		if (blob == null) {
			return null;
		}

		String mapKey = JenkinsResultsParserUtil.combine(
			scanCodeCloudBucket.getName(), "/", blob.getName());

		if (_scanCodeCloudObjects.containsKey(mapKey)) {
			return _scanCodeCloudObjects.get(mapKey);
		}

		ScanCodeCloudObject scanCodeCloudObject = new ScanCodeCloudObject(
			blob, scanCodeCloudBucket);

		_scanCodeCloudObjects.put(mapKey, scanCodeCloudObject);

		return scanCodeCloudObject;
	}

	private static final Map<String, ScanCodeCloudObject>
		_scanCodeCloudObjects = new HashMap<>();

}