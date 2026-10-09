/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerRegistry;
import com.liferay.portal.kernel.messaging.config.DefaultMessagingConfigurator;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Michael C. Han
 */
@RunWith(Arquillian.class)
public class DefaultMessagingConfiguratorTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			DefaultMessagingConfiguratorTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() {
		_serviceTracker.close();

		_defaultMessagingConfigurator.destroy();
	}

	@Test
	public void testPortalClassLoaderDestinationConfiguration()
		throws InterruptedException, InvalidSyntaxException {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		_defaultMessagingConfigurator = new DefaultMessagingConfigurator() {

			protected void initialize() {
				super.initialize();

				countDownLatch.countDown();
			}

		};

		Set<DestinationConfiguration> destinationConfigurations =
			new HashSet<>();

		destinationConfigurations.add(
			DestinationConfiguration.createSynchronousDestinationConfiguration(
				"liferay/portaltest1"));
		destinationConfigurations.add(
			DestinationConfiguration.createParallelDestinationConfiguration(
				"liferay/portaltest2"));

		_defaultMessagingConfigurator.setDestinationConfigurations(
			destinationConfigurations);

		List<MessageListener> messageListenersList1 = new ArrayList<>();

		Map<String, List<MessageListener>> messageListeners =
			HashMapBuilder.<String, List<MessageListener>>put(
				"liferay/portaltest1", messageListenersList1
			).build();

		messageListenersList1.add(
			new TestMessageListener("liferay/portaltest1"));

		List<MessageListener> messageListenersList2 = new ArrayList<>();

		messageListeners.put("liferay/portaltest2", messageListenersList2);

		messageListenersList2.add(
			new TestMessageListener("liferay/portaltest2"));

		_defaultMessagingConfigurator.setMessageListeners(messageListeners);

		_defaultMessagingConfigurator.afterPropertiesSet();

		_serviceTracker = new ServiceTracker<>(
			_bundleContext,
			_bundleContext.createFilter(
				"(&(destination.name=*portaltest*)(objectClass=com.liferay." +
					"portal.kernel.messaging.Destination))"),
			null);

		_serviceTracker.open();

		countDownLatch.await();

		Object[] services = _serviceTracker.getServices();

		Assert.assertEquals(Arrays.toString(services), 2, services.length);

		for (Object service : services) {
			Destination destination = (Destination)service;

			String destinationName = destination.getName();

			Assert.assertTrue(
				destinationName, destinationName.contains("portaltest"));

			List<MessageListener> destinationMessageListeners =
				_messageListenerRegistry.getMessageListeners(destinationName);

			if (destinationName.equals("liferay/portaltest1")) {
				Assert.assertEquals(
					destinationMessageListeners.toString(), 1,
					destinationMessageListeners.size());
			}

			if (destinationMessageListeners.isEmpty()) {
				continue;
			}

			Message message = new Message();

			message.setDestinationName(destinationName);

			destination.send(message);
		}
	}

	private static BundleContext _bundleContext;

	private DefaultMessagingConfigurator _defaultMessagingConfigurator;

	@Inject
	private MessageListenerRegistry _messageListenerRegistry;

	private ServiceTracker<Destination, Destination> _serviceTracker;

	private static class TestMessageListener implements MessageListener {

		public TestMessageListener(String destinationName) {
			_destinationName = destinationName;
		}

		@Override
		public void receive(Message message) {
			Assert.assertEquals(_destinationName, message.getDestinationName());
		}

		private final String _destinationName;

	}

}