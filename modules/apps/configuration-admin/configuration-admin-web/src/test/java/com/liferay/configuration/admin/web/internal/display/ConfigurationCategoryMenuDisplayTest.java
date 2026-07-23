/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.display;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class ConfigurationCategoryMenuDisplayTest extends Mockito {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetConfigurationEntries() {
		_testGetConfigurationEntries(
			ExtendedObjectClassDefinition.Scope.COMPANY);
		_testGetConfigurationEntries(ExtendedObjectClassDefinition.Scope.GROUP);
		_testGetConfigurationEntries(
			ExtendedObjectClassDefinition.Scope.SYSTEM);
	}

	private ConfigurationEntry _mockConfigurationEntry(String scope) {
		ConfigurationEntry configurationEntry = mock(ConfigurationEntry.class);

		when(
			configurationEntry.getKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		when(
			configurationEntry.getScope()
		).thenReturn(
			scope
		);

		return configurationEntry;
	}

	private void _testGetConfigurationEntries(
		ExtendedObjectClassDefinition.Scope scope) {

		ConfigurationEntry groupConfigurationEntry = _mockConfigurationEntry(
			ExtendedObjectClassDefinition.Scope.GROUP.toString());
		ConfigurationEntry systemConfigurationEntry = _mockConfigurationEntry(
			ExtendedObjectClassDefinition.Scope.SYSTEM.toString());

		ConfigurationCategoryMenuDisplay configurationCategoryMenuDisplay =
			new ConfigurationCategoryMenuDisplay(
				mock(ConfigurationCategoryDisplay.class),
				SetUtil.fromArray(
					groupConfigurationEntry, systemConfigurationEntry),
				scope);

		Collection<ConfigurationScopeDisplay> configurationScopeDisplays =
			configurationCategoryMenuDisplay.getConfigurationScopeDisplays();

		Assert.assertEquals(
			configurationScopeDisplays.toString(), 1,
			configurationScopeDisplays.size());

		Iterator<ConfigurationScopeDisplay> iterator =
			configurationScopeDisplays.iterator();

		ConfigurationScopeDisplay configurationScopeDisplay = iterator.next();

		Assert.assertEquals(
			scope.toString(), configurationScopeDisplay.getScope());

		List<ConfigurationEntry> configurationEntries =
			configurationScopeDisplay.getConfigurationEntries();

		Assert.assertEquals(
			configurationEntries.toString(), 2, configurationEntries.size());
		Assert.assertTrue(
			configurationEntries.contains(groupConfigurationEntry));
		Assert.assertTrue(
			configurationEntries.contains(systemConfigurationEntry));
	}

}