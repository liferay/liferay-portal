/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.Priority;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tina Tian
 * @author Shuyang Zhou
 */
@NewEnv(type = NewEnv.Type.CLASSLOADER)
public class ClusterLinkImplTest extends BaseClusterTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDeactivate() {
		ClusterLinkImpl clusterLinkImpl = _getClusterLinkImpl(1);

		List<TestClusterChannel> clusterChannels =
			TestClusterChannel.getClusterChannels();

		Assert.assertEquals(
			clusterChannels.toString(), 1, clusterChannels.size());

		TestClusterChannel clusterChannel = clusterChannels.get(0);

		ExecutorService executorService = clusterLinkImpl.getExecutorService();

		Assert.assertFalse(clusterChannel.isClosed());
		Assert.assertFalse(executorService.isShutdown());

		clusterLinkImpl.deactivate();

		Assert.assertTrue(clusterChannel.isClosed());
		Assert.assertTrue(executorService.isShutdown());
	}

	@Test
	public void testGetChannel() {
		ClusterLinkImpl clusterLinkImpl = _getClusterLinkImpl(2);

		ClusterChannel clusterChannel1 = clusterLinkImpl.getChannel(
			Priority.LEVEL1);

		Assert.assertSame(
			clusterChannel1, clusterLinkImpl.getChannel(Priority.LEVEL2));
		Assert.assertSame(
			clusterChannel1, clusterLinkImpl.getChannel(Priority.LEVEL3));
		Assert.assertSame(
			clusterChannel1, clusterLinkImpl.getChannel(Priority.LEVEL4));
		Assert.assertSame(
			clusterChannel1, clusterLinkImpl.getChannel(Priority.LEVEL5));

		ClusterChannel clusterChannel2 = clusterLinkImpl.getChannel(
			Priority.LEVEL6);

		Assert.assertSame(
			clusterChannel2, clusterLinkImpl.getChannel(Priority.LEVEL7));
		Assert.assertSame(
			clusterChannel2, clusterLinkImpl.getChannel(Priority.LEVEL8));
		Assert.assertSame(
			clusterChannel2, clusterLinkImpl.getChannel(Priority.LEVEL9));
		Assert.assertSame(
			clusterChannel2, clusterLinkImpl.getChannel(Priority.LEVEL10));

		List<TestClusterChannel> clusterChannels =
			TestClusterChannel.getClusterChannels();

		Assert.assertEquals(
			clusterChannels.toString(), 2, clusterChannels.size());

		Assert.assertNotEquals(clusterChannel1, clusterChannel2);
		Assert.assertTrue(
			clusterChannels.toString(),
			clusterChannels.contains(clusterChannel1));
		Assert.assertTrue(
			clusterChannels.toString(),
			clusterChannels.contains(clusterChannel2));
	}

	@Test
	public void testInitChannels() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ClusterLinkImpl.class.getName(), LoggerTestUtil.OFF)) {

			// Test 1, create ClusterLinkImpl#MAX_CHANNEL_COUNT channels

			List<LogEntry> logEntries = logCapture.getLogEntries();

			try {
				_getClusterLinkImpl(ClusterLinkImpl.MAX_CHANNEL_COUNT + 1);

				Assert.fail();
			}
			catch (IllegalStateException illegalStateException) {
				Assert.assertEquals(
					logEntries.toString(), 0, logEntries.size());
				Assert.assertEquals(
					"java.lang.IllegalArgumentException: Channel count must " +
						"be between 1 and " + ClusterLinkImpl.MAX_CHANNEL_COUNT,
					illegalStateException.getMessage());
			}

			// Test 2, create 0 channels

			logEntries = logCapture.resetPriority(String.valueOf(Level.SEVERE));

			try {
				_getClusterLinkImpl(0);

				Assert.fail();
			}
			catch (IllegalStateException illegalStateException) {
				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					"Unable to initialize channels", logEntry.getMessage());

				Assert.assertEquals(
					"java.lang.IllegalArgumentException: Channel count must " +
						"be between 1 and " + ClusterLinkImpl.MAX_CHANNEL_COUNT,
					illegalStateException.getMessage());
			}
		}
	}

	@Test
	public void testInitialize() {
		ClusterLinkImpl clusterLinkImpl = _getClusterLinkImpl(2);

		Assert.assertNotNull(clusterLinkImpl.getExecutorService());

		List<TestClusterChannel> clusterChannels =
			TestClusterChannel.getClusterChannels();

		Assert.assertEquals(
			clusterChannels.toString(), 2, clusterChannels.size());

		for (TestClusterChannel clusterChannel : clusterChannels) {
			Assert.assertFalse(clusterChannel.isClosed());

			CountDownLatch countDownLatch = ReflectionTestUtil.getFieldValue(
				clusterChannel.getClusterReceiver(), "_countDownLatch");

			Assert.assertEquals(0, countDownLatch.getCount());
		}
	}

	@Test
	public void testSendMulticastMessage() {
		ClusterLinkImpl clusterLinkImpl = _getClusterLinkImpl(1);

		List<Serializable> multicastMessages =
			TestClusterChannel.getMulticastMessages();
		List<ObjectValuePair<Serializable, Address>> unicastMessages =
			TestClusterChannel.getUnicastMessages();

		Assert.assertTrue(
			multicastMessages.toString(), multicastMessages.isEmpty());
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());

		Message message = new Message();

		clusterLinkImpl.sendMulticastMessage(message, Priority.LEVEL1);

		Assert.assertEquals(
			multicastMessages.toString(), 1, multicastMessages.size());
		Assert.assertTrue(
			multicastMessages.toString(), multicastMessages.contains(message));
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());
	}

	@Test
	public void testSendUnicastMessage() {
		ClusterLinkImpl clusterLinkImpl = _getClusterLinkImpl(1);

		List<Serializable> multicastMessages =
			TestClusterChannel.getMulticastMessages();
		List<ObjectValuePair<Serializable, Address>> unicastMessages =
			TestClusterChannel.getUnicastMessages();

		Assert.assertTrue(
			multicastMessages.toString(), multicastMessages.isEmpty());
		Assert.assertTrue(
			unicastMessages.toString(), unicastMessages.isEmpty());

		Message message = new Message();
		Address address = new TestAddress(-1);

		clusterLinkImpl.sendUnicastMessage(address, message, Priority.LEVEL1);

		Assert.assertTrue(
			multicastMessages.toString(), multicastMessages.isEmpty());
		Assert.assertEquals(
			unicastMessages.toString(), 1, unicastMessages.size());

		ObjectValuePair<Serializable, Address> unicastMessage =
			unicastMessages.get(0);

		Assert.assertSame(message, unicastMessage.getKey());
		Assert.assertSame(address, unicastMessage.getValue());
	}

	private ClusterLinkImpl _getClusterLinkImpl(int channels) {
		ClusterLinkImpl clusterLinkImpl = new ClusterLinkImpl() {

			@Override
			protected void modified(Map<String, Object> properties) {
				ReflectionTestUtil.setFieldValue(
					this, "_clusterChannelFactory",
					new TestClusterChannelFactory());
			}

		};

		ReflectionTestUtil.setFieldValue(
			clusterLinkImpl, "_portalExecutorManager",
			new MockPortalExecutorManager());

		for (int i = 0; i < channels; i++) {
			PropsUtil.set(
				PropsKeys.CLUSTER_LINK_CHANNEL_NAME_TRANSPORT +
					StringPool.PERIOD + i,
				"test-channel-name-transport-" + i);
			PropsUtil.set(
				PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_TRANSPORT +
					StringPool.PERIOD + i,
				"test-channel-properties-transport-" + i);
		}

		clusterLinkImpl.activate(Collections.emptyMap());

		return clusterLinkImpl;
	}

}