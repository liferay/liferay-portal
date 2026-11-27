/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorCompanyConfiguration;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;
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
 * @author Lucas Miranda
 */
public class
	PersistentAuditMessageProcessorCompanyConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_persistentAuditMessageProcessorCompanyConfigurationDisplayContext =
			new PersistentAuditMessageProcessorCompanyConfigurationDisplayContext(
				Mockito.mock(
					PersistentAuditMessageProcessorCompanyConfiguration.class),
				_persistentAuditMessageProcessorConfiguration);
	}

	@After
	public void tearDown() {
		_configurationOverridePropertiesUtilMockedStatic.close();
	}

	@Test
	public void testGetEnabledHelpMessage() {
		_mockPersistentAuditMessageProcessorConfiguration(true);

		Assert.assertEquals(
			StringPool.BLANK,
			_persistentAuditMessageProcessorCompanyConfigurationDisplayContext.
				getEnabledHelpMessage());

		_mockPersistentAuditMessageProcessorConfiguration(false);

		Assert.assertEquals(
			"the-database-processor-is-disabled-in-system-settings",
			_persistentAuditMessageProcessorCompanyConfigurationDisplayContext.
				getEnabledHelpMessage());

		_mockConfigurationOverridePropertiesUtil();

		Assert.assertEquals(
			"this-field-has-been-set-by-a-portal-property-and-cannot-be-" +
				"changed-here",
			_persistentAuditMessageProcessorCompanyConfigurationDisplayContext.
				getEnabledHelpMessage());

		_mockPersistentAuditMessageProcessorConfiguration(true);

		Assert.assertEquals(
			"this-field-has-been-set-by-a-portal-property-and-cannot-be-" +
				"changed-here",
			_persistentAuditMessageProcessorCompanyConfigurationDisplayContext.
				getEnabledHelpMessage());
	}

	@Test
	public void testIsEnabledInputDisabled() {
		_mockPersistentAuditMessageProcessorConfiguration(true);

		Assert.assertFalse(
			_persistentAuditMessageProcessorCompanyConfigurationDisplayContext.
				isEnabledInputDisabled());

		_mockPersistentAuditMessageProcessorConfiguration(false);

		Assert.assertTrue(
			_persistentAuditMessageProcessorCompanyConfigurationDisplayContext.
				isEnabledInputDisabled());

		_mockConfigurationOverridePropertiesUtil();

		Assert.assertTrue(
			_persistentAuditMessageProcessorCompanyConfigurationDisplayContext.
				isEnabledInputDisabled());

		_mockPersistentAuditMessageProcessorConfiguration(true);

		Assert.assertTrue(
			_persistentAuditMessageProcessorCompanyConfigurationDisplayContext.
				isEnabledInputDisabled());
	}

	private void _mockConfigurationOverridePropertiesUtil() {
		_configurationOverridePropertiesUtilMockedStatic.when(
			() -> ConfigurationOverridePropertiesUtil.getOverrideProperties(
				PersistentAuditMessageProcessorCompanyConfiguration.class.
					getName())
		).thenReturn(
			HashMapBuilder.<String, Object>put(
				"enabled", RandomTestUtil.randomString()
			).build()
		);
	}

	private void _mockPersistentAuditMessageProcessorConfiguration(
		boolean enabled) {

		Mockito.when(
			_persistentAuditMessageProcessorConfiguration.enabled()
		).thenReturn(
			enabled
		);
	}

	private MockedStatic<ConfigurationOverridePropertiesUtil>
		_configurationOverridePropertiesUtilMockedStatic = Mockito.mockStatic(
			ConfigurationOverridePropertiesUtil.class);
	private PersistentAuditMessageProcessorCompanyConfigurationDisplayContext
		_persistentAuditMessageProcessorCompanyConfigurationDisplayContext;
	private final PersistentAuditMessageProcessorConfiguration
		_persistentAuditMessageProcessorConfiguration = Mockito.mock(
			PersistentAuditMessageProcessorConfiguration.class);

}