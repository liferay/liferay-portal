/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.BundleContextTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTPublishMessageBusInterceptorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testInterceptBackgroundTaskStatusMessage() throws Exception {
		BundleContext bundleContext = BundleContextTestUtil.getBundleContext(
			CTPublishMessageBusInterceptorTest.class);
		TestMessageListener testMessageListener = new TestMessageListener();

		ServiceRegistration<MessageListener> serviceRegistration =
			bundleContext.registerService(
				MessageListener.class, testMessageListener,
				HashMapDictionaryBuilder.<String, Object>put(
					"destination.name", DestinationNames.BACKGROUND_TASK_STATUS
				).put(
					"service.ranking", Integer.MAX_VALUE
				).build());

		try {
			_assertSentMessageCount(
				1, BackgroundTaskConstants.STATUS_CANCELLED,
				testMessageListener);
			_assertSentMessageCount(
				1, BackgroundTaskConstants.STATUS_FAILED, testMessageListener);
			_assertSentMessageCount(
				2, BackgroundTaskConstants.STATUS_SUCCESSFUL,
				testMessageListener);
		}
		finally {
			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	private void _assertSentMessageCount(
			int expectedSentMessageCount, int status,
			TestMessageListener testMessageListener)
		throws Exception {

		Message message = new Message();

		message.put("status", status);
		message.put(
			"taskExecutorClassName",
			"com.liferay.change.tracking.internal.background.task." +
				"CTPublishBackgroundTaskExecutor");

		_messageBus.sendMessage(
			DestinationNames.BACKGROUND_TASK_STATUS, message);

		Assert.assertEquals(
			expectedSentMessageCount, testMessageListener._count);
	}

	@Inject
	private MessageBus _messageBus;

	private static class TestMessageListener implements MessageListener {

		@Override
		public void receive(Message message) throws MessageListenerException {
			_count++;
		}

		private int _count;

	}

}