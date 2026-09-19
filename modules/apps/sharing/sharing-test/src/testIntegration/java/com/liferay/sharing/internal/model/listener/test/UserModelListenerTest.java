/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.EmailAddressException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class UserModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-48130")
	public void testOnAfterCreate() throws Exception {
		_testOnAfterCreateWithDuplicateInviteCollaboratorTickets();
		_testOnAfterCreateWithExpiredInviteCollaboratorTicket();
		_testOnAfterCreateWithInvalidEmailAddress();
		_testOnAfterCreateWithInviteCollaboratorTickets();
		_testOnAfterCreateWithUpperCaseEmailAddress();
		_testOnAfterCreateWithWorkflow();
	}

	@Test
	@TestInfo("LPD-48130")
	public void testOnAfterUpdate() throws Exception {
		_testOnAfterUpdateWithExistingToUserSharingEntry();
		_testOnAfterUpdateWithInviteCollaboratorTicket();
	}

	@Test
	@TestInfo("LPD-48130")
	public void testOnBeforeRemove() throws Exception {
		_testOnBeforeRemoveWithToUserSharingEntries();
		_testOnBeforeRemoveWithoutToUserSharingEntries();
	}

	private Ticket _addInviteCollaboratorTicket(
			long classPK, String emailAddress)
		throws Exception {

		return _addInviteCollaboratorTicket(
			classPK, _normalizeEmailAddress(emailAddress),
			new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48)));
	}

	private Ticket _addInviteCollaboratorTicket(
			long classPK, String emailAddress, Date expirationDate)
		throws Exception {

		return _ticketLocalService.addTicket(
			TestPropsValues.getCompanyId(), Group.class.getName(), classPK,
			TicketConstants.TYPE_INVITE_COLLABORATOR, emailAddress, null,
			expirationDate, new ServiceContext());
	}

	private SharingEntry _addTicketSharingEntry(long classPK, long toTicketId)
		throws Exception {

		return _sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), toTicketId, 0, 0,
			_classNameLocalService.getClassNameId(Group.class.getName()),
			classPK, _group1.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));
	}

	private User _addUser(String emailAddress) throws Exception {
		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			StringPool.BLANK, emailAddress, RandomTestUtil.randomString(),
			LocaleUtil.getDefault(), "Test", "User", null,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		_users.add(user);

		return user;
	}

	private User _addUserWithWorkflow(String emailAddress) throws Exception {
		User user = _userLocalService.addUserWithWorkflow(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), true,
			null, null, false, RandomTestUtil.randomString(), emailAddress,
			LocaleUtil.getDefault(), "Test", StringPool.BLANK, "User", 0, 0,
			true, Calendar.JANUARY, 1, 1970, StringPool.BLANK,
			UserConstants.TYPE_REGULAR, null, null, null, null, false,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		_users.add(user);

		return user;
	}

	private void _assertSharingEntryToTicketId(
			SharingEntry sharingEntry, Ticket ticket)
		throws Exception {

		SharingEntry persistedSharingEntry =
			_sharingEntryLocalService.getSharingEntry(
				sharingEntry.getSharingEntryId());

		Assert.assertEquals(
			ticket.getTicketId(), persistedSharingEntry.getToTicketId());
		Assert.assertEquals(0, persistedSharingEntry.getToUserId());
	}

	private void _assertSharingEntryToUserId(
			SharingEntry sharingEntry, User user)
		throws Exception {

		SharingEntry persistedSharingEntry =
			_sharingEntryLocalService.getSharingEntry(
				sharingEntry.getSharingEntryId());

		Assert.assertEquals(0, persistedSharingEntry.getToTicketId());
		Assert.assertEquals(
			user.getUserId(), persistedSharingEntry.getToUserId());
	}

	private void _assertToTicketSharingEntriesCount(int count, Ticket ticket) {
		List<SharingEntry> toTicketSharingEntries =
			_sharingEntryLocalService.getToTicketSharingEntries(
				ticket.getTicketId());

		Assert.assertEquals(
			toTicketSharingEntries.toString(), count,
			toTicketSharingEntries.size());
	}

	private String _normalizeEmailAddress(String emailAddress) {
		return StringUtil.toLowerCase(StringUtil.trim(emailAddress));
	}

	private void _testOnAfterCreateWithDuplicateInviteCollaboratorTickets()
		throws Exception {

		String emailAddress = RandomTestUtil.randomString() + "@liferay.com";

		Ticket ticket1 = _addInviteCollaboratorTicket(
			_group1.getGroupId(), emailAddress);

		_addTicketSharingEntry(_group1.getGroupId(), ticket1.getTicketId());

		_assertToTicketSharingEntriesCount(1, ticket1);

		Ticket ticket2 = _addInviteCollaboratorTicket(
			_group1.getGroupId(), emailAddress);

		_addTicketSharingEntry(_group1.getGroupId(), ticket2.getTicketId());

		_assertToTicketSharingEntriesCount(1, ticket2);

		User user = _addUser(emailAddress);

		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket1.getTicketId()));
		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket2.getTicketId()));

		List<SharingEntry> toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(user.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 1, toUserSharingEntries.size());

		SharingEntry toUserSharingEntry = toUserSharingEntries.get(0);

		Assert.assertEquals(0, toUserSharingEntry.getToTicketId());

		_assertToTicketSharingEntriesCount(0, ticket1);
		_assertToTicketSharingEntriesCount(0, ticket2);
	}

	private void _testOnAfterCreateWithExpiredInviteCollaboratorTicket()
		throws Exception {

		String emailAddress = RandomTestUtil.randomString() + "@liferay.com";

		Ticket ticket = _addInviteCollaboratorTicket(
			_group1.getGroupId(), _normalizeEmailAddress(emailAddress),
			new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)));

		SharingEntry sharingEntry = _addTicketSharingEntry(
			_group1.getGroupId(), ticket.getTicketId());

		_addUser(emailAddress);

		Assert.assertNotNull(
			_ticketLocalService.fetchTicket(ticket.getTicketId()));

		_assertSharingEntryToTicketId(sharingEntry, ticket);
	}

	private void _testOnAfterCreateWithInvalidEmailAddress() throws Exception {
		Ticket ticket1 = _addInviteCollaboratorTicket(
			_group1.getGroupId(), null);

		try {
			_addTicketSharingEntry(_group1.getGroupId(), ticket1.getTicketId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				exception.getCause() instanceof EmailAddressException);
		}

		Ticket ticket2 = _addInviteCollaboratorTicket(
			_group1.getGroupId(), "not-an-email");

		try {
			_addTicketSharingEntry(_group1.getGroupId(), ticket2.getTicketId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				exception.getCause() instanceof EmailAddressException);
		}
	}

	private void _testOnAfterCreateWithInviteCollaboratorTickets()
		throws Exception {

		_group2 = GroupTestUtil.addGroup();

		String emailAddress1 = RandomTestUtil.randomString() + "@liferay.com";

		Ticket ticket1 = _addInviteCollaboratorTicket(
			_group1.getGroupId(), emailAddress1);

		SharingEntry sharingEntry1 = _addTicketSharingEntry(
			_group1.getGroupId(), ticket1.getTicketId());

		Ticket ticket2 = _addInviteCollaboratorTicket(
			_group2.getGroupId(), emailAddress1);

		SharingEntry sharingEntry2 = _addTicketSharingEntry(
			_group2.getGroupId(), ticket2.getTicketId());

		String emailAddress2 = RandomTestUtil.randomString() + "@liferay.com";

		Ticket ticket3 = _addInviteCollaboratorTicket(
			_group1.getGroupId(), emailAddress2);

		SharingEntry sharingEntry3 = _addTicketSharingEntry(
			_group1.getGroupId(), ticket3.getTicketId());

		User user = _addUser(emailAddress1);

		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket1.getTicketId()));
		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket2.getTicketId()));

		_assertSharingEntryToUserId(sharingEntry1, user);
		_assertSharingEntryToUserId(sharingEntry2, user);
		_assertSharingEntryToTicketId(sharingEntry3, ticket3);
	}

	private void _testOnAfterCreateWithUpperCaseEmailAddress()
		throws Exception {

		String emailAddress = RandomTestUtil.randomString() + "@liferay.com";

		Ticket ticket = _addInviteCollaboratorTicket(
			_group1.getGroupId(), StringUtil.toUpperCase(emailAddress),
			new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48)));

		SharingEntry sharingEntry = _addTicketSharingEntry(
			_group1.getGroupId(), ticket.getTicketId());

		User user = _addUser(emailAddress);

		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket.getTicketId()));

		_assertSharingEntryToUserId(sharingEntry, user);
	}

	private void _testOnAfterCreateWithWorkflow() throws Exception {
		_group2 = GroupTestUtil.addGroup();

		String emailAddress1 = RandomTestUtil.randomString() + "@liferay.com";

		Ticket ticket1 = _addInviteCollaboratorTicket(
			_group1.getGroupId(), emailAddress1);

		SharingEntry sharingEntry1 = _addTicketSharingEntry(
			_group1.getGroupId(), ticket1.getTicketId());

		Ticket ticket2 = _addInviteCollaboratorTicket(
			_group2.getGroupId(), emailAddress1);

		SharingEntry sharingEntry2 = _addTicketSharingEntry(
			_group2.getGroupId(), ticket2.getTicketId());

		String emailAddress2 = RandomTestUtil.randomString() + "@liferay.com";

		Ticket ticket3 = _addInviteCollaboratorTicket(
			_group1.getGroupId(), emailAddress2);

		SharingEntry sharingEntry3 = _addTicketSharingEntry(
			_group1.getGroupId(), ticket3.getTicketId());

		User user = _addUserWithWorkflow(emailAddress1);

		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket1.getTicketId()));
		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket2.getTicketId()));

		_assertSharingEntryToUserId(sharingEntry1, user);
		_assertSharingEntryToUserId(sharingEntry2, user);
		_assertSharingEntryToTicketId(sharingEntry3, ticket3);
	}

	private void _testOnAfterUpdateWithExistingToUserSharingEntry()
		throws Exception {

		String emailAddress = RandomTestUtil.randomString() + "@liferay.com";

		User user = _addUser(emailAddress);

		Ticket ticket = _addInviteCollaboratorTicket(
			_group1.getGroupId(), emailAddress);

		SharingEntry existingSharingEntry =
			_sharingEntryLocalService.addSharingEntry(
				null, TestPropsValues.getUserId(), 0, 0, user.getUserId(),
				_classNameLocalService.getClassNameId(Group.class.getName()),
				_group1.getGroupId(), _group1.getGroupId(), true,
				Arrays.asList(SharingEntryAction.VIEW), null,
				ServiceContextTestUtil.getServiceContext(
					_group1.getGroupId(), TestPropsValues.getUserId()));

		_addTicketSharingEntry(_group1.getGroupId(), ticket.getTicketId());

		_userLocalService.updateStatus(
			user.getUserId(), WorkflowConstants.STATUS_INACTIVE,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		_userLocalService.updateStatus(
			user.getUserId(), WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket.getTicketId()));

		_assertToTicketSharingEntriesCount(0, ticket);

		List<SharingEntry> toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(user.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 1, toUserSharingEntries.size());

		SharingEntry toUserSharingEntry = toUserSharingEntries.get(0);

		Assert.assertEquals(
			existingSharingEntry.getSharingEntryId(),
			toUserSharingEntry.getSharingEntryId());
	}

	private void _testOnAfterUpdateWithInviteCollaboratorTicket()
		throws Exception {

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
				null, TestPropsValues.getUserId(),
				TestPropsValues.getCompanyId(),
				WorkflowConstants.DEFAULT_GROUP_ID, User.class.getName(), 0, 0,
				"Single Approver", 1);

		String emailAddress = RandomTestUtil.randomString() + "@liferay.com";

		Ticket ticket = _addInviteCollaboratorTicket(
			_group1.getGroupId(), emailAddress);

		SharingEntry sharingEntry = _addTicketSharingEntry(
			_group1.getGroupId(), ticket.getTicketId());

		User user = _addUserWithWorkflow(emailAddress);

		Assert.assertEquals(WorkflowConstants.STATUS_PENDING, user.getStatus());

		Assert.assertNotNull(
			_ticketLocalService.fetchTicket(ticket.getTicketId()));

		_assertSharingEntryToTicketId(sharingEntry, ticket);

		_userLocalService.updateStatus(
			user.getUserId(), WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket.getTicketId()));

		_assertSharingEntryToUserId(sharingEntry, user);

		_workflowDefinitionLinkLocalService.deleteWorkflowDefinitionLink(
			workflowDefinitionLink);
	}

	private void _testOnBeforeRemoveWithToUserSharingEntries()
		throws Exception {

		User toUser = UserTestUtil.addGroupUser(
			_group1, RoleConstants.POWER_USER);

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, toUser.getUserId(),
			_classNameLocalService.getClassNameId(Group.class.getName()),
			_group1.getGroupId(), _group1.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		List<SharingEntry> toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				toUser.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 1, toUserSharingEntries.size());

		_userLocalService.deleteUser(toUser.getUserId());

		toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				toUser.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 0, toUserSharingEntries.size());
	}

	private void _testOnBeforeRemoveWithoutToUserSharingEntries()
		throws Exception {

		User toUser = UserTestUtil.addGroupUser(
			_group1, RoleConstants.POWER_USER);

		_users.add(toUser);

		User unrelatedUser = UserTestUtil.addGroupUser(
			_group1, RoleConstants.POWER_USER);

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, toUser.getUserId(),
			_classNameLocalService.getClassNameId(Group.class.getName()),
			_group1.getGroupId(), _group1.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId()));

		List<SharingEntry> toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				toUser.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 1, toUserSharingEntries.size());

		_userLocalService.deleteUser(unrelatedUser.getUserId());

		toUserSharingEntries =
			_sharingEntryLocalService.getToUserSharingEntries(
				toUser.getUserId());

		Assert.assertEquals(
			toUserSharingEntries.toString(), 1, toUserSharingEntries.size());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@Inject
	private TicketLocalService _ticketLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}