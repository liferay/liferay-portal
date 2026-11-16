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

import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Assert;
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

	@Test
	public void testGetHelpMessage() {
		_testGetHelpMessage(
			FileSystemAuditMessageProcessorConfigurationDisplayContext::
				getEnabledHelpMessage,
			"enabled");
		_testGetHelpMessage(
			FileSystemAuditMessageProcessorConfigurationDisplayContext::
				getGenerateChecksumHelpMessage,
			"generateChecksum");
		_testGetHelpMessage(
			FileSystemAuditMessageProcessorConfigurationDisplayContext::
				getOutputDirectoryHelpMessage,
			"outputDirectory");
		_testGetHelpMessage(
			FileSystemAuditMessageProcessorConfigurationDisplayContext::
				getOutputFormatHelpMessage,
			"outputFormat");
	}

	@Test
	public void testIsOverridden() {
		_testIsOverridden(
			FileSystemAuditMessageProcessorConfigurationDisplayContext::
				isEnabledOverridden,
			"enabled");
		_testIsOverridden(
			FileSystemAuditMessageProcessorConfigurationDisplayContext::
				isGenerateChecksumOverridden,
			"generateChecksum");
		_testIsOverridden(
			FileSystemAuditMessageProcessorConfigurationDisplayContext::
				isOutputDirectoryOverridden,
			"outputDirectory");
		_testIsOverridden(
			FileSystemAuditMessageProcessorConfigurationDisplayContext::
				isOutputFormatOverridden,
			"outputFormat");
	}

	private FileSystemAuditMessageProcessorConfigurationDisplayContext
		_createFileSystemAuditMessageProcessorConfigurationDisplayContext() {

		return new FileSystemAuditMessageProcessorConfigurationDisplayContext(
			Mockito.mock(FileSystemAuditMessageProcessorConfiguration.class));
	}

	private void _mockOverrideProperties(
		MockedStatic<ConfigurationOverridePropertiesUtil>
			configurationOverridePropertiesUtilMockedStatic,
		String key) {

		configurationOverridePropertiesUtilMockedStatic.when(
			() -> ConfigurationOverridePropertiesUtil.getOverrideProperties(
				FileSystemAuditMessageProcessorConfiguration.class.getName())
		).thenReturn(
			HashMapBuilder.<String, Object>put(
				key, RandomTestUtil.randomString()
			).build()
		);
	}

	private void _testGetHelpMessage(
		Function
			<FileSystemAuditMessageProcessorConfigurationDisplayContext, String>
				function,
		String key) {

		try (MockedStatic<ConfigurationOverridePropertiesUtil>
				configurationOverridePropertiesUtilMockedStatic =
					Mockito.mockStatic(
						ConfigurationOverridePropertiesUtil.class)) {

			FileSystemAuditMessageProcessorConfigurationDisplayContext
				fileSystemAuditMessageProcessorConfigurationDisplayContext =
					_createFileSystemAuditMessageProcessorConfigurationDisplayContext();

			Assert.assertEquals(
				StringPool.BLANK,
				function.apply(
					fileSystemAuditMessageProcessorConfigurationDisplayContext));

			_mockOverrideProperties(
				configurationOverridePropertiesUtilMockedStatic, key);

			Assert.assertEquals(
				"this-field-has-been-set-by-a-portal-property-and-cannot-be-" +
					"changed-here",
				function.apply(
					fileSystemAuditMessageProcessorConfigurationDisplayContext));
		}
	}

	private void _testIsOverridden(
		Predicate<FileSystemAuditMessageProcessorConfigurationDisplayContext>
			predicate,
		String key) {

		try (MockedStatic<ConfigurationOverridePropertiesUtil>
				configurationOverridePropertiesUtilMockedStatic =
					Mockito.mockStatic(
						ConfigurationOverridePropertiesUtil.class)) {

			FileSystemAuditMessageProcessorConfigurationDisplayContext
				fileSystemAuditMessageProcessorConfigurationDisplayContext =
					_createFileSystemAuditMessageProcessorConfigurationDisplayContext();

			Assert.assertFalse(
				predicate.test(
					fileSystemAuditMessageProcessorConfigurationDisplayContext));

			_mockOverrideProperties(
				configurationOverridePropertiesUtilMockedStatic, key);

			Assert.assertTrue(
				predicate.test(
					fileSystemAuditMessageProcessorConfigurationDisplayContext));
		}
	}

}