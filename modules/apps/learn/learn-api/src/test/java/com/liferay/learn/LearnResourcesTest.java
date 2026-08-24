/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.learn;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class LearnResourcesTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGlobalServerServesEveryLearnResource() {
		String dirName = System.getProperty("learn.resources.data.dir");

		Assert.assertNotNull(
			"The build did not set \"learn.resources.data.dir\"", dirName);

		File dir = new File(dirName);

		Assert.assertTrue(dirName + " is not a directory", dir.isDirectory());

		File[] files = dir.listFiles(
			(directory, name) -> name.endsWith(".json"));

		Assert.assertNotNull(dir.toString(), files);
		Assert.assertTrue("No learn resources in " + dir, files.length > 0);

		List<String> unservedURLs = new ArrayList<>();

		int timeout = _TIMEOUT;

		for (File file : files) {
			String url =
				"https://s3.amazonaws.com/learn-resources.liferay.com/" +
					file.getName();

			String failure = _getFailure(url, timeout);

			if (failure != null) {
				unservedURLs.add(failure);

				timeout = _TIMEOUT_AFTER_FAILURE;
			}
		}

		Assert.assertTrue(unservedURLs.toString(), unservedURLs.isEmpty());
	}

	private String _getFailure(String url, int timeout) {
		try {
			URL urlObject = new URL(url);

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)urlObject.openConnection();

			httpURLConnection.setConnectTimeout(timeout);
			httpURLConnection.setReadTimeout(timeout);

			int responseCode = httpURLConnection.getResponseCode();

			if (responseCode != HttpURLConnection.HTTP_OK) {
				return url + " answered " + responseCode;
			}

			byte[] bytes = new byte[1];

			try (InputStream inputStream = httpURLConnection.getInputStream()) {
				if (inputStream.read(bytes) == -1) {
					return url + " answered nothing";
				}
			}

			if (bytes[0] != '{') {
				return url + " does not answer with a JSON object";
			}

			return null;
		}
		catch (IOException ioException) {
			return url + " could not be read: " + ioException;
		}
	}

	private static final int _TIMEOUT = 30000;

	private static final int _TIMEOUT_AFTER_FAILURE = 5000;

}