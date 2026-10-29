/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.service.KBTemplateLocalService;
import com.liferay.knowledge.base.service.KBTemplateService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class KBTemplateServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		RoleTestUtil.addResourcePermission(
			_role, KBConstants.RESOURCE_NAME_ADMIN,
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group1.getGroupId()),
			KBActionKeys.DELETE_KB_TEMPLATES);

		_user = UserTestUtil.addUser(_group1.getGroupId());

		_userGroupRoleLocalService.addUserGroupRoles(
			_user.getUserId(), _group1.getGroupId(),
			new long[] {_role.getRoleId()});
	}

	@Test
	@TestInfo("LPD-111631")
	public void testAddKBTemplate() throws Exception {
		_testAddKBTemplateWithoutAddKBTemplatePermission(
			KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
			KBConstants.RESOURCE_NAME_ADMIN);
		_testAddKBTemplateWithoutAddKBTemplatePermission(
			KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
			KBConstants.RESOURCE_NAME_DISPLAY);

		_addPortletResourcePermissions(KBActionKeys.ADD_KB_TEMPLATE);

		_testAddKBTemplate(KBPortletKeys.KNOWLEDGE_BASE_ADMIN);
		_testAddKBTemplate(KBPortletKeys.KNOWLEDGE_BASE_DISPLAY);
		_testAddKBTemplateWithInvalidPortletId();
	}

	@Test
	public void testDeleteKBTemplates() throws Exception {
		_testDeleteKBTemplates();
		_testDeleteKBTemplatesWithKBTemplateFromDifferentGroup();
		_testDeleteKBTemplatesWithKBTemplateFromDifferentGroupAndDeletePermission();
		_testDeleteKBTemplatesWithKBTemplatesFromDifferentGroups();
	}

	private KBTemplate _addKBTemplate(Group group) throws Exception {
		return _kbTemplateLocalService.addKBTemplate(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId()));
	}

	private KBTemplate _addKBTemplate(String portletId) throws Exception {
		return _kbTemplateService.addKBTemplate(
			portletId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group1, _user.getUserId()));
	}

	private void _addPortletResourcePermissions(String actionId)
		throws Exception {

		RoleTestUtil.addResourcePermission(
			_role, KBConstants.RESOURCE_NAME_ADMIN,
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group1.getGroupId()),
			actionId);
		RoleTestUtil.addResourcePermission(
			_role, KBConstants.RESOURCE_NAME_DISPLAY,
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group1.getGroupId()),
			actionId);
	}

	private void _testAddKBTemplate(String portletId) throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertNotNull(_addKBTemplate(portletId));
		}
	}

	private void _testAddKBTemplateWithInvalidPortletId() throws Exception {
		int groupKBTemplatesCount =
			_kbTemplateLocalService.getGroupKBTemplatesCount(
				_group1.getGroupId());

		String portletId = RandomTestUtil.randomString();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			PrincipalException principalException = Assert.assertThrows(
				PrincipalException.class, () -> _addKBTemplate(portletId));

			Assert.assertEquals(
				"Invalid portlet ID " + portletId,
				principalException.getMessage());
		}

		Assert.assertEquals(
			groupKBTemplatesCount,
			_kbTemplateLocalService.getGroupKBTemplatesCount(
				_group1.getGroupId()));
	}

	private void _testAddKBTemplateWithoutAddKBTemplatePermission(
			String portletId, String resourceName)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			PrincipalException.MustHavePermission mustHavePermission =
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> _addKBTemplate(portletId));

			Assert.assertArrayEquals(
				new String[] {KBActionKeys.ADD_KB_TEMPLATE},
				mustHavePermission.actionId);
			Assert.assertEquals(
				_group1.getGroupId(), mustHavePermission.resourceId);
			Assert.assertEquals(resourceName, mustHavePermission.resourceName);
		}
	}

	private void _testDeleteKBTemplates() throws Exception {
		KBTemplate kbTemplate = _addKBTemplate(_group1);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_kbTemplateService.deleteKBTemplates(
				_group1.getGroupId(),
				new long[] {kbTemplate.getKbTemplateId()});
		}

		Assert.assertNull(
			_kbTemplateLocalService.fetchKBTemplate(
				kbTemplate.getKbTemplateId()));
	}

	private void _testDeleteKBTemplatesWithKBTemplateFromDifferentGroup()
		throws Exception {

		KBTemplate kbTemplate = _addKBTemplate(_group2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _kbTemplateService.deleteKBTemplates(
					_group1.getGroupId(),
					new long[] {kbTemplate.getKbTemplateId()}));
		}

		Assert.assertNotNull(
			_kbTemplateLocalService.fetchKBTemplate(
				kbTemplate.getKbTemplateId()));
	}

	private void _testDeleteKBTemplatesWithKBTemplateFromDifferentGroupAndDeletePermission()
		throws Exception {

		KBTemplate kbTemplate = _addKBTemplate(_group2);

		_regularRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			_regularRole.getCompanyId(), KBTemplate.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(kbTemplate.getKbTemplateId()),
			_regularRole.getRoleId(), new String[] {KBActionKeys.DELETE});

		_userLocalService.addRoleUser(_regularRole.getRoleId(), _user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_kbTemplateService.deleteKBTemplates(
				_group1.getGroupId(),
				new long[] {kbTemplate.getKbTemplateId()});
		}

		Assert.assertNull(
			_kbTemplateLocalService.fetchKBTemplate(
				kbTemplate.getKbTemplateId()));
	}

	private void _testDeleteKBTemplatesWithKBTemplatesFromDifferentGroups()
		throws Exception {

		KBTemplate kbTemplate1 = _addKBTemplate(_group1);
		KBTemplate kbTemplate2 = _addKBTemplate(_group2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _kbTemplateService.deleteKBTemplates(
					_group1.getGroupId(),
					new long[] {
						kbTemplate1.getKbTemplateId(),
						kbTemplate2.getKbTemplateId()
					}));
		}

		Assert.assertNotNull(
			_kbTemplateLocalService.fetchKBTemplate(
				kbTemplate1.getKbTemplateId()));
		Assert.assertNotNull(
			_kbTemplateLocalService.fetchKBTemplate(
				kbTemplate2.getKbTemplateId()));
	}

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private KBTemplateLocalService _kbTemplateLocalService;

	@Inject
	private KBTemplateService _kbTemplateService;

	@DeleteAfterTestRun
	private Role _regularRole;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}