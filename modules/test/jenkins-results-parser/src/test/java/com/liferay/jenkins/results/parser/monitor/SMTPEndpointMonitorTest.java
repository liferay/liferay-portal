/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class SMTPEndpointMonitorTest
	extends com.liferay.jenkins.results.parser.Test {

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		SMTPEndpointReader.setInstance(new SMTPEndpointReader());
	}

	@Test
	public void testExecuteConnectFailure() throws Exception {
		_testExecuteConnectFailure(
			JenkinsResultsParserUtil.combine(
				"Unable to read ", _URL, ": Connect timed out"),
			new SocketTimeoutException("Connect timed out"));
		_testExecuteConnectFailure(
			JenkinsResultsParserUtil.combine(
				"Unable to read ", _URL, ": Connection refused"),
			new ConnectException("Connection refused"));
		_testExecuteConnectFailure(
			JenkinsResultsParserUtil.combine(
				"Unable to resolve the host of ", _URL),
			new UnknownHostException(_HOST));
	}

	@Test
	public void testExecuteDefaultPort() throws Exception {
		SMTPEndpointReader smtpEndpointReader = _mockSMTPEndpointReader();

		Mockito.doThrow(
			new ConnectException("Connection refused")
		).when(
			smtpEndpointReader
		).doConnect(
			_HOST, 587, 27000
		);

		String url = "smtp://" + _HOST;

		MonitorResult monitorResult = _execute(url);

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Unable to read ", url, ": Connection refused"),
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteHandshakeFailure() throws Exception {
		SMTPEndpointReader smtpEndpointReader = _mockSMTPEndpointReader();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		Socket socket = _mockConnect(
			_newInputStream(
				"220\r\n250 ", RandomTestUtil.randomString(), "\r\n220\r\n"),
			byteArrayOutputStream, smtpEndpointReader);

		Mockito.doThrow(
			new SSLHandshakeException(
				"PKIX path validation failed: validity check failed")
		).when(
			smtpEndpointReader
		).doStartTLS(
			_HOST, _PORT, socket
		);

		MonitorResult monitorResult = _execute(_URL);

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Unable to read ", _URL,
				": PKIX path validation failed: validity check failed"),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());

		testEquals(
			"EHLO [127.0.0.1]\r\nSTARTTLS\r\n",
			byteArrayOutputStream.toString());
	}

	@Test
	public void testExecuteInvalidURL() throws Exception {
		_testExecuteInvalidURL("smtp:///" + RandomTestUtil.randomString());
		_testExecuteInvalidURL(
			JenkinsResultsParserUtil.combine(
				"smtp://", RandomTestUtil.randomString(), " ",
				RandomTestUtil.randomString()));
	}

	@Test
	public void testExecuteOK() throws Exception {
		_testExecuteOK(
			"EHLO [127.0.0.1]\r\nSTARTTLS\r\n",
			InetAddress.getByName("127.0.0.1"));
		_testExecuteOK(
			"EHLO [IPv6:0:0:0:0:0:0:0:1]\r\nSTARTTLS\r\n",
			InetAddress.getByName("::1"));
	}

	@Test
	public void testExecuteQuitReplyCode() throws Exception {
		SMTPEndpointReader smtpEndpointReader = _mockSMTPEndpointReader();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		Socket socket = _mockConnect(
			_newInputStream(
				"220\r\n250 ", RandomTestUtil.randomString(), "\r\n220\r\n"),
			byteArrayOutputStream, smtpEndpointReader);

		ByteArrayOutputStream sslByteArrayOutputStream =
			new ByteArrayOutputStream();

		SSLSocket sslSocket = _mockSocket(
			SSLSocket.class,
			_newInputStream("500 ", RandomTestUtil.randomString(), "\r\n"),
			sslByteArrayOutputStream);

		Mockito.doReturn(
			sslSocket
		).when(
			smtpEndpointReader
		).doStartTLS(
			_HOST, _PORT, socket
		);

		MonitorResult monitorResult = _execute(_URL);

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _URL,
				" returned the reply code 500 to the \"QUIT\" command"),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());

		testEquals(
			"EHLO [127.0.0.1]\r\nSTARTTLS\r\n",
			byteArrayOutputStream.toString());
		testEquals("QUIT\r\n", sslByteArrayOutputStream.toString());
	}

	@Test
	public void testExecuteReadFailure() throws Exception {
		_testExecuteReadFailure(
			JenkinsResultsParserUtil.combine(
				"Unable to read ", _URL, ": Connection was closed"),
			_newInputStream());

		InputStream inputStream = Mockito.mock(InputStream.class);

		Mockito.doThrow(
			new SocketTimeoutException("Read timed out")
		).when(
			inputStream
		).read(
			Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt()
		);

		_testExecuteReadFailure(
			JenkinsResultsParserUtil.combine(
				"Unable to read ", _URL, ": Read timed out"),
			inputStream);
	}

	@Test
	public void testExecuteReplyCode() throws Exception {
		_testExecuteReplyCode(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _URL,
				" returned the reply code 454 to the \"STARTTLS\" command"),
			"EHLO [127.0.0.1]\r\nSTARTTLS\r\n",
			_newInputStream(
				"220\r\n250 ", RandomTestUtil.randomString(), "\r\n454 ",
				RandomTestUtil.randomString(), "\r\n"));
		_testExecuteReplyCode(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _URL,
				" returned the reply code 55 to the connection"),
			"", _newInputStream("55\r\n"));
		_testExecuteReplyCode(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _URL,
				" returned the reply code 550 to the \"EHLO\" command"),
			"EHLO [127.0.0.1]\r\n",
			_newInputStream(
				"220\r\n550 ", RandomTestUtil.randomString(), "\r\n"));
		_testExecuteReplyCode(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _URL,
				" returned the reply code 554 to the connection"),
			"", _newInputStream("554 ", RandomTestUtil.randomString(), "\r\n"));
	}

	@Test
	public void testSMTPEndpointMonitor() {
		String url = "https://" + RandomTestUtil.randomString();

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Invalid url for monitor[a].parameter[url]: ", url),
			_testSMTPEndpointMonitorExpectedIllegalArgumentException(
				_newMonitorProperties(url)));

		Properties monitorProperties = new Properties();

		monitorProperties.setProperty("monitor[a].type", "smtp-endpoint");

		testEquals(
			"Missing required property monitor[a].parameter[url]",
			_testSMTPEndpointMonitorExpectedIllegalArgumentException(
				monitorProperties));
	}

	@Test
	public void testSMTPEndpointMonitorUserInfo() {
		String password = RandomTestUtil.randomString();

		String message =
			_testSMTPEndpointMonitorExpectedIllegalArgumentException(
				_newMonitorProperties(
					JenkinsResultsParserUtil.combine(
						"smtp://", RandomTestUtil.randomString(), ":", password,
						"@", _HOST)));

		Assert.assertFalse(message.contains(password));
		Assert.assertTrue(message.contains("[REDACTED]"));
	}

	private void _assertReadTimeouts(List<Integer> readTimeouts) {
		Assert.assertTrue(readTimeouts.get(0) > 27000);

		int previousReadTimeout = 54000;

		for (int readTimeout : readTimeouts) {
			Assert.assertTrue(readTimeout <= previousReadTimeout);
			Assert.assertTrue(readTimeout > 0);

			previousReadTimeout = readTimeout;
		}
	}

	private MonitorResult _execute(String url) {
		SMTPEndpointMonitor smtpEndpointMonitor = _newSMTPEndpointMonitor(
			_newMonitorProperties(url));

		return smtpEndpointMonitor.execute();
	}

	private Socket _mockConnect(
			InputStream inputStream, InetAddress localAddress,
			OutputStream outputStream, SMTPEndpointReader smtpEndpointReader)
		throws Exception {

		Socket socket = _mockSocket(Socket.class, inputStream, outputStream);

		Mockito.doReturn(
			localAddress
		).when(
			socket
		).getLocalAddress();

		Mockito.doReturn(
			socket
		).when(
			smtpEndpointReader
		).doConnect(
			_HOST, _PORT, 27000
		);

		return socket;
	}

	private Socket _mockConnect(
			InputStream inputStream, OutputStream outputStream,
			SMTPEndpointReader smtpEndpointReader)
		throws Exception {

		return _mockConnect(
			inputStream, InetAddress.getByName("127.0.0.1"), outputStream,
			smtpEndpointReader);
	}

	private SMTPEndpointReader _mockSMTPEndpointReader() {
		SMTPEndpointReader smtpEndpointReader = Mockito.mock(
			SMTPEndpointReader.class,
			invocation -> {
				String host = invocation.getArgument(0);

				throw new AssertionError("No output set for host: " + host);
			});

		SMTPEndpointReader.setInstance(smtpEndpointReader);

		return smtpEndpointReader;
	}

	private <T extends Socket> T _mockSocket(
			Class<T> clazz, InputStream inputStream, OutputStream outputStream)
		throws Exception {

		T socket = Mockito.mock(clazz);

		Mockito.doReturn(
			inputStream
		).when(
			socket
		).getInputStream();

		Mockito.doReturn(
			outputStream
		).when(
			socket
		).getOutputStream();

		return socket;
	}

	private InputStream _newInputStream(String... strings) {
		String string = JenkinsResultsParserUtil.combine(strings);

		return new ByteArrayInputStream(
			string.getBytes(StandardCharsets.US_ASCII));
	}

	private Properties _newMonitorProperties(String url) {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty("monitor[a].parameter[url]", url);
		monitorProperties.setProperty("monitor[a].type", "smtp-endpoint");

		return monitorProperties;
	}

	private SMTPEndpointMonitor _newSMTPEndpointMonitor(
		Properties monitorProperties) {

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		return new SMTPEndpointMonitor(monitorConfigs.get(0));
	}

	private void _testExecuteConnectFailure(
			String expectedMessage, IOException ioException)
		throws Exception {

		SMTPEndpointReader smtpEndpointReader = _mockSMTPEndpointReader();

		Mockito.doThrow(
			ioException
		).when(
			smtpEndpointReader
		).doConnect(
			_HOST, _PORT, 27000
		);

		MonitorResult monitorResult = _execute(_URL);

		testEquals(expectedMessage, monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
	}

	private void _testExecuteInvalidURL(String url) throws Exception {
		_mockSMTPEndpointReader();

		MonitorResult monitorResult = _execute(url);

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Invalid url for monitor[a].parameter[url]: ", url),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
	}

	private void _testExecuteOK(String expectedOutput, InetAddress localAddress)
		throws Exception {

		SMTPEndpointReader smtpEndpointReader = _mockSMTPEndpointReader();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		Socket socket = _mockConnect(
			_newInputStream(
				"220\r\n250-", RandomTestUtil.randomString(), "\r\n250 ",
				RandomTestUtil.randomString(), "\r\n220 ",
				RandomTestUtil.randomString(), "\r\n"),
			localAddress, byteArrayOutputStream, smtpEndpointReader);

		ByteArrayOutputStream sslByteArrayOutputStream =
			new ByteArrayOutputStream();

		SSLSocket sslSocket = _mockSocket(
			SSLSocket.class,
			_newInputStream("221 ", RandomTestUtil.randomString(), "\r\n"),
			sslByteArrayOutputStream);

		Mockito.doAnswer(
			invocation -> {
				Thread.sleep(10);

				return sslSocket;
			}
		).when(
			smtpEndpointReader
		).doStartTLS(
			_HOST, _PORT, socket
		);

		MonitorResult monitorResult = _execute(_URL);

		testEquals(
			JenkinsResultsParserUtil.combine("Endpoint ", _URL, " is OK"),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		testEquals(expectedOutput, byteArrayOutputStream.toString());
		testEquals("QUIT\r\n", sslByteArrayOutputStream.toString());

		ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(
			Integer.class);

		Mockito.verify(
			socket, Mockito.times(4)
		).setSoTimeout(
			argumentCaptor.capture()
		);

		Mockito.verify(
			sslSocket
		).setSoTimeout(
			argumentCaptor.capture()
		);

		List<Integer> readTimeouts = argumentCaptor.getAllValues();

		_assertReadTimeouts(readTimeouts);

		Assert.assertTrue(readTimeouts.get(4) < readTimeouts.get(0));
	}

	private void _testExecuteReadFailure(
			String expectedMessage, InputStream inputStream)
		throws Exception {

		SMTPEndpointReader smtpEndpointReader = _mockSMTPEndpointReader();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		Socket socket = _mockConnect(
			inputStream, byteArrayOutputStream, smtpEndpointReader);

		MonitorResult monitorResult = _execute(_URL);

		testEquals(expectedMessage, monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());

		testEquals("", byteArrayOutputStream.toString());

		ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(
			Integer.class);

		Mockito.verify(
			socket
		).setSoTimeout(
			argumentCaptor.capture()
		);

		_assertReadTimeouts(argumentCaptor.getAllValues());
	}

	private void _testExecuteReplyCode(
			String expectedMessage, String expectedOutput,
			InputStream inputStream)
		throws Exception {

		SMTPEndpointReader smtpEndpointReader = _mockSMTPEndpointReader();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		_mockConnect(inputStream, byteArrayOutputStream, smtpEndpointReader);

		MonitorResult monitorResult = _execute(_URL);

		testEquals(expectedMessage, monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());

		testEquals(expectedOutput, byteArrayOutputStream.toString());
	}

	private String _testSMTPEndpointMonitorExpectedIllegalArgumentException(
		Properties monitorProperties) {

		try {
			_newSMTPEndpointMonitor(monitorProperties);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			return illegalArgumentException.getMessage();
		}

		return null;
	}

	private static final String _HOST = RandomTestUtil.randomString();

	private static final int _PORT = 2525;

	private static final String _URL = JenkinsResultsParserUtil.combine(
		"smtp://", _HOST, ":", String.valueOf(_PORT));

}