/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.GroupDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.ResourcePermissionDataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.data.cleanup.test.util.DataCleanupTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class GroupDataCleanupPreupgradeProcessTest
	extends GroupDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_classNamesSavepointSafeCloseable =
			DataCleanupTestUtil.getClassNamesSavepointSafeCloseable();
	}

	@After
	public void tearDown() throws Exception {
		_classNamesSavepointSafeCloseable.close();
	}

	@Test
	public void testUpgrade() throws Exception {
		Group group = _groupLocalService.addGroup(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			GroupDataCleanupPreupgradeProcessTest.class.getName(),
			RandomTestUtil.randomLong(), 0,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			GroupConstants.TYPE_SITE_OPEN, null, false,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			"/" + RandomTestUtil.randomString(6), false, false, true,
			ServiceContextTestUtil.getServiceContext());

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			group.getGroupId(),
			_portal.getClassNameId(BlogsEntry.class.getName()), null, true,
			WorkflowConstants.STATUS_APPROVED);

		runSQL("delete from Group_ where groupId = " + group.getGroupId());

		upgrade();

		UpgradeProcess upgradeProcess =
			new ResourcePermissionDataCleanupPreupgradeProcess();

		upgradeProcess.upgrade();
	}

	private SafeCloseable _classNamesSavepointSafeCloseable;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Portal _portal;

}