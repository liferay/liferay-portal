/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.scancode;

import com.google.cloud.storage.Blob;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

		return _scanCodeCloudObjects.computeIfAbsent(
			mapKey, key -> new ScanCodeCloudObject(blob, scanCodeCloudBucket));
	}

	private static final Map<String, ScanCodeCloudObject>
		_scanCodeCloudObjects = new ConcurrentHashMap<>();

}