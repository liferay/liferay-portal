/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.internal.configuration;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.sharing.configuration.SharingConfiguration;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class SharingConfigurationFactoryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetGroupSharingConfigurationWhenCompanySharingIsDisabled()
		throws Exception {

		SharingConfiguration sharingConfiguration =
			_getGroupSharingConfiguration(false, true);

		Assert.assertFalse(sharingConfiguration.isAvailable());
		Assert.assertFalse(sharingConfiguration.isEnabled());
	}

	@Test
	public void testGetGroupSharingConfigurationWhenSharingIsEnabled()
		throws Exception {

		SharingConfiguration sharingConfiguration =
			_getGroupSharingConfiguration(true, true);

		Assert.assertTrue(sharingConfiguration.isAvailable());
		Assert.assertTrue(sharingConfiguration.isEnabled());
	}

	@Test
	public void testGetGroupSharingConfigurationWhenSystemSharingIsDisabled()
		throws Exception {

		SharingConfiguration sharingConfiguration =
			_getGroupSharingConfiguration(true, false);

		Assert.assertFalse(sharingConfiguration.isAvailable());
		Assert.assertFalse(sharingConfiguration.isEnabled());
	}

	private SharingConfiguration _getGroupSharingConfiguration(
			boolean companyEnabled, boolean systemEnabled)
		throws Exception {

		Mockito.when(
			group.getTypeSettingsProperties()
		).thenReturn(
			new UnicodeProperties()
		);

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		SharingCompanyConfiguration sharingCompanyConfiguration = Mockito.mock(
			SharingCompanyConfiguration.class);

		Mockito.when(
			sharingCompanyConfiguration.enabled()
		).thenReturn(
			companyEnabled
		);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			configurationProvider.getCompanyConfiguration(
				SharingCompanyConfiguration.class, companyId)
		).thenReturn(
			sharingCompanyConfiguration
		);

		SharingGroupConfiguration sharingGroupConfiguration = Mockito.mock(
			SharingGroupConfiguration.class);

		Mockito.when(
			sharingGroupConfiguration.enabled()
		).thenReturn(
			true
		);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			configurationProvider.getGroupConfiguration(
				SharingGroupConfiguration.class, companyId, groupId)
		).thenReturn(
			sharingGroupConfiguration
		);

		SharingConfigurationFactoryImpl sharingConfigurationFactoryImpl =
			new SharingConfigurationFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			sharingConfigurationFactoryImpl, "_configurationProvider",
			configurationProvider);

		sharingConfigurationFactoryImpl.activate(
			HashMapBuilder.<String, Object>put(
				"enabled", systemEnabled
			).build());

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		return sharingConfigurationFactoryImpl.getGroupSharingConfiguration(
			group);
	}

}