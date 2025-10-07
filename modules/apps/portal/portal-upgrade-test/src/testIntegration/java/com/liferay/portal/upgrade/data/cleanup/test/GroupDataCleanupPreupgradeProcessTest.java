/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.GroupDataCleanupPreupgradeProcess;

import java.util.List;

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
		_classNames = _classNameLocalService.getClassNames(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		_resourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@After
	public void tearDown() throws Exception {
		List<ClassName> classNames = ListUtil.remove(
			_classNameLocalService.getClassNames(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_classNames);

		for (ClassName className : classNames) {
			_classNameLocalService.deleteClassName(className);
		}

		List<ResourcePermission> resourcePermissions = ListUtil.remove(
			_resourcePermissionLocalService.getResourcePermissions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_resourcePermissions);

		for (ResourcePermission resourcePermission : resourcePermissions) {
			_resourcePermissionLocalService.deleteResourcePermission(
				resourcePermission);
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		Group group = _groupLocalService.addGroup(
			TestPropsValues.getUserId(), 0,
			GroupDataCleanupPreupgradeProcessTest.class.getName(),
			RandomTestUtil.randomLong(), 0,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			GroupConstants.TYPE_SITE_OPEN, false,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			"/" + RandomTestUtil.randomString(6), false, false, true,
			ServiceContextTestUtil.getServiceContext());

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			group.getGroupId(),
			_portal.getClassNameId(BlogsEntry.class.getName()), 0, true,
			WorkflowConstants.STATUS_APPROVED);

		runSQL("delete from Group_ where groupId = " + group.getGroupId());

		upgrade();
	}

	private static List<ClassName> _classNames;
	private static List<ResourcePermission> _resourcePermissions;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}