/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDisplayLayoutLocalService;
import com.liferay.commerce.product.service.CPDisplayLayoutService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CPDisplayLayoutServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testAddCPDisplayLayout() throws Exception {
		_addResourcePermission(
			CommerceCatalog.class.getName(), ActionKeys.VIEW);
		_addResourcePermission(Group.class.getName(), ActionKeys.ADD_LAYOUT);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpDisplayLayoutService.addCPDisplayLayout(
				_group.getGroupId(), CPDefinition.class,
				_cpDefinition.getCPDefinitionId(), null, layout.getUuid());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		_addResourcePermission(
			CommerceCatalog.class.getName(), ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpDisplayLayoutService.addCPDisplayLayout(
				_group.getGroupId(), CPDefinition.class,
				_cpDefinition.getCPDefinitionId(), null, layout.getUuid());
		}
	}

	@Test
	public void testUpdateCPDisplayLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		CPDisplayLayout cpDisplayLayout =
			_cpDisplayLayoutLocalService.addCPDisplayLayout(
				TestPropsValues.getUserId(), _group.getGroupId(),
				CPDefinition.class, _cpDefinition.getCPDefinitionId(), null,
				layout.getUuid());

		_addResourcePermission(
			CommerceCatalog.class.getName(), ActionKeys.VIEW);

		Layout publicLayout = LayoutTestUtil.addTypePortletLayout(_group);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpDisplayLayoutService.updateCPDisplayLayout(
				cpDisplayLayout.getCPDisplayLayoutId(),
				_cpDefinition.getCPDefinitionId(), null,
				publicLayout.getUuid());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.ADD_LAYOUT, principalException.getMessage(),
				_user.getUserId());
		}

		_addResourcePermission(Group.class.getName(), ActionKeys.ADD_LAYOUT);

		Layout privateLayout = LayoutTestUtil.addTypePortletLayout(
			_group, true);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpDisplayLayoutService.updateCPDisplayLayout(
				cpDisplayLayout.getCPDisplayLayoutId(),
				_cpDefinition.getCPDefinitionId(), null,
				privateLayout.getUuid());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.VIEW, principalException.getMessage(),
				_user.getUserId());
		}

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpDisplayLayoutService.updateCPDisplayLayout(
				cpDisplayLayout.getCPDisplayLayoutId(),
				_cpDefinition.getCPDefinitionId(), null,
				publicLayout.getUuid());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		_addResourcePermission(
			CommerceCatalog.class.getName(), ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			cpDisplayLayout = _cpDisplayLayoutService.updateCPDisplayLayout(
				cpDisplayLayout.getCPDisplayLayoutId(),
				_cpDefinition.getCPDefinitionId(), null,
				publicLayout.getUuid());

			Assert.assertEquals(
				publicLayout.getUuid(), cpDisplayLayout.getLayoutUuid());
		}
	}

	private void _addResourcePermission(String name, String actionId)
		throws Exception {

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), name,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			actionId);
	}

	private void _assertMessage(String actionId, String message, long userId) {
		Assert.assertTrue(
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", actionId,
					" permission for")));
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CPDefinition _cpDefinition;

	@Inject
	private CPDisplayLayoutLocalService _cpDisplayLayoutLocalService;

	@Inject
	private CPDisplayLayoutService _cpDisplayLayoutService;

	private Group _group;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}