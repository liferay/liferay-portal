/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.FIPSAlgorithmTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.MessageDigest;

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

}