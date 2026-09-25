/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.invitation.invite.members.constants.InviteMembersConstants;
import com.liferay.invitation.invite.members.model.MemberRequest;
import com.liferay.invitation.invite.members.service.MemberRequestLocalService;
import com.liferay.invitation.invite.members.service.MemberRequestService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TeamLocalService;
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
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class MemberRequestServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddMemberRequests() throws Exception {
		_testAddMemberRequests(
			PrincipalException.class, 0, 0, UserTestUtil.addUser());

		User user = UserTestUtil.addUser(_group.getGroupId());

		User receiverUser = UserTestUtil.addUser();

		_addMemberRequests(0, 0, receiverUser, user);

		Assert.assertTrue(
			_memberRequestLocalService.hasPendingMemberRequest(
				_group.getGroupId(), receiverUser.getUserId()));

		Role siteAdministratorRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

		_testAddMemberRequests(
			PrincipalException.class, siteAdministratorRole.getRoleId(), 0,
			user);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_testAddMemberRequests(
			PrincipalException.class, role.getRoleId(), 0, user);

		Team team = _teamLocalService.addTeam(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_testAddMemberRequests(
			PrincipalException.MustHavePermission.class, 0, team.getTeamId(),
			user);

		user = UserTestUtil.addGroupAdminUser(_group);

		receiverUser = UserTestUtil.addUser();

		_addMemberRequests(role.getRoleId(), 0, receiverUser, user);

		MemberRequest memberRequest =
			_memberRequestLocalService.getMemberRequest(
				_group.getGroupId(), receiverUser.getUserId(),
				InviteMembersConstants.STATUS_PENDING);

		Assert.assertEquals(role.getRoleId(), memberRequest.getInvitedRoleId());

		receiverUser = UserTestUtil.addUser();

		_addMemberRequests(0, team.getTeamId(), receiverUser, user);

		memberRequest = _memberRequestLocalService.getMemberRequest(
			_group.getGroupId(), receiverUser.getUserId(),
			InviteMembersConstants.STATUS_PENDING);

		Assert.assertEquals(team.getTeamId(), memberRequest.getInvitedTeamId());

		_otherGroup = GroupTestUtil.addGroup();

		team = _teamLocalService.addTeam(
			TestPropsValues.getUserId(), _otherGroup.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_otherGroup.getGroupId()));

		_testAddMemberRequests(
			PrincipalException.class, 0, team.getTeamId(), user);
	}

	private void _addMemberRequests(
			long invitedRoleId, long invitedTeamId, User receiverUser,
			User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_memberRequestService.addMemberRequests(
				_group.getGroupId(), new long[] {receiverUser.getUserId()},
				invitedRoleId, invitedTeamId, _getServiceContext());
		}
	}

	private ServiceContext _getServiceContext() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute(
			"createAccountURL", "http://" + RandomTestUtil.randomString());
		serviceContext.setAttribute(
			"loginURL", "http://" + RandomTestUtil.randomString());
		serviceContext.setAttribute(
			"redirectURL", "http://" + RandomTestUtil.randomString());

		return serviceContext;
	}

	private void _testAddMemberRequests(
			Class<? extends PrincipalException> exceptionClass,
			long invitedRoleId, long invitedTeamId, User user)
		throws Exception {

		User receiverUser = UserTestUtil.addUser();

		PrincipalException principalException = Assert.assertThrows(
			PrincipalException.class,
			() -> _addMemberRequests(
				invitedRoleId, invitedTeamId, receiverUser, user));

		Assert.assertEquals(exceptionClass, principalException.getClass());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			principalException = Assert.assertThrows(
				PrincipalException.class,
				() -> _memberRequestService.addMemberRequests(
					_group.getGroupId(),
					new String[] {receiverUser.getEmailAddress()},
					invitedRoleId, invitedTeamId, _getServiceContext()));
		}

		Assert.assertEquals(exceptionClass, principalException.getClass());

		Assert.assertFalse(
			_memberRequestLocalService.hasPendingMemberRequest(
				_group.getGroupId(), receiverUser.getUserId()));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MemberRequestLocalService _memberRequestLocalService;

	@Inject
	private MemberRequestService _memberRequestService;

	@DeleteAfterTestRun
	private Group _otherGroup;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private TeamLocalService _teamLocalService;

}