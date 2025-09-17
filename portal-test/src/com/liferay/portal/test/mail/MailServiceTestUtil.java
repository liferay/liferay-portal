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
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SocketUtil;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.test.mail.impl.MailMessageImpl;

import jakarta.mail.Session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;

import java.nio.channels.ServerSocketChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

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

		Object mailService = MailServiceUtil.getService();

		AopInvocationHandler aopInvocationHandler =
			ProxyUtil.fetchInvocationHandler(
				mailService, AopInvocationHandler.class);

		mailService = aopInvocationHandler.getTarget();

		ReflectionTestUtil.setFieldValue(
			mailService, "_sessions",
			ProxyUtil.newProxyInstance(
				mailService.getClass(
				).getClassLoader(),
				new Class<?>[] {Map.class},
				new InvocationHandler() {

					public Object invoke(
							Object proxy, Method method, Object[] args)
						throws Throwable {

						if (Objects.equals(method.getName(), "get")) {
							Properties properties = new Properties();

							properties.put("mail.pop3.host", "localhost");
							properties.put("mail.pop3.password", "");
							properties.put("mail.pop3.port", "110");
							properties.put("mail.pop3.user", "");
							properties.put("mail.smtp.auth", "false");
							properties.put("mail.smtp.host", "localhost");
							properties.put("mail.smtp.password", "");
							properties.put("mail.smtp.port", smtpPort);
							properties.put("mail.smtp.starttls.enable", "true");
							properties.put("mail.smtp.user", "");
							properties.put("mail.store.protocol", "pop3");
							properties.put("mail.transport.protocol", "smtp");

							return Session.getInstance(properties);
						}

						return null;
					}

				}));

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
	}

	public static void stop() throws Exception {
		if ((_smtpServer != null) && _smtpServer.isStopped()) {
			throw new IllegalStateException("Server is already stopped");
		}

		_smtpServer.stop();

		_smtpServer = null;
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

	private static SmtpServer _smtpServer;

}