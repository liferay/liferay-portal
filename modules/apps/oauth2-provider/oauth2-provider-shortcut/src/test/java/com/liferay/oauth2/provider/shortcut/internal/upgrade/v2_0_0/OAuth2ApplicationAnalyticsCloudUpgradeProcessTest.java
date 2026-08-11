/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.shortcut.internal.upgrade.v2_0_0;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Leslie Wong
 */
public class OAuth2ApplicationAnalyticsCloudUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.doAnswer(
			invocationOnMock -> {
				UnsafeConsumer<Long, Exception> unsafeConsumer =
					invocationOnMock.getArgument(0);

				unsafeConsumer.accept(_COMPANY_ID);

				return null;
			}
		).when(
			_companyLocalService
		).forEachCompanyId(
			Mockito.any()
		);
	}

	@Test
	public void testUpgradeAddsRedirectURI() throws Exception {
		OAuth2Application oAuth2Application = _mockOAuth2Application(
			Collections.singletonList(
				"https://analytics.liferay.com/oauth/receive"));

		_upgradeProcess.doUpgrade();

		Mockito.verify(
			oAuth2Application
		).setHomePageURL(
			"https://ldp.liferay.com"
		);

		ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(
			List.class);

		Mockito.verify(
			oAuth2Application
		).setRedirectURIsList(
			argumentCaptor.capture()
		);

		Assert.assertEquals(
			Arrays.asList(
				"https://analytics.liferay.com/oauth/receive",
				"https://ldp.liferay.com/oauth/receive"),
			argumentCaptor.getValue());

		Mockito.verify(
			_oAuth2ApplicationLocalService
		).updateOAuth2Application(
			oAuth2Application
		);
	}

	@Test
	public void testUpgradeDoesNotDuplicateRedirectURI() throws Exception {
		OAuth2Application oAuth2Application = _mockOAuth2Application(
			Arrays.asList(
				"https://analytics.liferay.com/oauth/receive",
				"https://ldp.liferay.com/oauth/receive"));

		_upgradeProcess.doUpgrade();

		Mockito.verify(
			oAuth2Application
		).setHomePageURL(
			"https://ldp.liferay.com"
		);

		Mockito.verify(
			oAuth2Application, Mockito.never()
		).setRedirectURIsList(
			Mockito.anyList()
		);

		Mockito.verify(
			_oAuth2ApplicationLocalService
		).updateOAuth2Application(
			oAuth2Application
		);
	}

	@Test
	public void testUpgradeSkipsCompanyWithoutOAuth2Application()
		throws Exception {

		Mockito.when(
			_oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					"ANALYTICS-CLOUD", _COMPANY_ID)
		).thenReturn(
			null
		);

		_upgradeProcess.doUpgrade();

		Mockito.verify(
			_oAuth2ApplicationLocalService, Mockito.never()
		).updateOAuth2Application(
			Mockito.any()
		);
	}

	private OAuth2Application _mockOAuth2Application(
		List<String> redirectURIsList) {

		OAuth2Application oAuth2Application = Mockito.mock(
			OAuth2Application.class);

		Mockito.when(
			oAuth2Application.getRedirectURIsList()
		).thenReturn(
			redirectURIsList
		);

		Mockito.when(
			_oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					"ANALYTICS-CLOUD", _COMPANY_ID)
		).thenReturn(
			oAuth2Application
		);

		return oAuth2Application;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final CompanyLocalService _companyLocalService = Mockito.mock(
		CompanyLocalService.class);
	private final OAuth2ApplicationLocalService _oAuth2ApplicationLocalService =
		Mockito.mock(OAuth2ApplicationLocalService.class);
	private final OAuth2ApplicationAnalyticsCloudUpgradeProcess
		_upgradeProcess = new OAuth2ApplicationAnalyticsCloudUpgradeProcess(
			_companyLocalService, _oAuth2ApplicationLocalService);

}