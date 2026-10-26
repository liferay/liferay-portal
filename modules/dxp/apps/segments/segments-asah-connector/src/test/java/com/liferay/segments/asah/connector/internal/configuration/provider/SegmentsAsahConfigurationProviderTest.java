/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.configuration.provider;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.segments.asah.connector.internal.configuration.SegmentsAsahCompanyConfiguration;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Cristina González
 */
public class SegmentsAsahConfigurationProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_segmentsAsahConfigurationProvider.activate(Collections.emptyMap());

		_configurationAdmin = Mockito.mock(ConfigurationAdmin.class);

		ReflectionTestUtil.setFieldValue(
			_segmentsAsahConfigurationProvider, "_configurationAdmin",
			_configurationAdmin);

		_configurationProvider = Mockito.mock(ConfigurationProvider.class);

		ReflectionTestUtil.setFieldValue(
			_segmentsAsahConfigurationProvider, "_configurationProvider",
			_configurationProvider);
	}

	@Test
	public void testGetAnonymousUserSegmentsCacheExpirationTimeWithCompanyConfiguration()
		throws Exception {

		Mockito.when(
			_configurationAdmin.listConfigurations(Mockito.anyString())
		).thenReturn(
			new Configuration[0]
		);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_configurationProvider.getCompanyConfiguration(
				SegmentsAsahCompanyConfiguration.class, companyId)
		).thenReturn(
			new SegmentsAsahCompanyConfiguration() {

				@Override
				public int anonymousUserSegmentsCacheExpirationTime() {
					return 100;
				}

			}
		);

		Assert.assertEquals(
			100L,
			_segmentsAsahConfigurationProvider.
				getAnonymousUserSegmentsCacheExpirationTime(companyId));
	}

	@Test
	public void testGetAnonymousUserSegmentsCacheExpirationTimeWithoutCompanyConfiguration()
		throws Exception {

		Mockito.when(
			_configurationAdmin.listConfigurations(Mockito.anyString())
		).thenReturn(
			null
		);

		Assert.assertEquals(
			86400L,
			_segmentsAsahConfigurationProvider.
				getAnonymousUserSegmentsCacheExpirationTime(
					-RandomTestUtil.randomLong()));
	}

	private ConfigurationAdmin _configurationAdmin;
	private ConfigurationProvider _configurationProvider;
	private final SegmentsAsahConfigurationProvider
		_segmentsAsahConfigurationProvider =
			new SegmentsAsahConfigurationProvider();

}