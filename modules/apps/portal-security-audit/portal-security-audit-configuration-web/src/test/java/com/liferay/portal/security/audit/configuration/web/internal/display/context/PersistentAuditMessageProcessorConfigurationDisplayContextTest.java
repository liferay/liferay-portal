/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
public class PersistentAuditMessageProcessorConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetHelpMessage() {
		_testGetHelpMessage(
			PersistentAuditMessageProcessorConfigurationDisplayContext::
				getBufferSizeHelpMessage,
			"bufferSize");
		_testGetHelpMessage(
			PersistentAuditMessageProcessorConfigurationDisplayContext::
				getEnabledHelpMessage,
			"enabled");
		_testGetHelpMessage(
			PersistentAuditMessageProcessorConfigurationDisplayContext::
				getFlushIntervalHelpMessage,
			"flushInterval");
	}

	@Test
	public void testIsOverridden() {
		_testIsOverridden(
			"bufferSize",
			PersistentAuditMessageProcessorConfigurationDisplayContext::
				isBufferSizeOverridden);
		_testIsOverridden(
			"enabled",
			PersistentAuditMessageProcessorConfigurationDisplayContext::
				isEnabledOverridden);
		_testIsOverridden(
			"flushInterval",
			PersistentAuditMessageProcessorConfigurationDisplayContext::
				isFlushIntervalOverridden);
	}

	private PersistentAuditMessageProcessorConfigurationDisplayContext
		_createDisplayContext() {

		return new PersistentAuditMessageProcessorConfigurationDisplayContext(
			Mockito.mock(PersistentAuditMessageProcessorConfiguration.class));
	}

	private void _mockOverrideProperties(
		MockedStatic<ConfigurationOverridePropertiesUtil>
			configurationOverridePropertiesUtilMockedStatic,
		String key) {

		configurationOverridePropertiesUtilMockedStatic.when(
			() -> ConfigurationOverridePropertiesUtil.getOverrideProperties(
				PersistentAuditMessageProcessorConfiguration.class.getName())
		).thenReturn(
			HashMapBuilder.<String, Object>put(
				key, RandomTestUtil.randomString()
			).build()
		);
	}

	private void _testGetHelpMessage(
		Function
			<PersistentAuditMessageProcessorConfigurationDisplayContext, String>
				function,
		String key) {

		try (MockedStatic<ConfigurationOverridePropertiesUtil>
				configurationOverridePropertiesUtilMockedStatic =
					Mockito.mockStatic(
						ConfigurationOverridePropertiesUtil.class)) {

			PersistentAuditMessageProcessorConfigurationDisplayContext
				persistentAuditMessageProcessorConfigurationDisplayContext =
					_createDisplayContext();

			Assert.assertEquals(
				StringPool.BLANK,
				function.apply(
					persistentAuditMessageProcessorConfigurationDisplayContext));

			_mockOverrideProperties(
				configurationOverridePropertiesUtilMockedStatic, key);

			Assert.assertEquals(
				"this-field-has-been-set-by-a-portal-property-and-cannot-be-" +
					"changed-here",
				function.apply(
					persistentAuditMessageProcessorConfigurationDisplayContext));
		}
	}

	private void _testIsOverridden(
		String key,
		Predicate<PersistentAuditMessageProcessorConfigurationDisplayContext>
			predicate) {

		try (MockedStatic<ConfigurationOverridePropertiesUtil>
				configurationOverridePropertiesUtilMockedStatic =
					Mockito.mockStatic(
						ConfigurationOverridePropertiesUtil.class)) {

			PersistentAuditMessageProcessorConfigurationDisplayContext
				persistentAuditMessageProcessorConfigurationDisplayContext =
					_createDisplayContext();

			Assert.assertFalse(
				predicate.test(
					persistentAuditMessageProcessorConfigurationDisplayContext));

			_mockOverrideProperties(
				configurationOverridePropertiesUtilMockedStatic, key);

			Assert.assertTrue(
				predicate.test(
					persistentAuditMessageProcessorConfigurationDisplayContext));
		}
	}

}