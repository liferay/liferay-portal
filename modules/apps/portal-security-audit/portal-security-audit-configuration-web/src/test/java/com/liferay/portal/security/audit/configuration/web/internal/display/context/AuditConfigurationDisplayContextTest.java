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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

	@Before
	public void setUp() {
		_configurationOverridePropertiesUtilMockedStatic = Mockito.mockStatic(
			ConfigurationOverridePropertiesUtil.class);

		_auditConfigurationDisplayContext =
			new AuditConfigurationDisplayContext(
				Mockito.mock(AuditConfiguration.class), false);
	}

	@After
	public void tearDown() {
		_configurationOverridePropertiesUtilMockedStatic.close();
	}

	@Test
	public void testGetEnabledHelpMessage() {
		Assert.assertEquals(
			StringPool.BLANK,
			_auditConfigurationDisplayContext.getEnabledHelpMessage());

		_mockOverrideProperties("enabled");

		Assert.assertEquals(
			"this-field-has-been-set-by-a-portal-property-and-cannot-be-" +
				"changed-here",
			_auditConfigurationDisplayContext.getEnabledHelpMessage());
	}

	@Test
	public void testGetPseudonymizationEnabledHelpMessage() {
		Assert.assertEquals(
			StringPool.BLANK,
			_auditConfigurationDisplayContext.
				getPseudonymizationEnabledHelpMessage());

		_mockOverrideProperties("pseudonymizationEnabled");

		Assert.assertEquals(
			"this-field-has-been-set-by-a-portal-property-and-cannot-be-" +
				"changed-here",
			_auditConfigurationDisplayContext.
				getPseudonymizationEnabledHelpMessage());
	}

	@Test
	public void testIsEnabledOverridden() {
		Assert.assertFalse(
			_auditConfigurationDisplayContext.isEnabledOverridden());

		_mockOverrideProperties("enabled");

		Assert.assertTrue(
			_auditConfigurationDisplayContext.isEnabledOverridden());
	}

	@Test
	public void testIsPseudonymizationEnabledOverridden() {
		Assert.assertFalse(
			_auditConfigurationDisplayContext.
				isPseudonymizationEnabledOverridden());

		_mockOverrideProperties("pseudonymizationEnabled");

		Assert.assertTrue(
			_auditConfigurationDisplayContext.
				isPseudonymizationEnabledOverridden());
	}

	private void _mockOverrideProperties(String key) {
		_configurationOverridePropertiesUtilMockedStatic.when(
			() -> ConfigurationOverridePropertiesUtil.getOverrideProperties(
				AuditConfiguration.class.getName())
		).thenReturn(
			HashMapBuilder.<String, Object>put(
				key, RandomTestUtil.randomString()
			).build()
		);
	}

	private AuditConfigurationDisplayContext _auditConfigurationDisplayContext;
	private MockedStatic<ConfigurationOverridePropertiesUtil>
		_configurationOverridePropertiesUtilMockedStatic;

}