/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.TriggerState;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Eric Yan
 */
@RunWith(Arquillian.class)
public class SchedulerEngineHelperImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() {
		AuditRequestContextThreadLocal.removeAuditRequestContext();
	}

	@Test
	public void testSchedulerJobGetsItsOwnAuditRequestContext()
		throws Exception {

		AuditRequestContext currentAuditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		String sessionID = RandomTestUtil.randomString();

		currentAuditRequestContext.setSessionID(sessionID);

		Bundle bundle = FrameworkUtil.getBundle(
			SchedulerEngineHelperImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String destinationName = RandomTestUtil.randomString();

		ServiceRegistration<Destination> serviceRegistration1 =
			bundleContext.registerService(
				Destination.class,
				_destinationFactory.createDestination(
					DestinationConfiguration.
						createSerialDestinationConfiguration(destinationName)),
				HashMapDictionaryBuilder.<String, Object>put(
					"destination.name", destinationName
				).build());

		List<AuditRequestContext> auditRequestContexts = new ArrayList<>();
		CountDownLatch countDownLatch = new CountDownLatch(1);
		String name = RandomTestUtil.randomString();

		ServiceRegistration<SchedulerJobConfiguration> serviceRegistration2 =
			bundleContext.registerService(
				SchedulerJobConfiguration.class,
				new SchedulerJobConfiguration() {

					@Override
					public String getDestinationName() {
						return destinationName;
					}

					@Override
					public UnsafeConsumer<Message, Exception>
						getJobExecutorUnsafeConsumer() {

						return message -> {
							auditRequestContexts.add(
								AuditRequestContextThreadLocal.
									getAuditRequestContext());

							countDownLatch.countDown();
						};
					}

					@Override
					public UnsafeRunnable<Exception>
						getJobExecutorUnsafeRunnable() {

						return null;
					}

					@Override
					public String getName() {
						return name;
					}

					@Override
					public TriggerConfiguration getTriggerConfiguration() {
						return TriggerConfiguration.createTriggerConfiguration(
							1, TimeUnit.YEAR);
					}

				},
				null);

		try {
			Message message = new Message();

			message.put(SchedulerEngine.GROUP_NAME, name);
			message.put(SchedulerEngine.JOB_NAME, name);

			_messageBus.sendMessage(destinationName, message);

			Assert.assertTrue(
				countDownLatch.await(
					10, java.util.concurrent.TimeUnit.SECONDS));
		}
		finally {
			serviceRegistration1.unregister();
			serviceRegistration2.unregister();
		}

		Assert.assertEquals(
			auditRequestContexts.toString(), 1, auditRequestContexts.size());

		AuditRequestContext auditRequestContext = auditRequestContexts.get(0);

		Assert.assertNotSame(currentAuditRequestContext, auditRequestContext);
		Assert.assertNotNull(auditRequestContext.getRequestId());
		Assert.assertNotEquals(
			currentAuditRequestContext.getRequestId(),
			auditRequestContext.getRequestId());
		Assert.assertNull(auditRequestContext.getSessionID());

		Assert.assertSame(
			currentAuditRequestContext,
			AuditRequestContextThreadLocal.getAuditRequestContext());
		Assert.assertEquals(
			sessionID, currentAuditRequestContext.getSessionID());
	}

	@Test
	public void testScriptingJob() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			SchedulerEngineHelperImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		CountDownLatch countDownLatch = new CountDownLatch(1);

		ServiceRegistration<?> serviceRegistration1 =
			bundleContext.registerService(
				AuditMessageProcessor.class,
				auditMessage -> {
					if (Objects.equals(
							TriggerState.COMPLETE.name(),
							auditMessage.getMessage())) {

						countDownLatch.countDown();
					}
				},
				HashMapDictionaryBuilder.<String, Object>put(
					"eventTypes", SchedulerEngine.SCHEDULER
				).build());

		ServiceRegistration<?> serviceRegistration2 =
			bundleContext.registerService(
				Destination.class,
				_destinationFactory.createDestination(
					DestinationConfiguration.
						createSynchronousDestinationConfiguration(
							DestinationNames.SCHEDULER_SCRIPTING)),
				HashMapDictionaryBuilder.<String, Object>put(
					"destination.name", DestinationNames.SCHEDULER_SCRIPTING
				).put(
					"service.ranking", Integer.MAX_VALUE
				).build());

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.portal.scheduler.internal.configuration." +
						"SchedulerEngineHelperConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"auditSchedulerJobEnabled", true
					).build());
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SchedulerEngineHelperImplTest.class.getName(),
				LoggerTestUtil.ERROR)) {

			String message = RandomTestUtil.randomString();
			Date startDate = new Date();

			_schedulerEngineHelper.addScriptingJob(
				_triggerFactory.createTrigger(
					"DEFAULT_SCRIPTING_JOB_NAME", "DEFAULT_SCRIPTING_JOB_NAME",
					startDate, new Date(startDate.getTime() + 1000), 1,
					TimeUnit.DAY),
				StorageType.MEMORY, null, "groovy",
				StringBundler.concat(
					"com.liferay.portal.kernel.log.LogFactoryUtil.getLog(\"",
					SchedulerEngineHelperImplTest.class.getName(),
					"\").error(\"", message, "\")"));

			Assert.assertTrue(
				countDownLatch.await(1, java.util.concurrent.TimeUnit.SECONDS));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(message, logEntry.getMessage());
		}
		finally {
			serviceRegistration1.unregister();
			serviceRegistration2.unregister();
		}
	}

	@Inject
	private DestinationFactory _destinationFactory;

	@Inject
	private MessageBus _messageBus;

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Inject
	private TriggerFactory _triggerFactory;

}