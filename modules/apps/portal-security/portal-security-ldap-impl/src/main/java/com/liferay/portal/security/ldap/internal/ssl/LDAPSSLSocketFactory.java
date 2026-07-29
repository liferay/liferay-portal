/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.ssl;

import com.liferay.portal.kernel.security.fips.FIPSModeValidator;

import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;

import java.security.GeneralSecurityException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author Jorge García Jiménez
 */
public class LDAPSSLSocketFactory extends SocketFactory {

	public static SocketFactory getDefault() {
		return _ldapSSLSocketFactory;
	}

	@Override
	public Socket createSocket() throws IOException {
		SSLSocket sslSocket = (SSLSocket)_sslSocketFactory.createSocket();

		_configure(sslSocket);

		return new LDAPSSLSocket(sslSocket);
	}

	@Override
	public Socket createSocket(InetAddress inetAddress, int port)
		throws IOException {

		SSLSocket sslSocket = (SSLSocket)_sslSocketFactory.createSocket(
			inetAddress, port);

		_configure(sslSocket);

		return sslSocket;
	}

	@Override
	public Socket createSocket(
			InetAddress inetAddress, int port, InetAddress localAddress,
			int localPort)
		throws IOException {

		SSLSocket sslSocket = (SSLSocket)_sslSocketFactory.createSocket(
			inetAddress, port, localAddress, localPort);

		_configure(sslSocket);

		return sslSocket;
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		SSLSocket sslSocket = (SSLSocket)_sslSocketFactory.createSocket(
			host, port);

		_configure(sslSocket);

		return sslSocket;
	}

	@Override
	public Socket createSocket(
			String host, int port, InetAddress inetAddress, int localPort)
		throws IOException {

		SSLSocket sslSocket = (SSLSocket)_sslSocketFactory.createSocket(
			host, port, inetAddress, localPort);

		_configure(sslSocket);

		return sslSocket;
	}

	private LDAPSSLSocketFactory() {
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");

			sslContext.init(null, null, null);

			_sslSocketFactory = sslContext.getSocketFactory();
		}
		catch (GeneralSecurityException generalSecurityException) {
			throw new IllegalStateException(
				"Unable to initialize LDAP SSL context",
				generalSecurityException);
		}
	}

	private void _configure(SSLSocket sslSocket) {
		SSLParameters sslParameters = sslSocket.getSSLParameters();

		sslParameters.setCipherSuites(
			FIPSModeValidator.getAllowedTLSCipherSuites(
				sslSocket.getSupportedCipherSuites()));
		sslParameters.setEndpointIdentificationAlgorithm("LDAPS");
		sslParameters.setProtocols(
			FIPSModeValidator.getAllowedTLSProtocols(
				sslSocket.getSupportedProtocols()));

		sslSocket.setSSLParameters(sslParameters);
	}

	private static final LDAPSSLSocketFactory _ldapSSLSocketFactory =
		new LDAPSSLSocketFactory();

	private final SSLSocketFactory _sslSocketFactory;

}