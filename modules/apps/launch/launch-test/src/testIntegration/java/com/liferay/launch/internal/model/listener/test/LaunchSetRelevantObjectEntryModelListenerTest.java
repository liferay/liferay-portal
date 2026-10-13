/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.launch.set.scheduler.LaunchSetScheduler;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@RunWith(Arquillian.class)
public class LaunchSetRelevantObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testOnBeforeRemove() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "LaunchSet");

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(), 0,
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
				HashMapBuilder.<String, Serializable>put(
					"name", RandomTestUtil.randomString()
				).build(),
				serviceContext);

		_launchSetScheduler.schedulePublish(
			launchSetObjectEntry.getObjectEntryId(),
			TestPropsValues.getUserId(),
			new Date(System.currentTimeMillis() + Time.DAY));

		String jobName = StringBundler.concat(
			launchSetObjectEntry.getObjectEntryId(), StringPool.AT,
			launchSetObjectEntry.getCompanyId());

		Assert.assertNotNull(
			_schedulerEngineHelper.getScheduledJob(
				jobName, _DESTINATION_NAME, StorageType.PERSISTED));

		_objectEntryLocalService.deleteObjectEntry(launchSetObjectEntry);

		Assert.assertNull(
			_schedulerEngineHelper.getScheduledJob(
				jobName, _DESTINATION_NAME, StorageType.PERSISTED));
	}

	private static final String _DESTINATION_NAME =
		"liferay/launch_set_scheduled_publish";

	@Inject
	private LaunchSetScheduler _launchSetScheduler;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

}