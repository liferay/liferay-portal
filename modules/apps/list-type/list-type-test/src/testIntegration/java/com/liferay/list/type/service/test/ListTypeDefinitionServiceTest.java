/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.constants.ListTypeActionKeys;
import com.liferay.list.type.constants.ListTypeConstants;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class ListTypeDefinitionServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_adminUser = TestPropsValues.getUser();
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testAddListTypeDefinition() throws Exception {

		// Can add list type definition with permission

		_setResourcePermissions(
			ListTypeConstants.RESOURCE_NAME, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ListTypeActionKeys.ADD_LIST_TYPE_DEFINITION});

		_testAddListTypeDefinition(_user);

		// Cannot add list type definition without permission

		_removeResourcePermission(
			ListTypeConstants.RESOURCE_NAME, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ListTypeActionKeys.ADD_LIST_TYPE_DEFINITION);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ",
				ListTypeActionKeys.ADD_LIST_TYPE_DEFINITION, " permission for ",
				ListTypeConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _testAddListTypeDefinition(_user));
	}

	@Test
	public void testDeleteListTypeDefinition() throws Exception {

		// Can delete list type definition with individual model permission

		ListTypeDefinition listTypeDefinition1 = _addListTypeDefinition(
			_adminUser);

		_setResourcePermissions(
			ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(listTypeDefinition1.getListTypeDefinitionId()),
			RoleConstants.USER, new String[] {ActionKeys.DELETE});

		_testDeleteListTypeDefinition(listTypeDefinition1, _user);

		// Can delete list type definition with model permission

		_setResourcePermissions(
			ListTypeDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ActionKeys.DELETE});

		_testDeleteListTypeDefinition(
			_addListTypeDefinition(_adminUser), _user);

		_removeResourcePermission(
			ListTypeDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ActionKeys.DELETE);

		// Can delete list type definition with owner permission

		_testDeleteListTypeDefinition(_addListTypeDefinition(_user), _user);

		// Cannot delete list type definition without model permission

		ListTypeDefinition listTypeDefinition2 = _addListTypeDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.DELETE,
				" permission for ", ListTypeDefinition.class.getName(),
				StringPool.SPACE,
				listTypeDefinition2.getListTypeDefinitionId()),
			() -> _testDeleteListTypeDefinition(listTypeDefinition2, _user));

		// Cannot delete list type definition without owner permission

		ListTypeDefinition listTypeDefinition3 = _addListTypeDefinition(_user);

		_removeResourcePermission(
			ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(listTypeDefinition3.getListTypeDefinitionId()),
			RoleConstants.OWNER, ActionKeys.DELETE);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.DELETE,
				" permission for ", ListTypeDefinition.class.getName(),
				StringPool.SPACE,
				listTypeDefinition3.getListTypeDefinitionId()),
			() -> _testDeleteListTypeDefinition(listTypeDefinition3, _user));
	}

	@Test
	public void testGetListTypeDefinition() throws Exception {

		// Can get list type definition with individual model permission

		ListTypeDefinition listTypeDefinition1 = _addListTypeDefinition(
			_adminUser);

		_setResourcePermissions(
			ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(listTypeDefinition1.getListTypeDefinitionId()),
			RoleConstants.USER, new String[] {ActionKeys.VIEW});

		_testGetListTypeDefinition(listTypeDefinition1, _user);

		// Can get list type definition with model permission

		_setResourcePermissions(
			ListTypeDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ActionKeys.VIEW});

		_testGetListTypeDefinition(_addListTypeDefinition(_adminUser), _user);

		_removeResourcePermission(
			ListTypeDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ActionKeys.VIEW);

		// Can get list type definition with owner permission

		_testGetListTypeDefinition(_addListTypeDefinition(_user), _user);

		// Cannot get list type definition without model permission

		ListTypeDefinition listTypeDefinition2 = _addListTypeDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.VIEW,
				" permission for ", ListTypeDefinition.class.getName(),
				StringPool.SPACE,
				listTypeDefinition2.getListTypeDefinitionId()),
			() -> _testGetListTypeDefinition(listTypeDefinition2, _user));

		// Cannot get list type definition without owner permission

		ListTypeDefinition listTypeDefinition3 = _addListTypeDefinition(_user);

		_removeResourcePermission(
			ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(listTypeDefinition3.getListTypeDefinitionId()),
			RoleConstants.OWNER, ActionKeys.VIEW);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.VIEW,
				" permission for ", ListTypeDefinition.class.getName(),
				StringPool.SPACE,
				listTypeDefinition3.getListTypeDefinitionId()),
			() -> _testGetListTypeDefinition(listTypeDefinition3, _user));
	}

	@Test
	public void testGetListTypeDefinitions() throws Exception {
		ListTypeDefinition adminUserListTypeDefinition = _addListTypeDefinition(
			_adminUser);
		ListTypeDefinition userListTypeDefinition = _addListTypeDefinition(
			_user);

		List<ListTypeDefinition> listTypeDefinitions = _getListTypeDefinitions(
			_adminUser);

		Assert.assertTrue(
			listTypeDefinitions.contains(adminUserListTypeDefinition));
		Assert.assertTrue(listTypeDefinitions.contains(userListTypeDefinition));

		listTypeDefinitions = _getListTypeDefinitions(_user);

		Assert.assertFalse(
			listTypeDefinitions.contains(adminUserListTypeDefinition));
		Assert.assertTrue(listTypeDefinitions.contains(userListTypeDefinition));

		_setResourcePermissions(
			ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				adminUserListTypeDefinition.getListTypeDefinitionId()),
			RoleConstants.USER, new String[] {ActionKeys.VIEW});

		listTypeDefinitions = _getListTypeDefinitions(_user);

		Assert.assertTrue(
			listTypeDefinitions.contains(adminUserListTypeDefinition));

		_setResourcePermissions(
			ListTypeDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ActionKeys.VIEW});

		Assert.assertEquals(
			_getListTypeDefinitions(_adminUser),
			_getListTypeDefinitions(_user));

		_removeResourcePermission(
			ListTypeDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ActionKeys.VIEW);
	}

	@Test
	public void testGetListTypeDefinitionsCount() throws Exception {
		int listTypeDefinitionsCount = _getListTypeDefinitionsCount(_user);

		_addListTypeDefinition(_adminUser);

		Assert.assertEquals(
			listTypeDefinitionsCount, _getListTypeDefinitionsCount(_user));

		_addListTypeDefinition(_user);

		Assert.assertEquals(
			listTypeDefinitionsCount + 1, _getListTypeDefinitionsCount(_user));

		ListTypeDefinition listTypeDefinition = _addListTypeDefinition(
			_adminUser);

		_setResourcePermissions(
			ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(listTypeDefinition.getListTypeDefinitionId()),
			RoleConstants.USER, new String[] {ActionKeys.VIEW});

		Assert.assertEquals(
			listTypeDefinitionsCount + 2, _getListTypeDefinitionsCount(_user));
	}

	@Test
	public void testUpdateListTypeDefinition() throws Exception {

		// Can update list type definition with individual model permission

		ListTypeDefinition listTypeDefinition1 = _addListTypeDefinition(
			_adminUser);

		_setResourcePermissions(
			ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(listTypeDefinition1.getListTypeDefinitionId()),
			RoleConstants.USER, new String[] {ActionKeys.UPDATE});

		_testUpdateListTypeDefinition(listTypeDefinition1, _user);

		// Can update list type definition with model permission

		_setResourcePermissions(
			ListTypeDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ActionKeys.UPDATE});

		_testUpdateListTypeDefinition(
			_addListTypeDefinition(_adminUser), _user);

		_removeResourcePermission(
			ListTypeDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ActionKeys.UPDATE);

		// Can update list type definition with owner permission

		_testUpdateListTypeDefinition(_addListTypeDefinition(_user), _user);

		// Cannot update list type definition without model permission

		ListTypeDefinition listTypeDefinition2 = _addListTypeDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.UPDATE,
				" permission for ", ListTypeDefinition.class.getName(),
				StringPool.SPACE,
				listTypeDefinition2.getListTypeDefinitionId()),
			() -> _testUpdateListTypeDefinition(listTypeDefinition2, _user));

		// Cannot update list type definition without owner permission

		ListTypeDefinition listTypeDefinition3 = _addListTypeDefinition(_user);

		_removeResourcePermission(
			ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(listTypeDefinition3.getListTypeDefinitionId()),
			RoleConstants.OWNER, ActionKeys.UPDATE);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.UPDATE,
				" permission for ", ListTypeDefinition.class.getName(),
				StringPool.SPACE,
				listTypeDefinition3.getListTypeDefinitionId()),
			() -> _testUpdateListTypeDefinition(listTypeDefinition3, _user));
	}

	private ListTypeDefinition _addListTypeDefinition(User user)
		throws Exception {

		return _listTypeDefinitionLocalService.addListTypeDefinition(
			null, user.getUserId(), RandomTestUtil.randomLocaleStringMap(),
			false, Collections.emptyList(), new ServiceContext());
	}

	private List<ListTypeDefinition> _getListTypeDefinitions(User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			return _listTypeDefinitionService.getListTypeDefinitions(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
	}

	private int _getListTypeDefinitionsCount(User user) throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			return _listTypeDefinitionService.getListTypeDefinitionsCount();
		}
	}

	private long _getRoleId(String roleName) throws Exception {
		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), roleName);

		return role.getRoleId();
	}

	private void _removeResourcePermission(
			String name, int scope, String primKey, String roleName,
			String actionId)
		throws Exception {

		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(), name, scope, primKey,
			_getRoleId(roleName), actionId);
	}

	private void _setResourcePermissions(
			String name, int scope, String primKey, String roleName,
			String[] actionIds)
		throws Exception {

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), name, scope, primKey,
			_getRoleId(roleName), actionIds);
	}

	private void _testAddListTypeDefinition(User user) throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_listTypeDefinitionService.addListTypeDefinition(
				null, RandomTestUtil.randomLocaleStringMap(), false,
				Collections.emptyList(), new ServiceContext());
		}
	}

	private void _testDeleteListTypeDefinition(
			ListTypeDefinition listTypeDefinition, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_listTypeDefinitionService.deleteListTypeDefinition(
				listTypeDefinition.getListTypeDefinitionId());
		}
	}

	private void _testGetListTypeDefinition(
			ListTypeDefinition listTypeDefinition, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_listTypeDefinitionService.getListTypeDefinition(
				listTypeDefinition.getListTypeDefinitionId());
		}
	}

	private void _testUpdateListTypeDefinition(
			ListTypeDefinition listTypeDefinition, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_listTypeDefinitionService.updateListTypeDefinition(
				listTypeDefinition.getExternalReferenceCode(),
				listTypeDefinition.getListTypeDefinitionId(),
				RandomTestUtil.randomLocaleStringMap(), Collections.emptyList(),
				new ServiceContext());
		}
	}

	private User _adminUser;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeDefinitionService _listTypeDefinitionService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

}