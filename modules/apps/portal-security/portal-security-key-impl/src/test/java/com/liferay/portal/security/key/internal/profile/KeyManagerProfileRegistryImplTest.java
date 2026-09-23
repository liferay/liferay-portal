/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.profile;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.internal.profile.configuration.KeyManagerConfiguration;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Christopher Kian
 */
public class KeyManagerProfileRegistryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		_setActiveProfileId(CustomKeyManagerProfile.PROFILE_ID);
		_setServiceTrackerMap(_serviceTrackerMap);
	}

	@Test
	public void testGetActiveKeyManagerProfile() {
		_testGetActiveKeyManagerProfile(CustomKeyManagerProfile.PROFILE_ID);
		_testGetActiveKeyManagerProfile(RandomTestUtil.randomString());
	}

	@Test
	public void testInit() throws Exception {
		_testInit(null, 1, 2, CustomKeyManagerProfile.PROFILE_ID);
		_testInit(
			CustomKeyManagerProfile.PROFILE_ID, 0, 1,
			RandomTestUtil.randomString());
	}

	@Test
	public void testInitResetsAfterFailure() throws Exception {
		_setActiveProfileId(null);

		KeyManagerProfile keyManagerProfile = Mockito.mock(
			KeyManagerProfile.class);

		Mockito.when(
			keyManagerProfile.getProfileId()
		).thenReturn(
			CustomKeyManagerProfile.PROFILE_ID
		);

		Mockito.doThrow(
			new RuntimeException()
		).when(
			keyManagerProfile
		).initialize();

		Mockito.when(
			_serviceTrackerMap.getService(CustomKeyManagerProfile.PROFILE_ID)
		).thenReturn(
			keyManagerProfile
		);

		for (int i = 0; i < 2; i++) {
			ReflectionTestUtil.invoke(
				_keyManagerProfileRegistryImpl, "_init",
				new Class<?>[] {KeyManagerProfile.class}, keyManagerProfile);
		}

		Mockito.verify(
			keyManagerProfile, Mockito.times(2)
		).initialize();
	}

	private void _setActiveProfileId(String activeProfileId) {
		KeyManagerConfiguration keyManagerConfiguration = Mockito.mock(
			KeyManagerConfiguration.class);

		Mockito.when(
			keyManagerConfiguration.activeProfileId()
		).thenReturn(
			activeProfileId
		);

		ReflectionTestUtil.setFieldValue(
			_keyManagerProfileRegistryImpl, "_keyManagerConfiguration",
			keyManagerConfiguration);
	}

	private void _setServiceTrackerMap(
		ServiceTrackerMap<String, KeyManagerProfile> serviceTrackerMap) {

		ReflectionTestUtil.setFieldValue(
			_keyManagerProfileRegistryImpl, "_serviceTrackerMap",
			serviceTrackerMap);
	}

	private void _testGetActiveKeyManagerProfile(String activeProfileId) {
		_setActiveProfileId(activeProfileId);

		Mockito.when(
			_serviceTrackerMap.getService(CustomKeyManagerProfile.PROFILE_ID)
		).thenReturn(
			_keyManagerProfile
		);

		Assert.assertSame(
			_keyManagerProfile,
			_keyManagerProfileRegistryImpl.getActiveKeyManagerProfile());
	}

	private void _testInit(
			String activeProfileId, int expectedInitializeCount,
			int invocationCount, String profileId)
		throws Exception {

		_setActiveProfileId(activeProfileId);

		KeyManagerProfile activeKeyManagerProfile = Mockito.mock(
			KeyManagerProfile.class);

		Mockito.when(
			activeKeyManagerProfile.getProfileId()
		).thenReturn(
			CustomKeyManagerProfile.PROFILE_ID
		);

		Mockito.when(
			_serviceTrackerMap.getService(CustomKeyManagerProfile.PROFILE_ID)
		).thenReturn(
			activeKeyManagerProfile
		);

		KeyManagerProfile keyManagerProfile = Mockito.mock(
			KeyManagerProfile.class);

		Mockito.when(
			keyManagerProfile.getProfileId()
		).thenReturn(
			profileId
		);

		for (int i = 0; i < invocationCount; i++) {
			ReflectionTestUtil.invoke(
				_keyManagerProfileRegistryImpl, "_init",
				new Class<?>[] {KeyManagerProfile.class}, keyManagerProfile);
		}

		Mockito.verify(
			keyManagerProfile, Mockito.times(expectedInitializeCount)
		).initialize();
	}

	@Mock
	private KeyManagerProfile _keyManagerProfile;

	private final KeyManagerProfileRegistryImpl _keyManagerProfileRegistryImpl =
		new KeyManagerProfileRegistryImpl();

	@Mock
	private ServiceTrackerMap<String, KeyManagerProfile> _serviceTrackerMap;

}