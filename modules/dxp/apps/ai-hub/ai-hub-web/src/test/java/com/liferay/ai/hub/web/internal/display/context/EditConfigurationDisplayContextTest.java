/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.ai.hub.web.internal.test.util.DisplayContextTestUtil;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Pedro Leite
 */
public class EditConfigurationDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		HttpServletRequest httpServletRequest =
			DisplayContextTestUtil.setUpHttpServletRequest();

		DisplayContextTestUtil.setUpThemeDisplay(
			Mockito.mock(Company.class), Mockito.mock(Group.class),
			httpServletRequest);

		_oAuth2ApplicationLocalService = Mockito.mock(
			OAuth2ApplicationLocalService.class);

		_editConfigurationDisplayContext = new EditConfigurationDisplayContext(
			httpServletRequest, _oAuth2ApplicationLocalService);
	}

	@Test
	public void testGetReactData() throws Exception {
		try (MockedStatic<AccountEntryUtil> accountEntryUtilMockedStatic =
				Mockito.mockStatic(AccountEntryUtil.class)) {

			AccountEntry accountEntry = Mockito.mock(AccountEntry.class);

			Mockito.when(
				accountEntry.getAccountEntryId()
			).thenReturn(
				1L
			);

			accountEntryUtilMockedStatic.when(
				() -> AccountEntryUtil.getUserAccountEntry(Mockito.anyLong())
			).thenReturn(
				accountEntry
			);

			OAuth2Application oAuth2Application = Mockito.mock(
				OAuth2Application.class);

			Mockito.when(
				oAuth2Application.getClientId()
			).thenReturn(
				"test-client-id"
			);

			Mockito.when(
				_oAuth2ApplicationLocalService.
					fetchOAuth2ApplicationByExternalReferenceCode(
						Mockito.anyString(), Mockito.anyLong())
			).thenReturn(
				oAuth2Application
			);

			Map<String, Object> reactData =
				_editConfigurationDisplayContext.getReactData();

			Assert.assertEquals(1L, reactData.get("accountEntryId"));
			Assert.assertEquals(
				"http://localhost:8080/web/test", reactData.get("backURL"));
			Assert.assertEquals("test-client-id", reactData.get("clientId"));
			Assert.assertEquals(
				"1-ai-hub-configuration",
				reactData.get("externalReferenceCode"));
		}

		try (MockedStatic<AccountEntryUtil> accountEntryUtilMockedStatic =
				Mockito.mockStatic(AccountEntryUtil.class)) {

			accountEntryUtilMockedStatic.when(
				() -> AccountEntryUtil.getUserAccountEntry(Mockito.anyLong())
			).thenReturn(
				null
			);

			Map<String, Object> reactData =
				_editConfigurationDisplayContext.getReactData();

			Assert.assertNull(reactData.get("accountEntryId"));
			Assert.assertEquals(
				"http://localhost:8080/web/test", reactData.get("backURL"));
			Assert.assertNull(reactData.get("clientId"));
			Assert.assertNull(reactData.get("externalReferenceCode"));
		}
	}

	private EditConfigurationDisplayContext _editConfigurationDisplayContext;
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}