/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.security.audit.router.configuration.FileSystemAuditMessageProcessorConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Álvaro Saugar
 */
public class FileSystemAuditMessageProcessorConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_configurationOverridePropertiesUtilMockedStatic = Mockito.mockStatic(
			ConfigurationOverridePropertiesUtil.class);

		_fileSystemAuditMessageProcessorConfigurationDisplayContext =
			new FileSystemAuditMessageProcessorConfigurationDisplayContext(
				Mockito.mock(
					FileSystemAuditMessageProcessorConfiguration.class));
	}

	@After
	public void tearDown() {
		_configurationOverridePropertiesUtilMockedStatic.close();
	}

	@Test
	public void testGetEnabledHelpMessage() {
		Assert.assertEquals(
			StringPool.BLANK,
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				getEnabledHelpMessage());

		_mockOverrideProperties("enabled");

		Assert.assertEquals(
			_HELP_MESSAGE,
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				getEnabledHelpMessage());
	}

	@Test
	public void testGetGenerateChecksumHelpMessage() {
		Assert.assertEquals(
			StringPool.BLANK,
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				getGenerateChecksumHelpMessage());

		_mockOverrideProperties("generateChecksum");

		Assert.assertEquals(
			_HELP_MESSAGE,
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				getGenerateChecksumHelpMessage());
	}

	@Test
	public void testGetOutputDirectoryHelpMessage() {
		Assert.assertEquals(
			StringPool.BLANK,
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				getOutputDirectoryHelpMessage());

		_mockOverrideProperties("outputDirectory");

		Assert.assertEquals(
			_HELP_MESSAGE,
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				getOutputDirectoryHelpMessage());
	}

	@Test
	public void testGetOutputFormatHelpMessage() {
		Assert.assertEquals(
			StringPool.BLANK,
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				getOutputFormatHelpMessage());

		_mockOverrideProperties("outputFormat");

		Assert.assertEquals(
			_HELP_MESSAGE,
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				getOutputFormatHelpMessage());
	}

	@Test
	public void testIsEnabledOverridden() {
		Assert.assertFalse(
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				isEnabledOverridden());

		_mockOverrideProperties("enabled");

		Assert.assertTrue(
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				isEnabledOverridden());
	}

	@Test
	public void testIsGenerateChecksumOverridden() {
		Assert.assertFalse(
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				isGenerateChecksumOverridden());

		_mockOverrideProperties("generateChecksum");

		Assert.assertTrue(
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				isGenerateChecksumOverridden());
	}

	@Test
	public void testIsOutputDirectoryOverridden() {
		Assert.assertFalse(
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				isOutputDirectoryOverridden());

		_mockOverrideProperties("outputDirectory");

		Assert.assertTrue(
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				isOutputDirectoryOverridden());
	}

	@Test
	public void testIsOutputFormatOverridden() {
		Assert.assertFalse(
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				isOutputFormatOverridden());

		_mockOverrideProperties("outputFormat");

		Assert.assertTrue(
			_fileSystemAuditMessageProcessorConfigurationDisplayContext.
				isOutputFormatOverridden());
	}

	private void _mockOverrideProperties(String key) {
		_configurationOverridePropertiesUtilMockedStatic.when(
			() -> ConfigurationOverridePropertiesUtil.getOverrideProperties(
				FileSystemAuditMessageProcessorConfiguration.class.getName())
		).thenReturn(
			HashMapBuilder.<String, Object>put(
				key, RandomTestUtil.randomString()
			).build()
		);
	}

	private static final String _HELP_MESSAGE =
		"this-field-has-been-set-by-a-portal-property-and-cannot-be-changed-" +
			"here";

	private MockedStatic<ConfigurationOverridePropertiesUtil>
		_configurationOverridePropertiesUtilMockedStatic;
	private FileSystemAuditMessageProcessorConfigurationDisplayContext
		_fileSystemAuditMessageProcessorConfigurationDisplayContext;

}