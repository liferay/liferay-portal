/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.certificate;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.runtime.certificate.CertificateEntityId;
import com.liferay.saml.runtime.certificate.CertificateTool;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.opensaml.security.x509.X509Support;

/**
 * @author Pedro Victor Silvestre
 */
public class X509SupportTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDecodeCertificate() throws Exception {
		KeyPair keyPair = _certificateTool.generateKeyPair("RSA", 2048);

		CertificateEntityId certificateEntityId = new CertificateEntityId(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		long time = System.currentTimeMillis();

		X509Certificate x509Certificate = _certificateTool.generateCertificate(
			keyPair, certificateEntityId, certificateEntityId, new Date(time),
			new Date(time + Time.YEAR), "SHA256withRSA");

		byte[] encodedBytes = x509Certificate.getEncoded();

		X509Certificate decodedX509Certificate = X509Support.decodeCertificate(
			encodedBytes);

		Assert.assertArrayEquals(
			encodedBytes, decodedX509Certificate.getEncoded());
	}

	@Test
	public void testGetCommonNames() {
		List<String> commonNames = X509Support.getCommonNames(
			new X500Principal("CN=leaf,CN=root,O=Liferay"));

		Assert.assertEquals(commonNames.toString(), 2, commonNames.size());
		Assert.assertEquals("leaf", commonNames.get(0));
		Assert.assertEquals("root", commonNames.get(1));
	}

	@Test
	public void testUnwrapDEROctetString() throws Exception {
		byte[] shortFormBytes = {
			0x04, 0x03, (byte)0xAA, (byte)0xBB, (byte)0xCC
		};

		Assert.assertArrayEquals(
			new byte[] {(byte)0xAA, (byte)0xBB, (byte)0xCC},
			(byte[])ReflectionTestUtil.invoke(
				X509Support.class, "unwrapDEROctetString",
				new Class<?>[] {byte[].class}, shortFormBytes));

		byte[] contentBytes = new byte[200];

		for (int i = 0; i < contentBytes.length; i++) {
			contentBytes[i] = (byte)i;
		}

		byte[] longFormBytes = new byte[3 + contentBytes.length];

		longFormBytes[0] = 0x04;
		longFormBytes[1] = (byte)0x81;
		longFormBytes[2] = (byte)contentBytes.length;

		System.arraycopy(
			contentBytes, 0, longFormBytes, 3, contentBytes.length);

		Assert.assertArrayEquals(
			contentBytes,
			(byte[])ReflectionTestUtil.invoke(
				X509Support.class, "unwrapDEROctetString",
				new Class<?>[] {byte[].class}, longFormBytes));
	}

	private final CertificateTool _certificateTool = new CertificateToolImpl();

}