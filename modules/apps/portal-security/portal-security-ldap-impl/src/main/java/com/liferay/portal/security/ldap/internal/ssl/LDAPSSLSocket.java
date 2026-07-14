/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import java.nio.channels.SocketChannel;

import java.util.Collections;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * @author Jorge García Jiménez
 */
public class LDAPSSLSocket extends SSLSocket {

	public LDAPSSLSocket(SSLSocket sslSocket) {
		_sslSocket = sslSocket;
	}

	@Override
	public void addHandshakeCompletedListener(
		HandshakeCompletedListener listener) {

		_sslSocket.addHandshakeCompletedListener(listener);
	}

	@Override
	public void bind(SocketAddress bindpoint) throws IOException {
		_sslSocket.bind(bindpoint);
	}

	@Override
	public void close() throws IOException {
		_sslSocket.close();
	}

	@Override
	public void connect(SocketAddress endpoint) throws IOException {
		connect(endpoint, 0);
	}

	@Override
	public void connect(SocketAddress endpoint, int timeout)
		throws IOException {

		if (endpoint instanceof InetSocketAddress) {
			InetSocketAddress inetSocketAddress = (InetSocketAddress)endpoint;

			SSLParameters sslParameters = _sslSocket.getSSLParameters();

			sslParameters.setServerNames(
				Collections.singletonList(
					new SNIHostName(inetSocketAddress.getHostString())));

			_sslSocket.setSSLParameters(sslParameters);
		}

		_sslSocket.connect(endpoint, timeout);
	}

	@Override
	public SocketChannel getChannel() {
		return _sslSocket.getChannel();
	}

	@Override
	public String[] getEnabledCipherSuites() {
		return _sslSocket.getEnabledCipherSuites();
	}

	@Override
	public String[] getEnabledProtocols() {
		return _sslSocket.getEnabledProtocols();
	}

	@Override
	public boolean getEnableSessionCreation() {
		return _sslSocket.getEnableSessionCreation();
	}

	@Override
	public SSLSession getHandshakeSession() {
		return _sslSocket.getHandshakeSession();
	}

	@Override
	public InetAddress getInetAddress() {
		return _sslSocket.getInetAddress();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return _sslSocket.getInputStream();
	}

	@Override
	public boolean getKeepAlive() throws SocketException {
		return _sslSocket.getKeepAlive();
	}

	@Override
	public InetAddress getLocalAddress() {
		return _sslSocket.getLocalAddress();
	}

	@Override
	public int getLocalPort() {
		return _sslSocket.getLocalPort();
	}

	@Override
	public SocketAddress getLocalSocketAddress() {
		return _sslSocket.getLocalSocketAddress();
	}

	@Override
	public boolean getNeedClientAuth() {
		return _sslSocket.getNeedClientAuth();
	}

	@Override
	public boolean getOOBInline() throws SocketException {
		return _sslSocket.getOOBInline();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return _sslSocket.getOutputStream();
	}

	@Override
	public int getPort() {
		return _sslSocket.getPort();
	}

	@Override
	public int getReceiveBufferSize() throws SocketException {
		return _sslSocket.getReceiveBufferSize();
	}

	@Override
	public SocketAddress getRemoteSocketAddress() {
		return _sslSocket.getRemoteSocketAddress();
	}

	@Override
	public boolean getReuseAddress() throws SocketException {
		return _sslSocket.getReuseAddress();
	}

	@Override
	public int getSendBufferSize() throws SocketException {
		return _sslSocket.getSendBufferSize();
	}

	@Override
	public SSLSession getSession() {
		return _sslSocket.getSession();
	}

	@Override
	public int getSoLinger() throws SocketException {
		return _sslSocket.getSoLinger();
	}

	@Override
	public int getSoTimeout() throws SocketException {
		return _sslSocket.getSoTimeout();
	}

	@Override
	public SSLParameters getSSLParameters() {
		return _sslSocket.getSSLParameters();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return _sslSocket.getSupportedCipherSuites();
	}

	@Override
	public String[] getSupportedProtocols() {
		return _sslSocket.getSupportedProtocols();
	}

	@Override
	public boolean getTcpNoDelay() throws SocketException {
		return _sslSocket.getTcpNoDelay();
	}

	@Override
	public int getTrafficClass() throws SocketException {
		return _sslSocket.getTrafficClass();
	}

	@Override
	public boolean getUseClientMode() {
		return _sslSocket.getUseClientMode();
	}

	@Override
	public boolean getWantClientAuth() {
		return _sslSocket.getWantClientAuth();
	}

	@Override
	public boolean isBound() {
		return _sslSocket.isBound();
	}

	@Override
	public boolean isClosed() {
		return _sslSocket.isClosed();
	}

	@Override
	public boolean isConnected() {
		return _sslSocket.isConnected();
	}

	@Override
	public boolean isInputShutdown() {
		return _sslSocket.isInputShutdown();
	}

	@Override
	public boolean isOutputShutdown() {
		return _sslSocket.isOutputShutdown();
	}

	@Override
	public void removeHandshakeCompletedListener(
		HandshakeCompletedListener listener) {

		_sslSocket.removeHandshakeCompletedListener(listener);
	}

	@Override
	public void sendUrgentData(int data) throws IOException {
		_sslSocket.sendUrgentData(data);
	}

	@Override
	public void setEnabledCipherSuites(String[] suites) {
		_sslSocket.setEnabledCipherSuites(suites);
	}

	@Override
	public void setEnabledProtocols(String[] protocols) {
		_sslSocket.setEnabledProtocols(protocols);
	}

	@Override
	public void setEnableSessionCreation(boolean flag) {
		_sslSocket.setEnableSessionCreation(flag);
	}

	@Override
	public void setKeepAlive(boolean on) throws SocketException {
		_sslSocket.setKeepAlive(on);
	}

	@Override
	public void setNeedClientAuth(boolean need) {
		_sslSocket.setNeedClientAuth(need);
	}

	@Override
	public void setOOBInline(boolean on) throws SocketException {
		_sslSocket.setOOBInline(on);
	}

	@Override
	public void setPerformancePreferences(
		int connectionTime, int latency, int bandwidth) {

		_sslSocket.setPerformancePreferences(
			connectionTime, latency, bandwidth);
	}

	@Override
	public void setReceiveBufferSize(int size) throws SocketException {
		_sslSocket.setReceiveBufferSize(size);
	}

	@Override
	public void setReuseAddress(boolean on) throws SocketException {
		_sslSocket.setReuseAddress(on);
	}

	@Override
	public void setSendBufferSize(int size) throws SocketException {
		_sslSocket.setSendBufferSize(size);
	}

	@Override
	public void setSoLinger(boolean on, int linger) throws SocketException {
		_sslSocket.setSoLinger(on, linger);
	}

	@Override
	public void setSoTimeout(int timeout) throws SocketException {
		_sslSocket.setSoTimeout(timeout);
	}

	@Override
	public void setSSLParameters(SSLParameters params) {
		_sslSocket.setSSLParameters(params);
	}

	@Override
	public void setTcpNoDelay(boolean on) throws SocketException {
		_sslSocket.setTcpNoDelay(on);
	}

	@Override
	public void setTrafficClass(int tc) throws SocketException {
		_sslSocket.setTrafficClass(tc);
	}

	@Override
	public void setUseClientMode(boolean mode) {
		_sslSocket.setUseClientMode(mode);
	}

	@Override
	public void setWantClientAuth(boolean want) {
		_sslSocket.setWantClientAuth(want);
	}

	@Override
	public void shutdownInput() throws IOException {
		_sslSocket.shutdownInput();
	}

	@Override
	public void shutdownOutput() throws IOException {
		_sslSocket.shutdownOutput();
	}

	@Override
	public void startHandshake() throws IOException {
		_sslSocket.startHandshake();
	}

	@Override
	public String toString() {
		return _sslSocket.toString();
	}

	private final SSLSocket _sslSocket;

}