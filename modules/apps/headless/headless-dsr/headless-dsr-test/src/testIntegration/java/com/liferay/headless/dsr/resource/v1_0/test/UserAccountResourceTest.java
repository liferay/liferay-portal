/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.resource.v1_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.dsr.client.dto.v1_0.UserAccount;
import com.liferay.headless.dsr.client.pagination.Page;
import com.liferay.headless.dsr.client.pagination.Pagination;
import com.liferay.headless.dsr.client.problem.Problem;
import com.liferay.headless.dsr.client.resource.v1_0.UserAccountResource;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.dsr.site.initializer.constants.DSRRoleConstants;
import com.liferay.site.dsr.site.initializer.constants.DSRTicketConstants;
import com.liferay.site.dsr.site.initializer.test.util.DSRTestUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class UserAccountResourceTest extends BaseUserAccountResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		DSRTestUtil.getOrAddGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, serviceContext);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", TestPropsValues.getCompanyId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		_objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToDSRRooms_accountEntryId",
				_accountEntry.getAccountEntryId()
			).build(),
			serviceContext);

		_objectEntry = _objectEntryLocalService.getObjectEntry(
			_objectEntry.getObjectEntryId());

		String password = RandomTestUtil.randomString();

		_siteMemberUserAccountResource = _getUserAccountResource(
			password, _addUser(password, RoleConstants.SITE_MEMBER));
	}

	@Override
	@Test
	public void testDeleteRoomUserAccount() throws Exception {
		super.testDeleteRoomUserAccount();

		_testDeleteRoomUserAccountWithContentContributor();
		_testDeleteRoomUserAccountWithMembershipExpirationDate();
	}

	@Override
	@Test
	public void testGetRoomUserAccountsPage() throws Exception {
		super.testGetRoomUserAccountsPage();

		_testGetRoomUserAccountsPageWithMembershipExpirationDate();
	}

	@Override
	@Test
	public void testPatchRoomUserAccount() throws Exception {
		_testPatchRoomUserAccount();
		_testPatchRoomUserAccountWithContentContributor();
		_testPatchRoomUserAccountWithoutRoleKey();
	}

	@Override
	@Test
	public void testPostRoomUserAccount() throws Exception {
		super.testPostRoomUserAccount();

		_testPostRoomUserAccount();
		_testPostRoomUserAccountSiteMember();
		_testPostRoomUserAccountWithArchivedRoom();
		_testPostRoomUserAccountWithMembershipExpirationDate();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"emailAddress"};
	}

	@Override
	protected UserAccount randomUserAccount() throws Exception {
		User user = UserTestUtil.addUser();

		return new UserAccount() {
			{
				emailAddress = user.getEmailAddress();
			}
		};
	}

	@Override
	protected Long testDeleteRoomUserAccount_getRoomId() throws Exception {
		return _objectEntry.getObjectEntryId();
	}

	@Override
	protected Long testGetRoomUserAccountsPage_getRoomId() throws Exception {
		return _objectEntry.getObjectEntryId();
	}

	private User _addUser(String password, String roleName) throws Exception {
		long groupId = _getGroupId(_objectEntry);

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {groupId}, ServiceContextTestUtil.getServiceContext());

		Role role = _roleLocalService.getRole(
			_objectEntry.getCompanyId(), roleName);

		_userGroupRoleLocalService.addUserGroupRoles(
			new long[] {user.getUserId()}, groupId, role.getRoleId());

		return user;
	}

	private Ticket _fetchExpireMembershipTicket(long userId) throws Exception {
		for (Ticket ticket :
				_ticketLocalService.getTickets(
					TestPropsValues.getCompanyId(), Group.class.getName(),
					_getGroupId(_objectEntry),
					DSRTicketConstants.TYPE_EXPIRE_MEMBERSHIP)) {

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				ticket.getExtraInfo());

			if (jsonObject.getLong("userId") == userId) {
				return ticket;
			}
		}

		return null;
	}

	private long _getGroupId(ObjectEntry objectEntry) throws Exception {
		Group group = _groupLocalService.getGroup(
			MapUtil.getLong(objectEntry.getValues(), "siteId"));

		return group.getGroupId();
	}

	private UserAccountResource _getUserAccountResource(
			String password, User user)
		throws Exception {

		return UserAccountResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _testDeleteRoomUserAccountWithContentContributor()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = _addUser(
			password, DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR);

		UserAccountResource userAccountResource = _getUserAccountResource(
			password, user);

		try {
			userAccountResource.deleteRoomUserAccount(
				_objectEntry.getObjectEntryId(), user.getUserId());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertNotNull(problemException);
		}

		user = _addUser(
			RandomTestUtil.randomString(),
			DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR);

		try {
			userAccountResource.deleteRoomUserAccount(
				_objectEntry.getObjectEntryId(), user.getUserId());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertNotNull(problemException);
		}

		user = _addUser(
			RandomTestUtil.randomString(), RoleConstants.SITE_MEMBER);

		userAccountResource.deleteRoomUserAccount(
			_objectEntry.getObjectEntryId(), user.getUserId());

		Assert.assertFalse(
			_groupLocalService.hasUserGroup(
				user.getUserId(), _getGroupId(_objectEntry)));
	}

	private void _testDeleteRoomUserAccountWithMembershipExpirationDate()
		throws Exception {

		Date expirationDate = DateUtil.getTomorrowDate();
		User user = UserTestUtil.addUser();

		userAccountResource.postRoomUserAccount(
			_objectEntry.getObjectEntryId(),
			new UserAccount() {
				{
					emailAddress = user.getEmailAddress();
					membershipExpirationDate = expirationDate;
				}
			});

		userAccountResource.deleteRoomUserAccount(
			_objectEntry.getObjectEntryId(), user.getUserId());

		Assert.assertNull(_fetchExpireMembershipTicket(user.getUserId()));
	}

	private void _testGetRoomUserAccountsPageWithMembershipExpirationDate()
		throws Exception {

		Date expirationDate = DateUtil.getTomorrowDate();
		User user = UserTestUtil.addUser();

		userAccountResource.postRoomUserAccount(
			_objectEntry.getObjectEntryId(),
			new UserAccount() {
				{
					emailAddress = user.getEmailAddress();
					membershipExpirationDate = expirationDate;
				}
			});

		Page<UserAccount> page = userAccountResource.getRoomUserAccountsPage(
			_objectEntry.getObjectEntryId(), Pagination.of(1, 100));

		Assert.assertTrue(
			ListUtil.exists(
				ListUtil.fromCollection(page.getItems()),
				userAccount ->
					Objects.equals(userAccount.getId(), user.getUserId()) &&
					Objects.equals(
						userAccount.getMembershipExpirationDate(),
						expirationDate)));
	}

	private void _testPatchRoomUserAccount() throws Exception {
		UserAccount postUserAccount = testPostRoomUserAccount_addUserAccount(
			randomUserAccount());

		UserAccount patchUserAccount = userAccountResource.patchRoomUserAccount(
			_objectEntry.getObjectEntryId(), postUserAccount.getId(),
			new UserAccount() {
				{
					membershipExpirationDate = DateUtil.getTomorrowDate();
					roleKey = RoleConstants.SITE_ADMINISTRATOR;
				}
			});

		Assert.assertEquals(postUserAccount.getId(), patchUserAccount.getId());
		Assert.assertNotNull(patchUserAccount.getMembershipExpirationDate());
		Assert.assertEquals(
			RoleConstants.SITE_ADMINISTRATOR, patchUserAccount.getRoleKey());

		patchUserAccount = userAccountResource.patchRoomUserAccount(
			_objectEntry.getObjectEntryId(), postUserAccount.getId(),
			new UserAccount() {
				{
					roleKey = RoleConstants.SITE_ADMINISTRATOR;
				}
			});

		Assert.assertNull(patchUserAccount.getMembershipExpirationDate());

		try {
			userAccountResource.patchRoomUserAccount(
				_objectEntry.getObjectEntryId(), postUserAccount.getId(),
				new UserAccount() {
					{
						membershipExpirationDate = new Date(
							System.currentTimeMillis() - Time.DAY);
					}
				});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(
				"Expiration date must be a future date.", problem.getTitle());
		}
	}

	private void _testPatchRoomUserAccountWithContentContributor()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = _addUser(
			password, DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR);

		UserAccountResource userAccountResource = _getUserAccountResource(
			password, user);

		Date expirationDate = DateUtil.getTomorrowDate();

		try {
			userAccountResource.patchRoomUserAccount(
				_objectEntry.getObjectEntryId(), user.getUserId(),
				new UserAccount() {
					{
						membershipExpirationDate = expirationDate;
					}
				});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertNotNull(problemException);
		}

		try {
			userAccountResource.patchRoomUserAccount(
				_objectEntry.getObjectEntryId(), user.getUserId(),
				new UserAccount() {
					{
						roleKey = RoleConstants.SITE_MEMBER;
					}
				});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertNotNull(problemException);
		}

		user = _addUser(
			RandomTestUtil.randomString(),
			DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR);

		try {
			userAccountResource.patchRoomUserAccount(
				_objectEntry.getObjectEntryId(), user.getUserId(),
				new UserAccount() {
					{
						membershipExpirationDate = expirationDate;
					}
				});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertNotNull(problemException);
		}

		user = _addUser(
			RandomTestUtil.randomString(), RoleConstants.SITE_MEMBER);

		UserAccount userAccount = userAccountResource.patchRoomUserAccount(
			_objectEntry.getObjectEntryId(), user.getUserId(),
			new UserAccount() {
				{
					membershipExpirationDate = expirationDate;
				}
			});

		Assert.assertEquals(
			expirationDate, userAccount.getMembershipExpirationDate());
	}

	private void _testPatchRoomUserAccountWithoutRoleKey() throws Exception {
		User user = _addUser(
			RandomTestUtil.randomString(),
			DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR);

		Date expirationDate = DateUtil.getTomorrowDate();

		UserAccount userAccount = userAccountResource.patchRoomUserAccount(
			_objectEntry.getObjectEntryId(), user.getUserId(),
			new UserAccount() {
				{
					membershipExpirationDate = expirationDate;
				}
			});

		Assert.assertEquals(
			expirationDate, userAccount.getMembershipExpirationDate());
		Assert.assertEquals(
			DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR,
			userAccount.getRoleKey());
	}

	private void _testPostRoomUserAccount() throws Exception {
		_objectEntry = _objectEntryLocalService.getObjectEntry(
			_objectEntry.getObjectEntryId());

		UserAccount randomUserAccount1 = randomUserAccount();

		UserAccount postUserAccount = testPostRoomUserAccount_addUserAccount(
			randomUserAccount1);

		assertEquals(randomUserAccount1, postUserAccount);
		assertValid(postUserAccount);

		Assert.assertTrue(
			_accountEntryUserRelLocalService.hasAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), postUserAccount.getId()));

		List<Ticket> tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			_getGroupId(_objectEntry), DSRTicketConstants.TYPE_INVITE_MEMBER);

		Assert.assertEquals(tickets.toString(), 2, tickets.size());

		Assert.assertTrue(
			ListUtil.exists(
				tickets,
				ticket -> {
					try {
						JSONObject jsonObject = _jsonFactory.createJSONObject(
							ticket.getExtraInfo());

						return Objects.equals(
							randomUserAccount1.getEmailAddress(),
							jsonObject.get("emailAddress"));
					}
					catch (Exception exception) {
						return false;
					}
				}));

		List<NotificationQueueEntry> notificationQueueEntries =
			_notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertTrue(
			ListUtil.exists(
				notificationQueueEntries,
				notificationQueueEntry -> {
					Map<String, Object> notificationRecipientSettingsMap =
						NotificationRecipientSettingUtil.
							getNotificationRecipientSettingsMap(
								notificationQueueEntry);

					return Objects.equals(
						randomUserAccount1.getEmailAddress(),
						String.valueOf(
							notificationRecipientSettingsMap.get(
								NotificationRecipientSettingConstants.
									NAME_TO)));
				}));

		UserAccount randomUserAccount2 = randomUserAccount();

		randomUserAccount2.setEmailAddress(
			RandomTestUtil.randomString() + "@liferay.com");

		postUserAccount = testPostRoomUserAccount_addUserAccount(
			randomUserAccount2);

		assertEquals(randomUserAccount2, postUserAccount);
		assertValid(postUserAccount);

		tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			_getGroupId(_objectEntry), DSRTicketConstants.TYPE_INVITE_MEMBER);

		Assert.assertEquals(tickets.toString(), 3, tickets.size());
		Assert.assertTrue(
			ListUtil.exists(
				tickets,
				ticket -> {
					try {
						JSONObject jsonObject = _jsonFactory.createJSONObject(
							ticket.getExtraInfo());

						return Objects.equals(
							randomUserAccount2.getEmailAddress(),
							jsonObject.get("emailAddress"));
					}
					catch (Exception exception) {
						return false;
					}
				}));

		notificationQueueEntries =
			_notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertTrue(
			ListUtil.exists(
				notificationQueueEntries,
				notificationQueueEntry -> {
					Map<String, Object> notificationRecipientSettingsMap =
						NotificationRecipientSettingUtil.
							getNotificationRecipientSettingsMap(
								notificationQueueEntry);

					return Objects.equals(
						randomUserAccount2.getEmailAddress(),
						String.valueOf(
							notificationRecipientSettingsMap.get(
								NotificationRecipientSettingConstants.
									NAME_TO)));
				}));
	}

	private void _testPostRoomUserAccountSiteMember() throws Exception {
		try {
			_siteMemberUserAccountResource.postRoomUserAccount(
				testGetRoomUserAccountsPage_getRoomId(), randomUserAccount());
			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			String message = problemException.getMessage();

			Assert.assertTrue(message, message.contains("Forbidden"));
		}
	}

	private void _testPostRoomUserAccountWithArchivedRoom() throws Exception {
		_objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), _objectEntry.getObjectEntryId(), 0,
			HashMapBuilder.putAll(
				_objectEntry.getValues()
			).put(
				"roomStatus", WorkflowConstants.STATUS_INACTIVE
			).build(),
			ServiceContextTestUtil.getServiceContext());

		try {
			userAccountResource.postRoomUserAccount(
				_objectEntry.getObjectEntryId(), randomUserAccount());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(
				UnsupportedOperationException.class.getSimpleName(),
				problem.getType());
		}
		finally {
			_objectEntry = _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), _objectEntry.getObjectEntryId(), 0,
				HashMapBuilder.putAll(
					_objectEntry.getValues()
				).put(
					"roomStatus", WorkflowConstants.STATUS_APPROVED
				).build(),
				ServiceContextTestUtil.getServiceContext());
		}
	}

	private void _testPostRoomUserAccountWithMembershipExpirationDate()
		throws Exception {

		Date expirationDate = DateUtil.getTomorrowDate();
		User user = UserTestUtil.addUser();

		UserAccount postUserAccount = userAccountResource.postRoomUserAccount(
			_objectEntry.getObjectEntryId(),
			new UserAccount() {
				{
					emailAddress = user.getEmailAddress();
					membershipExpirationDate = expirationDate;
				}
			});

		Assert.assertEquals(
			expirationDate, postUserAccount.getMembershipExpirationDate());

		Ticket ticket = _fetchExpireMembershipTicket(user.getUserId());

		Assert.assertEquals(expirationDate, ticket.getExpirationDate());

		try {
			userAccountResource.postRoomUserAccount(
				_objectEntry.getObjectEntryId(),
				new UserAccount() {
					{
						emailAddress = user.getEmailAddress();
						membershipExpirationDate = new Date(
							System.currentTimeMillis() - Time.DAY);
					}
				});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(
				"Expiration date must be a future date.", problem.getTitle());
		}
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private NotificationQueueEntryLocalService
		_notificationQueueEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private UserAccountResource _siteMemberUserAccountResource;

	@Inject
	private TicketLocalService _ticketLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}