/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.cluster;

import com.liferay.portal.kernel.cluster.ClusterEvent;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class ReplicasClusterListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setEmbeddedCluster(true);
		_setMasterExecutor(true);

		Mockito.when(
			_replicasClusterContext.getClusterSize()
		).thenReturn(
			_REPLICAS + 1
		);

		Mockito.when(
			_replicasClusterContext.getReplicasManager()
		).thenReturn(
			_replicasManager
		);

		Mockito.when(
			_replicasClusterContext.getTargetIndexNames()
		).thenReturn(
			_INDICES
		);

		_replicasClusterListener = new ReplicasClusterListener(
			_replicasClusterContext);
	}

	@Test
	public void testAHappyDay() {
		processClusterEvent();
		_assertReplicasChanged();
	}

	@Test
	public void testLiferayClusterReportsEmpty() {
		Mockito.when(
			_replicasClusterContext.getClusterSize()
		).thenReturn(
			0
		);

		processClusterEvent();

		Mockito.verify(
			_replicasManager
		).updateNumberOfReplicas(
			0, _INDICES
		);
	}

	@Test
	public void testMasterTokenAcquired() {
		masterTokenAcquired();

		_assertReplicasChanged();
	}

	@Test
	public void testMasterTokenReleased() {
		masterTokenReleased();

		_assertReplicasUnchanged();
	}

	@Test
	public void testNonmasterLiferayNodeDoesNothing() {
		_setMasterExecutor(false);

		processClusterEvent();

		_assertReplicasUnchanged();
	}

	@Test
	public void testRemoteElasticsearchClusterIsLeftAlone() {
		_setEmbeddedCluster(false);

		processClusterEvent();

		_assertReplicasUnchanged();
	}

	@Test
	public void testResilientToUpdateFailures() {
		Throwable throwable = new RuntimeException();

		Mockito.doThrow(
			throwable
		).when(
			_replicasManager
		).updateNumberOfReplicas(
			Mockito.anyInt(), Mockito.any()
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ReplicasClusterListener.class.getName(), LoggerTestUtil.WARN)) {

			masterTokenAcquired();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to update number of replicas", logEntry.getMessage());
			Assert.assertSame(throwable, logEntry.getThrowable());
		}
	}

	protected void masterTokenAcquired() {
		_replicasClusterListener.masterTokenAcquired();
	}

	protected void masterTokenReleased() {
		_replicasClusterListener.masterTokenReleased();
	}

	protected void processClusterEvent() {
		_replicasClusterListener.processClusterEvent(ClusterEvent.join());
	}

	private void _assertReplicasChanged() {
		Mockito.verify(
			_replicasManager
		).updateNumberOfReplicas(
			_REPLICAS, _INDICES
		);
	}

	private void _assertReplicasUnchanged() {
		Mockito.verify(
			_replicasManager, Mockito.never()
		).updateNumberOfReplicas(
			Mockito.anyInt(), Mockito.any()
		);
	}

	private void _setEmbeddedCluster(boolean value) {
		Mockito.when(
			_replicasClusterContext.isEmbeddedOperationMode()
		).thenReturn(
			value
		);
	}

	private void _setMasterExecutor(boolean value) {
		Mockito.when(
			_replicasClusterContext.isMaster()
		).thenReturn(
			value
		);
	}

	private static final String[] _INDICES = {
		RandomTestUtil.randomString(), RandomTestUtil.randomString()
	};

	private static final int _REPLICAS = RandomTestUtil.randomInt() - 1;

	private final ReplicasClusterContext _replicasClusterContext = Mockito.mock(
		ReplicasClusterContext.class);
	private ReplicasClusterListener _replicasClusterListener;
	private final ReplicasManager _replicasManager = Mockito.mock(
		ReplicasManager.class);

}