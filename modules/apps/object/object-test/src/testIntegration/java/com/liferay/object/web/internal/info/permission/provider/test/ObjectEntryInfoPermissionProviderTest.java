/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.permission.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 * @author Nathaly Gomes
 */
@RunWith(Arquillian.class)
public class ObjectEntryInfoPermissionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testHasViewPermission() throws Exception {
		_testHasViewPermissionForCustomObjectDefinition(
			false, false, PermissionCheckerFactoryUtil.create(_user));
		_testHasViewPermissionForCustomObjectDefinition(
			false, true, PermissionThreadLocal.getPermissionChecker());
		_testHasViewPermissionForCustomObjectDefinition(
			true, true, PermissionThreadLocal.getPermissionChecker());
		_testHasViewPermissionForModifiableSystemObjectDefinition(false, false);
		_testHasViewPermissionForModifiableSystemObjectDefinition(true, false);
		_testHasViewPermissionForSiteRole();
		_testHasViewPermissionForUnmodifiableSystemObjectDefinition(false);
		_testHasViewPermissionForUnmodifiableSystemObjectDefinition(true);
	}

	@Test
	@TestInfo("LPD-83634")
	public void testHasViewPermissionWithFF() throws Exception {
		_testHasViewPermissionForCustomObjectDefinition(
			false, false, PermissionCheckerFactoryUtil.create(_user));
		_testHasViewPermissionForCustomObjectDefinition(
			true, true, PermissionThreadLocal.getPermissionChecker());
		_testHasViewPermissionForCustomObjectDefinition(
			true, true, PermissionThreadLocal.getPermissionChecker());
		_testHasViewPermissionForModifiableSystemObjectDefinition(false, false);
		_testHasViewPermissionForModifiableSystemObjectDefinition(true, true);
		_testHasViewPermissionForSiteRole();
		_testHasViewPermissionForUnmodifiableSystemObjectDefinition(false);
		_testHasViewPermissionForUnmodifiableSystemObjectDefinition(true);
	}

	private ObjectDefinition _publishCustomObjectDefinition(
			boolean enableFormContainer)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).build()));

		objectDefinition.setEnableFormContainer(enableFormContainer);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private void _testHasViewPermissionForCustomObjectDefinition(
			boolean enableFormContainer, boolean expectedResult,
			PermissionChecker permissionChecker)
		throws Exception {

		ObjectDefinition objectDefinition = _publishCustomObjectDefinition(
			enableFormContainer);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			InfoPermissionProvider<ObjectEntry> infoPermissionProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoPermissionProvider.class,
					objectDefinition.getClassName());

			Assert.assertEquals(
				expectedResult,
				infoPermissionProvider.hasViewPermission(permissionChecker));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	private void _testHasViewPermissionForModifiableSystemObjectDefinition(
			boolean enableFormContainer, boolean expectedResult)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		objectDefinition.setEnableFormContainer(enableFormContainer);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);
		objectDefinition =
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		try {
			InfoPermissionProvider<ObjectEntry> infoPermissionProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoPermissionProvider.class,
					objectDefinition.getClassName());

			Assert.assertEquals(
				expectedResult,
				infoPermissionProvider.hasViewPermission(
					PermissionThreadLocal.getPermissionChecker()));
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	private void _testHasViewPermissionForSiteRole() throws Exception {
		Group group = GroupTestUtil.addGroup();
		Role siteRole = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);
		User siteUser = UserTestUtil.addUser();

		_userGroupRoleLocalService.addUserGroupRoles(
			siteUser.getUserId(), group.getGroupId(),
			new long[] {siteRole.getRoleId()});
		_userLocalService.addGroupUsers(
			group.getGroupId(), new long[] {siteUser.getUserId()});

		ObjectDefinition objectDefinition = _publishCustomObjectDefinition(
			true);

		Portlet portlet = _portletLocalService.getPortletById(
			objectDefinition.getCompanyId(), objectDefinition.getPortletId());

		_resourcePermissionLocalService.setResourcePermissions(
			objectDefinition.getCompanyId(), portlet.getRootPortletId(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(group.getGroupId()),
			siteRole.getRoleId(), new String[] {ActionKeys.VIEW});

		InfoPermissionProvider<ObjectEntry> infoPermissionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoPermissionProvider.class, objectDefinition.getClassName());

		Assert.assertFalse(
			infoPermissionProvider.hasViewPermission(
				null, group.getGroupId(),
				PermissionCheckerFactoryUtil.create(_user)));
		Assert.assertTrue(
			infoPermissionProvider.hasViewPermission(
				null, group.getGroupId(),
				PermissionCheckerFactoryUtil.create(siteUser)));

		_groupLocalService.deleteGroup(group);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
		_roleLocalService.deleteRole(siteRole);
		_userLocalService.deleteUser(siteUser);
	}

	private void _testHasViewPermissionForUnmodifiableSystemObjectDefinition(
			boolean enableFormContainer)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(),
						"x" + RandomTestUtil.randomString())));

		objectDefinition.setEnableFormContainer(enableFormContainer);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		try {
			Assert.assertNull(
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoPermissionProvider.class,
					objectDefinition.getClassName()));
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}