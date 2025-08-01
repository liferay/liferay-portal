/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.mail;

import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;
import com.dumbster.smtp.mailstores.RollingMailStore;

import com.liferay.mail.kernel.service.MailServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PrefsPropsTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SocketUtil;
import com.liferay.portal.test.mail.impl.MailMessageImpl;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;

import java.nio.channels.ServerSocketChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Adam Brandizzi
 */
public class MailServiceTestUtil {

	public static void clearMessages() {
		_smtpServer.clearMessages();
	}

	public static int getInboxSize() {
		return _smtpServer.getEmailCount();
	}

	public static MailMessage getLastMailMessage() {
		com.dumbster.smtp.MailMessage[] mailMessages =
			_smtpServer.getMessages();

		if (mailMessages.length > 0) {
			return new MailMessageImpl(mailMessages[mailMessages.length - 1]);
		}

		throw new IndexOutOfBoundsException(
			"There are no messages in the inbox");
	}

	public static MailMessage getMailMessage(
		String headerName, String[] headerValues) {

		Arrays.sort(headerValues);

		for (com.dumbster.smtp.MailMessage mailMessage :
				_smtpServer.getMessages()) {

			String mailMessageHeaderValue = mailMessage.getFirstHeaderValue(
				headerName);

			if (mailMessageHeaderValue == null) {
				continue;
			}

			String[] mailMessageHeaderValues = mailMessageHeaderValue.split(
				"[,;]\\s*");

			Arrays.sort(mailMessageHeaderValues);

			if (Arrays.equals(mailMessageHeaderValues, headerValues)) {
				return new MailMessageImpl(mailMessage);
			}
		}

		return null;
	}

	public static List<MailMessage> getMailMessages(
		String headerName, String headerValue) {

		List<com.dumbster.smtp.MailMessage> mailMessages = new ArrayList<>();

		for (com.dumbster.smtp.MailMessage mailMessage :
				_smtpServer.getMessages()) {

			if (headerName.equals("Body")) {
				String body = mailMessage.getBody();

				if (body.equals(headerValue)) {
					mailMessages.add(mailMessage);
				}
			}
			else {
				String mailMessageHeaderValue = mailMessage.getFirstHeaderValue(
					headerName);

				if (mailMessageHeaderValue.equals(headerValue)) {
					mailMessages.add(mailMessage);
				}
			}
		}

		return _wrapMailMessages(mailMessages);
	}

	public static boolean lastMailMessageContains(String text) {
		MailMessage mailMessage = getLastMailMessage();

		String bodyMailMessage = mailMessage.getBody();

		return bodyMailMessage.contains(text);
	}

	public static void start() throws Exception {
		if (_smtpServer != null) {
			throw new IllegalStateException("Server is already running");
		}

		int smtpPort = _getFreePort();

		_safeCloseable = PrefsPropsTestUtil.swapWithSafeCloseable(
			CompanyThreadLocal.getCompanyId(), PropsKeys.MAIL_SESSION_MAIL,
			true);

		_smtpServer = new SmtpServer();

		_smtpServer.setMailStore(
			new RollingMailStore() {

				@Override
				public void addMessage(com.dumbster.smtp.MailMessage message) {
					try {
						List<com.dumbster.smtp.MailMessage> receivedMail =
							ReflectionTestUtil.getFieldValue(
								this, "receivedMail");

						receivedMail.add(message);

						if (getEmailCount() > 100) {
							receivedMail.remove(0);
						}
					}
					catch (Exception exception) {
						throw new RuntimeException(exception);
					}
				}

			});
		_smtpServer.setPort(smtpPort);
		_smtpServer.setThreaded(false);

		ReflectionTestUtil.invoke(
			SmtpServerFactory.class, "startServerThread",
			new Class<?>[] {SmtpServer.class}, _smtpServer);

		MailServiceUtil.clearSession();
	}

	public static void stop() throws Exception {
		if ((_smtpServer != null) && _smtpServer.isStopped()) {
			throw new IllegalStateException("Server is already stopped");
		}

		_smtpServer.stop();

		_smtpServer = null;

		_safeCloseable.close();

		MailServiceUtil.clearSession();
	}

	private static int _getFreePort() throws Exception {
		try (ServerSocketChannel serverSocketChannel =
				SocketUtil.createServerSocketChannel(
					InetAddress.getLocalHost(), _START_PORT,
					new SocketUtil.ServerSocketConfigurator() {

						@Override
						public void configure(ServerSocket serverSocket)
							throws SocketException {

							serverSocket.setReuseAddress(true);
						}

					})) {

			ServerSocket serverSocket = serverSocketChannel.socket();

			return serverSocket.getLocalPort();
		}
	}

	private static List<MailMessage> _wrapMailMessages(
		List<com.dumbster.smtp.MailMessage> mailMessages) {

		return TransformUtil.transform(
			mailMessages, mailMessage -> new MailMessageImpl(mailMessage));
	}

	private static final int _START_PORT = 3241;

	private static SafeCloseable _safeCloseable;
	private static SmtpServer _smtpServer;

}