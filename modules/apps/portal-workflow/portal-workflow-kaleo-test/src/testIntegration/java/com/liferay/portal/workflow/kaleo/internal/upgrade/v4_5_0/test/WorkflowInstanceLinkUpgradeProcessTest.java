/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v4_5_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.exception.NoSuchWorkflowInstanceLinkException;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mateus Xavier
 */
@RunWith(Arquillian.class)
public class WorkflowInstanceLinkUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			BlogsEntry.class.getName(), 0, 0, "Single Approver", 1);

		BlogsEntry blogsEntry1 = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
		BlogsEntry blogsEntry2 = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		WorkflowInstanceLink workflowInstanceLink1 =
			_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				blogsEntry1.getCompanyId(), blogsEntry1.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry1.getEntryId());

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			"delete from KaleoInstance where kaleoInstanceId = " +
				workflowInstanceLink1.getWorkflowInstanceId());

		_runUpgrade();

		Assert.assertThrows(
			NoSuchWorkflowInstanceLinkException.class,
			() -> _workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				workflowInstanceLink1.getWorkflowInstanceLinkId()));

		Assert.assertNotNull(
			_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				blogsEntry2.getCompanyId(), blogsEntry2.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry2.getEntryId()));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(4, 5, 0));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		CacheRegistryUtil.clear();
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.portal.workflow.kaleo.internal.upgrade.registry.KaleoServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}