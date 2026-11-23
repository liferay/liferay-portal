/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.zip.internal.reader.factory;

import com.liferay.portal.kernel.test.util.DependenciesTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.zip.internal.reader.ZipReaderImpl;
import com.liferay.portal.zip.internal.reader.ZipReaderImplTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 */
public class ZipReaderFactoryImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());
	}

	@Test
	public void testZipReader() throws Exception {
		ZipReaderFactory zipReaderFactory = new ZipReaderFactoryImpl();

		try (ZipReader zipReader = zipReaderFactory.getZipReader(
				DependenciesTestUtil.getDependencyAsFile(
					ZipReaderImplTest.class, "file.zip"))) {

			Assert.assertTrue(zipReader instanceof ZipReaderImpl);
		}

		try (ZipReader zipReader = zipReaderFactory.getZipReader(
				DependenciesTestUtil.getDependencyAsInputStream(
					ZipReaderImplTest.class, "file.zip"))) {

			Assert.assertTrue(zipReader instanceof ZipReaderImpl);
		}
	}

}