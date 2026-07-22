/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.dsr.client.dto.v1_0.InvitedMember;
import com.liferay.headless.dsr.client.dto.v1_0.UserAccount;
import com.liferay.headless.dsr.client.pagination.Page;
import com.liferay.headless.dsr.client.problem.Problem;
import com.liferay.headless.dsr.client.resource.v1_0.InvitedMemberResource;
import com.liferay.headless.dsr.client.resource.v1_0.UserAccountResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.dsr.site.initializer.constants.DSRRoleConstants;
import com.liferay.site.dsr.site.initializer.test.util.DSRTestUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class InvitedMemberResourceTest
	extends BaseInvitedMemberResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		DSRTestUtil.getOrAddGroup();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", TestPropsValues.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		String password = RandomTestUtil.randomString();

		User user1 = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[0], ServiceContextTestUtil.getServiceContext());

		_objectEntry = _objectEntryLocalService.addObjectEntry(
			0, user1.getUserId(), objectDefinition.getObjectDefinitionId(), 0,
			null,
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToDSRRooms_accountEntryId",
				() -> {
					AccountEntry accountEntry =
						_accountEntryLocalService.addAccountEntry(
							StringPool.BLANK, TestPropsValues.getUserId(), 0,
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString(), null,
							RandomTestUtil.randomString() + "@liferay.com",
							null, null, "business", 1, serviceContext);

					return accountEntry.getAccountEntryId();
				}
			).build(),
			serviceContext);

		_objectEntry = _objectEntryLocalService.getObjectEntry(
			_objectEntry.getObjectEntryId());

		long groupId = MapUtil.getLong(_objectEntry.getValues(), "siteId");

		_userLocalService.addGroupUser(groupId, user1.getUserId());

		Role role = _roleLocalService.getRole(
			_objectEntry.getCompanyId(), DSRRoleConstants.NAME_DSR_SELLER);

		_userGroupRoleLocalService.addUserGroupRoles(
			new long[] {user1.getUserId()}, groupId, role.getRoleId());

		_invitedMemberDSRSellerResource = InvitedMemberResource.builder(
		).authentication(
			user1.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		User user2 = UserTestUtil.getAdminUser(objectDefinition.getCompanyId());

		_userAccountAdminResource = UserAccountResource.builder(
		).authentication(
			user2.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).build();

		_user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {groupId}, ServiceContextTestUtil.getServiceContext());

		role = _roleLocalService.getRole(
			_objectEntry.getCompanyId(),
			DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR);

		_userGroupRoleLocalService.addUserGroupRoles(
			new long[] {_user.getUserId()}, groupId, role.getRoleId());

		_invitedMemberDSRContributorResource = InvitedMemberResource.builder(
		).authentication(
			_user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
		_userAccountDSRContributorResource = UserAccountResource.builder(
		).authentication(
			_user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).build();
	}

	@Override
	@Test
	public void testGetRoomInvitedMembersPage() throws Exception {
		super.testGetRoomInvitedMembersPage();

		_testGetRoomInvitedMembersPageWithMembershipExpirationDate();
	}

	@Override
	@Test
	public void testPatchRoomInvitedMember() throws Exception {
		InvitedMember invitedMember1 = randomInvitedMember();

		UserAccount userAccount1 =
			_userAccountDSRContributorResource.postRoomUserAccount(
				_objectEntry.getObjectEntryId(),
				new UserAccount() {
					{
						setEmailAddress(invitedMember1::getEmailAddress);
					}
				});

		invitedMember1.setId(userAccount1.getId());

		InvitedMember patchedInvitedMember =
			_invitedMemberDSRContributorResource.patchRoomInvitedMember(
				_objectEntry.getObjectEntryId(), invitedMember1.getId(),
				new InvitedMember() {
					{
						membershipExpirationDate = new Date(
							((System.currentTimeMillis() + Time.DAY) / 1000) *
								1000);
						roleKey = DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR;
					}
				});

		Assert.assertEquals(
			invitedMember1.getId(), patchedInvitedMember.getId());
		Assert.assertNotNull(
			patchedInvitedMember.getMembershipExpirationDate());
		Assert.assertEquals(
			Long.valueOf(_user.getUserId()), patchedInvitedMember.getOwnerId());
		Assert.assertEquals(
			DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR,
			patchedInvitedMember.getRoleKey());

		InvitedMember invitedMember2 = randomInvitedMember();

		invitedMember2.setMembershipExpirationDate(
			new Date(((System.currentTimeMillis() + Time.DAY) / 1000) * 1000));

		UserAccount userAccount2 =
			_userAccountAdminResource.postRoomUserAccount(
				_objectEntry.getObjectEntryId(),
				new UserAccount() {
					{
						setEmailAddress(invitedMember2::getEmailAddress);
						setMembershipExpirationDate(
							invitedMember2::getMembershipExpirationDate);
					}
				});

		Assert.assertEquals(
			invitedMember2.getMembershipExpirationDate(),
			userAccount2.getMembershipExpirationDate());

		invitedMember2.setId(userAccount2.getId());

		try {
			_invitedMemberDSRContributorResource.patchRoomInvitedMember(
				_objectEntry.getObjectEntryId(), invitedMember2.getId(),
				new InvitedMember() {
					{
						roleKey = DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR;
					}
				});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			String message = problemException.getMessage();

			Assert.assertTrue(message, message.contains("Forbidden"));
		}

		patchedInvitedMember =
			_invitedMemberDSRSellerResource.patchRoomInvitedMember(
				_objectEntry.getObjectEntryId(), invitedMember1.getId(),
				new InvitedMember() {
					{
						membershipExpirationDate = new Date(
							((System.currentTimeMillis() + Time.MONTH) / 1000) *
								1000);
						roleKey = RoleConstants.SITE_MEMBER;
					}
				});

		Assert.assertEquals(
			invitedMember1.getId(), patchedInvitedMember.getId());
		Assert.assertNotEquals(
			invitedMember1.getMembershipExpirationDate(),
			patchedInvitedMember.getMembershipExpirationDate());
		Assert.assertEquals(
			Long.valueOf(_user.getUserId()), patchedInvitedMember.getOwnerId());
		Assert.assertEquals(
			RoleConstants.SITE_MEMBER, patchedInvitedMember.getRoleKey());

		patchedInvitedMember =
			_invitedMemberDSRSellerResource.patchRoomInvitedMember(
				_objectEntry.getObjectEntryId(), invitedMember1.getId(),
				new InvitedMember() {
					{
						roleKey = RoleConstants.SITE_MEMBER;
					}
				});

		Assert.assertNull(patchedInvitedMember.getMembershipExpirationDate());

		patchedInvitedMember =
			_invitedMemberDSRSellerResource.patchRoomInvitedMember(
				_objectEntry.getObjectEntryId(), invitedMember1.getId(),
				new InvitedMember() {
					{
						roleKey = DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR;
					}
				});

		Assert.assertEquals(
			DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR,
			patchedInvitedMember.getRoleKey());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"emailAddress"};
	}

	@Override
	protected InvitedMember randomInvitedMember() throws Exception {
		return new InvitedMember() {
			{
				emailAddress = RandomTestUtil.randomString() + "@liferay.com";
			}
		};
	}

	@Override
	protected InvitedMember testDeleteRoomInvitedMember_addInvitedMember()
		throws Exception {

		return testGetRoomInvitedMembersPage_addInvitedMember(
			_objectEntry.getObjectEntryId(), randomInvitedMember());
	}

	@Override
	protected Long testDeleteRoomInvitedMember_getRoomId() throws Exception {
		return _objectEntry.getObjectEntryId();
	}

	@Override
	protected InvitedMember testGetRoomInvitedMembersPage_addInvitedMember(
			Long roomId, InvitedMember invitedMember)
		throws Exception {

		UserAccount userAccount = _userAccountAdminResource.postRoomUserAccount(
			_objectEntry.getObjectEntryId(),
			new UserAccount() {
				{
					setEmailAddress(invitedMember::getEmailAddress);
				}
			});

		invitedMember.setId(userAccount.getId());

		return invitedMember;
	}

	@Override
	protected Long testGetRoomInvitedMembersPage_getRoomId() throws Exception {
		return _objectEntry.getObjectEntryId();
	}

	private void _testGetRoomInvitedMembersPageWithMembershipExpirationDate()
		throws Exception {

		InvitedMember invitedMember = randomInvitedMember();

		invitedMember.setMembershipExpirationDate(
			new Date(((System.currentTimeMillis() + Time.DAY) / 1000) * 1000));

		UserAccount userAccount =
			_userAccountDSRContributorResource.postRoomUserAccount(
				_objectEntry.getObjectEntryId(),
				new UserAccount() {
					{
						setEmailAddress(invitedMember::getEmailAddress);
						setMembershipExpirationDate(
							invitedMember::getMembershipExpirationDate);
					}
				});

		Page<InvitedMember> page =
			invitedMemberResource.getRoomInvitedMembersPage(
				_objectEntry.getObjectEntryId());

		Assert.assertTrue(
			ListUtil.exists(
				ListUtil.fromCollection(page.getItems()),
				curInvitedMember ->
					Objects.equals(
						curInvitedMember.getId(), userAccount.getId()) &&
					Objects.equals(
						curInvitedMember.getMembershipExpirationDate(),
						invitedMember.getMembershipExpirationDate())));
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private InvitedMemberResource _invitedMemberDSRContributorResource;
	private InvitedMemberResource _invitedMemberDSRSellerResource;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;
	private UserAccountResource _userAccountAdminResource;
	private UserAccountResource _userAccountDSRContributorResource;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}