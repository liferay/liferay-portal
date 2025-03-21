/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.service.impl;

import com.liferay.mail.kernel.auth.token.provider.MailAuthTokenProvider;
import com.liferay.mail.kernel.auth.token.provider.MailAuthTokenProviderRegistryUtil;
import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.jndi.JNDIUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

import java.io.IOException;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * @author Brian Wing Shun Chan
 */
@CTAware
public class MailServiceImpl implements IdentifiableOSGiService, MailService {

	@Clusterable
	@Override
	public void clearSession() {
		clearSession(CompanyConstants.SYSTEM);
	}

	@Clusterable
	@Override
	public void clearSession(long companyId) {
		if (companyId == CompanyConstants.SYSTEM) {
			_sessions.clear();
		}

		_sessions.remove(companyId);
	}

	@Override
	public String getOSGiServiceIdentifier() {
		return MailService.class.getName();
	}

	@Override
	public Session getSession() {
		return getSession(CompanyThreadLocal.getCompanyId());
	}

	@Override
	public Session getSession(Account account) {
		Session session = Session.getInstance(_getProperties(account));

		_debug(session.getProperties());

		if (_log.isDebugEnabled()) {
			session.setDebug(true);
		}

		return session;
	}

	@Override
	public Session getSession(long companyId) {
		Session session = _sessions.get(companyId);

		if (session != null) {
			return session;
		}

		session = _createMailSession();

		Function<String, String> function =
			(String key) -> PrefsPropsUtil.getString(
				companyId, key,
				PrefsPropsUtil.getString(key, PropsUtil.get(key)));

		if (!GetterUtil.getBoolean(
				function.apply(PropsKeys.MAIL_SESSION_MAIL))) {

			_sessions.put(companyId, session);

			return session;
		}

		String advancedPropertiesString = function.apply(
			PropsKeys.MAIL_SESSION_MAIL_ADVANCED_PROPERTIES);
		String pop3Host = function.apply(PropsKeys.MAIL_SESSION_MAIL_POP3_HOST);
		String pop3Password = function.apply(
			PropsKeys.MAIL_SESSION_MAIL_POP3_PASSWORD);
		int pop3Port = GetterUtil.getInteger(
			function.apply(PropsKeys.MAIL_SESSION_MAIL_POP3_PORT));
		String pop3User = function.apply(PropsKeys.MAIL_SESSION_MAIL_POP3_USER);
		String smtpHost = function.apply(PropsKeys.MAIL_SESSION_MAIL_SMTP_HOST);
		String smtpPassword = function.apply(
			PropsKeys.MAIL_SESSION_MAIL_SMTP_PASSWORD);
		int smtpPort = GetterUtil.getInteger(
			function.apply(PropsKeys.MAIL_SESSION_MAIL_SMTP_PORT));
		boolean smtpStartTLSEnable = GetterUtil.getBoolean(
			function.apply(PropsKeys.MAIL_SESSION_MAIL_SMTP_STARTTLS_ENABLE));
		String smtpUser = function.apply(PropsKeys.MAIL_SESSION_MAIL_SMTP_USER);
		String storeProtocol = function.apply(
			PropsKeys.MAIL_SESSION_MAIL_STORE_PROTOCOL);
		String transportProtocol = function.apply(
			PropsKeys.MAIL_SESSION_MAIL_TRANSPORT_PROTOCOL);

		Properties properties = session.getProperties();

		// Incoming

		if (!storeProtocol.equals(Account.PROTOCOL_POPS)) {
			storeProtocol = Account.PROTOCOL_POP;
		}

		properties.setProperty("mail.store.protocol", storeProtocol);

		String storePrefix = "mail." + storeProtocol + ".";

		boolean oAuth2AuthEnable = false;

		MailAuthTokenProvider pop3MailAuthTokenProvider =
			MailAuthTokenProviderRegistryUtil.getMailAuthTokenProvider(
				companyId, pop3Host, storeProtocol);

		if (pop3MailAuthTokenProvider != null) {
			oAuth2AuthEnable = true;

			pop3Password = pop3MailAuthTokenProvider.getAccessToken(companyId);

			properties.put(storePrefix + "auth.mechanisms", "XOAUTH2");
			properties.put(
				storePrefix + "auth.xoauth2.two.line.authentication.format",
				"true");
		}

		properties.setProperty(storePrefix + "host", pop3Host);
		properties.setProperty(storePrefix + "password", pop3Password);
		properties.setProperty(storePrefix + "port", String.valueOf(pop3Port));
		properties.setProperty(storePrefix + "user", pop3User);

		// Outgoing

		if (!transportProtocol.equals(Account.PROTOCOL_SMTPS)) {
			transportProtocol = Account.PROTOCOL_SMTP;
		}

		properties.setProperty("mail.transport.protocol", transportProtocol);

		String transportPrefix = "mail." + transportProtocol + ".";

		boolean smtpAuth = false;

		if (Validator.isNotNull(smtpPassword) ||
			Validator.isNotNull(smtpUser)) {

			smtpAuth = true;
		}

		properties.setProperty(
			transportPrefix + "auth", String.valueOf(smtpAuth));

		MailAuthTokenProvider smtpMailAuthTokenProvider =
			MailAuthTokenProviderRegistryUtil.getMailAuthTokenProvider(
				companyId, smtpHost, transportProtocol);

		if (smtpMailAuthTokenProvider != null) {
			oAuth2AuthEnable = true;

			properties.put(transportPrefix + "auth.mechanisms", "XOAUTH2");
			properties.put(
				transportPrefix + "auth.xoauth2.two.line.authentication.format",
				"false");

			smtpPassword = smtpMailAuthTokenProvider.getAccessToken(companyId);
		}

		properties.setProperty(transportPrefix + "host", smtpHost);
		properties.setProperty(transportPrefix + "password", smtpPassword);
		properties.setProperty(
			transportPrefix + "port", String.valueOf(smtpPort));
		properties.setProperty(
			transportPrefix + "starttls.enable",
			String.valueOf(smtpStartTLSEnable));
		properties.setProperty(transportPrefix + "user", smtpUser);

		// Advanced

		try {
			if (Validator.isNotNull(advancedPropertiesString)) {
				Properties advancedProperties = PropertiesUtil.load(
					advancedPropertiesString);

				for (Map.Entry<Object, Object> entry :
						advancedProperties.entrySet()) {

					String key = (String)entry.getKey();
					String value = (String)entry.getValue();

					properties.setProperty(key, value);
				}
			}
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException);
			}
		}

		if (smtpAuth) {
			session = Session.getInstance(
				properties,
				new Authenticator() {

					protected PasswordAuthentication
						getPasswordAuthentication() {

						return new PasswordAuthentication(
							smtpUser,
							properties.getProperty(
								transportPrefix + "password"));
					}

				});
		}
		else {
			session = Session.getInstance(properties);
		}

		_debug(properties);

		if (_log.isDebugEnabled()) {
			session.setDebug(true);
		}

		if (!oAuth2AuthEnable) {
			_sessions.put(companyId, session);
		}

		return session;
	}

	@Override
	public void sendEmail(MailMessage mailMessage) {
		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				if (_log.isDebugEnabled()) {
					_log.debug("sendEmail");
				}

				MessageBusUtil.sendMessage(DestinationNames.MAIL, mailMessage);

				return null;
			});
	}

	private Session _createMailSession() {
		Properties properties = PropsUtil.getProperties("mail.session.", true);

		String jndiName = properties.getProperty("jndi.name");

		if (Validator.isNotNull(jndiName)) {
			try {
				Properties jndiEnvironmentProperties = PropsUtil.getProperties(
					PropsKeys.JNDI_ENVIRONMENT, true);

				Context context = new InitialContext(jndiEnvironmentProperties);

				return (Session)JNDIUtil.lookup(context, jndiName);
			}
			catch (Exception exception) {
				_log.error("Unable to lookup " + jndiName, exception);
			}
		}

		return Session.getInstance(properties);
	}

	private void _debug(Properties properties) {
		if (!_log.isDebugEnabled()) {
			return;
		}

		_log.debug("Properties:");

		for (String name : properties.stringPropertyNames()) {
			String value = properties.getProperty(name);

			if (name.contains("password")) {
				value = "***";
			}

			_log.debug(StringBundler.concat(name, StringPool.EQUAL, value));
		}
	}

	private Properties _getProperties(Account account) {
		Properties properties = new Properties();

		String protocol = account.getProtocol();

		properties.setProperty("mail.transport.protocol", protocol);
		properties.setProperty("mail." + protocol + ".host", account.getHost());
		properties.setProperty(
			"mail." + protocol + ".port", String.valueOf(account.getPort()));

		if (account.isRequiresAuthentication()) {
			properties.setProperty("mail." + protocol + ".auth", "true");
			properties.setProperty(
				"mail." + protocol + ".user", account.getUser());
			properties.setProperty(
				"mail." + protocol + ".password", account.getPassword());
		}

		if (account.isSecure()) {
			properties.setProperty(
				"mail." + protocol + ".socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
			properties.setProperty(
				"mail." + protocol + ".socketFactory.fallback", "false");
			properties.setProperty(
				"mail." + protocol + ".socketFactory.port",
				String.valueOf(account.getPort()));
		}

		return properties;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MailServiceImpl.class);

	private final Map<Long, Session> _sessions = new ConcurrentHashMap<>();

}