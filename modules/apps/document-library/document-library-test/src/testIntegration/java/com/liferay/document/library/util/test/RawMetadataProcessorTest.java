/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.processor.RawMetadataProcessor;
import com.liferay.document.library.kernel.processor.RawMetadataProcessorUtil;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class RawMetadataProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testIsSupported() throws Exception {
		Assert.assertFalse(
			_rawMetadataProcessor.isSupported("application/zip"));
		Assert.assertTrue(_rawMetadataProcessor.isSupported("application/pdf"));

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_CLASS_NAME_DL_FILE_ENTRY_RAW_METADATA_PROCASSOR_CONFIGURATION,
					HashMapDictionaryBuilder.<String, Object>put(
						"excludedMimeTypes", ""
					).build())) {

			Assert.assertTrue(
				_rawMetadataProcessor.isSupported("application/zip"));
		}
	}

	private static final String
		_CLASS_NAME_DL_FILE_ENTRY_RAW_METADATA_PROCASSOR_CONFIGURATION =
			"com.liferay.document.library.configuration." +
				"DLFileEntryRawMetadataProcessorConfiguration";

	private final RawMetadataProcessor _rawMetadataProcessor =
		RawMetadataProcessorUtil.getRawMetadataProcessor();

}