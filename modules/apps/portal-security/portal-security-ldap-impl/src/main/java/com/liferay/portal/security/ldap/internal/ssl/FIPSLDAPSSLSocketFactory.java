/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.ssl;

import com.liferay.portal.kernel.util.ArrayUtil;

import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;

import java.security.GeneralSecurityException;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author Jorge García Jiménez
 */
public class FIPSLDAPSSLSocketFactory extends SocketFactory {

	public static SocketFactory getDefault() {
		return _INSTANCE;
	}

	public static void setCipherSuitesOverride(String[] cipherSuites) {
		if (ArrayUtil.isEmpty(cipherSuites)) {
			_cipherSuitesOverride.remove();
		}
		else {
			_cipherSuitesOverride.set(cipherSuites.clone());
		}
	}

	@Override
	public Socket createSocket() throws IOException {
		return _constrain((SSLSocket)_sslSocketFactory.createSocket());
	}

	@Override
	public Socket createSocket(InetAddress address, int port)
		throws IOException {

		return _constrain(
			(SSLSocket)_sslSocketFactory.createSocket(address, port));
	}

	@Override
	public Socket createSocket(
			InetAddress address, int port, InetAddress localAddress,
			int localPort)
		throws IOException {

		return _constrain(
			(SSLSocket)_sslSocketFactory.createSocket(
				address, port, localAddress, localPort));
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		return _constrain(
			(SSLSocket)_sslSocketFactory.createSocket(host, port));
	}

	@Override
	public Socket createSocket(
			String host, int port, InetAddress localAddress, int localPort)
		throws IOException {

		return _constrain(
			(SSLSocket)_sslSocketFactory.createSocket(
				host, port, localAddress, localPort));
	}

	private FIPSLDAPSSLSocketFactory() {
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");

			sslContext.init(null, null, null);

			_sslSocketFactory = sslContext.getSocketFactory();
		}
		catch (GeneralSecurityException generalSecurityException) {
			throw new IllegalStateException(
				"Unable to initialize FIPS LDAP SSL context",
				generalSecurityException);
		}
	}

	private SSLSocket _constrain(SSLSocket sslSocket) {
		sslSocket.setEnabledProtocols(
			_intersect(_ENABLED_PROTOCOLS, sslSocket.getSupportedProtocols()));

		String[] cipherSuitesOverride = _cipherSuitesOverride.get();

		String[] desired =
			(cipherSuitesOverride != null) ? cipherSuitesOverride :
				_FIPS_CIPHER_SUITES_ALLOWLIST;

		String[] enabled = _intersect(
			desired, sslSocket.getSupportedCipherSuites());

		if (enabled.length == 0) {
			throw new IllegalStateException(
				"No FIPS-approved cipher suites are supported by the " +
					"installed JSSE provider; check the FIPS JCE/JSSE " +
						"configuration");
		}

		sslSocket.setEnabledCipherSuites(enabled);

		return sslSocket;
	}

	private String[] _intersect(String[] desired, String[] supported) {
		Set<String> supportedSet = new LinkedHashSet<>(
			Arrays.asList(supported));

		Set<String> result = new LinkedHashSet<>();

		for (String candidate : desired) {
			if (supportedSet.contains(candidate)) {
				result.add(candidate);
			}
		}

		return result.toArray(new String[0]);
	}

	private static final String[] _ENABLED_PROTOCOLS = {"TLSv1.2", "TLSv1.3"};

	private static final String[] _FIPS_CIPHER_SUITES_ALLOWLIST = {
		"TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384",
		"TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
		"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
		"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
		"TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"
	};

	private static final FIPSLDAPSSLSocketFactory _INSTANCE =
		new FIPSLDAPSSLSocketFactory();

	private static final ThreadLocal<String[]> _cipherSuitesOverride =
		new ThreadLocal<>();

	private final SSLSocketFactory _sslSocketFactory;

}