/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.ssl;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.InetSocketAddress;
import java.net.Socket;

import java.util.List;

import javax.net.SocketFactory;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class LDAPSSLSocketFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testConnect() throws Exception {
		SSLSocket sslSocket = Mockito.mock(SSLSocket.class);

		Mockito.when(
			sslSocket.getSSLParameters()
		).thenReturn(
			new SSLParameters()
		);

		LDAPSSLSocket ldapSSLSocket = new LDAPSSLSocket(sslSocket);

		ldapSSLSocket.connect(
			InetSocketAddress.createUnresolved("localhost", 636));

		ArgumentCaptor<SSLParameters> argumentCaptor = ArgumentCaptor.forClass(
			SSLParameters.class);

		Mockito.verify(
			sslSocket
		).setSSLParameters(
			argumentCaptor.capture()
		);

		SSLParameters sslParameters = argumentCaptor.getValue();

		List<SNIServerName> serverNames = sslParameters.getServerNames();

		SNIHostName sniHostName = (SNIHostName)serverNames.get(0);

		Assert.assertEquals("localhost", sniHostName.getAsciiName());

		Assert.assertEquals(serverNames.toString(), 1, serverNames.size());
	}

	@Test
	public void testCreateSocket() throws Exception {
		SocketFactory socketFactory = LDAPSSLSocketFactory.getDefault();

		Socket socket = socketFactory.createSocket();

		Assert.assertTrue(socket instanceof LDAPSSLSocket);
	}

}