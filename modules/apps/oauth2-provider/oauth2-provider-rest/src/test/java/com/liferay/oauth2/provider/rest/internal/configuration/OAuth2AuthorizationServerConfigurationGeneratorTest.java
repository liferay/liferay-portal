/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.configuration;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jwk.KeyType;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Caio Farias
 */
public class OAuth2AuthorizationServerConfigurationGeneratorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testActivate() throws Exception {
		OAuth2AuthorizationServerConfigurationGenerator
			oAuth2AuthorizationServerConfigurationGenerator =
				new OAuth2AuthorizationServerConfigurationGenerator();

		Configuration configuration = Mockito.mock(Configuration.class);

		ConfigurationAdmin configurationAdmin = Mockito.mock(
			ConfigurationAdmin.class);

		Mockito.when(
			configurationAdmin.getConfiguration(
				OAuth2AuthorizationServerConfiguration.class.getName(),
				StringPool.QUESTION)
		).thenReturn(
			configuration
		);

		ReflectionTestUtil.setFieldValue(
			oAuth2AuthorizationServerConfigurationGenerator,
			"_configurationAdmin", configurationAdmin);

		String identifier = StringBundler.concat(
			"config/", OAuth2AuthorizationServerConfiguration.class.getName(),
			"/0/oauth2.authorization.server.jwt.access.token.signing.json.web.",
			"key");
		String keyReferenceString = RandomTestUtil.randomString();
		SecretResolver secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			secretResolver.store(
				Mockito.eq(CompanyConstants.SYSTEM), Mockito.eq(identifier),
				Mockito.anyString())
		).thenReturn(
			keyReferenceString
		);

		ReflectionTestUtil.setFieldValue(
			oAuth2AuthorizationServerConfigurationGenerator, "_secretResolver",
			secretResolver);

		oAuth2AuthorizationServerConfigurationGenerator.activate();

		ArgumentCaptor<String> valueArgumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			secretResolver
		).store(
			Mockito.eq(CompanyConstants.SYSTEM), Mockito.eq(identifier),
			valueArgumentCaptor.capture()
		);

		JsonWebKey jsonWebKey = JwkUtils.readJwkKey(
			valueArgumentCaptor.getValue());

		Assert.assertEquals("RS256", jsonWebKey.getAlgorithm());
		Assert.assertEquals(KeyType.RSA, jsonWebKey.getKeyType());
		Assert.assertNotNull(
			jsonWebKey.getProperty(JsonWebKey.RSA_PRIVATE_EXP));

		ArgumentCaptor<Dictionary<String, Object>> dictionaryArgumentCaptor =
			ArgumentCaptor.forClass(Dictionary.class);

		Mockito.verify(
			configuration
		).update(
			dictionaryArgumentCaptor.capture()
		);

		Dictionary<String, Object> dictionary =
			dictionaryArgumentCaptor.getValue();

		Assert.assertEquals(
			keyReferenceString,
			dictionary.get(
				"oauth2.authorization.server.jwt.access.token.signing.json." +
					"web.key"));
	}

}