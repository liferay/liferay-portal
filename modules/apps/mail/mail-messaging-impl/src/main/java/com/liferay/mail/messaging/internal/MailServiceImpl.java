/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.messaging.internal;

import com.liferay.mail.kernel.auth.token.provider.MailAuthTokenProvider;
import com.liferay.mail.kernel.auth.token.provider.MailAuthTokenProviderRegistryUtil;
import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.mail.settings.configuration.MailSettingCompanyConfiguration;
import com.liferay.mail.settings.configuration.MailSettingSystemConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.jndi.JNDIUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

import java.io.IOException;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.mail.settings.configuration.MailSettingSystemConfiguration",
	service = AopService.class
)
@CTAware
public class MailServiceImpl
	implements AopService, IdentifiableOSGiService, MailService {

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
	public Class<?>[] getAopInterfaces() {
		return new Class<?>[] {
			MailService.class, IdentifiableOSGiService.class
		};
	}

	@Override
	public String getMailId(String mx, String popPortletPrefix, Object... ids) {
		StringBundler sb = new StringBundler((ids.length * 2) + 7);

		sb.append(StringPool.LESS_THAN);
		sb.append(popPortletPrefix);

		if (!popPortletPrefix.endsWith(StringPool.PERIOD)) {
			sb.append(StringPool.PERIOD);
		}

		for (int i = 0; i < ids.length; i++) {
			Object id = ids[i];

			if (i != 0) {
				sb.append(StringPool.PERIOD);
			}

			sb.append(id);
		}

		sb.append(StringPool.AT);

		if (Validator.isNotNull(getPOPServerSubdomain())) {
			sb.append(getPOPServerSubdomain());
			sb.append(StringPool.PERIOD);
		}

		sb.append(mx);
		sb.append(StringPool.GREATER_THAN);

		return sb.toString();
	}

	@Override
	public String getOSGiServiceIdentifier() {
		return MailService.class.getName();
	}

	@Override
	public String getPOPServerSubdomain() {
		return _mailSettingSystemConfiguration.popServerSubdomain();
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

		String jndiName = _mailSettingSystemConfiguration.jndiName();

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

		MailSettingCompanyConfiguration mailSettingCompanyConfiguration = null;

		try {
			mailSettingCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					MailSettingCompanyConfiguration.class, companyId);
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		String advancedPropertiesString =
			mailSettingCompanyConfiguration.additionalJavaMailProperties();
		String pop3Host = mailSettingCompanyConfiguration.incomingPOPServer();
		String pop3Password = mailSettingCompanyConfiguration.popPassword();
		int pop3Port = GetterUtil.getInteger(
			mailSettingCompanyConfiguration.incomingPOPPort());
		String pop3User = mailSettingCompanyConfiguration.popUserName();
		String smtpHost = mailSettingCompanyConfiguration.outgoingSMTPServer();
		String smtpPassword = mailSettingCompanyConfiguration.smtpPassword();
		int smtpPort = GetterUtil.getInteger(
			mailSettingCompanyConfiguration.outgoingSMTPPort());
		boolean smtpStartTLSEnable = GetterUtil.getBoolean(
			mailSettingCompanyConfiguration.enableStartTLS());
		String smtpUser = mailSettingCompanyConfiguration.smtpUserName();
		String storeProtocol = mailSettingCompanyConfiguration.storeProtocol();
		String transportProtocol =
			mailSettingCompanyConfiguration.transportProtocol();

		Properties properties = new Properties();

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
	public boolean isPOPServerNotificationsEnabled(long companyId) {
		MailSettingCompanyConfiguration mailSettingCompanyConfiguration = null;

		try {
			mailSettingCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					MailSettingCompanyConfiguration.class, companyId);
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		return mailSettingCompanyConfiguration.enablePOPServerNotifications();
	}

	@Override
	public boolean isPOPServerUser(String emailAddress) {
		MailSettingCompanyConfiguration mailSettingCompanyConfiguration = null;

		try {
			mailSettingCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					MailSettingCompanyConfiguration.class,
					CompanyThreadLocal.getCompanyId());
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		return StringUtil.equalsIgnoreCase(
			emailAddress, mailSettingCompanyConfiguration.popUserName());
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

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_mailSettingSystemConfiguration = ConfigurableUtil.createConfigurable(
			MailSettingSystemConfiguration.class, properties);
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

	@Reference
	private ConfigurationProvider _configurationProvider;

	private volatile MailSettingSystemConfiguration
		_mailSettingSystemConfiguration;
	private final Map<Long, Session> _sessions = new ConcurrentHashMap<>();

}