/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alejo Ceballos
 */
public class CharsetDetectorTest {

	@Test
	public void testReadUS_ASCII() throws IOException {
		Assert.assertEquals(
			StandardCharsets.US_ASCII,
			_charsetDetector.detect(_getFile("charset_us-ascii.csv")));
	}

	@Test
	public void testReadWINDOWS_1252() throws IOException {
		Assert.assertEquals(
			Charset.forName("windows-1252"),
			_charsetDetector.detect(_getFile("charset_windows-1252.csv")));
	}

	private File _getFile(String fileName) {
		URL resourceURL = Objects.requireNonNull(
			_classLoader.getResource(fileName));

		return new File(resourceURL.getFile());
	}

	private final CharsetDetector _charsetDetector = new CharsetDetector();
	private final ClassLoader _classLoader = getClass().getClassLoader();

}