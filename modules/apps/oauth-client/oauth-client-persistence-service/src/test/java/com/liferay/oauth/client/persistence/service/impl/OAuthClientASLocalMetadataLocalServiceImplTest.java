/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.FIPSAlgorithmTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.MessageDigest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lucas Miranda
 */
public class OAuthClientASLocalMetadataLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGenerateLocalWellKnownURI() throws Exception {
		FIPSAlgorithmTestUtil.assertAlgorithmSwitch(
			DigesterUtil.MD5, MessageDigest.class, DigesterUtil.SHA_256,
			MessageDigest::getInstance,
			() -> ReflectionTestUtil.invoke(
				new OAuthClientASLocalMetadataLocalServiceImpl(),
				"_generateLocalWellKnownURI",
				new Class<?>[] {String.class, String.class, String.class},
				"https://" + RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), "openid-configuration"));
	}

	@Test
	public void testGenerateLocalWellKnownURIOAuthAS() {
		String issuer = "https://" + RandomTestUtil.randomString() + ".com";

		Assert.assertEquals(
			issuer + _OAUTH_AS_LOCAL_WELL_KNOWN_PATH,
			_generateOAuthASLocalWellKnownURI(issuer));

		String path = StringBundler.concat(
			StringPool.SLASH, RandomTestUtil.randomString(), StringPool.SLASH,
			RandomTestUtil.randomString());

		Assert.assertEquals(
			issuer + _OAUTH_AS_LOCAL_WELL_KNOWN_PATH + path,
			_generateOAuthASLocalWellKnownURI(issuer + path));

		String encodedPath = StringBundler.concat(
			StringPool.SLASH, RandomTestUtil.randomString(), "%20a/",
			RandomTestUtil.randomString());

		Assert.assertEquals(
			issuer + _OAUTH_AS_LOCAL_WELL_KNOWN_PATH + encodedPath,
			_generateOAuthASLocalWellKnownURI(issuer + encodedPath));
	}

	private String _generateOAuthASLocalWellKnownURI(String issuer) {
		return ReflectionTestUtil.invoke(
			new OAuthClientASLocalMetadataLocalServiceImpl(),
			"_generateLocalWellKnownURI",
			new Class<?>[] {String.class, String.class, String.class}, issuer,
			null, "oauth-authorization-server");
	}

	private static final String _OAUTH_AS_LOCAL_WELL_KNOWN_PATH =
		"/.well-known/oauth-authorization-server";

}