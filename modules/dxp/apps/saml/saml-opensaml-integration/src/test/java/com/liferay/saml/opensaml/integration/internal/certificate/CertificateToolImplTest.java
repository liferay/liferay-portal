/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.certificate;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.runtime.certificate.CertificateEntityId;

import java.math.BigInteger;

import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import java.util.Arrays;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Pedro Victor Silvestre
 * @author Manuele Castro
 */
public class CertificateToolImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_keyPair = _certificateToolImpl.generateKeyPair("RSA", 2048);
	}

	@After
	public void tearDown() throws Exception {
		if (_autoCloseable != null) {
			_autoCloseable.close();

			_autoCloseable = null;
		}
	}

	@Test
	public void testGenerateCertificate() throws Exception {
		String country = RandomTestUtil.randomString();
		String commonName = RandomTestUtil.randomString();
		String locality = RandomTestUtil.randomString();
		String organization = RandomTestUtil.randomString();
		String organizationUnit = RandomTestUtil.randomString();
		String state = RandomTestUtil.randomString();

		X509Certificate x509Certificate1 = _generateCertificate(
			country, commonName, locality, organization, organizationUnit,
			state);

		X500Principal subjectX500Principal =
			x509Certificate1.getSubjectX500Principal();

		Assert.assertEquals(
			x509Certificate1.getIssuerX500Principal(), subjectX500Principal);

		String subjectDN = subjectX500Principal.getName();

		Assert.assertTrue(subjectDN.contains("C=" + country));
		Assert.assertTrue(subjectDN.contains("CN=" + commonName));
		Assert.assertTrue(subjectDN.contains("L=" + locality));
		Assert.assertTrue(subjectDN.contains("O=" + organization));
		Assert.assertTrue(subjectDN.contains("OU=" + organizationUnit));
		Assert.assertTrue(subjectDN.contains("ST=" + state));

		String fingerprint = _certificateToolImpl.getFingerprint(
			"SHA-256", x509Certificate1);

		String[] fingerprintParts = fingerprint.split(":");

		Assert.assertEquals(
			Arrays.toString(fingerprintParts), 32, fingerprintParts.length);

		PublicKey keyPairPublicKey = _keyPair.getPublic();

		x509Certificate1.verify(keyPairPublicKey);

		PublicKey certificatePublicKey = x509Certificate1.getPublicKey();

		Assert.assertArrayEquals(
			keyPairPublicKey.getEncoded(), certificatePublicKey.getEncoded());

		boolean[] keyUsage = x509Certificate1.getKeyUsage();

		Assert.assertTrue(keyUsage[0]);
		Assert.assertTrue(keyUsage[2]);

		x509Certificate1.checkValidity();

		Assert.assertEquals(3, x509Certificate1.getVersion());
		Assert.assertEquals(-1, x509Certificate1.getBasicConstraints());

		X509Certificate x509Certificate2 = _generateCertificate(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Assert.assertNotEquals(
			x509Certificate1.getSerialNumber(),
			x509Certificate2.getSerialNumber());
	}

	@Test
	public void testGenerateKeyPair() throws Exception {
		_assertKeyPair(512, _certificateToolImpl.generateKeyPair("RSA", 512));
		_assertKeyPair(2048, _certificateToolImpl.generateKeyPair("RSA", 2048));

		KeyPair keyPair = _certificateToolImpl.generateKeyPair("DSA", 2048);

		PublicKey publicKey = keyPair.getPublic();

		Assert.assertEquals("DSA", publicKey.getAlgorithm());

		_autoCloseable = ReflectionTestUtil.setFieldValueWithAutoCloseable(
			PropsValues.class, "FIPS_ENABLED", true);

		_assertFail("DSA", 2048);
		_assertFail("RSA", 512);
		_assertFail("RSA", 1024);

		_assertKeyPair(2048, _certificateToolImpl.generateKeyPair("RSA", 2048));
		_assertKeyPair(3072, _certificateToolImpl.generateKeyPair("RSA", 3072));
		_assertKeyPair(4096, _certificateToolImpl.generateKeyPair("RSA", 4096));
	}

	private void _assertFail(String algorithm, int keySize) {
		Assert.assertThrows(
			InvalidParameterException.class,
			() -> _certificateToolImpl.generateKeyPair(algorithm, keySize));
	}

	private void _assertKeyPair(int expectedBitLength, KeyPair keyPair) {
		PublicKey publicKey = keyPair.getPublic();

		Assert.assertEquals("RSA", publicKey.getAlgorithm());

		RSAPublicKey rsaPublicKey = (RSAPublicKey)publicKey;

		BigInteger modulus = rsaPublicKey.getModulus();

		Assert.assertEquals(expectedBitLength, modulus.bitLength());
	}

	private X509Certificate _generateCertificate(
			String country, String commonName, String locality,
			String organization, String organizationUnit, String state)
		throws Exception {

		CertificateEntityId certificateEntityId = new CertificateEntityId(
			commonName, organization, organizationUnit, locality, state,
			country);

		Date startDate = new Date();

		Date endDate = new Date(
			startDate.getTime() + (365L * 24 * 60 * 60 * 1000));

		return _certificateToolImpl.generateCertificate(
			_keyPair, certificateEntityId, certificateEntityId, startDate,
			endDate, "SHA256withRSA");
	}

	private AutoCloseable _autoCloseable;
	private final CertificateToolImpl _certificateToolImpl =
		new CertificateToolImpl();
	private KeyPair _keyPair;

}