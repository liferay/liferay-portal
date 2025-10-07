/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.mail.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.mail.settings.configuration.MailSettingSystemConfiguration;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.test.ReloadURLClassLoader;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import jakarta.mail.internet.InternetAddress;

import java.io.Closeable;
import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class MailEngineTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Test
	public void testSendMail() throws Throwable {
		try (TestMailEngine testMailEngine = new TestMailEngine()) {
			testMailEngine.send(
				new MailMessage(
					new InternetAddress("from@test.com"),
					new InternetAddress("to@test.com"), "Hello",
					"My name is Inigo Montoya.", true));

			Assert.assertEquals(1, MailServiceTestUtil.getInboxSize());

			List<com.liferay.portal.test.mail.MailMessage> mailMessages =
				MailServiceTestUtil.getMailMessages(
					"Body", "My name is Inigo Montoya.");

			Assert.assertEquals(
				mailMessages.toString(), 1, mailMessages.size());

			mailMessages = MailServiceTestUtil.getMailMessages(
				"Subject", "Hello");

			Assert.assertEquals(
				mailMessages.toString(), 1, mailMessages.size());

			mailMessages = MailServiceTestUtil.getMailMessages(
				"To", "to@test.com");

			Assert.assertEquals(
				mailMessages.toString(), 1, mailMessages.size());
		}
	}

	@Test
	public void testSendMailWithLimit() throws Throwable {
		MailMessage mailMessage = new MailMessage(
			new InternetAddress("from@test.com"),
			new InternetAddress("to@test.com"), "Hello",
			"My name is Inigo Montoya.", true);

		try (TestMailEngine testMailEngine = new TestMailEngine(
				2L, Time.YEAR / Time.SECOND)) {

			testMailEngine.send(mailMessage);
			testMailEngine.send(mailMessage);

			try {
				testMailEngine.send(mailMessage);

				Assert.fail();
			}
			catch (Throwable throwable) {
				Assert.assertEquals(
					"com.liferay.portal.kernel.exception.PortalException: " +
						"Unable to exceed maximum number of allowed mail " +
							"messages",
					throwable.getMessage());
			}

			testMailEngine.setLastResetTime(
				System.currentTimeMillis() - Time.YEAR - Time.SECOND);

			testMailEngine.send(mailMessage);
			testMailEngine.send(mailMessage);

			try {
				testMailEngine.send(mailMessage);

				Assert.fail();
			}
			catch (Throwable throwable) {
				Assert.assertEquals(
					"com.liferay.portal.kernel.exception.PortalException: " +
						"Unable to exceed maximum number of allowed mail " +
							"messages",
					throwable.getMessage());
			}
		}
	}

	@Inject
	private static MailService _mailService;

	private static class TestMailEngine implements Closeable {

		@Override
		public void close() throws IOException {
			PropsUtil.set(
				PropsKeys.DATA_LIMIT_MAIL_MESSAGE_MAX_COUNT,
				_originalMaxMailMessageCount);
			PropsUtil.set(
				PropsKeys.DATA_LIMIT_MAIL_MESSAGE_MAX_PERIOD,
				_originalMaxMailMessagePeriod);
		}

		public void send(MailMessage mailMessage) throws Throwable {
			try {
				MailSettingSystemConfiguration mailSettingSystemConfiguration =
					ConfigurableUtil.createConfigurable(
						MailSettingSystemConfiguration.class,
						Collections.emptyMap());

				_sendMethod.invoke(
					null, _mailService, mailMessage,
					mailSettingSystemConfiguration.batchSize(),
					mailSettingSystemConfiguration.throwsExceptionOnFailure());
			}
			catch (InvocationTargetException invocationTargetException) {
				throw invocationTargetException.getTargetException();
			}
		}

		public void setLastResetTime(long lastResetTime) {
			_lastResetTime.set(lastResetTime);
		}

		private TestMailEngine() throws Exception {
			this(
				GetterUtil.getLong(
					PropsUtil.get(PropsKeys.DATA_LIMIT_MAIL_MESSAGE_MAX_COUNT)),
				GetterUtil.getLong(
					PropsUtil.get(
						PropsKeys.DATA_LIMIT_MAIL_MESSAGE_MAX_PERIOD)));
		}

		private TestMailEngine(
				long maxMailMessageCount, long maxMailMessagePeriod)
			throws Exception {

			_originalMaxMailMessageCount = PropsUtil.get(
				PropsKeys.DATA_LIMIT_MAIL_MESSAGE_MAX_COUNT);
			_originalMaxMailMessagePeriod = PropsUtil.get(
				PropsKeys.DATA_LIMIT_MAIL_MESSAGE_MAX_PERIOD);

			PropsUtil.set(
				PropsKeys.DATA_LIMIT_MAIL_MESSAGE_MAX_COUNT,
				String.valueOf(maxMailMessageCount));
			PropsUtil.set(
				PropsKeys.DATA_LIMIT_MAIL_MESSAGE_MAX_PERIOD,
				String.valueOf(maxMailMessagePeriod));

			Bundle portalUtilTestBundle = FrameworkUtil.getBundle(
				MailEngineTest.class);

			Bundle mailMessagingImplBundle = BundleUtil.getBundle(
				portalUtilTestBundle.getBundleContext(),
				"com.liferay.mail.messaging.impl");

			BundleWiring bundleWiring = mailMessagingImplBundle.adapt(
				BundleWiring.class);

			ClassLoader classLoader = bundleWiring.getClassLoader();

			Class<?> mailEngineClass = classLoader.loadClass(
				"com.liferay.mail.messaging.internal.MailEngine");

			Class<?> innerClass = mailEngineClass.getDeclaredClasses()[0];

			ClassLoader reloadURLClassLoader = new ReloadURLClassLoader(
				mailEngineClass, innerClass);

			reloadURLClassLoader.loadClass(innerClass.getName());

			Class<?> reloadMailEngineClass = reloadURLClassLoader.loadClass(
				mailEngineClass.getName());

			_sendMethod = ReflectionUtil.getDeclaredMethod(
				reloadMailEngineClass, "send", MailService.class,
				MailMessage.class, String.class, boolean.class);

			Field field = ReflectionUtil.getDeclaredField(
				reloadMailEngineClass, "_lastResetTime");

			_lastResetTime = (AtomicLong)field.get(null);
		}

		private final AtomicLong _lastResetTime;
		private final String _originalMaxMailMessageCount;
		private final String _originalMaxMailMessagePeriod;
		private final Method _sendMethod;

	}

}