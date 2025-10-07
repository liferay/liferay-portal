/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.ClusterEvent;
import com.liferay.portal.kernel.cluster.ClusterEventListener;
import com.liferay.portal.kernel.cluster.ClusterEventType;
import com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.cluster.FutureClusterResponses;
import com.liferay.portal.kernel.concurrent.NoticeableFuture;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Closeable;

import java.lang.reflect.Field;

import java.net.InetAddress;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;

/**
 * @author Matthew Tambara
 */
public class ClusterMasterExecutorImplTest extends BaseClusterTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testClusterMasterTokenClusterEventListener() throws Exception {

		// Test 1, test when coordiator is not changed

		ClusterMasterExecutorImpl clusterMasterExecutorImpl =
			new ClusterMasterExecutorImpl();

		MockClusterExecutor mockClusterExecutor = _getMockClusterExecutor(true);

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor", mockClusterExecutor);

		try (Closeable closeable = _activate(clusterMasterExecutorImpl)) {
			mockClusterExecutor.addClusterNode(
				_TEST_ADDRESS,
				new ClusterNode(
					_TEST_CLUSTER_NODE_ID, InetAddress.getLocalHost()));

			Assert.assertTrue(clusterMasterExecutorImpl.isMaster());

			List<ClusterEventListener> clusterEventListeners =
				mockClusterExecutor.getClusterEventListeners();

			ClusterEventListener clusterEventListener =
				clusterEventListeners.get(0);

			clusterEventListener.processClusterEvent(
				new ClusterEvent(ClusterEventType.COORDINATOR_ADDRESS_UPDATE));

			Assert.assertTrue(clusterMasterExecutorImpl.isMaster());

			// Test 2, test JOIN event when coordiator is changed

			mockClusterExecutor.setCoordinatorAddress(_TEST_ADDRESS);

			clusterEventListener.processClusterEvent(
				new ClusterEvent(ClusterEventType.JOIN));

			Assert.assertTrue(clusterMasterExecutorImpl.isMaster());

			// Test 3, test DEPART event when coordiator is changed

			mockClusterExecutor.setCoordinatorAddress(_TEST_ADDRESS);

			clusterEventListener.processClusterEvent(
				new ClusterEvent(ClusterEventType.DEPART));

			Assert.assertTrue(clusterMasterExecutorImpl.isMaster());

			// Test 4, test COORDINATOR_ADDRESS_UPDATE event when coordiator is
			// changed

			mockClusterExecutor.setCoordinatorAddress(_TEST_ADDRESS);

			clusterEventListener.processClusterEvent(
				new ClusterEvent(ClusterEventType.COORDINATOR_ADDRESS_UPDATE));

			Assert.assertFalse(clusterMasterExecutorImpl.isMaster());
		}
	}

	@Test
	public void testClusterMasterTokenTransitionListeners() throws Exception {

		// Test 1, register cluster master token transition listener

		ClusterMasterExecutorImpl clusterMasterExecutorImpl =
			new ClusterMasterExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor",
			_getMockClusterExecutor(true));

		try (Closeable closeable = _activate(clusterMasterExecutorImpl)) {
			Set<ClusterMasterTokenTransitionListener>
				clusterMasterTokenTransitionListeners =
					ReflectionTestUtil.getFieldValue(
						clusterMasterExecutorImpl,
						"_clusterMasterTokenTransitionListeners");

			Assert.assertTrue(clusterMasterTokenTransitionListeners.isEmpty());

			ClusterMasterTokenTransitionListener
				mockClusterMasterTokenTransitionListener =
					new MockClusterMasterTokenTransitionListener();

			clusterMasterExecutorImpl.addClusterMasterTokenTransitionListener(
				mockClusterMasterTokenTransitionListener);

			Assert.assertEquals(
				clusterMasterTokenTransitionListeners.toString(), 1,
				clusterMasterTokenTransitionListeners.size());

			// Test 2, unregister cluster master token transition listener

			clusterMasterExecutorImpl.
				removeClusterMasterTokenTransitionListener(
					mockClusterMasterTokenTransitionListener);

			Assert.assertTrue(clusterMasterTokenTransitionListeners.isEmpty());

			// Test 3, set cluster master token transition listeners

			clusterMasterExecutorImpl.setClusterMasterTokenTransitionListeners(
				Collections.singleton(
					mockClusterMasterTokenTransitionListener));

			Assert.assertEquals(
				clusterMasterTokenTransitionListeners.toString(), 1,
				clusterMasterTokenTransitionListeners.size());
		}
	}

	@Test
	public void testDeactivate() throws Exception {

		// Test 1, destroy when cluster link is enabled

		ClusterMasterExecutorImpl clusterMasterExecutorImpl =
			new ClusterMasterExecutorImpl();

		MockClusterExecutor mockClusterExecutor = _getMockClusterExecutor(true);

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor", mockClusterExecutor);

		List<ClusterEventListener> clusterEventListeners = null;

		try (Closeable closeable = _activate(clusterMasterExecutorImpl)) {
			clusterEventListeners =
				mockClusterExecutor.getClusterEventListeners();

			Assert.assertEquals(
				clusterEventListeners.toString(), 1,
				clusterEventListeners.size());
		}

		clusterEventListeners = mockClusterExecutor.getClusterEventListeners();

		Assert.assertTrue(
			clusterEventListeners.toString(), clusterEventListeners.isEmpty());

		// Test 2, destory when cluster link is disabled

		clusterMasterExecutorImpl = new ClusterMasterExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor",
			_getMockClusterExecutor(false));

		clusterMasterExecutorImpl.activate(_bundleContext);

		clusterMasterExecutorImpl.deactivate();
	}

	@Test
	public void testExecuteOnMasterDisabled() throws Exception {

		// Test 1, execute without exception when log is eanbled

		ClusterMasterExecutorImpl clusterMasterExecutorImpl =
			new ClusterMasterExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor",
			_getMockClusterExecutor(false));

		clusterMasterExecutorImpl.activate(_bundleContext);

		Assert.assertFalse(clusterMasterExecutorImpl.isEnabled());

		String timeString = String.valueOf(System.currentTimeMillis());

		MethodHandler methodHandler = new MethodHandler(
			_TEST_METHOD, timeString);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ClusterMasterExecutorImpl.class.getName(),
				LoggerTestUtil.WARN)) {

			NoticeableFuture<String> noticeableFuture =
				clusterMasterExecutorImpl.executeOnMaster(methodHandler);

			Assert.assertSame(timeString, noticeableFuture.get());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Executing on the local node because the cluster master " +
					"executor is disabled",
				logEntry.getMessage());
		}

		// Test 2, execute without exception when log is disabled

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ClusterMasterExecutorImpl.class.getName(),
				LoggerTestUtil.OFF)) {

			NoticeableFuture<String> noticeableFuture =
				clusterMasterExecutorImpl.executeOnMaster(methodHandler);

			Assert.assertSame(timeString, noticeableFuture.get());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
		}

		// Test 3, execute with exception

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ClusterMasterExecutorImpl.class.getName(),
				LoggerTestUtil.WARN)) {

			try {
				clusterMasterExecutorImpl.executeOnMaster(null);

				Assert.fail();
			}
			catch (SystemException systemException) {
				Throwable throwable = systemException.getCause();

				Assert.assertSame(
					NullPointerException.class, throwable.getClass());

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					"Executing on the local node because the cluster master " +
						"executor is disabled",
					logEntry.getMessage());
			}
		}
	}

	@Test
	public void testExecuteOnMasterEnabled() throws Exception {

		// Test 1, execute without exception

		ClusterMasterExecutorImpl clusterMasterExecutorImpl =
			new ClusterMasterExecutorImpl();

		MockClusterExecutor mockClusterExecutor = _getMockClusterExecutor(true);

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor", mockClusterExecutor);

		try (Closeable closeable = _activate(clusterMasterExecutorImpl)) {
			Assert.assertTrue(clusterMasterExecutorImpl.isEnabled());

			String timeString = String.valueOf(System.currentTimeMillis());

			NoticeableFuture<String> noticeableFuture =
				clusterMasterExecutorImpl.executeOnMaster(
					new MethodHandler(_TEST_METHOD, timeString));

			Assert.assertSame(timeString, noticeableFuture.get());

			// Test 2, execute with exception

			try {
				clusterMasterExecutorImpl.executeOnMaster(_BAD_METHOD_HANDLER);

				Assert.fail();
			}
			catch (SystemException systemException) {
				Assert.assertEquals(
					"Unable to execute on master " +
						mockClusterExecutor.getLocalClusterNodeId(),
					systemException.getMessage());
			}
		}
	}

	@Test
	public void testGetMasterClusterNodeId() throws Exception {

		// Test 1, master to slave

		ClusterMasterExecutorImpl clusterMasterExecutorImpl =
			new ClusterMasterExecutorImpl();

		MockClusterExecutor mockClusterExecutor = _getMockClusterExecutor(true);

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor", mockClusterExecutor);

		try (Closeable closeable = _activate(clusterMasterExecutorImpl)) {
			Assert.assertEquals(
				mockClusterExecutor.getLocalClusterNodeId(),
				clusterMasterExecutorImpl.getMasterClusterNodeId(true));
			Assert.assertTrue(clusterMasterExecutorImpl.isMaster());

			mockClusterExecutor.addClusterNode(
				_TEST_ADDRESS,
				new ClusterNode(
					_TEST_CLUSTER_NODE_ID, InetAddress.getLocalHost()));

			MockClusterMasterTokenTransitionListener
				mockClusterMasterTokenTransitionListener =
					new MockClusterMasterTokenTransitionListener();

			clusterMasterExecutorImpl.addClusterMasterTokenTransitionListener(
				mockClusterMasterTokenTransitionListener);

			Address oldCoordinatorAddress =
				mockClusterExecutor.getCoordinatorAddress();

			mockClusterExecutor.setCoordinatorAddress(_TEST_ADDRESS);

			Assert.assertEquals(
				_TEST_CLUSTER_NODE_ID,
				clusterMasterExecutorImpl.getMasterClusterNodeId(true));
			Assert.assertFalse(clusterMasterExecutorImpl.isMaster());
			Assert.assertTrue(
				mockClusterMasterTokenTransitionListener.
					isMasterTokenReleasedNotified());

			// Test 2, slave to master

			mockClusterExecutor.setCoordinatorAddress(oldCoordinatorAddress);

			Assert.assertEquals(
				mockClusterExecutor.getLocalClusterNodeId(),
				clusterMasterExecutorImpl.getMasterClusterNodeId(true));
			Assert.assertTrue(clusterMasterExecutorImpl.isMaster());
			Assert.assertTrue(
				mockClusterMasterTokenTransitionListener.
					isMasterTokenAcquiredNotified());
		}
	}

	@Test
	public void testGetMasterClusterNodeIdRetry() throws Exception {

		// Test 1, retry to get cluster node when log is enabled

		final ClusterMasterExecutorImpl clusterMasterExecutorImpl =
			new ClusterMasterExecutorImpl();

		MockClusterExecutor mockClusterExecutor = _getMockClusterExecutor(true);

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor", mockClusterExecutor);

		try (Closeable closeable = _activate(clusterMasterExecutorImpl)) {
			mockClusterExecutor.setCoordinatorAddress(_TEST_ADDRESS);

			mockClusterExecutor.block();

			Thread thread = new Thread() {

				@Override
				public void run() {
					try (LogCapture logCapture =
							LoggerTestUtil.configureLog4JLogger(
								ClusterMasterExecutorImpl.class.getName(),
								LoggerTestUtil.INFO)) {

						Assert.assertEquals(
							_TEST_CLUSTER_NODE_ID,
							clusterMasterExecutorImpl.getMasterClusterNodeId(
								false));

						List<LogEntry> logEntries = logCapture.getLogEntries();

						Assert.assertEquals(
							logEntries.toString(), 1, logEntries.size());

						LogEntry logEntry = logEntries.get(0);

						Assert.assertEquals(
							StringBundler.concat(
								"Unable to get cluster node information for ",
								"coordinator address ", _TEST_ADDRESS,
								". Trying again."),
							logEntry.getMessage());
					}
				}

			};

			thread.start();

			mockClusterExecutor.waitUntilBlock(1);

			mockClusterExecutor.unblock(1);

			Assert.assertNull(mockClusterExecutor.waitClusterNodeId());

			mockClusterExecutor.waitUntilBlock(1);

			ClusterNode clusterNode = new ClusterNode(
				_TEST_CLUSTER_NODE_ID, InetAddress.getLocalHost());

			mockClusterExecutor.addClusterNode(_TEST_ADDRESS, clusterNode);

			mockClusterExecutor.unblock(1);

			Assert.assertSame(
				_TEST_CLUSTER_NODE_ID, mockClusterExecutor.waitClusterNodeId());

			thread.join();

			// Test 2, retry to get cluster node when log is disabled

			mockClusterExecutor.removeClusterNode(_TEST_ADDRESS);

			mockClusterExecutor.block();

			thread = new Thread() {

				@Override
				public void run() {
					try (LogCapture logCapture =
							LoggerTestUtil.configureLog4JLogger(
								ClusterMasterExecutorImpl.class.getName(),
								LoggerTestUtil.OFF)) {

						Assert.assertEquals(
							_TEST_CLUSTER_NODE_ID,
							clusterMasterExecutorImpl.getMasterClusterNodeId(
								false));

						List<LogEntry> logEntries = logCapture.getLogEntries();

						Assert.assertTrue(
							logEntries.toString(), logEntries.isEmpty());
					}
				}

			};

			thread.start();

			mockClusterExecutor.waitUntilBlock(1);

			mockClusterExecutor.unblock(1);

			Assert.assertNull(mockClusterExecutor.waitClusterNodeId());

			mockClusterExecutor.waitUntilBlock(1);

			mockClusterExecutor.addClusterNode(_TEST_ADDRESS, clusterNode);

			mockClusterExecutor.unblock(1);

			Assert.assertSame(
				_TEST_CLUSTER_NODE_ID, mockClusterExecutor.waitClusterNodeId());

			thread.join();
		}
	}

	@Test
	public void testInitialize() throws Exception {

		// Test 1, initialize when cluster link is disabled

		ClusterMasterExecutorImpl clusterMasterExecutorImpl =
			new ClusterMasterExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor",
			_getMockClusterExecutor(false));

		clusterMasterExecutorImpl.activate(_bundleContext);

		Assert.assertFalse(clusterMasterExecutorImpl.isEnabled());
		Assert.assertTrue(clusterMasterExecutorImpl.isMaster());

		// Test 2, initialize when cluster link is enabled and master is exist

		clusterMasterExecutorImpl = new ClusterMasterExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor",
			_getMockClusterExecutor(true));

		try (Closeable closeable = _activate(clusterMasterExecutorImpl)) {
			Assert.assertTrue(clusterMasterExecutorImpl.isEnabled());
			Assert.assertTrue(clusterMasterExecutorImpl.isMaster());
		}

		// Test 3, initialize when cluster link is enabled and master is not
		// exist

		MockClusterExecutor mockClusterExecutor = _getMockClusterExecutor(true);

		mockClusterExecutor.addClusterNode(
			_TEST_ADDRESS,
			new ClusterNode(_TEST_CLUSTER_NODE_ID, InetAddress.getLocalHost()));

		mockClusterExecutor.setCoordinatorAddress(_TEST_ADDRESS);

		clusterMasterExecutorImpl = new ClusterMasterExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor", mockClusterExecutor);

		try (Closeable closeable = _activate(clusterMasterExecutorImpl)) {
			Assert.assertTrue(clusterMasterExecutorImpl.isEnabled());
			Assert.assertFalse(clusterMasterExecutorImpl.isMaster());
		}
	}

	@NewEnv(type = NewEnv.Type.CLASSLOADER)
	@Test
	public void testMisc() {
		ClusterMasterExecutorImpl clusterMasterExecutorImpl =
			new ClusterMasterExecutorImpl();

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor",
			_getMockClusterExecutor(false));

		clusterMasterExecutorImpl.activate(_bundleContext);

		ReflectionTestUtil.setFieldValue(
			clusterMasterExecutorImpl, "_clusterExecutor",
			_getMockClusterExecutor(true));

		clusterMasterExecutorImpl.activate(_bundleContext);
		clusterMasterExecutorImpl.deactivate();
	}

	@Test
	public void testNotifyMasterTokenTransitionListeners() {

		// Test 1, notify when master is required

		ClusterMasterExecutorImpl clusterMasterExecutorImpl =
			new ClusterMasterExecutorImpl();

		MockClusterMasterTokenTransitionListener
			mockClusterMasterTokenTransitionListener =
				new MockClusterMasterTokenTransitionListener();

		clusterMasterExecutorImpl.addClusterMasterTokenTransitionListener(
			mockClusterMasterTokenTransitionListener);

		clusterMasterExecutorImpl.notifyMasterTokenTransitionListeners(true);

		Assert.assertTrue(
			mockClusterMasterTokenTransitionListener.
				isMasterTokenAcquiredNotified());
		Assert.assertFalse(
			mockClusterMasterTokenTransitionListener.
				isMasterTokenReleasedNotified());

		// Test 2, notify when master is released

		clusterMasterExecutorImpl = new ClusterMasterExecutorImpl();

		mockClusterMasterTokenTransitionListener =
			new MockClusterMasterTokenTransitionListener();

		clusterMasterExecutorImpl.addClusterMasterTokenTransitionListener(
			mockClusterMasterTokenTransitionListener);

		clusterMasterExecutorImpl.notifyMasterTokenTransitionListeners(false);

		Assert.assertFalse(
			mockClusterMasterTokenTransitionListener.
				isMasterTokenAcquiredNotified());
		Assert.assertTrue(
			mockClusterMasterTokenTransitionListener.
				isMasterTokenReleasedNotified());
	}

	private Closeable _activate(
		ClusterMasterExecutorImpl clusterMasterExecutorImpl) {

		clusterMasterExecutorImpl.activate(_bundleContext);

		return clusterMasterExecutorImpl::deactivate;
	}

	private MockClusterExecutor _getMockClusterExecutor(boolean enabled) {
		MockClusterExecutor mockClusterExecutor = new MockClusterExecutor(
			enabled);

		ReflectionTestUtil.setFieldValue(
			mockClusterExecutor, "_clusterChannelFactory",
			new TestClusterChannelFactory());
		ReflectionTestUtil.setFieldValue(
			mockClusterExecutor, "_portalExecutorManager",
			new MockPortalExecutorManager());
		ReflectionTestUtil.setFieldValue(
			mockClusterExecutor, "_serviceTrackerList",
			ServiceTrackerListFactory.open(
				_bundleContext, ClusterEventListener.class));

		mockClusterExecutor.clusterExecutorConfiguration =
			new ClusterExecutorConfiguration() {

				@Override
				public long clusterNodeAddressTimeout() {
					return 100;
				}

				@Override
				public boolean debugEnabled() {
					return false;
				}

				@Override
				public String[] excludedPropertyKeys() {
					return new String[] {
						"access_key", "connection_password",
						"connection_username", "secret_access_key"
					};
				}

			};

		if (enabled) {
			mockClusterExecutor.initialize(
				"test-channel-logic-name-mock", "test-channel-properties-mock",
				"test-channel-name-mock");

			ClusterChannel clusterChannel =
				mockClusterExecutor.getClusterChannel();

			mockClusterExecutor.addClusterNode(
				clusterChannel.getLocalAddress(),
				mockClusterExecutor.getLocalClusterNode());
		}

		return mockClusterExecutor;
	}

	private static final MethodHandler _BAD_METHOD_HANDLER = new MethodHandler(
		new MethodKey());

	private static final Address _TEST_ADDRESS = new TestAddress(-1);

	private static final String _TEST_CLUSTER_NODE_ID = "test.cluster.node.id";

	private static final MethodKey _TEST_METHOD = new MethodKey(
		TestBean.class, "testMethod1", String.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static class MockClusterExecutor extends ClusterExecutorImpl {

		public void addClusterNode(Address address, ClusterNode clusterNode) {
			if (!_enabled) {
				return;
			}

			_clusterNodes.put(address, clusterNode);
		}

		public void block() {
			_semaphore = new Semaphore(0);
		}

		@Override
		public FutureClusterResponses execute(ClusterRequest clusterRequest) {
			if (!_enabled) {
				return null;
			}

			if (clusterRequest.getPayload() == _BAD_METHOD_HANDLER) {
				throw new RuntimeException();
			}

			return super.execute(clusterRequest);
		}

		@Override
		public List<ClusterNode> getClusterNodes() {
			if (!isEnabled()) {
				return Collections.emptyList();
			}

			return super.getClusterNodes();
		}

		public Address getCoordinatorAddress() {
			ClusterChannel clusterChannel = getClusterChannel();

			ClusterReceiver clusterReceiver =
				clusterChannel.getClusterReceiver();

			return clusterReceiver.getCoordinatorAddress();
		}

		@Override
		public ClusterNode getLocalClusterNode() {
			if (!_enabled) {
				return null;
			}

			return super.getLocalClusterNode();
		}

		public String getLocalClusterNodeId() {
			ClusterNode clusterNode = getLocalClusterNode();

			return clusterNode.getClusterNodeId();
		}

		@Override
		public boolean isClusterNodeAlive(String clusterNodeId) {
			if (Validator.isNull(clusterNodeId)) {
				throw new NullPointerException();
			}

			if (!_enabled) {
				return false;
			}

			return super.isClusterNodeAlive(clusterNodeId);
		}

		@Override
		public boolean isEnabled() {
			return _enabled;
		}

		public void removeClusterNode(Address address) {
			if (!_enabled) {
				return;
			}

			_clusterNodes.remove(address);
		}

		public void setCoordinatorAddress(Address address) throws Exception {
			ClusterChannel clusterChannel = getClusterChannel();

			ClusterReceiver clusterReceiver =
				clusterChannel.getClusterReceiver();

			Field field = ReflectionUtil.getDeclaredField(
				BaseClusterReceiver.class, "_coordinatorAddress");

			field.set(clusterReceiver, address);
		}

		public void unblock(int permits) {
			_semaphore.release(permits);
		}

		public String waitClusterNodeId() throws Exception {
			try {
				return _clusterNodeIdExchanger.exchange(
					null, 1000, TimeUnit.MILLISECONDS);
			}
			catch (TimeoutException timeoutException) {
				return "null";
			}
		}

		public void waitUntilBlock(int threadCount) {
			Semaphore semaphore = _semaphore;

			if (semaphore != null) {
				while (semaphore.getQueueLength() < threadCount);
			}
		}

		@Override
		protected String getClusterNodeId(Address address) {
			Semaphore semaphore = _semaphore;

			try {
				if (semaphore != null) {
					semaphore.acquire();
				}

				String clusterNodeId = null;

				ClusterNode clusterNode = _clusterNodes.get(address);

				if (clusterNode != null) {
					clusterNodeId = clusterNode.getClusterNodeId();
				}

				if (semaphore != null) {
					_clusterNodeIdExchanger.exchange(clusterNodeId);
				}

				return clusterNodeId;
			}
			catch (Exception exception) {
				throw new IllegalStateException(exception);
			}
		}

		private MockClusterExecutor(boolean enabled) {
			_enabled = enabled;
		}

		private final Exchanger<String> _clusterNodeIdExchanger =
			new Exchanger<>();
		private final Map<Address, ClusterNode> _clusterNodes =
			new ConcurrentHashMap<>();
		private final boolean _enabled;
		private volatile Semaphore _semaphore;

	}

	private static class MockClusterMasterTokenTransitionListener
		implements ClusterMasterTokenTransitionListener {

		public boolean isMasterTokenAcquiredNotified() {
			return _masterTokenAcquiredNotified;
		}

		public boolean isMasterTokenReleasedNotified() {
			return _masterTokenReleasedNotified;
		}

		@Override
		public void masterTokenAcquired() {
			_masterTokenAcquiredNotified = true;
		}

		@Override
		public void masterTokenReleased() {
			_masterTokenReleasedNotified = true;
		}

		private boolean _masterTokenAcquiredNotified;
		private boolean _masterTokenReleasedNotified;

	}

}