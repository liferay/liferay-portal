/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
public class PortletDataContextZipWriterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_portletDataContext =
			PortletDataContextFactoryUtil.createExportPortletDataContext(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				new HashMap<>(), null, null, _zipWriter);
	}

	@Test
	public void testMultipleByteArraysAddition() throws Exception {
		byte[] bytes = {Byte.MIN_VALUE, Byte.MAX_VALUE};

		_portletDataContext.addZipEntry(_PATH, bytes);

		_portletDataContext.addZipEntry(_PATH, RandomTestUtil.randomBytes());

		_assertZipEntry(bytes);
	}

	@Test
	public void testMultipleInputStreamsAddition() throws Exception {
		byte[] bytes = {Byte.MIN_VALUE, Byte.MAX_VALUE};

		_portletDataContext.addZipEntry(_PATH, new ByteArrayInputStream(bytes));

		_portletDataContext.addZipEntry(
			_PATH, new ByteArrayInputStream(RandomTestUtil.randomBytes()));

		_assertZipEntry(bytes);
	}

	@Test
	public void testMultipleStringsAddition() throws Exception {
		String string = RandomTestUtil.randomString();

		_portletDataContext.addZipEntry(_PATH, string);

		_portletDataContext.addZipEntry(_PATH, RandomTestUtil.randomString());

		_assertZipEntry(string.getBytes(StandardCharsets.UTF_8));
	}

	private void _assertZipEntry(byte[] bytes) {
		try (ZipReader zipReader = new ZipReader(_zipWriter.getFile())) {
			Assert.assertEquals(
				Collections.singletonList("test.xml"), zipReader.getEntries());
			Assert.assertArrayEquals(
				bytes, zipReader.getEntryAsByteArray(_PATH));
		}
	}

	private static final String _PATH = "/test.xml";

	private PortletDataContext _portletDataContext;
	private final ZipWriter _zipWriter = new ZipWriter();

}