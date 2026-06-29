/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.certificate;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.runtime.certificate.CertificateEntityId;
import com.liferay.saml.runtime.certificate.CertificateTool;

import java.io.ByteArrayInputStream;

import java.math.BigInteger;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.util.Date;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = CertificateTool.class)
public class CertificateToolImpl implements CertificateTool {

	@Override
	public X509Certificate generateCertificate(
			KeyPair keyPair, CertificateEntityId issuerCertificateEntityId,
			CertificateEntityId subjectCertificateEntityId, Date startDate,
			Date endDate, String signatureAlgorithm)
		throws CertificateException {

		JcaX509CertificateConverter jcaX509CertificateConverter =
			new JcaX509CertificateConverter();

		PublicKey publicKey = keyPair.getPublic();

		try (ByteArrayInputStream byteArrayInputStream =
				new ByteArrayInputStream(publicKey.getEncoded());

			ASN1InputStream asn1InputStream = new ASN1InputStream(
				byteArrayInputStream)) {

			X500Name issuerX500Name = _createX500Name(
				issuerCertificateEntityId);
			X500Name subjectX500Name = _createX500Name(
				subjectCertificateEntityId);

			X509v1CertificateBuilder x509v1CertificateBuilder =
				new X509v1CertificateBuilder(
					issuerX500Name,
					BigInteger.valueOf(System.currentTimeMillis()), startDate,
					endDate, subjectX500Name,
					SubjectPublicKeyInfo.getInstance(
						asn1InputStream.readObject()));

			JcaContentSignerBuilder jcaContentSignerBuilder =
				new JcaContentSignerBuilder(signatureAlgorithm);

			return jcaX509CertificateConverter.getCertificate(
				x509v1CertificateBuilder.build(
					jcaContentSignerBuilder.build(keyPair.getPrivate())));
		}
		catch (Exception exception) {
			throw new CertificateException(exception);
		}
	}

	@Override
	public KeyPair generateKeyPair(String algorithm, int keySize)
		throws NoSuchAlgorithmException {

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
			algorithm);

		keyPairGenerator.initialize(keySize);

		return keyPairGenerator.genKeyPair();
	}

	@Override
	public String getFingerprint(
			String algorithm, X509Certificate x509Certificate)
		throws CertificateException, NoSuchAlgorithmException {

		MessageDigest messageDigest = MessageDigest.getInstance(algorithm);

		messageDigest.update(x509Certificate.getEncoded());

		byte[] digest = messageDigest.digest();

		StringBundler sb = new StringBundler((digest.length * 2) - 1);

		for (int i = 0; i < digest.length; i++) {
			String hex = String.format("%02X", digest[i]);

			sb.append(hex);

			if ((i + 1) < digest.length) {
				sb.append(CharPool.COLON);
			}
		}

		return sb.toString();
	}

	@Override
	public String getSerialNumber(X509Certificate x509Certificate) {
		BigInteger serialNumber = x509Certificate.getSerialNumber();

		byte[] bytes = serialNumber.toByteArray();

		StringBundler sb = new StringBundler(bytes.length);

		for (byte b : bytes) {
			sb.append(Integer.toHexString(b & 0xff));
		}

		return sb.toString();
	}

	@Override
	public String getSubjectName(X509Certificate x509Certificate) {
		if (x509Certificate == null) {
			return null;
		}

		Principal principal = x509Certificate.getSubjectDN();

		if (principal != null) {
			return principal.getName();
		}

		return null;
	}

	private X500Name _createX500Name(CertificateEntityId certificateEntityId) {
		X500NameBuilder x500NameBuilder = new X500NameBuilder(BCStyle.INSTANCE);

		if (Validator.isNotNull(certificateEntityId.getCommonName())) {
			x500NameBuilder.addRDN(
				BCStyle.CN, certificateEntityId.getCommonName());
		}

		if (Validator.isNotNull(certificateEntityId.getOrganization())) {
			x500NameBuilder.addRDN(
				BCStyle.O, certificateEntityId.getOrganization());
		}

		if (Validator.isNotNull(certificateEntityId.getOrganizationUnit())) {
			x500NameBuilder.addRDN(
				BCStyle.OU, certificateEntityId.getOrganizationUnit());
		}

		if (Validator.isNotNull(certificateEntityId.getLocality())) {
			x500NameBuilder.addRDN(
				BCStyle.L, certificateEntityId.getLocality());
		}

		if (Validator.isNotNull(certificateEntityId.getState())) {
			x500NameBuilder.addRDN(BCStyle.ST, certificateEntityId.getState());
		}

		if (Validator.isAlphanumericName(certificateEntityId.getCountry())) {
			x500NameBuilder.addRDN(BCStyle.C, certificateEntityId.getCountry());
		}

		return x500NameBuilder.build();
	}

}