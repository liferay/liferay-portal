/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.certificate;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.runtime.certificate.CertificateEntityId;
import com.liferay.saml.runtime.certificate.CertificateTool;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Pedro Victor Silvestre
 */
public class CertificateToolImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_certificateTool = new CertificateToolImpl();

		_keyPair = _certificateTool.generateKeyPair("RSA", 2048);
	}

	@Test
	public void testGenerateCertificate() throws Exception {
		long time = System.currentTimeMillis();

		X509Certificate x509Certificate = _certificateTool.generateCertificate(
			_keyPair, _certificateEntityId, _certificateEntityId,
			new Date(time), new Date(time + Time.YEAR), "SHA256withRSA");

		x509Certificate.verify(_keyPair.getPublic());

		x509Certificate.checkValidity();

		Assert.assertEquals(
			x509Certificate.getIssuerX500Principal(),
			x509Certificate.getSubjectX500Principal());

		String subjectName = _certificateTool.getSubjectName(x509Certificate);

		Assert.assertTrue(
			subjectName, subjectName.contains("CN=" + _COMMON_NAME));

		String fingerprint = _certificateTool.getFingerprint(
			"SHA-256", x509Certificate);

		String[] fingerprintParts = fingerprint.split(":");

		Assert.assertEquals(
			Arrays.toString(fingerprintParts), 32, fingerprintParts.length);
	}

	private static final String _COMMON_NAME = RandomTestUtil.randomString();

	private final CertificateEntityId _certificateEntityId =
		new CertificateEntityId(
			_COMMON_NAME, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	private CertificateTool _certificateTool;
	private KeyPair _keyPair;

}