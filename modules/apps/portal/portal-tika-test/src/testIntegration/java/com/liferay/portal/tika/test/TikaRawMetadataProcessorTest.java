/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tika.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class TikaRawMetadataProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetRawMetadataWithTextPlain() throws Exception {
		Map<String, String[]> rawMetadata =
			_rawMetadataProcessor.getRawMetadata(
				ContentTypes.APPLICATION_JAVASCRIPT,
				new ByteArrayInputStream(
					"function TikaRawMetadataProcessorTest() {}".getBytes()));

		String[] values = rawMetadata.get("Content-Type");

		Assert.assertTrue(
			values[0], values[0].startsWith("application/javascript;"));
	}

	@Inject
	private RawMetadataProcessor _rawMetadataProcessor;

}