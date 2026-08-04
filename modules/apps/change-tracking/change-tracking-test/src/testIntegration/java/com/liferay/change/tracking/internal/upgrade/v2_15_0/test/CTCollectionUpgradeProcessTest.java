/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.upgrade.v2_15_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kiana Suetani
 */
@RunWith(Arquillian.class)
public class CTCollectionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.change.tracking.internal.upgrade.v2_15_0." +
				"CTCollectionUpgradeProcess");
	}

	@After
	public void tearDown() throws Exception {
		for (String jobName : _jobNames) {
			_schedulerEngineHelper.delete(
				jobName, CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
				StorageType.PERSISTED);
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		CTCollection scheduledCTCollection = _addCTCollection(true);

		Date date = new Date(
			System.currentTimeMillis() + RandomTestUtil.nextLong());

		_scheduleJob(scheduledCTCollection, date);

		CTCollection unscheduledCTCollection = _addCTCollection(false);

		_scheduleJob(unscheduledCTCollection, date);

		_upgradeProcess.upgrade();

		CacheRegistryUtil.clear();

		scheduledCTCollection = _ctCollectionLocalService.getCTCollection(
			scheduledCTCollection.getCtCollectionId());

		Assert.assertEquals(
			Time.getShortTimestamp(date),
			Time.getShortTimestamp(scheduledCTCollection.getScheduledDate()));

		unscheduledCTCollection = _ctCollectionLocalService.getCTCollection(
			unscheduledCTCollection.getCtCollectionId());

		Assert.assertNull(unscheduledCTCollection.getScheduledDate());
	}

	private CTCollection _addCTCollection(boolean scheduled) throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		if (scheduled) {
			ctCollection.setStatus(WorkflowConstants.STATUS_SCHEDULED);

			ctCollection = _ctCollectionLocalService.updateCTCollection(
				ctCollection);
		}

		return ctCollection;
	}

	private void _scheduleJob(CTCollection ctCollection, Date date)
		throws Exception {

		String jobName = StringBundler.concat(
			ctCollection.getCtCollectionId(), StringPool.AT,
			ctCollection.getCompanyId());

		_schedulerEngineHelper.schedule(
			_triggerFactory.createTrigger(
				jobName, CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
				date, null, "0 0 12 * * ?", null),
			StorageType.PERSISTED,
			String.valueOf(ctCollection.getCtCollectionId()),
			CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH, new Message());

		_jobNames.add(jobName);
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	private final List<String> _jobNames = new ArrayList<>();

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Inject
	private TriggerFactory _triggerFactory;

	private UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "(&(component.name=com.liferay.change.tracking.internal.upgrade.registry.ChangeTrackingServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}