/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.certificate;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.runtime.certificate.CertificateTool;

import java.security.KeyPair;
import java.security.PrivateKey;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.opensaml.security.crypto.KeySupport;

/**
 * @author Pedro Victor Silvestre
 */
public class KeySupportTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void test() throws Exception {
		KeyPair keyPair = _certificateTool.generateKeyPair("RSA", 2048);

		PrivateKey privateKey = keyPair.getPrivate();

		byte[] bytes = privateKey.getEncoded();

		PrivateKey decodedPrivateKey = KeySupport.decodePrivateKey(bytes, null);

		Assert.assertArrayEquals(bytes, decodedPrivateKey.getEncoded());

		String pemKey = StringBundler.concat(
			"-----BEGIN PRIVATE KEY-----\n", Base64.encode(bytes),
			"\n-----END PRIVATE KEY-----");

		PrivateKey decodedPEMPrivateKey = KeySupport.decodePrivateKey(
			pemKey.getBytes(), null);

		Assert.assertArrayEquals(bytes, decodedPEMPrivateKey.getEncoded());
	}

	private final CertificateTool _certificateTool = new CertificateToolImpl();

}