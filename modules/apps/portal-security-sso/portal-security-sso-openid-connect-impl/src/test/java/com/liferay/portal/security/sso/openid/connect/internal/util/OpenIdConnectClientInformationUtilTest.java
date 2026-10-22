/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.util;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.sso.openid.connect.internal.configuration.OpenIdConnectProviderConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;

import java.util.Dictionary;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Caio Farias
 */
public class OpenIdConnectClientInformationUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetOIDCClientInformation() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		long guestUserId = RandomTestUtil.randomLong();

		Mockito.when(
			_userLocalService.getGuestUserId(companyId)
		).thenReturn(
			guestUserId
		);

		String keyReferenceString = _getKeyReferenceString(
			RandomTestUtil.randomString());
		String otherKeyReferenceString = _getKeyReferenceString(
			RandomTestUtil.randomString());
		String systemKeyReferenceString = _getKeyReferenceString(
			RandomTestUtil.randomString());

		Configuration[] configurations = {
			_getConfiguration(
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", companyId
				).put(
					"openIdConnectClientSecret", keyReferenceString
				).build()),
			_getConfiguration(
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", companyId + 1
				).put(
					"openIdConnectClientSecret", otherKeyReferenceString
				).build()),
			_getConfiguration(
				HashMapDictionaryBuilder.<String, Object>put(
					"openIdConnectClientSecret", systemKeyReferenceString
				).build())
		};

		Mockito.when(
			_configurationAdmin.listConfigurations(
				StringBundler.concat(
					"(|(service.factoryPid=",
					OpenIdConnectProviderConfiguration.class.getName(),
					")(service.factoryPid=",
					OpenIdConnectProviderConfiguration.class.getName(),
					".scoped))"))
		).thenReturn(
			configurations
		);

		String clientSecret = RandomTestUtil.randomString();

		Mockito.when(
			_secretResolver.resolve(Mockito.eq(companyId), Mockito.anyString())
		).thenReturn(
			clientSecret
		);

		long oAuthClientEntryId = RandomTestUtil.randomLong();

		_testGetOIDCClientInformation(
			keyReferenceString, companyId, clientSecret, oAuthClientEntryId,
			guestUserId);
		_testGetOIDCClientInformation(
			keyReferenceString, companyId, keyReferenceString,
			oAuthClientEntryId, guestUserId + 1);
		_testGetOIDCClientInformation(
			otherKeyReferenceString, companyId, otherKeyReferenceString,
			oAuthClientEntryId, guestUserId);
		_testGetOIDCClientInformation(
			systemKeyReferenceString, companyId, clientSecret,
			oAuthClientEntryId, guestUserId);

		_testGetOIDCClientInformation(
			_getKeyReferenceString(
				StringBundler.concat(
					OAuthClientEntry.class.getSimpleName(), StringPool.SLASH,
					companyId, StringPool.SLASH, oAuthClientEntryId)),
			companyId, clientSecret, oAuthClientEntryId, guestUserId + 1);
		_testGetOIDCClientInformation(
			clientSecret, companyId, clientSecret, oAuthClientEntryId,
			guestUserId + 1);

		Mockito.when(
			_configurationAdmin.listConfigurations(Mockito.anyString())
		).thenReturn(
			null
		);

		_testGetOIDCClientInformation(
			keyReferenceString, companyId, keyReferenceString,
			oAuthClientEntryId, guestUserId);
	}

	private Configuration _getConfiguration(
		Dictionary<String, Object> properties) {

		Configuration configuration = Mockito.mock(Configuration.class);

		Mockito.when(
			configuration.getProperties()
		).thenReturn(
			properties
		);

		return configuration;
	}

	private String _getKeyReferenceString(String identifier) {
		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				identifier, RandomTestUtil.randomString(),
				KeyReference.Type.SECRET));
	}

	private void _testGetOIDCClientInformation(
			String clientSecret, long companyId, String expectedClientSecret,
			long oAuthClientEntryId, long userId)
		throws Exception {

		OAuthClientEntry oAuthClientEntry = Mockito.mock(
			OAuthClientEntry.class);

		Mockito.when(
			oAuthClientEntry.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			oAuthClientEntry.getInfoJSON()
		).thenReturn(
			JSONUtil.put(
				"client_id", RandomTestUtil.randomString()
			).put(
				"client_secret", clientSecret
			).toString()
		);

		Mockito.when(
			oAuthClientEntry.getOAuthClientEntryId()
		).thenReturn(
			oAuthClientEntryId
		);

		Mockito.when(
			oAuthClientEntry.getUserId()
		).thenReturn(
			userId
		);

		OIDCClientInformation oidcClientInformation =
			OpenIdConnectClientInformationUtil.getOIDCClientInformation(
				_configurationAdmin, oAuthClientEntry, _secretResolver,
				_userLocalService);

		Secret secret = oidcClientInformation.getSecret();

		Assert.assertEquals(expectedClientSecret, secret.getValue());
	}

	private final ConfigurationAdmin _configurationAdmin = Mockito.mock(
		ConfigurationAdmin.class);
	private final SecretResolver _secretResolver = Mockito.mock(
		SecretResolver.class);
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}