/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;
import com.liferay.site.cms.site.initializer.util.RoleUtil;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class CMPPermissionsObjectDefinitionLocalServiceWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(
			CMPPermissionsObjectDefinitionLocalServiceWrapperTest.class);
	}

	@Test
	public void testPublishSystemObjectDefinition() throws Exception {
		_testPublishSystemObjectDefinition();
		_testPublishSystemObjectDefinition("L_CMP_PROJECT_LINK");
		_testPublishSystemObjectDefinition("L_CMP_TASK");
		_testPublishSystemObjectDefinition("L_CMP_TASK_LINK");
	}

	private void _assertHasResourcePermission(
			String actionId, ObjectDefinition objectDefinition, long roleId)
		throws Exception {

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()), roleId,
				actionId));
	}

	private void _testPublishSystemObjectDefinition() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				"L_CMP_PROJECT_MANAGEMENT_DEFINITIONS",
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(),
				objectFolder.getObjectFolderId(),
				ObjectDefinitionTestUtil.getUniqueRandomClassName(), null, true,
				false, true, false, true, false, false, false, false, false,
				null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, "Test", null, null, null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				WorkflowConstants.STATUS_DRAFT, Collections.emptyList(),
				List.of(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList());

		objectDefinition =
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		Role role = RoleUtil.getOrAddCMSAdministratorRole(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId());

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY));

		_assertHasResourcePermission(
			ActionKeys.DELETE, objectDefinition, role.getRoleId());
		_assertHasResourcePermission(
			ActionKeys.PERMISSIONS, objectDefinition, role.getRoleId());
		_assertHasResourcePermission(
			ActionKeys.UPDATE, objectDefinition, role.getRoleId());
		_assertHasResourcePermission(
			ActionKeys.VIEW, objectDefinition, role.getRoleId());
	}

	private void _testPublishSystemObjectDefinition(
			String externalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, TestPropsValues.getCompanyId());

		Role projectContributorRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.PROJECT_CONTRIBUTOR);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				projectContributorRole.getRoleId(),
				ObjectActionKeys.ADD_OBJECT_ENTRY));

		Role projectManagerRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.PROJECT_MANAGER);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				projectManagerRole.getRoleId(),
				ObjectActionKeys.ADD_OBJECT_ENTRY));
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}