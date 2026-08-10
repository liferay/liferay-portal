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
import com.liferay.portal.test.rule.LiferayUnitTestRule;

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
	public void testGetEnabledHelpMessage() {
		try (MockedStatic<ConfigurationOverridePropertiesUtil>
				configurationOverridePropertiesUtilMockedStatic =
					Mockito.mockStatic(
						ConfigurationOverridePropertiesUtil.class)) {

			AuditConfigurationDisplayContext auditConfigurationDisplayContext =
				_createDisplayContext();

			Assert.assertEquals(
				StringPool.BLANK,
				auditConfigurationDisplayContext.getEnabledHelpMessage());

			_mockOverrideProperties(
				configurationOverridePropertiesUtilMockedStatic, "enabled");

			Assert.assertEquals(
				"this-field-has-been-set-by-a-portal-property-and-cannot-be-" +
					"changed-here",
				auditConfigurationDisplayContext.getEnabledHelpMessage());
		}
	}

	@Test
	public void testIsEnabledOverridden() {
		try (MockedStatic<ConfigurationOverridePropertiesUtil>
				configurationOverridePropertiesUtilMockedStatic =
					Mockito.mockStatic(
						ConfigurationOverridePropertiesUtil.class)) {

			AuditConfigurationDisplayContext auditConfigurationDisplayContext =
				_createDisplayContext();

			Assert.assertFalse(
				auditConfigurationDisplayContext.isEnabledOverridden());

			_mockOverrideProperties(
				configurationOverridePropertiesUtilMockedStatic, "enabled");

			Assert.assertTrue(
				auditConfigurationDisplayContext.isEnabledOverridden());
		}
	}

	private AuditConfigurationDisplayContext _createDisplayContext() {
		return new AuditConfigurationDisplayContext(
			Mockito.mock(AuditConfiguration.class));
	}

	private void _mockOverrideProperties(
		MockedStatic<ConfigurationOverridePropertiesUtil>
			configurationOverridePropertiesUtilMockedStatic,
		String key) {

		configurationOverridePropertiesUtilMockedStatic.when(
			() -> ConfigurationOverridePropertiesUtil.getOverrideProperties(
				AuditConfiguration.class.getName())
		).thenReturn(
			HashMapBuilder.<String, Object>put(
				key, RandomTestUtil.randomString()
			).build()
		);
	}

}