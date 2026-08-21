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

		// Every learn message in the product renders from one of these
		// resources, and a read that fails is swallowed into an empty object,
		// so a resource the global server stops serving makes learn links
		// vanish with nothing in the log. This repository holds a copy of every
		// resource the product asks for, so the directory names what has to be
		// served.

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

				// The first failure answers what the remaining reads are
				// asking: the doubt is no longer about one resource but about
				// the server or the route to it. So every read before it waits
				// as long as a slow continuous integration machine can need,
				// and every read after it waits only long enough to tell a
				// server that is down from one that is merely slow.

				timeout = _TIMEOUT_AFTER_FAILURE;
			}
		}

		Assert.assertTrue(unservedURLs.toString(), unservedURLs.isEmpty());
	}

	private String _getFailure(String url, int timeout) {
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(
				url
			).openConnection();

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

			// A read that fails is reported like any other, rather than thrown,
			// so one unreachable resource does not hide the state of the rest.

			return url + " could not be read: " + ioException;
		}
	}

	private static final int _TIMEOUT = 30000;

	private static final int _TIMEOUT_AFTER_FAILURE = 5000;

}