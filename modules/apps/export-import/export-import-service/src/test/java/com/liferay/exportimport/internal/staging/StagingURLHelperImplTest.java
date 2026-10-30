/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.staging;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.RemoteOptionsException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.configuration.StagingConfiguration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Vendel Töreki
 */
public class StagingURLHelperImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_configurationProvider.getCompanyConfiguration(
				Mockito.eq(StagingConfiguration.class), Mockito.anyLong())
		).thenReturn(
			_stagingConfiguration
		);

		ReflectionTestUtil.setFieldValue(
			_stagingURLHelperImpl, "_configurationProvider",
			_configurationProvider);
	}

	@Test
	public void testBuildRemoteURL() {
		_setStagingRemoteHostsAllowed();

		Assert.assertEquals(
			"http://127.0.0.1:18778",
			_stagingURLHelperImpl.buildRemoteURL(
				"127.0.0.1", 18778, null, false));
		Assert.assertEquals(
			"https://live.example.com/portal",
			_stagingURLHelperImpl.buildRemoteURL(
				"live.example.com", 0, "/portal", true));
	}

	@Test
	public void testBuildRemoteURLWhenConfigurationIsUnavailable()
		throws Exception {

		Mockito.when(
			_configurationProvider.getCompanyConfiguration(
				Mockito.eq(StagingConfiguration.class), Mockito.anyLong())
		).thenThrow(
			new ConfigurationException()
		);

		_assertRemoteHostNotAllowed(
			() -> _stagingURLHelperImpl.buildRemoteURL(
				"live.example.com", RandomTestUtil.randomInt(), null, false),
			"live.example.com");
	}

	@Test
	public void testBuildRemoteURLWhenRemoteHostIsAllowed() {
		_setStagingRemoteHostsAllowed("192.168.1.5", "live.example.com");

		Assert.assertEquals(
			"https://192.168.1.5:8443",
			_stagingURLHelperImpl.buildRemoteURL(
				"192.168.1.5", 8443, null, true));
		Assert.assertEquals(
			"http://LIVE.example.com:8080",
			_stagingURLHelperImpl.buildRemoteURL(
				"LIVE.example.com", 8080, null, false));
	}

	@Test
	public void testBuildRemoteURLWhenRemoteHostIsNotAllowed() {
		_setStagingRemoteHostsAllowed("live.example.com");

		_assertRemoteHostNotAllowed(
			() -> _stagingURLHelperImpl.buildRemoteURL(
				"127.0.0.1", RandomTestUtil.randomInt(), null, false),
			"127.0.0.1");
		_assertRemoteHostNotAllowed(
			() -> _stagingURLHelperImpl.buildRemoteURL(
				"live.example.com.evil.com", RandomTestUtil.randomInt(), null,
				false),
			"live.example.com.evil.com");
		_assertRemoteHostNotAllowed(
			() -> _stagingURLHelperImpl.buildRemoteURL(
				UnicodePropertiesBuilder.put(
					"remoteAddress", "10.0.0.1"
				).put(
					"remotePort", String.valueOf(RandomTestUtil.randomInt())
				).build()),
			"10.0.0.1");
	}

	private void _assertRemoteHostNotAllowed(
		Runnable runnable, String remoteAddress) {

		SystemException systemException = Assert.assertThrows(
			SystemException.class, runnable::run);

		Assert.assertEquals(
			"Remote host " + remoteAddress + " is not allowed",
			systemException.getMessage());

		RemoteOptionsException remoteOptionsException =
			(RemoteOptionsException)systemException.getCause();

		Assert.assertEquals(
			remoteAddress, remoteOptionsException.getRemoteAddress());
		Assert.assertEquals(
			RemoteOptionsException.REMOTE_ADDRESS,
			remoteOptionsException.getType());
	}

	private void _setStagingRemoteHostsAllowed(
		String... stagingRemoteHostsAllowed) {

		Mockito.when(
			_stagingConfiguration.stagingRemoteHostsAllowed()
		).thenReturn(
			stagingRemoteHostsAllowed
		);
	}

	private final ConfigurationProvider _configurationProvider = Mockito.mock(
		ConfigurationProvider.class);
	private final StagingConfiguration _stagingConfiguration = Mockito.mock(
		StagingConfiguration.class);
	private final StagingURLHelperImpl _stagingURLHelperImpl =
		new StagingURLHelperImpl();

}