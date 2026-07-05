/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cluster.ClusterEventListener;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.cluster.FutureClusterResponses;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.InetAddress;
import java.net.NetworkInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
public class PortalInstancesTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			ClusterExecutor.class,
			new ClusterExecutor() {

				@Override
				public FutureClusterResponses execute(
					ClusterRequest clusterRequest) {

					_clusterRequests.add(clusterRequest);

					return null;
				}

				@Override
				public InetAddress getBindInetAddress() {
					return null;
				}

				@Override
				public NetworkInterface getBindNetworkInterface() {
					return null;
				}

				@Override
				public List<ClusterEventListener> getClusterEventListeners() {
					return Collections.emptyList();
				}

				@Override
				public List<ClusterNode> getClusterNodes() {
					return Collections.emptyList();
				}

				@Override
				public ClusterNode getLocalClusterNode() {
					return _localClusterNode;
				}

				@Override
				public boolean isClusterNodeAlive(String clusterNodeId) {
					if (!_clusterEnabled) {
						Assert.fail(
							"Cluster node aliveness was checked while " +
								"clustering is disabled");
					}

					return _aliveClusterNodeIds.contains(clusterNodeId);
				}

				@Override
				public boolean isEnabled() {
					return _clusterEnabled;
				}

			},
			null);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Before
	public void setUp() {
		_aliveClusterNodeIds.clear();
		_clusterEnabled = false;
		_clusterRequests.clear();
		_localClusterNode = null;
	}

	@After
	public void tearDown() {
		Map<Long, ?> companyIdsInDeletionProcess =
			_getCompanyIdsInDeletionProcess();

		companyIdsInDeletionProcess.clear();
	}

	@Test
	public void testIsCompanyInDeletionProcessWhenClusteringIsDisabled() {
		long companyId = RandomTestUtil.randomLong();

		_addCompanyIdInDeletionProcess(
			companyId, "goneClusterNode", System.currentTimeMillis());

		Assert.assertTrue(
			PortalInstances.isCompanyInDeletionProcess(companyId));
	}

	@Test
	public void testIsCompanyInDeletionProcessWhenEntryIsExpired() {
		long companyId = RandomTestUtil.randomLong();

		_addCompanyIdInDeletionProcess(
			companyId, null,
			System.currentTimeMillis() -
				PropsValues.COMPANY_DELETE_IN_PROCESS_MAX_TIME - 1000);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalInstances.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertFalse(
				PortalInstances.isCompanyInDeletionProcess(companyId));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				message,
				message.startsWith(
					"Removing company " + companyId +
						" from the deletion process after "));
		}

		Map<Long, ?> companyIdsInDeletionProcess =
			_getCompanyIdsInDeletionProcess();

		Assert.assertFalse(
			companyIdsInDeletionProcess.toString(),
			companyIdsInDeletionProcess.containsKey(companyId));
	}

	@Test
	public void testIsCompanyInDeletionProcessWhenOriginClusterNodeIsAlive() {
		_clusterEnabled = true;

		_aliveClusterNodeIds.add("aliveClusterNode");

		long companyId = RandomTestUtil.randomLong();

		_addCompanyIdInDeletionProcess(
			companyId, "aliveClusterNode", System.currentTimeMillis());

		Assert.assertTrue(
			PortalInstances.isCompanyInDeletionProcess(companyId));
	}

	@Test
	public void testIsCompanyInDeletionProcessWhenOriginClusterNodeIsGone() {
		_clusterEnabled = true;

		long companyId = RandomTestUtil.randomLong();

		_addCompanyIdInDeletionProcess(
			companyId, "goneClusterNode", System.currentTimeMillis());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalInstances.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertFalse(
				PortalInstances.isCompanyInDeletionProcess(companyId));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Removing company ", companyId,
					" from the deletion process because cluster node ",
					"\"goneClusterNode\" left the cluster"),
				logEntry.getMessage());
		}

		Map<Long, ?> companyIdsInDeletionProcess =
			_getCompanyIdsInDeletionProcess();

		Assert.assertFalse(
			companyIdsInDeletionProcess.toString(),
			companyIdsInDeletionProcess.containsKey(companyId));
	}

	@Test
	public void testRemoteReleaseRequiresMatchingEntry() {
		long companyId = RandomTestUtil.randomLong();
		long timestamp = System.currentTimeMillis();

		_addCompanyIdInDeletionProcess(
			companyId, "aliveClusterNode", timestamp);

		_removeCompanyIdInDeletionProcess(
			companyId, "aliveClusterNode", timestamp + 1);

		Assert.assertTrue(
			PortalInstances.isCompanyInDeletionProcess(companyId));

		_removeCompanyIdInDeletionProcess(
			companyId, "otherClusterNode", timestamp);

		Assert.assertTrue(
			PortalInstances.isCompanyInDeletionProcess(companyId));

		_removeCompanyIdInDeletionProcess(
			companyId, "aliveClusterNode", timestamp);

		Assert.assertFalse(
			PortalInstances.isCompanyInDeletionProcess(companyId));
	}

	@Test
	public void testSetCompanyInDeletionProcessWithSafeCloseable() {
		long companyId = RandomTestUtil.randomLong();

		Assert.assertFalse(
			PortalInstances.isCompanyInDeletionProcess(companyId));

		SafeCloseable safeCloseable =
			PortalInstances.setCompanyInDeletionProcessWithSafeCloseable(
				companyId);

		Assert.assertTrue(
			PortalInstances.isCompanyInDeletionProcess(companyId));

		safeCloseable.close();

		Assert.assertFalse(
			PortalInstances.isCompanyInDeletionProcess(companyId));
	}

	@Test
	public void testSetCompanyInDeletionProcessWithSafeCloseableMulticasts()
		throws Exception {

		_clusterEnabled = true;

		_aliveClusterNodeIds.add("localClusterNode");

		_localClusterNode = new ClusterNode(
			"localClusterNode", InetAddress.getLoopbackAddress());

		long companyId = RandomTestUtil.randomLong();

		SafeCloseable safeCloseable =
			PortalInstances.setCompanyInDeletionProcessWithSafeCloseable(
				companyId);

		Assert.assertEquals(
			_clusterRequests.toString(), 1, _clusterRequests.size());

		safeCloseable.close();

		Assert.assertEquals(
			_clusterRequests.toString(), 2, _clusterRequests.size());

		Assert.assertFalse(
			PortalInstances.isCompanyInDeletionProcess(companyId));

		ClusterRequest addClusterRequest = _clusterRequests.get(0);

		Assert.assertTrue(addClusterRequest.isFireAndForget());

		MethodHandler addMethodHandler =
			(MethodHandler)addClusterRequest.getPayload();

		addMethodHandler.invoke();

		Assert.assertTrue(
			PortalInstances.isCompanyInDeletionProcess(companyId));

		ClusterRequest removeClusterRequest = _clusterRequests.get(1);

		Assert.assertTrue(removeClusterRequest.isFireAndForget());

		MethodHandler removeMethodHandler =
			(MethodHandler)removeClusterRequest.getPayload();

		removeMethodHandler.invoke();

		Assert.assertFalse(
			PortalInstances.isCompanyInDeletionProcess(companyId));
	}

	@Test
	public void testSetCompanyInDeletionProcessWithSafeCloseableWhenAlreadyInDeletion() {
		long companyId = RandomTestUtil.randomLong();

		try (SafeCloseable safeCloseable =
				PortalInstances.setCompanyInDeletionProcessWithSafeCloseable(
					companyId)) {

			try {
				PortalInstances.setCompanyInDeletionProcessWithSafeCloseable(
					companyId);

				Assert.fail();
			}
			catch (UnsupportedOperationException
						unsupportedOperationException) {

				Assert.assertEquals(
					companyId + " is already in deletion",
					unsupportedOperationException.getMessage());
			}
		}
	}

	private void _addCompanyIdInDeletionProcess(
		long companyId, String clusterNodeId, long timestamp) {

		ReflectionTestUtil.invoke(
			PortalInstances.class, "_addCompanyIdInDeletionProcess",
			new Class<?>[] {long.class, String.class, long.class}, companyId,
			clusterNodeId, timestamp);
	}

	private Map<Long, ?> _getCompanyIdsInDeletionProcess() {
		return ReflectionTestUtil.getFieldValue(
			PortalInstances.class, "_companyIdsInDeletionProcess");
	}

	private void _removeCompanyIdInDeletionProcess(
		long companyId, String clusterNodeId, long timestamp) {

		ReflectionTestUtil.invoke(
			PortalInstances.class, "_removeCompanyIdInDeletionProcess",
			new Class<?>[] {long.class, String.class, long.class}, companyId,
			clusterNodeId, timestamp);
	}

	private static final Set<String> _aliveClusterNodeIds = new HashSet<>();
	private static boolean _clusterEnabled;
	private static final List<ClusterRequest> _clusterRequests =
		new ArrayList<>();
	private static ClusterNode _localClusterNode;
	private static ServiceRegistration<ClusterExecutor> _serviceRegistration;

}