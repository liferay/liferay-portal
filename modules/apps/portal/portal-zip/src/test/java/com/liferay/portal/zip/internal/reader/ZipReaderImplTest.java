/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.zip.internal.reader;

import com.liferay.portal.kernel.test.util.DependenciesTestUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.zip.ZipException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 */
public class ZipReaderImplTest extends BaseZipReaderTestCase {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCloseClosesEntryInputStream() throws Exception {
		ZipReader zipReader = getZipReader(
			DependenciesTestUtil.getDependencyAsInputStream(
				getClass(), "file.zip"));

		InputStream inputStream = zipReader.getEntryAsInputStream("0.txt");

		zipReader.close();

		try {
			inputStream.read();

			Assert.fail();
		}
		catch (ZipException zipException) {
			Assert.assertEquals("ZipFile closed", zipException.getMessage());
		}
	}

	@Override
	protected ZipReader getZipReader(File file) {
		return new ZipReaderImpl(file);
	}

	@Override
	protected ZipReader getZipReader(InputStream inputStream)
		throws IOException {

		return new ZipReaderImpl(inputStream);
	}

}