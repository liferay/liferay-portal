/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.multiple.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cluster.ClusterEventListener;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterInvokeAcceptor;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.cluster.ClusterableContextThreadLocal;
import com.liferay.portal.kernel.cluster.FutureClusterResponses;
import com.liferay.portal.kernel.concurrent.DefaultNoticeableFuture;
import com.liferay.portal.kernel.concurrent.NoticeableFuture;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.scheduler.JobState;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.TriggerState;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.servlet.PluginContextLifecycleThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.lang.reflect.Constructor;

import java.net.InetAddress;
import java.net.NetworkInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Tina Tian
 */
public class ClusterSchedulerEngineTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpPropsUtil();

		_setUpSchedulerEngineHelperUtil();
		_setUpClusterSchedulerEngine();
		_setUpClusterInvokeAcceptor();
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testDeleteOnMaster() throws SchedulerException {

		// Test 1, MEMORY_CLUSTERED job by jobName and groupName

		_mockClusterMasterExecutor.reset(true, 0, 0);

		_mockSchedulerEngine.resetJobs(4, 4);

		_clusterSchedulerEngine.start();

		List<SchedulerResponse> schedulerResponses =
			_clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

		Assert.assertEquals(
			schedulerResponses.toString(), 4, schedulerResponses.size());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		_clusterSchedulerEngine.delete(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertEquals(
			schedulerResponses.toString(), 3, schedulerResponses.size());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 2, MEMORY_CLUSTERED jobs by groupName

		_clusterSchedulerEngine.delete(
			_MEMORY_CLUSTER_TEST_GROUP_NAME, StorageType.MEMORY_CLUSTERED);

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 3, PERSISTED job by jobName and groupName

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.PERSISTED);

		Assert.assertEquals(
			schedulerResponses.toString(), 4, schedulerResponses.size());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		_clusterSchedulerEngine.delete(
			_TEST_JOB_NAME_0, _PERSISTENT_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.PERSISTED);

		Assert.assertEquals(
			schedulerResponses.toString(), 3, schedulerResponses.size());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 4, PERSISTED jobs by groupName

		_clusterSchedulerEngine.delete(
			_PERSISTENT_TEST_GROUP_NAME, StorageType.PERSISTED);

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.PERSISTED);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));
	}

	@Test
	public void testDeleteOnSlave() throws SchedulerException {

		// Test 1, MEMORY_CLUSTERED job by jobName and groupName

		_mockClusterMasterExecutor.reset(false, 4, 0);

		_mockSchedulerEngine.resetJobs(0, 4);

		_clusterSchedulerEngine.start();

		List<SchedulerResponse> schedulerResponses =
			_clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		Assert.assertEquals(
			_memoryClusteredJobs.toString(), 4, _memoryClusteredJobs.size());

		_clusterSchedulerEngine.delete(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		Assert.assertEquals(
			_memoryClusteredJobs.toString(), 3, _memoryClusteredJobs.size());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 2, not existed group

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			_NOT_EXISTED_GROUP_NAME, StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		schedulerResponses = _getMemoryClusteredJobs(_NOT_EXISTED_GROUP_NAME);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		_clusterSchedulerEngine.delete(
			_NOT_EXISTED_GROUP_NAME, StorageType.MEMORY_CLUSTERED);

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			_NOT_EXISTED_GROUP_NAME, StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		schedulerResponses = _getMemoryClusteredJobs(_NOT_EXISTED_GROUP_NAME);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 3, MEMORY_CLUSTERED jobs by groupName

		_clusterSchedulerEngine.delete(
			_MEMORY_CLUSTER_TEST_GROUP_NAME, StorageType.MEMORY_CLUSTERED);

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));
	}

	@Test
	public void testGetSchedulerJobs() throws SchedulerException {
		_mockClusterMasterExecutor.reset(true, 0, 0);

		_mockSchedulerEngine.resetJobs(4, 2);

		_clusterSchedulerEngine.start();

		List<SchedulerResponse> schedulerResponses =
			_clusterSchedulerEngine.getScheduledJobs();

		Assert.assertEquals(
			schedulerResponses.toString(), 6, schedulerResponses.size());
	}

	@Test
	public void testMasterToSlave() throws SchedulerException {

		// Test 1, with log disabled

		_mockClusterMasterExecutor.reset(true, 0, 0);

		_mockSchedulerEngine.resetJobs(4, 2);

		_clusterSchedulerEngine.start();

		Assert.assertTrue(_mockClusterMasterExecutor.isMaster());

		List<SchedulerResponse> schedulerResponses =
			_clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

		Assert.assertEquals(
			schedulerResponses.toString(), 4, schedulerResponses.size());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ClusterSchedulerEngine.class.getName(), LoggerTestUtil.OFF)) {

			_mockClusterMasterExecutor.reset(false, 4, 2);

			ClusterMasterTokenTransitionListener
				clusterMasterTokenTransitionListener =
					_mockClusterMasterExecutor.
						getClusterMasterTokenTransitionListener();

			clusterMasterTokenTransitionListener.masterTokenReleased();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());

			Assert.assertFalse(_mockClusterMasterExecutor.isMaster());

			schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

			Assert.assertTrue(
				schedulerResponses.toString(), schedulerResponses.isEmpty());

			Assert.assertEquals(
				_memoryClusteredJobs.toString(), 4,
				_memoryClusteredJobs.size());

			// Test 2, with log enabled

			_mockClusterMasterExecutor.reset(true, 0, 0);

			_mockSchedulerEngine.resetJobs(4, 2);

			_clusterSchedulerEngine.start();

			_memoryClusteredJobs.clear();

			Assert.assertTrue(_mockClusterMasterExecutor.isMaster());

			schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

			Assert.assertEquals(
				schedulerResponses.toString(), 4, schedulerResponses.size());

			Assert.assertTrue(_memoryClusteredJobs.isEmpty());

			logEntries = logCapture.resetPriority(Level.ALL.toString());

			_mockClusterMasterExecutor.reset(false, 4, 2);

			clusterMasterTokenTransitionListener =
				_mockClusterMasterExecutor.
					getClusterMasterTokenTransitionListener();

			clusterMasterTokenTransitionListener.masterTokenReleased();

			Assert.assertEquals(logEntries.toString(), 3, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Load 4 memory clustered jobs from master",
				logEntry.getMessage());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				"4 MEMORY_CLUSTERED jobs stopped running on this node",
				logEntry.getMessage());

			logEntry = logEntries.get(2);

			Assert.assertEquals(
				"Unable to notify slave", logEntry.getMessage());

			Assert.assertFalse(_mockClusterMasterExecutor.isMaster());

			schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

			Assert.assertTrue(
				schedulerResponses.toString(), schedulerResponses.isEmpty());

			Assert.assertEquals(
				_memoryClusteredJobs.toString(), 4,
				_memoryClusteredJobs.size());
		}
	}

	@Test
	public void testPauseAndResumeOnMaster() throws SchedulerException {

		// Test 1, MEMORY_CLUSTERED job by jobName and groupName

		_mockClusterMasterExecutor.reset(true, 0, 0);

		_mockSchedulerEngine.resetJobs(4, 4);

		_clusterSchedulerEngine.start();

		SchedulerResponse schedulerResponse =
			_clusterSchedulerEngine.getScheduledJob(
				_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
				StorageType.MEMORY_CLUSTERED);

		_assertTriggerState(schedulerResponse, TriggerState.NORMAL);

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		_clusterSchedulerEngine.pause(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		schedulerResponse = _clusterSchedulerEngine.getScheduledJob(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		_assertTriggerState(schedulerResponse, TriggerState.PAUSED);

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		_clusterSchedulerEngine.resume(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		schedulerResponse = _clusterSchedulerEngine.getScheduledJob(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		_assertTriggerState(schedulerResponse, TriggerState.NORMAL);

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 2, PERSISTED job by jobName and groupName

		schedulerResponse = _clusterSchedulerEngine.getScheduledJob(
			_TEST_JOB_NAME_0, _PERSISTENT_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		_assertTriggerState(schedulerResponse, TriggerState.NORMAL);

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		_clusterSchedulerEngine.pause(
			_TEST_JOB_NAME_0, _PERSISTENT_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		schedulerResponse = _clusterSchedulerEngine.getScheduledJob(
			_TEST_JOB_NAME_0, _PERSISTENT_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		_assertTriggerState(schedulerResponse, TriggerState.PAUSED);

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		_clusterSchedulerEngine.resume(
			_TEST_JOB_NAME_0, _PERSISTENT_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		schedulerResponse = _clusterSchedulerEngine.getScheduledJob(
			_TEST_JOB_NAME_0, _PERSISTENT_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		_assertTriggerState(schedulerResponse, TriggerState.NORMAL);

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));
	}

	@Test
	public void testPauseAndResumeOnSlave() throws SchedulerException {

		// Test 1, MEMORY_CLUSTERED job by jobName and groupName

		_mockClusterMasterExecutor.reset(false, 4, 0);

		_mockSchedulerEngine.resetJobs(0, 4);

		_clusterSchedulerEngine.start();

		Assert.assertNull(
			_clusterSchedulerEngine.getScheduledJob(
				_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
				StorageType.MEMORY_CLUSTERED));

		SchedulerResponse schedulerResponse = _getMemoryClusteredJob(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME);

		_assertTriggerState(schedulerResponse, TriggerState.NORMAL);

		_clusterSchedulerEngine.pause(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		Assert.assertNull(
			_clusterSchedulerEngine.getScheduledJob(
				_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
				StorageType.MEMORY_CLUSTERED));

		schedulerResponse = _getMemoryClusteredJob(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME);

		_assertTriggerState(schedulerResponse, TriggerState.PAUSED);

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		_clusterSchedulerEngine.resume(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		Assert.assertNull(
			_clusterSchedulerEngine.getScheduledJob(
				_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
				StorageType.MEMORY_CLUSTERED));

		schedulerResponse = _getMemoryClusteredJob(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME);

		_assertTriggerState(schedulerResponse, TriggerState.NORMAL);

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 2, not existed job

		Assert.assertNull(
			_clusterSchedulerEngine.getScheduledJob(
				_TEST_JOB_NAME_0, _NOT_EXISTED_GROUP_NAME,
				StorageType.MEMORY_CLUSTERED));
		Assert.assertNull(
			_getMemoryClusteredJob(_TEST_JOB_NAME_0, _NOT_EXISTED_GROUP_NAME));

		_clusterSchedulerEngine.pause(
			_TEST_JOB_NAME_0, _NOT_EXISTED_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		Assert.assertNull(
			_clusterSchedulerEngine.getScheduledJob(
				_TEST_JOB_NAME_0, _NOT_EXISTED_GROUP_NAME,
				StorageType.MEMORY_CLUSTERED));
		Assert.assertNull(
			_getMemoryClusteredJob(_TEST_JOB_NAME_0, _NOT_EXISTED_GROUP_NAME));

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));
	}

	@Test
	public void testRunOnMaster() throws SchedulerException {
		_mockClusterMasterExecutor.reset(true, 0, 0);

		_clusterSchedulerEngine.start();

		long companyId = RandomTestUtil.randomLong();

		_clusterSchedulerEngine.run(
			companyId, _TEST_JOB_NAME_PREFIX, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		Assert.assertNull(_mockClusterMasterExecutor.geMethodHandler());

		_mockClusterMasterExecutor.reset(true, 0, 0);

		_clusterSchedulerEngine.run(
			companyId, _TEST_JOB_NAME_PREFIX, _PERSISTENT_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		Assert.assertNull(_mockClusterMasterExecutor.geMethodHandler());
	}

	@Test
	public void testRunOnSlave()
		throws NoSuchMethodException, SchedulerException {

		_mockClusterMasterExecutor.reset(false, 0, 0);

		_clusterSchedulerEngine.start();

		long companyId = RandomTestUtil.randomLong();

		_clusterSchedulerEngine.run(
			companyId, _TEST_JOB_NAME_PREFIX, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		MethodHandler methodHandler =
			_mockClusterMasterExecutor.geMethodHandler();

		Assert.assertNotNull(methodHandler);

		MethodKey methodKey = methodHandler.getMethodKey();

		Assert.assertNotNull(methodKey.getMethod());
		Assert.assertEquals(
			new MethodKey(
				SchedulerEngineHelperUtil.class, "run", long.class,
				String.class, String.class, StorageType.class),
			methodKey);

		_mockClusterMasterExecutor.reset(true, 0, 0);

		_clusterSchedulerEngine.run(
			companyId, _TEST_JOB_NAME_PREFIX, _PERSISTENT_TEST_GROUP_NAME,
			StorageType.PERSISTED);

		Assert.assertNull(_mockClusterMasterExecutor.geMethodHandler());
	}

	@Test
	public void testScheduleOnMaster() throws SchedulerException {

		// Test 1, MEMORY_CLUSTERED job

		_mockClusterMasterExecutor.reset(true, 0, 0);

		_mockSchedulerEngine.resetJobs(1, 1);

		MockClusterExecutor mockClusterExecutor = new MockClusterExecutor();

		_clusterSchedulerEngine.setClusterExecutor(mockClusterExecutor);

		_clusterSchedulerEngine.start();

		List<SchedulerResponse> schedulerResponses =
			_clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

		Assert.assertEquals(
			schedulerResponses.toString(), 1, schedulerResponses.size());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterRequest clusterRequest = mockClusterExecutor.getClusterRequest();

		Assert.assertNull(clusterRequest);

		_clusterSchedulerEngine.schedule(
			getTrigger(
				_TEST_JOB_NAME_PREFIX + "new", _MEMORY_CLUSTER_TEST_GROUP_NAME,
				_DEFAULT_INTERVAL),
			StringPool.BLANK, StringPool.BLANK, new Message(),
			StorageType.MEMORY_CLUSTERED);

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertEquals(
			schedulerResponses.toString(), 2, schedulerResponses.size());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		clusterRequest = mockClusterExecutor.getClusterRequest();

		Assert.assertNotNull(clusterRequest);
		Assert.assertTrue(clusterRequest.isMulticast());
		Assert.assertTrue(clusterRequest.isSkipLocal());

		MethodHandler methodHandler =
			(MethodHandler)clusterRequest.getPayload();

		Assert.assertEquals(
			new MethodKey(
				ClusterSchedulerEngine.class, "_addMemoryClusteredJob",
				SchedulerResponse.class, String.class),
			methodHandler.getMethodKey());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 2, PERSISTED job

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.PERSISTED);

		Assert.assertEquals(
			schedulerResponses.toString(), 1, schedulerResponses.size());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		_clusterSchedulerEngine.schedule(
			getTrigger(
				_TEST_JOB_NAME_PREFIX + "new", _PERSISTENT_TEST_GROUP_NAME,
				_DEFAULT_INTERVAL),
			StringPool.BLANK, StringPool.BLANK, new Message(),
			StorageType.PERSISTED);

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.PERSISTED);

		Assert.assertEquals(
			schedulerResponses.toString(), 2, schedulerResponses.size());

		Assert.assertTrue(_memoryClusteredJobs.isEmpty());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));
	}

	@Test
	public void testScheduleOnSlave() throws SchedulerException {
		_mockClusterMasterExecutor.reset(false, 1, 0);

		_mockSchedulerEngine.resetJobs(0, 0);

		_clusterSchedulerEngine.start();

		List<SchedulerResponse> schedulerResponses =
			_clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		Assert.assertEquals(
			_memoryClusteredJobs.toString(), 1, _memoryClusteredJobs.size());

		_mockClusterMasterExecutor.reset(false, 2, 0);

		_clusterSchedulerEngine.schedule(
			getTrigger(
				_TEST_JOB_NAME_PREFIX + "1", _MEMORY_CLUSTER_TEST_GROUP_NAME,
				_DEFAULT_INTERVAL),
			StringPool.BLANK, StringPool.BLANK, new Message(),
			StorageType.MEMORY_CLUSTERED);

		schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		Assert.assertEquals(
			_memoryClusteredJobs.toString(), 2, _memoryClusteredJobs.size());

		ClusterInvokeThreadLocal.setEnabled(false);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));
	}

	@Test
	public void testShutdown() throws SchedulerException {
		_clusterSchedulerEngine.start();

		Assert.assertNotNull(
			_mockClusterMasterExecutor.
				getClusterMasterTokenTransitionListener());

		_clusterSchedulerEngine.shutdown();

		Assert.assertNull(
			_mockClusterMasterExecutor.
				getClusterMasterTokenTransitionListener());
	}

	@Test
	public void testSlaveToMaster() throws SchedulerException {

		// Test 1, with log disabled

		_mockClusterMasterExecutor.reset(false, 4, 0);

		_mockSchedulerEngine.resetJobs(0, 0);

		_clusterSchedulerEngine.start();

		Assert.assertFalse(_mockClusterMasterExecutor.isMaster());

		List<SchedulerResponse> schedulerResponses =
			_clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			schedulerResponses.toString(), schedulerResponses.isEmpty());

		Assert.assertEquals(
			_memoryClusteredJobs.toString(), 4, _memoryClusteredJobs.size());

		_clusterSchedulerEngine.pause(
			_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
			StorageType.MEMORY_CLUSTERED);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ClusterSchedulerEngine.class.getName(), LoggerTestUtil.OFF)) {

			_mockClusterMasterExecutor.reset(true, 0, 0);

			ClusterMasterTokenTransitionListener
				clusterMasterTokenTransitionListener =
					_mockClusterMasterExecutor.
						getClusterMasterTokenTransitionListener();

			clusterMasterTokenTransitionListener.masterTokenAcquired();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());

			Assert.assertTrue(_mockClusterMasterExecutor.isMaster());

			schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

			Assert.assertEquals(
				schedulerResponses.toString(), 4, schedulerResponses.size());

			SchedulerResponse schedulerResponse =
				_clusterSchedulerEngine.getScheduledJob(
					_TEST_JOB_NAME_0, _MEMORY_CLUSTER_TEST_GROUP_NAME,
					StorageType.MEMORY_CLUSTERED);

			_assertTriggerState(schedulerResponse, TriggerState.PAUSED);

			Assert.assertTrue(_memoryClusteredJobs.isEmpty());

			// Test 2, with log enabled

			_mockClusterMasterExecutor.reset(false, 4, 0);

			_mockSchedulerEngine.resetJobs(0, 0);

			_clusterSchedulerEngine.start();

			Assert.assertFalse(_mockClusterMasterExecutor.isMaster());

			schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

			Assert.assertTrue(
				schedulerResponses.toString(), schedulerResponses.isEmpty());

			Assert.assertEquals(
				_memoryClusteredJobs.toString(), 4,
				_memoryClusteredJobs.size());

			logEntries = logCapture.resetPriority(Level.ALL.toString());

			_mockClusterMasterExecutor.reset(true, 0, 0);

			clusterMasterTokenTransitionListener =
				_mockClusterMasterExecutor.
					getClusterMasterTokenTransitionListener();

			clusterMasterTokenTransitionListener.masterTokenAcquired();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"4 MEMORY_CLUSTERED jobs started running on this node",
				logEntry.getMessage());

			logEntry = logEntries.get(1);

			Assert.assertEquals(
				"Unable to notify slave", logEntry.getMessage());

			Assert.assertTrue(_mockClusterMasterExecutor.isMaster());

			schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(
				StorageType.MEMORY_CLUSTERED);

			Assert.assertEquals(
				schedulerResponses.toString(), 4, schedulerResponses.size());

			Assert.assertTrue(_memoryClusteredJobs.isEmpty());
		}
	}

	@Test
	public void testThreadLocal() throws SchedulerException {

		// Test 1, PERSISTED when portal is starting

		ClusterInvokeThreadLocal.setEnabled(false);

		PluginContextLifecycleThreadLocal.setInitializing(false);
		PluginContextLifecycleThreadLocal.setDestroying(false);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.PERSISTED);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 2, MEMORY_CLUSTERED when portal is starting

		ClusterInvokeThreadLocal.setEnabled(false);

		PluginContextLifecycleThreadLocal.setInitializing(false);
		PluginContextLifecycleThreadLocal.setDestroying(false);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		_clusterSchedulerEngine.start();

		// Test 3, PERSISTED when portal is started

		ClusterInvokeThreadLocal.setEnabled(false);

		PluginContextLifecycleThreadLocal.setInitializing(false);
		PluginContextLifecycleThreadLocal.setDestroying(false);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.PERSISTED);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 4, MEMORY_CLUSTERED when portal is started

		ClusterInvokeThreadLocal.setEnabled(false);

		PluginContextLifecycleThreadLocal.setInitializing(false);
		PluginContextLifecycleThreadLocal.setDestroying(false);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 5, PERSISTED when plugin is starting

		ClusterInvokeThreadLocal.setEnabled(false);

		PluginContextLifecycleThreadLocal.setInitializing(true);
		PluginContextLifecycleThreadLocal.setDestroying(false);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.PERSISTED);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 6, MEMORY_CLUSTERED when plugin is starting

		ClusterInvokeThreadLocal.setEnabled(false);

		PluginContextLifecycleThreadLocal.setInitializing(true);
		PluginContextLifecycleThreadLocal.setDestroying(false);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 7, PERSISTED when plugin is destroying

		ClusterInvokeThreadLocal.setEnabled(false);

		PluginContextLifecycleThreadLocal.setInitializing(false);
		PluginContextLifecycleThreadLocal.setDestroying(true);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.PERSISTED);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 8, MEMORY_CLUSTERED when plugin is destroying

		ClusterInvokeThreadLocal.setEnabled(false);

		PluginContextLifecycleThreadLocal.setInitializing(false);
		PluginContextLifecycleThreadLocal.setDestroying(true);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 9, PERSISTED when cluster invoke is disabled

		ClusterInvokeThreadLocal.setEnabled(false);

		PluginContextLifecycleThreadLocal.setInitializing(false);
		PluginContextLifecycleThreadLocal.setDestroying(false);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.PERSISTED);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 10, PERSISTED when cluster invoke is enabled

		ClusterInvokeThreadLocal.setEnabled(true);

		PluginContextLifecycleThreadLocal.setInitializing(false);
		PluginContextLifecycleThreadLocal.setDestroying(false);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.PERSISTED);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 11, MEMORY_CLUSTERED when cluster invoke is disabled

		ClusterInvokeThreadLocal.setEnabled(false);

		PluginContextLifecycleThreadLocal.setInitializing(false);
		PluginContextLifecycleThreadLocal.setDestroying(false);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertTrue(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));

		// Test 12, MEMORY_CLUSTERED when cluster invoke is enabled

		ClusterInvokeThreadLocal.setEnabled(true);

		PluginContextLifecycleThreadLocal.setInitializing(false);
		PluginContextLifecycleThreadLocal.setDestroying(false);

		_clusterSchedulerEngine.setClusterableThreadLocal(
			StorageType.MEMORY_CLUSTERED);

		Assert.assertFalse(
			_clusterInvokeAcceptor.accept(
				ClusterableContextThreadLocal.collectThreadLocalContext()));
	}

	protected static Trigger getTrigger(
		String jobName, String groupName, int interval) {

		return new MockTrigger(
			jobName, groupName, null, null, interval, TimeUnit.SECOND);
	}

	private void _assertTriggerState(
		SchedulerResponse schedulerResponse,
		TriggerState expectedTriggerState) {

		Message message = schedulerResponse.getMessage();

		JobState jobState = (JobState)message.get(SchedulerEngine.JOB_STATE);

		Assert.assertEquals(expectedTriggerState, jobState.getTriggerState());
	}

	private SchedulerResponse _getMemoryClusteredJob(
		String jobName, String groupName) {

		ObjectValuePair<SchedulerResponse, TriggerState> objectValuePair =
			_memoryClusteredJobs.get(
				StringBundler.concat(groupName, StringPool.PERIOD, jobName));

		if (objectValuePair == null) {
			return null;
		}

		SchedulerResponse schedulerResponse = objectValuePair.getKey();

		Message message = schedulerResponse.getMessage();

		message.put(
			SchedulerEngine.JOB_STATE,
			new JobState(objectValuePair.getValue()));

		return schedulerResponse;
	}

	private List<SchedulerResponse> _getMemoryClusteredJobs(String groupName) {
		List<SchedulerResponse> schedulerResponses = new ArrayList<>();

		for (ObjectValuePair<SchedulerResponse, TriggerState> objectValuePair :
				_memoryClusteredJobs.values()) {

			SchedulerResponse schedulerResponse = objectValuePair.getKey();

			if (groupName.equals(schedulerResponse.getGroupName())) {
				schedulerResponses.add(
					_getMemoryClusteredJob(
						schedulerResponse.getJobName(), groupName));
			}
		}

		return schedulerResponses;
	}

	private void _setUpClusterInvokeAcceptor() throws Exception {
		Class<? extends ClusterInvokeAcceptor> clusterInvokeAcceptorClass =
			(Class<? extends ClusterInvokeAcceptor>)Class.forName(
				SchedulerClusterInvokeAcceptor.class.getName());

		Constructor<? extends ClusterInvokeAcceptor> constructor =
			clusterInvokeAcceptorClass.getDeclaredConstructor();

		constructor.setAccessible(true);

		_clusterInvokeAcceptor = constructor.newInstance();
	}

	private void _setUpClusterSchedulerEngine() {
		_mockSchedulerEngine = new MockSchedulerEngine();

		_clusterSchedulerEngine = new ClusterSchedulerEngine(
			_mockSchedulerEngine, new MockTriggerFactory());

		_clusterSchedulerEngine.setClusterMasterExecutor(
			_mockClusterMasterExecutor);

		_memoryClusteredJobs = ReflectionTestUtil.getFieldValue(
			_clusterSchedulerEngine, "_memoryClusteredJobs");
	}

	private void _setUpPropsUtil() {
		PropsUtil.set(PropsKeys.CLUSTERABLE_ADVICE_CALL_MASTER_TIMEOUT, "100");
	}

	private void _setUpSchedulerEngineHelperUtil() {
		SchedulerEngineHelper schedulerEngineHelper = Mockito.mock(
			SchedulerEngineHelper.class);

		Mockito.when(
			schedulerEngineHelper.getJobState(
				Mockito.any(SchedulerResponse.class))
		).then(
			new Answer<TriggerState>() {

				@Override
				public TriggerState answer(InvocationOnMock invocationOnMock)
					throws Throwable {

					Object[] args = invocationOnMock.getArguments();

					SchedulerResponse schedulerResponse =
						(SchedulerResponse)args[0];

					Message message = schedulerResponse.getMessage();

					JobState jobState = (JobState)message.get(
						SchedulerEngine.JOB_STATE);

					return jobState.getTriggerState();
				}

			}
		);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			SchedulerEngineHelper.class, schedulerEngineHelper, null);
	}

	private static final int _DEFAULT_INTERVAL = 20;

	private static final String _MEMORY_CLUSTER_TEST_GROUP_NAME =
		"memory.cluster.test.group";

	private static final String _NOT_EXISTED_GROUP_NAME =
		"not.existed.test.group";

	private static final String _PERSISTENT_TEST_GROUP_NAME =
		"persistent.test.group";

	private static final String _TEST_JOB_NAME_0 = "test.job.0";

	private static final String _TEST_JOB_NAME_PREFIX = "test.job.";

	private static final MethodKey _getScheduledJobMethodKey = new MethodKey(
		SchedulerEngineHelperUtil.class, "getScheduledJob", String.class,
		String.class, StorageType.class);
	private static final MethodKey _getScheduledJobsMethodKey = new MethodKey(
		SchedulerEngineHelperUtil.class, "getScheduledJobs", StorageType.class);

	private ClusterInvokeAcceptor _clusterInvokeAcceptor;
	private ClusterSchedulerEngine _clusterSchedulerEngine;
	private Map<String, ObjectValuePair<SchedulerResponse, TriggerState>>
		_memoryClusteredJobs;
	private final MockClusterMasterExecutor _mockClusterMasterExecutor =
		new MockClusterMasterExecutor();
	private MockSchedulerEngine _mockSchedulerEngine;
	private ServiceRegistration<?> _serviceRegistration;

	private static class MockClusterExecutor implements ClusterExecutor {

		@Override
		public FutureClusterResponses execute(ClusterRequest clusterRequest) {
			_clusterRequest = clusterRequest;

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
			return null;
		}

		@Override
		public List<ClusterNode> getClusterNodes() {
			return null;
		}

		public ClusterRequest getClusterRequest() {
			return _clusterRequest;
		}

		@Override
		public ClusterNode getLocalClusterNode() {
			return null;
		}

		@Override
		public boolean isClusterNodeAlive(String clusterNodeId) {
			return false;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		private ClusterRequest _clusterRequest;

	}

	private static class MockClusterMasterExecutor
		implements ClusterMasterExecutor {

		@Override
		public void addClusterMasterTokenTransitionListener(
			ClusterMasterTokenTransitionListener
				clusterMasterTokenAcquisitionListener) {

			_clusterMasterTokenTransitionListener =
				clusterMasterTokenAcquisitionListener;
		}

		@Override
		public <T> NoticeableFuture<T> executeOnMaster(
			MethodHandler methodHandler) {

			_methodHandler = methodHandler;

			if (_exception) {
				throw new SystemException();
			}

			T result = null;

			MethodKey methodKey = methodHandler.getMethodKey();

			if (methodKey.equals(_getScheduledJobsMethodKey)) {
				StorageType storageType =
					(StorageType)methodHandler.getArguments()[0];

				result = (T)_mockSchedulerEngine.getScheduledJobs(storageType);
			}
			else if (methodKey.equals(_getScheduledJobMethodKey)) {
				String jobName = (String)methodHandler.getArguments()[0];
				String groupName = (String)methodHandler.getArguments()[1];
				StorageType storageType =
					(StorageType)methodHandler.getArguments()[2];

				result = (T)_mockSchedulerEngine.getScheduledJob(
					jobName, groupName, storageType);
			}

			DefaultNoticeableFuture<T> defaultNoticeableFuture =
				new DefaultNoticeableFuture<>();

			defaultNoticeableFuture.set(result);

			return defaultNoticeableFuture;
		}

		public MethodHandler geMethodHandler() {
			return _methodHandler;
		}

		public ClusterMasterTokenTransitionListener
			getClusterMasterTokenTransitionListener() {

			return _clusterMasterTokenTransitionListener;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public boolean isMaster() {
			return _master;
		}

		@Override
		public void removeClusterMasterTokenTransitionListener(
			ClusterMasterTokenTransitionListener
				clusterMasterTokenAcquisitionListener) {

			if (_clusterMasterTokenTransitionListener ==
					clusterMasterTokenAcquisitionListener) {

				_clusterMasterTokenTransitionListener = null;
			}
		}

		public void reset(
			boolean master, int memoryClusterJobs, int persistentJobs) {

			_master = master;

			_methodHandler = null;

			_mockSchedulerEngine.resetJobs(memoryClusterJobs, persistentJobs);
		}

		public void setException(boolean exception) {
			_exception = exception;
		}

		private ClusterMasterTokenTransitionListener
			_clusterMasterTokenTransitionListener;
		private boolean _exception;
		private boolean _master;
		private MethodHandler _methodHandler;
		private final MockSchedulerEngine _mockSchedulerEngine =
			new MockSchedulerEngine();

	}

	private static class MockSchedulerEngine implements SchedulerEngine {

		@Override
		public void delete(String groupName, StorageType storageType) {
			Set<String> keySet = _defaultJobs.keySet();

			Iterator<String> iterator = keySet.iterator();

			while (iterator.hasNext()) {
				String key = iterator.next();

				if (key.contains(groupName) &&
					key.contains(storageType.toString())) {

					iterator.remove();
				}
			}
		}

		@Override
		public void delete(
			String jobName, String groupName, StorageType storageType) {

			_defaultJobs.remove(_getFullName(jobName, groupName, storageType));
		}

		@Override
		public SchedulerResponse getScheduledJob(
			String jobName, String groupName, StorageType storageType) {

			return _defaultJobs.get(
				_getFullName(jobName, groupName, storageType));
		}

		@Override
		public List<SchedulerResponse> getScheduledJobs() {
			return new ArrayList<>(_defaultJobs.values());
		}

		@Override
		public List<SchedulerResponse> getScheduledJobs(
			StorageType storageType) {

			List<SchedulerResponse> schedulerResponses = new ArrayList<>();

			for (SchedulerResponse schedulerResponse : _defaultJobs.values()) {
				if (storageType == schedulerResponse.getStorageType()) {
					schedulerResponses.add(schedulerResponse);
				}
			}

			return schedulerResponses;
		}

		@Override
		public List<SchedulerResponse> getScheduledJobs(
			String groupName, StorageType storageType) {

			List<SchedulerResponse> schedulerResponses = new ArrayList<>();

			for (Map.Entry<String, SchedulerResponse> entry :
					_defaultJobs.entrySet()) {

				String key = entry.getKey();

				if (key.contains(groupName) &&
					key.contains(storageType.toString())) {

					schedulerResponses.add(entry.getValue());
				}
			}

			return schedulerResponses;
		}

		@Override
		public void pause(
			String jobName, String groupName, StorageType storageType) {

			SchedulerResponse schedulerResponse = getScheduledJob(
				jobName, groupName, storageType);

			Message message = schedulerResponse.getMessage();

			message.put(
				SchedulerEngine.JOB_STATE, new JobState(TriggerState.PAUSED));
		}

		public void resetJobs(int memoryClusterJobs, int persistentJobs) {
			_defaultJobs.clear();

			for (int i = 0; i < memoryClusterJobs; i++) {
				_addJobs(
					_TEST_JOB_NAME_PREFIX.concat(String.valueOf(i)),
					_MEMORY_CLUSTER_TEST_GROUP_NAME,
					StorageType.MEMORY_CLUSTERED, null, null);
			}

			for (int i = 0; i < persistentJobs; i++) {
				_addJobs(
					_TEST_JOB_NAME_PREFIX.concat(String.valueOf(i)),
					_PERSISTENT_TEST_GROUP_NAME, StorageType.PERSISTED, null,
					null);
			}
		}

		@Override
		public void resume(
			String jobName, String groupName, StorageType storageType) {

			SchedulerResponse schedulerResponse = getScheduledJob(
				jobName, groupName, storageType);

			Message message = schedulerResponse.getMessage();

			message.put(
				SchedulerEngine.JOB_STATE, new JobState(TriggerState.NORMAL));
		}

		@Override
		public void run(
			long companyId, String jobName, String groupName,
			StorageType storageType) {
		}

		@Override
		public void schedule(
				Trigger trigger, String description, String destinationName,
				Message message, StorageType storageType)
			throws SchedulerException {

			String jobName = trigger.getJobName();

			if (!jobName.startsWith(_TEST_JOB_NAME_PREFIX)) {
				throw new SchedulerException("Invalid job name " + jobName);
			}

			_addJobs(
				trigger.getJobName(), trigger.getGroupName(), storageType,
				trigger, message);
		}

		@Override
		public void shutdown() {
			_defaultJobs.clear();
		}

		@Override
		public void start() {
		}

		@Override
		public void validateTrigger(Trigger trigger, StorageType storageType) {
		}

		private SchedulerResponse _addJobs(
			String jobName, String groupName, StorageType storageType,
			Trigger trigger, Message message) {

			SchedulerResponse schedulerResponse = new SchedulerResponse();

			schedulerResponse.setDestinationName(
				DestinationNames.SCHEDULER_DISPATCH);
			schedulerResponse.setGroupName(groupName);
			schedulerResponse.setJobName(jobName);

			if (message == null) {
				message = new Message();
			}

			message.put(
				SchedulerEngine.JOB_STATE, new JobState(TriggerState.NORMAL));

			schedulerResponse.setMessage(message);
			schedulerResponse.setStorageType(storageType);

			if (trigger == null) {
				trigger = getTrigger(jobName, groupName, _DEFAULT_INTERVAL);
			}

			schedulerResponse.setTrigger(trigger);

			_defaultJobs.put(
				_getFullName(jobName, groupName, storageType),
				schedulerResponse);

			return schedulerResponse;
		}

		private String _getFullName(
			String jobName, String groupName, StorageType storageType) {

			return StringBundler.concat(
				groupName, StringPool.PERIOD, jobName, storageType);
		}

		private final Map<String, SchedulerResponse> _defaultJobs =
			new HashMap<>();

	}

	private static class MockTrigger implements Trigger {

		public MockTrigger(
			String jobName, String groupName, Date startDate, Date endDate,
			int interval, TimeUnit timeUnit) {

			_jobName = jobName;
			_groupName = groupName;

			if (startDate != null) {
				_startDate = startDate;
			}
			else {
				_startDate = new Date();
			}

			_endDate = endDate;
			_interval = interval;
			_timeUnit = timeUnit;
		}

		@Override
		public Date getEndDate() {
			return _endDate;
		}

		@Override
		public Date getFireDateAfter(Date date) {
			if (_timeUnit != TimeUnit.SECOND) {
				return null;
			}

			long nextFirTime = _startDate.getTime();

			do {
				nextFirTime += _interval * 1000;
			}
			while (nextFirTime <= date.getTime());

			return new Date(nextFirTime);
		}

		@Override
		public String getGroupName() {
			return _groupName;
		}

		public int getInterval() {
			return _interval;
		}

		@Override
		public String getJobName() {
			return _jobName;
		}

		@Override
		public Date getStartDate() {
			return _startDate;
		}

		public TimeUnit getTimeUnit() {
			return _timeUnit;
		}

		@Override
		public Serializable getWrappedTrigger() {
			return null;
		}

		private final Date _endDate;
		private final String _groupName;
		private final int _interval;
		private final String _jobName;
		private final Date _startDate;
		private final TimeUnit _timeUnit;

	}

	private static class MockTriggerFactory implements TriggerFactory {

		@Override
		public Trigger createTrigger(
			String jobName, String groupName, Date startDate, Date endDate,
			int interval, TimeUnit timeUnit) {

			return null;
		}

		@Override
		public Trigger createTrigger(
			String jobName, String groupName, Date startDate, Date endDate,
			String cronExpression) {

			return null;
		}

		@Override
		public Trigger createTrigger(
			String jobName, String groupName, Date startDate, Date endDate,
			String cronExpression, TimeZone timeZone) {

			return null;
		}

		@Override
		public Trigger createTrigger(
			Trigger trigger, Date startDate, Date endDate) {

			MockTrigger mockTrigger = (MockTrigger)trigger;

			return new MockTrigger(
				mockTrigger.getJobName(), mockTrigger.getGroupName(), startDate,
				endDate, mockTrigger.getInterval(), mockTrigger.getTimeUnit());
		}

	}

}