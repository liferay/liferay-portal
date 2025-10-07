/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.comparator.RoleRoleIdComparator;
import com.liferay.portal.security.permission.test.util.BasePermissionTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class RoleServiceTest extends BasePermissionTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	@Override
	public void tearDown() throws Exception {
		for (Role role : _roles) {
			_roleService.deleteRole(role.getRoleId());
		}

		if (_className != null) {
			_classNameLocalService.deleteClassName(_className);
		}

		super.tearDown();
	}

	@Test
	public void testGetOrAddEmptyRole() throws Exception {
		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			RoleTestUtil.addResourcePermission(
				role1, PortletKeys.PORTAL, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				ActionKeys.ADD_ROLE);

			User user = UserTestUtil.addUser();

			UserLocalServiceUtil.addRoleUser(
				role1.getRoleId(), user.getUserId());

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				Role role2 = _roleService.getOrAddEmptyRole(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), 0,
					RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

				Assert.assertNotNull(role2);
			}

			// Without permissions

			user = UserTestUtil.addUser();

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				_roleService.getOrAddEmptyRole(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), 0,
					RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertNotNull(principalException);
			}
		}
	}

	@Test
	public void testSearch() throws Exception {
		List<Role> roles = _roleService.search(
			group.getCompanyId(), StringPool.BLANK,
			new Integer[] {RoleConstants.TYPE_REGULAR}, new LinkedHashMap<>(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(
			"Role not found with permissions", roles.contains(_role));

		removePortletModelViewPermission();

		roles = _roleService.search(
			group.getCompanyId(), StringPool.BLANK,
			new Integer[] {RoleConstants.TYPE_REGULAR}, new LinkedHashMap<>(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertFalse(
			"Role found without permissions", roles.contains(_role));
	}

	@Test
	public void testSearchCount() throws Exception {
		int initialCount = _roleService.searchCount(
			group.getCompanyId(), StringPool.BLANK,
			new Integer[] {RoleConstants.TYPE_REGULAR}, new LinkedHashMap<>());

		removePortletModelViewPermission();

		Assert.assertEquals(
			initialCount - 1,
			_roleService.searchCount(
				group.getCompanyId(), StringPool.BLANK,
				new Integer[] {RoleConstants.TYPE_REGULAR},
				new LinkedHashMap<>()));
	}

	@Test
	public void testSearchWithClassNameId() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_className = _classNameLocalService.addClassName(
			RandomTestUtil.randomString());

		List<Role> expectedRoles = new ArrayList<>();

		expectedRoles.add(
			_roleService.addRole(
				RandomTestUtil.randomString(), _className.getClassName(),
				RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RoleConstants.TYPE_PROVIDER, null, null));
		expectedRoles.add(
			_roleService.addRole(
				RandomTestUtil.randomString(), _className.getClassName(),
				RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RoleConstants.TYPE_PROVIDER, null, null));
		expectedRoles.add(
			_roleService.addRole(
				RandomTestUtil.randomString(), _className.getClassName(),
				RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RoleConstants.TYPE_PROVIDER, null, null));

		_roles.addAll(expectedRoles);

		_roles.add(
			_roleService.addRole(
				RandomTestUtil.randomString(), Role.class.getName(),
				RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RoleConstants.TYPE_PROVIDER, null, null));

		LinkedHashMap<String, Object> params =
			LinkedHashMapBuilder.<String, Object>put(
				"classNameId", _className.getClassNameId()
			).build();

		Assert.assertEquals(
			expectedRoles.size(),
			_roleService.searchCount(
				TestPropsValues.getCompanyId(), StringPool.BLANK,
				new Integer[] {RoleConstants.TYPE_PROVIDER}, params));

		List<Role> roles = _roleService.search(
			group.getCompanyId(), StringPool.BLANK,
			new Integer[] {RoleConstants.TYPE_PROVIDER}, params,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			RoleRoleIdComparator.getInstance(false));

		Assert.assertEquals(
			ListUtil.sort(
				expectedRoles, RoleRoleIdComparator.getInstance(false)),
			roles);
	}

	@Override
	protected void doSetUp() throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
	}

	@Override
	protected String getPrimKey() {
		return String.valueOf(_role.getRoleId());
	}

	@Override
	protected String getResourceName() {
		return Role.class.getName();
	}

	@Override
	protected String getRoleName() {
		return RoleConstants.USER;
	}

	private ClassName _className;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Role _role;

	private final List<Role> _roles = new ArrayList<>();

	@Inject
	private RoleService _roleService;

}