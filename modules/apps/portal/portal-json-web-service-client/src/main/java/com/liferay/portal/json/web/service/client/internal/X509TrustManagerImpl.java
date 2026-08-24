/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json.web.service.client.internal;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public class X509TrustManagerImpl implements X509TrustManager {

	public X509TrustManagerImpl() {
		if (PropsValues.FIPS_ENABLED) {
			throw new SecurityException(
				"Self signed certificates are not allowed in FIPS mode");
		}

		try {
			_defaultX509TrustManager = _getX509TrustManager(null);
			_extraX509TrustManager = null;
			_trustSelfSignedCertificates = true;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public X509TrustManagerImpl(
		KeyStore keyStore, boolean trustSelfSignedCertificates) {

		if (PropsValues.FIPS_ENABLED && trustSelfSignedCertificates) {
			throw new SecurityException(
				"Self signed certificates are not allowed in FIPS mode");
		}

		try {
			_defaultX509TrustManager = _getX509TrustManager(null);

			if (keyStore != null) {
				_extraX509TrustManager = _getX509TrustManager(keyStore);
			}
			else {
				_extraX509TrustManager = null;
			}

			_trustSelfSignedCertificates = trustSelfSignedCertificates;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void checkClientTrusted(
			X509Certificate[] x509Certificates, String authType)
		throws CertificateException {

		if (!_trustSelfSignedCertificates || (x509Certificates.length != 1)) {
			try {
				_defaultX509TrustManager.checkClientTrusted(
					x509Certificates, authType);
			}
			catch (CertificateException certificateException) {
				if (_extraX509TrustManager != null) {
					_extraX509TrustManager.checkClientTrusted(
						x509Certificates, authType);
				}
				else {
					throw certificateException;
				}
			}
		}
	}

	@Override
	public void checkServerTrusted(
			X509Certificate[] x509Certificates, String authType)
		throws CertificateException {

		if (!_trustSelfSignedCertificates || (x509Certificates.length != 1)) {
			try {
				_defaultX509TrustManager.checkServerTrusted(
					x509Certificates, authType);
			}
			catch (CertificateException certificateException) {
				if (_extraX509TrustManager != null) {
					_extraX509TrustManager.checkServerTrusted(
						x509Certificates, authType);
				}
				else {
					throw certificateException;
				}
			}
		}
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		if (_extraX509TrustManager != null) {
			return ArrayUtil.append(
				_defaultX509TrustManager.getAcceptedIssuers(),
				_extraX509TrustManager.getAcceptedIssuers());
		}

		return _defaultX509TrustManager.getAcceptedIssuers();
	}

	private X509TrustManager _getX509TrustManager(KeyStore keyStore)
		throws Exception {

		TrustManagerFactory trustManagerFactory =
			TrustManagerFactory.getInstance(
				TrustManagerFactory.getDefaultAlgorithm());

		trustManagerFactory.init(keyStore);

		for (TrustManager trustManager :
				trustManagerFactory.getTrustManagers()) {

			if (trustManager instanceof X509TrustManager) {
				return (X509TrustManager)trustManager;
			}
		}

		return null;
	}

	private final X509TrustManager _defaultX509TrustManager;
	private final X509TrustManager _extraX509TrustManager;
	private final boolean _trustSelfSignedCertificates;

}