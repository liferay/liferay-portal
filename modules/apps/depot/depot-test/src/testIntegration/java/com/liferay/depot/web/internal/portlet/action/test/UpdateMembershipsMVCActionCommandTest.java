/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class UpdateMembershipsMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testProcessActionWithAddGroupDepotIds() throws Exception {
		_mvcActionCommand.processAction(
			new MockActionRequest(
				PermissionThreadLocal.getPermissionChecker(),
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_groupLocalService.getGroup(TestPropsValues.getGroupId()),
				_user, new long[] {_depotEntry.getGroupId()}, null),
			null);

		boolean found = false;

		for (long groupId :
				_userLocalService.getGroupPrimaryKeys(_user.getUserId())) {

			if (groupId == _depotEntry.getGroupId()) {
				found = true;

				break;
			}
		}

		Assert.assertTrue(found);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testProcessActionWithAddGroupDepotIdsWithoutPermissions()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		User organizationOwnerUser = UserTestUtil.addOrganizationOwnerUser(
			organization);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(organizationOwnerUser);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		String originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(organizationOwnerUser.getUserId());

		try {
			ReflectionTestUtil.invoke(
				_mvcActionCommand, "_validateGroupIds",
				new Class<?>[] {ActionRequest.class},
				new MockActionRequest(
					permissionChecker,
					_companyLocalService.getCompany(
						TestPropsValues.getCompanyId()),
					_groupLocalService.getGroup(TestPropsValues.getGroupId()),
					organizationOwnerUser,
					new long[] {_depotEntry.getGroupId()}, null));
		}
		finally {
			PrincipalThreadLocal.setName(originalName);

			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_userLocalService.deleteUser(organizationOwnerUser);

			_organizationLocalService.deleteOrganization(organization);
		}
	}

	@Test
	public void testProcessActionWithDeleteGroupDepotIds() throws Exception {
		Set<Long> groupIds = new HashSet<>(
			Collections.singleton(_user.getGroupId()));

		groupIds.add(_depotEntry.getGroupId());

		_updateUser(groupIds, _user);

		Role role = _roleLocalService.getRole(
			_depotEntry.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		_userGroupRoleLocalService.addUserGroupRoles(
			_user.getUserId(), _depotEntry.getGroupId(),
			new long[] {role.getRoleId()});

		_mvcActionCommand.processAction(
			new MockActionRequest(
				PermissionThreadLocal.getPermissionChecker(),
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_groupLocalService.getGroup(TestPropsValues.getGroupId()),
				_user, null, new long[] {_depotEntry.getGroupId()}),
			null);

		boolean found = false;

		for (long groupId :
				_userLocalService.getGroupPrimaryKeys(_user.getUserId())) {

			if (groupId == _depotEntry.getGroupId()) {
				found = true;

				break;
			}
		}

		Assert.assertFalse(found);

		Assert.assertEquals(
			0,
			_userGroupRoleLocalService.getUserGroupRolesCount(
				_user.getUserId(), _depotEntry.getGroupId()));
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testProcessActionWithDeleteGroupDepotIdsWithoutPermissions()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		User organizationOwnerUser = UserTestUtil.addOrganizationOwnerUser(
			organization);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(organizationOwnerUser);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		String originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(organizationOwnerUser.getUserId());

		try {
			Set<Long> groupIds = new HashSet<>(
				Collections.singleton(organizationOwnerUser.getGroupId()));

			groupIds.add(_depotEntry.getGroupId());

			_updateUser(groupIds, organizationOwnerUser);

			Role role = _roleLocalService.getRole(
				_depotEntry.getCompanyId(), RoleConstants.ORGANIZATION_OWNER);

			_userGroupRoleLocalService.addUserGroupRoles(
				organizationOwnerUser.getUserId(), _depotEntry.getGroupId(),
				new long[] {role.getRoleId()});

			ReflectionTestUtil.invoke(
				_mvcActionCommand, "_validateGroupIds",
				new Class<?>[] {ActionRequest.class},
				new MockActionRequest(
					permissionChecker,
					_companyLocalService.getCompany(
						TestPropsValues.getCompanyId()),
					_groupLocalService.getGroup(TestPropsValues.getGroupId()),
					organizationOwnerUser, null,
					new long[] {_depotEntry.getGroupId()}));
		}
		finally {
			PrincipalThreadLocal.setName(originalName);

			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			_userLocalService.deleteUser(organizationOwnerUser);

			_organizationLocalService.deleteOrganization(organization);
		}
	}

	@Test
	public void testProcessActionWithNullParameters() throws Exception {
		long[] initialGroupIds = _userLocalService.getGroupPrimaryKeys(
			_user.getUserId());

		_mvcActionCommand.processAction(
			new MockActionRequest(
				PermissionThreadLocal.getPermissionChecker(),
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_groupLocalService.getGroup(TestPropsValues.getGroupId()),
				_user, null, null),
			null);

		long[] actualGroupIds = _userLocalService.getGroupPrimaryKeys(
			_user.getUserId());

		Assert.assertEquals(
			Arrays.toString(actualGroupIds), initialGroupIds.length,
			actualGroupIds.length);
	}

	private void _updateUser(Set<Long> groupIds, User user) throws Exception {
		Contact contact = user.getContact();

		Calendar birthdayCal = CalendarFactoryUtil.getCalendar();

		birthdayCal.setTime(user.getBirthday());

		_userLocalService.updateUser(
			user.getUserId(), user.getPassword(), null, null,
			user.isPasswordReset(), null, null, user.getScreenName(),
			user.getEmailAddress(), true, null, user.getLanguageId(),
			user.getTimeZoneId(), user.getGreeting(), user.getComments(),
			user.getFirstName(), user.getMiddleName(), user.getLastName(),
			contact.getPrefixListTypeId(), contact.getSuffixListTypeId(),
			user.isFemale(), birthdayCal.get(Calendar.MONTH),
			birthdayCal.get(Calendar.DATE), birthdayCal.get(Calendar.YEAR),
			contact.getSmsSn(), contact.getFacebookSn(), contact.getJabberSn(),
			contact.getSkypeSn(), contact.getTwitterSn(), user.getJobTitle(),
			ArrayUtil.toLongArray(groupIds), user.getOrganizationIds(), null,
			null, user.getUserGroupIds(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "mvc.command.name=/depot/update_memberships",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	private static class MockActionRequest
		extends MockLiferayPortletActionRequest {

		public MockActionRequest(
			PermissionChecker permissionChecker, Company company, Group group,
			User user, long[] addDepotGroupIds, long[] deleteGroupIds) {

			_permissionChecker = permissionChecker;
			_company = company;
			_group = group;
			_user = user;

			_parameters = HashMapBuilder.put(
				"addDepotGroupIds",
				() -> {
					if (addDepotGroupIds == null) {
						return new String[] {""};
					}

					StringBundler sb = new StringBundler(
						addDepotGroupIds.length);

					for (long addDepotGroupId : addDepotGroupIds) {
						sb.append(String.valueOf(addDepotGroupId));
					}

					return new String[] {sb.toString()};
				}
			).put(
				"deleteDepotGroupIds",
				() -> {
					if (deleteGroupIds == null) {
						return new String[] {""};
					}

					StringBundler sb = new StringBundler(deleteGroupIds.length);

					for (long deleteGroupId : deleteGroupIds) {
						sb.append(String.valueOf(deleteGroupId));
					}

					return new String[] {sb.toString()};
				}
			).put(
				"p_u_i_d", new String[] {String.valueOf(user.getUserId())}
			).build();
		}

		@Override
		public Object getAttribute(String name) {
			if (!Objects.equals(name, WebKeys.THEME_DISPLAY)) {
				return null;
			}

			try {
				return _getThemeDisplay();
			}
			catch (Exception exception) {
				throw new AssertionError(exception);
			}
		}

		@Override
		public HttpServletRequest getHttpServletRequest() {
			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setParameters(getParameterMap());

			return mockHttpServletRequest;
		}

		@Override
		public String getParameter(String name) {
			String[] parameter = _parameters.get(name);

			if (parameter == null) {
				return null;
			}

			return parameter[0];
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return _parameters;
		}

		private ThemeDisplay _getThemeDisplay() throws Exception {
			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setCompany(_company);
			themeDisplay.setPermissionChecker(_permissionChecker);
			themeDisplay.setScopeGroupId(_group.getGroupId());
			themeDisplay.setUser(_user);

			return themeDisplay;
		}

		private final Company _company;
		private final Group _group;
		private final Map<String, String[]> _parameters;
		private final PermissionChecker _permissionChecker;
		private final User _user;

	}

}