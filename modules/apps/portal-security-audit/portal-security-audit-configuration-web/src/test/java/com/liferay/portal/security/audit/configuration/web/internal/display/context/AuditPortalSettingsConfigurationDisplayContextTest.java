/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;
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
 * @author Christian Moura
 */
public class AuditPortalSettingsConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetHelpMessage() {
		_testGetHelpMessage(
			AuditPortalSettingsConfigurationDisplayContext::
				getEnabledHelpMessage,
			AuditConfiguration.class, "enabled");
		_testGetHelpMessage(
			AuditPortalSettingsConfigurationDisplayContext::
				getPersistentAuditMessageProcessorBufferSizeHelpMessage,
			PersistentAuditMessageProcessorConfiguration.class, "bufferSize");
		_testGetHelpMessage(
			AuditPortalSettingsConfigurationDisplayContext::
				getPersistentAuditMessageProcessorEnabledHelpMessage,
			PersistentAuditMessageProcessorConfiguration.class, "enabled");
		_testGetHelpMessage(
			AuditPortalSettingsConfigurationDisplayContext::
				getPersistentAuditMessageProcessorFlushIntervalHelpMessage,
			PersistentAuditMessageProcessorConfiguration.class,
			"flushInterval");
	}

	@Test
	public void testIsOverridden() {
		_testIsOverridden(
			AuditPortalSettingsConfigurationDisplayContext::isEnabledOverridden,
			AuditConfiguration.class, "enabled");
		_testIsOverridden(
			AuditPortalSettingsConfigurationDisplayContext::
				isPersistentAuditMessageProcessorBufferSizeOverridden,
			PersistentAuditMessageProcessorConfiguration.class, "bufferSize");
		_testIsOverridden(
			AuditPortalSettingsConfigurationDisplayContext::
				isPersistentAuditMessageProcessorEnabledOverridden,
			PersistentAuditMessageProcessorConfiguration.class, "enabled");
		_testIsOverridden(
			AuditPortalSettingsConfigurationDisplayContext::
				isPersistentAuditMessageProcessorFlushIntervalOverridden,
			PersistentAuditMessageProcessorConfiguration.class,
			"flushInterval");
	}

	private AuditPortalSettingsConfigurationDisplayContext
		_createDisplayContext() {

		return new AuditPortalSettingsConfigurationDisplayContext(
			Mockito.mock(AuditConfiguration.class),
			Mockito.mock(PersistentAuditMessageProcessorConfiguration.class));
	}

	private void _mockOverrideProperties(
		Class<?> clazz,
		MockedStatic<ConfigurationOverridePropertiesUtil>
			configurationOverridePropertiesUtilMockedStatic,
		String key) {

		configurationOverridePropertiesUtilMockedStatic.when(
			() -> ConfigurationOverridePropertiesUtil.getOverrideProperties(
				clazz.getName())
		).thenReturn(
			HashMapBuilder.<String, Object>put(
				key, RandomTestUtil.randomString()
			).build()
		);
	}

	private void _testGetHelpMessage(
		Function<AuditPortalSettingsConfigurationDisplayContext, String>
			function,
		Class<?> clazz, String key) {

		try (MockedStatic<ConfigurationOverridePropertiesUtil>
				configurationOverridePropertiesUtilMockedStatic =
					Mockito.mockStatic(
						ConfigurationOverridePropertiesUtil.class)) {

			AuditPortalSettingsConfigurationDisplayContext
				auditPortalSettingsConfigurationDisplayContext =
					_createDisplayContext();

			Assert.assertEquals(
				StringPool.BLANK,
				function.apply(auditPortalSettingsConfigurationDisplayContext));

			_mockOverrideProperties(
				clazz, configurationOverridePropertiesUtilMockedStatic, key);

			Assert.assertEquals(
				"this-field-has-been-set-by-a-portal-property-and-cannot-be-" +
					"changed-here",
				function.apply(auditPortalSettingsConfigurationDisplayContext));
		}
	}

	private void _testIsOverridden(
		Predicate<AuditPortalSettingsConfigurationDisplayContext> predicate,
		Class<?> clazz, String key) {

		try (MockedStatic<ConfigurationOverridePropertiesUtil>
				configurationOverridePropertiesUtilMockedStatic =
					Mockito.mockStatic(
						ConfigurationOverridePropertiesUtil.class)) {

			AuditPortalSettingsConfigurationDisplayContext
				auditPortalSettingsConfigurationDisplayContext =
					_createDisplayContext();

			Assert.assertFalse(
				predicate.test(auditPortalSettingsConfigurationDisplayContext));

			_mockOverrideProperties(
				clazz, configurationOverridePropertiesUtilMockedStatic, key);

			Assert.assertTrue(
				predicate.test(auditPortalSettingsConfigurationDisplayContext));
		}
	}

}