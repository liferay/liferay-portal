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
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class OAuth2ApplicationLDPDomainUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_user = TestPropsValues.getUser();

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					_EXTERNAL_REFERENCE_CODE, _user.getCompanyId());

		if (oAuth2Application == null) {
			_oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
				_EXTERNAL_REFERENCE_CODE, _user.getUserId(),
				_user.getScreenName(),
				Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
				"client_secret_post", _user.getUserId(),
				OAuth2SecureRandomGenerator.generateClientId(),
				ClientProfile.HEADLESS_SERVER.id(),
				OAuth2SecureRandomGenerator.generateClientSecret(), null, null,
				_HOME_PAGE_URL_ORIGINAL, 0, null, RandomTestUtil.randomString(),
				null, Collections.singletonList(_REDIRECT_URI_ORIGINAL), false,
				false,
				builder -> {
				},
				new ServiceContext());

			return;
		}

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update OAuth2Application set homePageURL = ?, redirectURIs " +
					"= ? where oAuth2ApplicationId = ?")) {

			preparedStatement.setString(1, _HOME_PAGE_URL_ORIGINAL);
			preparedStatement.setString(2, _REDIRECT_URI_ORIGINAL);
			preparedStatement.setLong(
				3, oAuth2Application.getOAuth2ApplicationId());

			preparedStatement.executeUpdate();
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		_runUpgrade();

		Assert.assertEquals(
			"https://ldp.liferay.com", _getColumnValue("homePageURL"));
		Assert.assertEquals(
			Arrays.asList(_REDIRECT_URI_ORIGINAL, _REDIRECT_URI_LDP),
			_getRedirectURIs());
	}

	@Test
	public void testUpgradeDoesNotDuplicateRedirectURI() throws Exception {
		_runUpgrade();
		_runUpgrade();

		Assert.assertEquals(
			Arrays.asList(_REDIRECT_URI_ORIGINAL, _REDIRECT_URI_LDP),
			_getRedirectURIs());
	}

	private String _getColumnValue(String columnName) throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", columnName, " from OAuth2Application where ",
					"companyId = ? and externalReferenceCode = ?"))) {

			preparedStatement.setLong(1, _user.getCompanyId());
			preparedStatement.setString(2, _EXTERNAL_REFERENCE_CODE);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				return resultSet.getString(columnName);
			}
		}
	}

	private List<String> _getRedirectURIs() throws Exception {
		return ListUtil.fromArray(
			StringUtil.split(
				_getColumnValue("redirectURIs"), CharPool.NEW_LINE));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.oauth2.provider.shortcut.internal.upgrade.v2_0_0." +
			"OAuth2ApplicationLDPDomainUpgradeProcess";

	private static final String _EXTERNAL_REFERENCE_CODE = "ANALYTICS-CLOUD";

	private static final String _HOME_PAGE_URL_ORIGINAL =
		"https://" + RandomTestUtil.randomString();

	private static final String _REDIRECT_URI_LDP =
		"https://ldp.liferay.com/oauth/receive";

	private static final String _REDIRECT_URI_ORIGINAL =
		"https://" + RandomTestUtil.randomString() + "/oauth/receive";

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.oauth2.provider.shortcut.internal.upgrade.registry.OAuth2ProviderShortcutUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	private User _user;

}