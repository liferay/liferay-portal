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
public class AuditConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetHelpMessage() {
		_testGetHelpMessage(
			AuditConfigurationDisplayContext::getEnabledHelpMessage,
			AuditConfiguration.class, "enabled");
		_testGetHelpMessage(
			AuditConfigurationDisplayContext::
				getPersistentAuditMessageProcessorBufferSizeHelpMessage,
			PersistentAuditMessageProcessorConfiguration.class, "bufferSize");
		_testGetHelpMessage(
			AuditConfigurationDisplayContext::
				getPersistentAuditMessageProcessorEnabledHelpMessage,
			PersistentAuditMessageProcessorConfiguration.class, "enabled");
		_testGetHelpMessage(
			AuditConfigurationDisplayContext::
				getPersistentAuditMessageProcessorFlushIntervalHelpMessage,
			PersistentAuditMessageProcessorConfiguration.class,
			"flushInterval");
	}

	@Test
	public void testIsOverridden() {
		_testIsOverridden(
			AuditConfigurationDisplayContext::isEnabledOverridden,
			AuditConfiguration.class, "enabled");
		_testIsOverridden(
			AuditConfigurationDisplayContext::
				isPersistentAuditMessageProcessorBufferSizeOverridden,
			PersistentAuditMessageProcessorConfiguration.class, "bufferSize");
		_testIsOverridden(
			AuditConfigurationDisplayContext::
				isPersistentAuditMessageProcessorEnabledOverridden,
			PersistentAuditMessageProcessorConfiguration.class, "enabled");
		_testIsOverridden(
			AuditConfigurationDisplayContext::
				isPersistentAuditMessageProcessorFlushIntervalOverridden,
			PersistentAuditMessageProcessorConfiguration.class,
			"flushInterval");
	}

	private AuditConfigurationDisplayContext _createDisplayContext() {
		return new AuditConfigurationDisplayContext(
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
		Function<AuditConfigurationDisplayContext, String> function,
		Class<?> clazz, String key) {

		try (MockedStatic<ConfigurationOverridePropertiesUtil>
				configurationOverridePropertiesUtilMockedStatic =
					Mockito.mockStatic(
						ConfigurationOverridePropertiesUtil.class)) {

			AuditConfigurationDisplayContext auditConfigurationDisplayContext =
				_createDisplayContext();

			Assert.assertEquals(
				StringPool.BLANK,
				function.apply(auditConfigurationDisplayContext));

			_mockOverrideProperties(
				clazz, configurationOverridePropertiesUtilMockedStatic, key);

			Assert.assertEquals(
				"this-field-has-been-set-by-a-portal-property-and-cannot-be-" +
					"changed-here",
				function.apply(auditConfigurationDisplayContext));
		}
	}

	private void _testIsOverridden(
		Predicate<AuditConfigurationDisplayContext> predicate, Class<?> clazz,
		String key) {

		try (MockedStatic<ConfigurationOverridePropertiesUtil>
				configurationOverridePropertiesUtilMockedStatic =
					Mockito.mockStatic(
						ConfigurationOverridePropertiesUtil.class)) {

			AuditConfigurationDisplayContext auditConfigurationDisplayContext =
				_createDisplayContext();

			Assert.assertFalse(
				predicate.test(auditConfigurationDisplayContext));

			_mockOverrideProperties(
				clazz, configurationOverridePropertiesUtilMockedStatic, key);

			Assert.assertTrue(predicate.test(auditConfigurationDisplayContext));
		}
	}

}