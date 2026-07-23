/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class CMSPermissionsObjectDefinitionLocalServiceWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);
	}

	@Test
	public void testPublishCustomObjectDefinition() throws Exception {
		_testPublishCustomObjectDefinition();
		_testPublishCustomObjectDefinition(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);
		_testPublishCustomObjectDefinition(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES);
	}

	@Test
	public void testPublishSystemObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());

		_assertResourcePermission(
			ActionKeys.VIEW, objectDefinition.getObjectDefinitionId(),
			ObjectDefinition.class.getName(),
			_roleLocalService.getRole(
				TestPropsValues.getCompanyId(), RoleConstants.GUEST));
		_assertResourcePermission(
			ActionKeys.VIEW, objectDefinition.getObjectDefinitionId(),
			ObjectDefinition.class.getName(),
			_roleLocalService.getRole(
				TestPropsValues.getCompanyId(), RoleConstants.USER));
	}

	private void _assertResourcePermission(
			String actionId, long classPK, String resourceName, Role role)
		throws Exception {

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), resourceName,
				ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(classPK),
				role.getRoleId(), actionId));
	}

	private void _assertResourcePermission(
			String actionId, String resourceName, Role role)
		throws Exception {

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), resourceName,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				role.getRoleId(), actionId));
	}

	private ObjectDefinition _publishCustomObjectDefinition(long objectFolderId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), objectFolderId, null, true,
				false, true, false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"A" + RandomTestUtil.randomString(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_DEPOT,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "text")),
				Collections.emptyList(), new ServiceContext());

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		_objectDefinitions.add(objectDefinition);

		return objectDefinition;
	}

	private void _testPublishCustomObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition = _publishCustomObjectDefinition(0);

		Role cmsAdministratorRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				cmsAdministratorRole.getRoleId(),
				ObjectActionKeys.ADD_OBJECT_ENTRY));

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				guestRole.getRoleId(), ActionKeys.VIEW));

		Role assetLibraryAdministratorRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				assetLibraryAdministratorRole.getRoleId(),
				ObjectActionKeys.ADD_OBJECT_ENTRY));

		Role assetLibraryContentReviewerRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				assetLibraryContentReviewerRole.getRoleId(),
				ObjectActionKeys.ADD_OBJECT_ENTRY));
	}

	private void _testPublishCustomObjectDefinition(
			String objectFolderExternalReferenceCode)
		throws Exception {

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				objectFolderExternalReferenceCode,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition = _publishCustomObjectDefinition(
			objectFolder.getObjectFolderId());

		Role cmsAdministratorRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		_assertResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY,
			objectDefinition.getResourceName(), cmsAdministratorRole);
		_assertResourcePermission(
			ActionKeys.DELETE, objectDefinition.getClassName(),
			cmsAdministratorRole);
		_assertResourcePermission(
			ActionKeys.PERMISSIONS, objectDefinition.getClassName(),
			cmsAdministratorRole);
		_assertResourcePermission(
			ActionKeys.UPDATE, objectDefinition.getClassName(),
			cmsAdministratorRole);
		_assertResourcePermission(
			ActionKeys.VIEW, objectDefinition.getClassName(),
			cmsAdministratorRole);

		_assertResourcePermission(
			ActionKeys.VIEW, objectDefinition.getObjectDefinitionId(),
			ObjectDefinition.class.getName(),
			_roleLocalService.getRole(
				TestPropsValues.getCompanyId(), RoleConstants.GUEST));
		_assertResourcePermission(
			ActionKeys.VIEW, objectDefinition.getObjectDefinitionId(),
			ObjectDefinition.class.getName(),
			_roleLocalService.getRole(
				TestPropsValues.getCompanyId(), RoleConstants.USER));

		Role assetLibraryAdministratorRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				assetLibraryAdministratorRole.getRoleId(),
				ObjectActionKeys.ADD_OBJECT_ENTRY));

		Role assetLibraryContentReviewerRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				assetLibraryContentReviewerRole.getRoleId(),
				ObjectActionKeys.ADD_OBJECT_ENTRY));
	}

	@Inject
	private static GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}