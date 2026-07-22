/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.object.deployer.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntryModel;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.dsr.site.initializer.test.util.DSRLayoutTestUtil;
import com.liferay.site.dsr.site.initializer.test.util.DSRTestUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionDeployerImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		DSRTestUtil.getOrAddGroup();

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		_objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", TestPropsValues.getCompanyId());

		_objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"name", "A" + RandomTestUtil.randomString()
			).put(
				"r_accountToDSRRooms_accountEntryId",
				accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(
				_objectDefinition.getClassName()),
			_objectEntry.getObjectEntryId());

		_objectEntry = _objectEntryLocalService.getObjectEntry(
			_group.getClassPK());
	}

	@Test
	public void testDeploy() throws Exception {
		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.
				fetchLayoutSetPrototypeByUuidAndCompanyId(
					"L_DSR_LAYOUT_SET_PROTOTYPE",
					TestPropsValues.getCompanyId());

		Assert.assertNotNull(layoutSetPrototype);

		FragmentCollection fragmentCollection =
			FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
				layoutSetPrototype.getGroupId(), "dsr");

		Assert.assertTrue(
			ArrayUtil.containsAll(
				TransformUtil.transformToArray(
					FragmentEntryLocalServiceUtil.getFragmentEntries(
						layoutSetPrototype.getGroupId(),
						fragmentCollection.getFragmentCollectionId(), 0),
					FragmentEntryModel::getName, String.class),
				new String[] {
					"Gallery Block", "Header Main", "Header User",
					"Our Team Block", "PDF Preview Block",
					"Question and Answer Block", "Text Block", "Timeline Block",
					"Video Block", "Welcome Block"
				}));

		DSRLayoutTestUtil.assertLayouts(
			layoutSetPrototype.getGroupId(),
			new String[] {"Documents", "Login", "Onboarding"}, true);

		Assert.assertNotNull(
			_roleLocalService.fetchRoleByExternalReferenceCode(
				"L_DSR_CONTENT_CONTRIBUTOR", TestPropsValues.getCompanyId()));

		Role role = _roleLocalService.fetchRoleByExternalReferenceCode(
			"L_DSR_SELLER", TestPropsValues.getCompanyId());

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				LayoutSetPrototype.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layoutSetPrototype.getLayoutSetPrototypeId()),
				role.getRoleId(), ActionKeys.VIEW));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), PortletKeys.PORTAL,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				role.getRoleId(), ActionKeys.VIEW_CONTROL_PANEL));

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				role.getCompanyId(), _objectDefinition.getResourceName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				role.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY));

		String[] actionIds = TransformUtil.transformToArray(
			_resourceActionLocalService.getResourceActions(
				"com.liferay.document.library"),
			ResourceAction::getActionId, String.class);

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.OWNER);

		_assertHasResourcePermissions(
			actionIds, layoutSetPrototype.getGroupId(),
			Arrays.asList(
				ActionKeys.ADD_DOCUMENT, ActionKeys.ADVANCED_UPDATE,
				ActionKeys.UPDATE, ActionKeys.SUBSCRIBE, ActionKeys.VIEW),
			role.getRoleId());

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.SITE_MEMBER);

		_assertHasResourcePermissions(
			actionIds, layoutSetPrototype.getGroupId(),
			Arrays.asList(ActionKeys.SUBSCRIBE, ActionKeys.VIEW),
			role.getRoleId());

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		_assertHasResourcePermissions(
			actionIds, layoutSetPrototype.getGroupId(),
			List.of(ActionKeys.VIEW), role.getRoleId());

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), "DSR Content Contributor");

		_assertHasResourcePermissions(
			actionIds, layoutSetPrototype.getGroupId(),
			Arrays.asList(
				ActionKeys.ADD_DOCUMENT, ActionKeys.ADVANCED_UPDATE,
				ActionKeys.UPDATE, ActionKeys.SUBSCRIBE, ActionKeys.VIEW),
			role.getRoleId());

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), "DSR Seller");

		_assertHasResourcePermissions(
			actionIds, layoutSetPrototype.getGroupId(),
			Arrays.asList(ActionKeys.VIEW), role.getRoleId());

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				_objectDefinition.getClassName());

		Class<?> clazz = modelResourcePermission.getClass();

		Assert.assertEquals(
			"DSRDefaultPermissionObjectEntryModelResourcePermission",
			clazz.getSimpleName());

		User user = UserTestUtil.addGroupUser(
			_group, RoleConstants.SITE_MEMBER);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, _objectEntry,
					ActionKeys.ADD_DISCUSSION));
			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, _objectEntry, ActionKeys.VIEW));
		}

		role = _roleLocalService.fetchRoleByExternalReferenceCode(
			"L_DSR_SELLER", TestPropsValues.getCompanyId());
		user = UserTestUtil.addUser();

		_userLocalService.addRoleUsers(
			role.getRoleId(), new long[] {user.getUserId()});

		role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUsers(
			role.getRoleId(), new long[] {user.getUserId()});

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), _objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.UPDATE);

		permissionChecker = PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertFalse(
				modelResourcePermission.contains(
					permissionChecker, _objectEntry, ActionKeys.UPDATE));
			Assert.assertFalse(
				modelResourcePermission.contains(
					permissionChecker, _objectEntry, ActionKeys.VIEW));
		}

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			MapUtil.getLong(
				_objectEntry.getValues(), "r_accountToDSRRooms_accountEntryId"),
			user.getUserId());

		permissionChecker = PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertFalse(
				modelResourcePermission.contains(
					permissionChecker, _objectEntry, ActionKeys.UPDATE));
			Assert.assertTrue(
				modelResourcePermission.contains(
					permissionChecker, _objectEntry, ActionKeys.VIEW));
		}
	}

	private void _assertHasResourcePermissions(
			String[] actionIds, long groupId, List<String> roleActionIds,
			long roleId)
		throws Exception {

		for (String actionId : actionIds) {
			if (roleActionIds.contains(actionId)) {
				Assert.assertTrue(
					_resourcePermissionLocalService.hasResourcePermission(
						TestPropsValues.getCompanyId(),
						"com.liferay.document.library",
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(groupId), roleId, actionId));
			}
			else {
				Assert.assertFalse(
					_resourcePermissionLocalService.hasResourcePermission(
						TestPropsValues.getCompanyId(),
						"com.liferay.document.library",
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(groupId), roleId, actionId));
			}
		}
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}