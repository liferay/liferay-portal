/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.messaging;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Vendel Toreki
 * @author Carlos Correa
 */
public class DispatchConfiguratorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(
			_destinationFactory.createDestination(Mockito.any())
		).thenReturn(
			Mockito.mock(Destination.class)
		);

		Mockito.when(
			_allNodesDispatchTrigger.getDispatchTaskClusterMode()
		).thenReturn(
			DispatchTaskClusterMode.ALL_NODES.getMode()
		);

		Mockito.when(
			_notApplicableDispatchTrigger.getDispatchTaskClusterMode()
		).thenReturn(
			DispatchTaskClusterMode.NOT_APPLICABLE.getMode()
		);

		Mockito.when(
			_singleNodeMemoryClusteredDispatchTrigger.
				getDispatchTaskClusterMode()
		).thenReturn(
			DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED.getMode()
		);

		Mockito.when(
			_singleNodePersistedDispatchTrigger.getDispatchTaskClusterMode()
		).thenReturn(
			DispatchTaskClusterMode.SINGLE_NODE_PERSISTED.getMode()
		);

		List<DispatchTrigger> dispatchTriggers = Arrays.asList(
			_allNodesDispatchTrigger, _notApplicableDispatchTrigger,
			_singleNodeMemoryClusteredDispatchTrigger,
			_singleNodePersistedDispatchTrigger);

		for (DispatchTrigger dispatchTrigger : dispatchTriggers) {
			Mockito.when(
				dispatchTrigger.getDispatchTriggerId()
			).thenReturn(
				RandomTestUtil.randomLong()
			);

			Mockito.when(
				dispatchTrigger.getTimeZoneId()
			).thenReturn(
				"UTC"
			);
		}

		Mockito.when(
			_dispatchTriggerLocalService.getActiveDispatchTriggers(
				Mockito.anyList())
		).thenAnswer(
			invocationOnMock -> {
				List<DispatchTaskClusterMode> dispatchTaskClusterModes =
					invocationOnMock.getArgument(0);

				return ListUtil.filter(
					dispatchTriggers,
					dispatchTrigger -> dispatchTaskClusterModes.contains(
						DispatchTaskClusterMode.valueOf(
							dispatchTrigger.getDispatchTaskClusterMode())));
			}
		);

		Mockito.when(
			_triggerFactory.createTrigger(
				Mockito.anyString(), Mockito.anyString(),
				Mockito.nullable(Date.class), Mockito.nullable(Date.class),
				Mockito.nullable(String.class), Mockito.any(TimeZone.class))
		).thenAnswer(
			invocationOnMock -> {
				Trigger trigger = Mockito.mock(Trigger.class);

				Mockito.when(
					trigger.getJobName()
				).thenReturn(
					invocationOnMock.getArgument(0)
				);

				return trigger;
			}
		);
	}

	@Test
	public void testActivateDeactivateSkipListenerWhenClusterDisabled()
		throws Exception {

		Mockito.when(
			_clusterMasterExecutor.isEnabled()
		).thenReturn(
			false
		);

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			false
		);

		_dispatchConfigurator.activate(_bundleContext);

		Mockito.reset(_schedulerEngineHelper);

		Mockito.verify(
			_clusterMasterExecutor, Mockito.never()
		).addClusterMasterTokenTransitionListener(
			Mockito.any()
		);

		_dispatchConfigurator.deactivate();

		Mockito.verify(
			_clusterMasterExecutor, Mockito.never()
		).removeClusterMasterTokenTransitionListener(
			Mockito.any()
		);
	}

	@Test
	public void testActivateSchedulesAllTypesOfJobsOnMasterNode()
		throws Exception {

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			true
		);

		_dispatchConfigurator.activate(_bundleContext);

		Mockito.verify(
			_dispatchTriggerLocalService
		).getActiveDispatchTriggers(
			Mockito.eq(
				Arrays.asList(
					DispatchTaskClusterMode.ALL_NODES,
					DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED,
					DispatchTaskClusterMode.SINGLE_NODE_PERSISTED))
		);

		_verifySchedule(_allNodesDispatchTrigger, StorageType.MEMORY);

		_verifyNeverSchedule(_notApplicableDispatchTrigger);

		_verifySchedule(
			_singleNodeMemoryClusteredDispatchTrigger,
			StorageType.MEMORY_CLUSTERED);

		_verifySchedule(
			_singleNodePersistedDispatchTrigger, StorageType.PERSISTED);
	}

	@Test
	public void testActivateSchedulesOnlyAllNodesJobs() throws Exception {
		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			false
		);

		_dispatchConfigurator.activate(_bundleContext);

		Mockito.verify(
			_dispatchTriggerLocalService
		).getActiveDispatchTriggers(
			Mockito.eq(
				Collections.singletonList(DispatchTaskClusterMode.ALL_NODES))
		);

		_verifySchedule(_allNodesDispatchTrigger, StorageType.MEMORY);

		_verifyNeverSchedule(_notApplicableDispatchTrigger);

		_verifyNeverSchedule(_singleNodeMemoryClusteredDispatchTrigger);

		_verifyNeverSchedule(_singleNodePersistedDispatchTrigger);
	}

	@Test
	public void testActivateSchedulesRemainingJobsWhenSchedulerCheckFails()
		throws Exception {

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			true
		);

		Mockito.when(
			_schedulerEngineHelper.getScheduledJob(
				_getJobName(_singleNodeMemoryClusteredDispatchTrigger),
				_getGroupName(_singleNodeMemoryClusteredDispatchTrigger),
				StorageType.MEMORY_CLUSTERED)
		).thenThrow(
			new SchedulerException()
		);

		_dispatchConfigurator.activate(_bundleContext);

		_verifyNeverSchedule(_singleNodeMemoryClusteredDispatchTrigger);

		_verifySchedule(
			_singleNodePersistedDispatchTrigger, StorageType.PERSISTED);
	}

	@Test
	public void testActivateSkipsAlreadyScheduledJobs() throws Exception {
		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			true
		);

		Mockito.when(
			_schedulerEngineHelper.getScheduledJob(
				Mockito.anyString(), Mockito.anyString(), Mockito.any())
		).thenReturn(
			Mockito.mock(SchedulerResponse.class)
		);

		_dispatchConfigurator.activate(_bundleContext);

		_verifyNeverGetScheduledJob(_allNodesDispatchTrigger);

		_verifySchedule(_allNodesDispatchTrigger, StorageType.MEMORY);

		_verifyNeverSchedule(_singleNodeMemoryClusteredDispatchTrigger);

		_verifyNeverSchedule(_singleNodePersistedDispatchTrigger);
	}

	@Test
	public void testDeactivateUnschedulesAllTypeOfJobsOnMasterNode()
		throws Exception {

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			true
		);

		ServiceRegistration<Destination> serviceRegistration = Mockito.mock(
			ServiceRegistration.class);

		ReflectionTestUtil.setFieldValue(
			_dispatchConfigurator, "_serviceRegistration", serviceRegistration);

		_dispatchConfigurator.deactivate();

		Mockito.verify(
			_dispatchTriggerLocalService
		).getActiveDispatchTriggers(
			Mockito.eq(
				Arrays.asList(
					DispatchTaskClusterMode.ALL_NODES,
					DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED,
					DispatchTaskClusterMode.SINGLE_NODE_PERSISTED))
		);

		_verifyDelete(_allNodesDispatchTrigger, StorageType.MEMORY);

		_verifyNeverGetScheduledJob(_allNodesDispatchTrigger);

		_verifyNeverDelete(_notApplicableDispatchTrigger);

		_verifyDelete(
			_singleNodeMemoryClusteredDispatchTrigger,
			StorageType.MEMORY_CLUSTERED);

		_verifyDelete(
			_singleNodePersistedDispatchTrigger, StorageType.PERSISTED);
	}

	@Test
	public void testDeactivateUnschedulesOnlyAllNodesJobs() throws Exception {
		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			false
		);

		ServiceRegistration<Destination> serviceRegistration = Mockito.mock(
			ServiceRegistration.class);

		ReflectionTestUtil.setFieldValue(
			_dispatchConfigurator, "_serviceRegistration", serviceRegistration);

		_dispatchConfigurator.deactivate();

		Mockito.verify(
			_dispatchTriggerLocalService
		).getActiveDispatchTriggers(
			Mockito.eq(
				Collections.singletonList(DispatchTaskClusterMode.ALL_NODES))
		);

		_verifyDelete(_allNodesDispatchTrigger, StorageType.MEMORY);

		_verifyNeverGetScheduledJob(_allNodesDispatchTrigger);

		_verifyNeverDelete(_notApplicableDispatchTrigger);

		_verifyNeverDelete(_singleNodeMemoryClusteredDispatchTrigger);

		_verifyNeverDelete(_singleNodePersistedDispatchTrigger);
	}

	@Test
	public void testMasterTokenAcquiredSchedulesOnlySingleNodeJobs()
		throws Exception {

		Mockito.when(
			_clusterMasterExecutor.isEnabled()
		).thenReturn(
			true
		);

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			false
		);

		_dispatchConfigurator.activate(_bundleContext);

		Mockito.reset(_schedulerEngineHelper);

		ArgumentCaptor<ClusterMasterTokenTransitionListener> argumentCaptor =
			ArgumentCaptor.forClass(ClusterMasterTokenTransitionListener.class);

		Mockito.verify(
			_clusterMasterExecutor
		).addClusterMasterTokenTransitionListener(
			argumentCaptor.capture()
		);

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			true
		);

		ClusterMasterTokenTransitionListener
			clusterMasterTokenTransitionListener = argumentCaptor.getValue();

		clusterMasterTokenTransitionListener.masterTokenAcquired();

		Mockito.verify(
			_dispatchTriggerLocalService
		).getActiveDispatchTriggers(
			Mockito.eq(
				Arrays.asList(
					DispatchTaskClusterMode.SINGLE_NODE_MEMORY_CLUSTERED,
					DispatchTaskClusterMode.SINGLE_NODE_PERSISTED))
		);

		_verifyNeverSchedule(_allNodesDispatchTrigger);

		_verifyNeverSchedule(_notApplicableDispatchTrigger);

		_verifySchedule(
			_singleNodeMemoryClusteredDispatchTrigger,
			StorageType.MEMORY_CLUSTERED);

		_verifySchedule(
			_singleNodePersistedDispatchTrigger, StorageType.PERSISTED);

		_dispatchConfigurator.deactivate();

		Mockito.verify(
			_clusterMasterExecutor
		).removeClusterMasterTokenTransitionListener(
			Mockito.same(clusterMasterTokenTransitionListener)
		);
	}

	@Test
	public void testMasterTokenReleasedTriggersNoSchedulerChanges()
		throws Exception {

		Mockito.when(
			_clusterMasterExecutor.isEnabled()
		).thenReturn(
			true
		);

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			true
		);

		_dispatchConfigurator.activate(_bundleContext);

		Mockito.reset(_schedulerEngineHelper);

		ArgumentCaptor<ClusterMasterTokenTransitionListener> argumentCaptor =
			ArgumentCaptor.forClass(ClusterMasterTokenTransitionListener.class);

		Mockito.verify(
			_clusterMasterExecutor
		).addClusterMasterTokenTransitionListener(
			argumentCaptor.capture()
		);

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			false
		);

		ClusterMasterTokenTransitionListener
			clusterMasterTokenTransitionListener = argumentCaptor.getValue();

		clusterMasterTokenTransitionListener.masterTokenReleased();

		Mockito.verify(
			_dispatchTriggerLocalService
		).getActiveDispatchTriggers(
			Mockito.anyList()
		);

		Mockito.verify(
			_schedulerEngineHelper, Mockito.never()
		).schedule(
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.any()
		);

		Mockito.verify(
			_schedulerEngineHelper, Mockito.never()
		).delete(
			Mockito.any(), Mockito.any(), Mockito.any()
		);

		_dispatchConfigurator.deactivate();

		Mockito.verify(
			_clusterMasterExecutor
		).removeClusterMasterTokenTransitionListener(
			Mockito.same(clusterMasterTokenTransitionListener)
		);
	}

	@Test
	public void testMasterTokenTransitionSkipsAllNodesJobs() throws Exception {
		Mockito.when(
			_clusterMasterExecutor.isEnabled()
		).thenReturn(
			true
		);

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			false
		);

		_dispatchConfigurator.activate(_bundleContext);

		Mockito.reset(_schedulerEngineHelper);

		ArgumentCaptor<ClusterMasterTokenTransitionListener> argumentCaptor =
			ArgumentCaptor.forClass(ClusterMasterTokenTransitionListener.class);

		Mockito.verify(
			_clusterMasterExecutor
		).addClusterMasterTokenTransitionListener(
			argumentCaptor.capture()
		);

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			true
		);

		ClusterMasterTokenTransitionListener
			clusterMasterTokenTransitionListener = argumentCaptor.getValue();

		clusterMasterTokenTransitionListener.masterTokenAcquired();

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			false
		);

		clusterMasterTokenTransitionListener.masterTokenReleased();

		Mockito.when(
			_clusterMasterExecutor.isMaster()
		).thenReturn(
			true
		);

		clusterMasterTokenTransitionListener.masterTokenAcquired();

		_verifyNeverSchedule(_allNodesDispatchTrigger);

		_verifyNeverDelete(_allNodesDispatchTrigger);

		_dispatchConfigurator.deactivate();
	}

	private String _getGroupName(DispatchTrigger dispatchTrigger) {
		return StringBundler.concat(
			"DISPATCH_GROUP_",
			String.format("%07d", dispatchTrigger.getDispatchTriggerId()), "@",
			dispatchTrigger.getCompanyId());
	}

	private String _getJobName(DispatchTrigger dispatchTrigger) {
		return StringBundler.concat(
			"DISPATCH_JOB_",
			String.format("%07d", dispatchTrigger.getDispatchTriggerId()), "@",
			dispatchTrigger.getCompanyId());
	}

	private void _verifyDelete(
			DispatchTrigger dispatchTrigger, StorageType storageType)
		throws Exception {

		Mockito.verify(
			_schedulerEngineHelper
		).delete(
			_getJobName(dispatchTrigger), _getGroupName(dispatchTrigger),
			storageType
		);
	}

	private void _verifyNeverDelete(DispatchTrigger dispatchTrigger)
		throws Exception {

		Mockito.verify(
			_schedulerEngineHelper, Mockito.never()
		).delete(
			Mockito.eq(_getJobName(dispatchTrigger)), Mockito.anyString(),
			Mockito.any()
		);
	}

	private void _verifyNeverGetScheduledJob(DispatchTrigger dispatchTrigger)
		throws Exception {

		Mockito.verify(
			_schedulerEngineHelper, Mockito.never()
		).getScheduledJob(
			Mockito.eq(_getJobName(dispatchTrigger)), Mockito.anyString(),
			Mockito.any()
		);
	}

	private void _verifyNeverSchedule(DispatchTrigger dispatchTrigger)
		throws Exception {

		String jobName = _getJobName(dispatchTrigger);

		Mockito.verify(
			_schedulerEngineHelper, Mockito.never()
		).schedule(
			Mockito.argThat(trigger -> jobName.equals(trigger.getJobName())),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
		);
	}

	private void _verifySchedule(
			DispatchTrigger dispatchTrigger, StorageType storageType)
		throws Exception {

		String jobName = _getJobName(dispatchTrigger);

		Mockito.verify(
			_schedulerEngineHelper
		).schedule(
			Mockito.argThat(trigger -> jobName.equals(trigger.getJobName())),
			Mockito.eq(storageType), Mockito.isNull(),
			Mockito.eq(DispatchConstants.EXECUTOR_DESTINATION_NAME),
			Mockito.any(Message.class)
		);
	}

	@Mock
	private DispatchTrigger _allNodesDispatchTrigger;

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	@Mock
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Mock
	private DestinationFactory _destinationFactory;

	@InjectMocks
	private final DispatchConfigurator _dispatchConfigurator =
		new DispatchConfigurator();

	@Mock
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Mock
	private DispatchTrigger _notApplicableDispatchTrigger;

	@Mock
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Mock
	private DispatchTrigger _singleNodeMemoryClusteredDispatchTrigger;

	@Mock
	private DispatchTrigger _singleNodePersistedDispatchTrigger;

	@Mock
	private TriggerFactory _triggerFactory;

}