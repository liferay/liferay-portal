/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.shortcut.internal.upgrade.v2_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class OAuth2ApplicationAnalyticsCloudUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		User user = TestPropsValues.getUser();

		_setUpOAuth2Application(user);

		_runUpgrade();

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					"ANALYTICS-CLOUD", user.getCompanyId());

		Assert.assertEquals(
			"https://ldp.liferay.com", oAuth2Application.getHomePageURL());
		Assert.assertEquals(
			Arrays.asList(
				"https://analytics.liferay.com/oauth/receive",
				"https://ldp.liferay.com/oauth/receive"),
			oAuth2Application.getRedirectURIsList());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private void _setUpOAuth2Application(User user) throws Exception {
		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					"ANALYTICS-CLOUD", user.getCompanyId());

		if (oAuth2Application == null) {
			_oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
				"ANALYTICS-CLOUD", user.getUserId(), user.getScreenName(),
				Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
				"client_secret_post", user.getUserId(),
				OAuth2SecureRandomGenerator.generateClientId(),
				ClientProfile.HEADLESS_SERVER.id(),
				OAuth2SecureRandomGenerator.generateClientSecret(), null, null,
				"https://analytics.liferay.com", 0, null, "Analytics Cloud",
				null,
				Collections.singletonList(
					"https://analytics.liferay.com/oauth/receive"),
				false, false,
				builder -> {
				},
				new ServiceContext());

			return;
		}

		oAuth2Application.setHomePageURL("https://analytics.liferay.com");
		oAuth2Application.setRedirectURIsList(
			Collections.singletonList(
				"https://analytics.liferay.com/oauth/receive"));

		_oAuth2ApplicationLocalService.updateOAuth2Application(
			oAuth2Application);
	}

	private static final String _CLASS_NAME =
		"com.liferay.oauth2.provider.shortcut.internal.upgrade.v2_0_0." +
			"OAuth2ApplicationAnalyticsCloudUpgradeProcess";

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.oauth2.provider.shortcut.internal.upgrade.registry.OAuth2ProviderShortcutUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}