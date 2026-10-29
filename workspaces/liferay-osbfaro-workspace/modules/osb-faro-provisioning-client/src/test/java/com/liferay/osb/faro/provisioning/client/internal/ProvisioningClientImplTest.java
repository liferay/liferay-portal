/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.internal;

import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.util.KoroneikiHttpUtil;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class ProvisioningClientImplTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "FARO_PROJECT_ID",
			RandomTestUtil.randomString());
		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "MOCK_PROJECT_ID",
			RandomTestUtil.randomString());

		ReflectionTestUtil.setFieldValue(
			_provisioningClientImpl, "_portal", _portal);
		ReflectionTestUtil.setFieldValue(
			_provisioningClientImpl, "_userLocalService", _userLocalService);
	}

	@Test
	public void testAddCorpProjectUsers() throws Exception {
		try (MockedStatic<KoroneikiHttpUtil> koroneikiHttpUtilMockedStatic =
				Mockito.mockStatic(KoroneikiHttpUtil.class)) {

			_provisioningClientImpl.addCorpProjectUsers(
				FaroPropsValues.FARO_PROJECT_ID,
				new String[] {RandomTestUtil.randomString()});
			_provisioningClientImpl.addCorpProjectUsers(
				FaroPropsValues.MOCK_PROJECT_ID,
				new String[] {RandomTestUtil.randomString()});

			koroneikiHttpUtilMockedStatic.verifyNoInteractions();
		}
	}

	@Test
	public void testAddUserCorpProjectRoles() throws Exception {
		try (MockedStatic<KoroneikiHttpUtil> koroneikiHttpUtilMockedStatic =
				Mockito.mockStatic(KoroneikiHttpUtil.class)) {

			_provisioningClientImpl.addUserCorpProjectRoles(
				FaroPropsValues.FARO_PROJECT_ID,
				new String[] {RandomTestUtil.randomString()},
				RandomTestUtil.randomString());
			_provisioningClientImpl.addUserCorpProjectRoles(
				FaroPropsValues.MOCK_PROJECT_ID,
				new String[] {RandomTestUtil.randomString()},
				RandomTestUtil.randomString());

			koroneikiHttpUtilMockedStatic.verifyNoInteractions();
		}
	}

	@Test
	public void testGetOSBAccountEntries() throws Exception {
		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "FARO_MOCK_OSB_ACCOUNT_ENTRY",
			RandomTestUtil.randomString());

		List<OSBAccountEntry> osbAccountEntries =
			_provisioningClientImpl.getOSBAccountEntries(
				RandomTestUtil.randomString(), new String[0]);

		Assert.assertEquals(
			osbAccountEntries.toString(), 1, osbAccountEntries.size());

		OSBAccountEntry osbAccountEntry = osbAccountEntries.get(0);

		Assert.assertEquals("Mock Project", osbAccountEntry.getName());

		ReflectionTestUtil.setFieldValue(
			FaroPropsValues.class, "FARO_MOCK_OSB_ACCOUNT_ENTRY", null);

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getEmailAddress()
		).thenReturn(
			"test@liferay.com"
		);

		Mockito.when(
			_userLocalService.fetchUserByUuidAndCompanyId(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			user
		);

		osbAccountEntries = _provisioningClientImpl.getOSBAccountEntries(
			RandomTestUtil.randomString(), new String[0]);

		Assert.assertTrue(osbAccountEntries.isEmpty());
	}

	@Test
	public void testGetOSBAccountEntry() throws Exception {
		OSBAccountEntry osbAccountEntry =
			_provisioningClientImpl.getOSBAccountEntry(
				FaroPropsValues.FARO_PROJECT_ID);

		Assert.assertEquals("Liferay Demo", osbAccountEntry.getName());

		osbAccountEntry = _provisioningClientImpl.getOSBAccountEntry(
			FaroPropsValues.MOCK_PROJECT_ID);

		Assert.assertEquals("Mock Project", osbAccountEntry.getName());
	}

	private final Portal _portal = Mockito.mock(Portal.class);
	private final ProvisioningClientImpl _provisioningClientImpl =
		new ProvisioningClientImpl();
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}