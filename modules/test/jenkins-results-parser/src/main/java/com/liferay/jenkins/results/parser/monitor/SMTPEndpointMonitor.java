/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLSocket;

/**
 * @author Calum Ragan
 */
public class SMTPEndpointMonitor extends BaseMonitor {

	public SMTPEndpointMonitor(MonitorConfig monitorConfig) {
		super(monitorConfig);

		_endpointURL = getRequiredURLParameter(
			"url", monitorConfig.getParameters(), "smtp://");
	}

	@Override
	public MonitorResult execute() {
		URI uri = null;

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			uri = new URI(_endpointURL);
		}
		catch (URISyntaxException uriSyntaxException) {
			return _newInvalidURLMonitorResult(currentTimeMillis);
		}

		if (uri.getHost() == null) {
			return _newInvalidURLMonitorResult(currentTimeMillis);
		}

		try {
			return _execute(currentTimeMillis, uri);
		}
		catch (Exception exception) {
			return new MonitorResult(
				_getFailureMessage(exception), null,
				MonitorResult.Status.CRITICAL, currentTimeMillis);
		}
	}

	private MonitorResult _execute(long currentTimeMillis, URI uri)
		throws IOException {

		String host = uri.getHost();

		int port = uri.getPort();

		if (port == -1) {
			port = _PORT_DEFAULT;
		}

		int timeoutMillis = getAttemptTimeoutMillis(0);

		long deadlineMillis = System.currentTimeMillis() + (2 * timeoutMillis);

		try (Socket socket = SMTPEndpointReader.connect(
				host, port, timeoutMillis)) {

			BufferedReader bufferedReader = _newBufferedReader(socket);

			String replyCode = _readReplyCode(
				bufferedReader, deadlineMillis, socket);

			if (!replyCode.equals("220")) {
				return _newReplyCodeMonitorResult(
					currentTimeMillis, replyCode, "the connection");
			}

			replyCode = _sendCommand(
				bufferedReader, "EHLO " + _getAddressLiteral(socket),
				deadlineMillis, socket);

			if (!replyCode.equals("250")) {
				return _newReplyCodeMonitorResult(
					currentTimeMillis, replyCode, "the \"EHLO\" command");
			}

			replyCode = _sendCommand(
				bufferedReader, "STARTTLS", deadlineMillis, socket);

			if (!replyCode.equals("220")) {
				return _newReplyCodeMonitorResult(
					currentTimeMillis, replyCode, "the \"STARTTLS\" command");
			}

			_setReadTimeout(deadlineMillis, socket);

			SSLSocket sslSocket = SMTPEndpointReader.startTLS(
				host, port, socket);

			replyCode = _sendCommand(
				_newBufferedReader(sslSocket), "QUIT", deadlineMillis,
				sslSocket);

			if (!replyCode.equals("221")) {
				return _newReplyCodeMonitorResult(
					currentTimeMillis, replyCode, "the \"QUIT\" command");
			}
		}

		return new MonitorResult(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _endpointURL, " is OK"),
			null, MonitorResult.Status.OK, currentTimeMillis);
	}

	private String _getAddressLiteral(Socket socket) {
		InetAddress inetAddress = socket.getLocalAddress();

		if (inetAddress instanceof Inet6Address) {
			return "[IPv6:" + inetAddress.getHostAddress() + "]";
		}

		return "[" + inetAddress.getHostAddress() + "]";
	}

	private String _getFailureMessage(Exception exception) {
		if (exception instanceof UnknownHostException) {
			return JenkinsResultsParserUtil.combine(
				"Unable to resolve the host of ", _endpointURL);
		}

		return JenkinsResultsParserUtil.combine(
			"Unable to read ", _endpointURL, ": ",
			JenkinsResultsParserUtil.getMessage(exception));
	}

	private BufferedReader _newBufferedReader(Socket socket)
		throws IOException {

		return new BufferedReader(
			new InputStreamReader(
				socket.getInputStream(), StandardCharsets.US_ASCII));
	}

	private MonitorResult _newInvalidURLMonitorResult(long currentTimeMillis) {
		return new MonitorResult(
			getInvalidValueMessage("parameter", "url", _endpointURL), null,
			MonitorResult.Status.CRITICAL, currentTimeMillis);
	}

	private MonitorResult _newReplyCodeMonitorResult(
		long currentTimeMillis, String replyCode, String request) {

		return new MonitorResult(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _endpointURL, " returned the reply code ",
				replyCode, " to ", request),
			null, MonitorResult.Status.CRITICAL, currentTimeMillis);
	}

	private String _readReplyCode(
			BufferedReader bufferedReader, long deadlineMillis, Socket socket)
		throws IOException {

		_setReadTimeout(deadlineMillis, socket);

		String line = bufferedReader.readLine();

		while ((line != null) && (line.length() > 3) &&
			   (line.charAt(3) == '-')) {

			line = bufferedReader.readLine();
		}

		if (line == null) {
			throw new EOFException("Connection was closed");
		}

		if (line.length() < 3) {
			return line;
		}

		return line.substring(0, 3);
	}

	private String _sendCommand(
			BufferedReader bufferedReader, String command, long deadlineMillis,
			Socket socket)
		throws IOException {

		OutputStream outputStream = socket.getOutputStream();

		String line = command + "\r\n";

		outputStream.write(line.getBytes(StandardCharsets.US_ASCII));

		outputStream.flush();

		return _readReplyCode(bufferedReader, deadlineMillis, socket);
	}

	private void _setReadTimeout(long deadlineMillis, Socket socket)
		throws IOException {

		long remainingMillis = deadlineMillis - System.currentTimeMillis();

		if (remainingMillis <= 0) {
			throw new SocketTimeoutException("Timed out");
		}

		socket.setSoTimeout((int)remainingMillis);
	}

	private static final int _PORT_DEFAULT = 587;

	private final String _endpointURL;

}