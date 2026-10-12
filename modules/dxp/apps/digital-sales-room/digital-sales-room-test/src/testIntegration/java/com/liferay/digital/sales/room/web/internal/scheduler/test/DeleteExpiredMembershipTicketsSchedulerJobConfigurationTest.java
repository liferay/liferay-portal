/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.sales.room.constants.DSRTicketConstants;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;

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
public class DeleteExpiredMembershipTicketsSchedulerJobConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_userLocalService.addGroupUser(
			_group.getGroupId(), TestPropsValues.getUserId());

		_user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(_group.getGroupId(), _user.getUserId());
	}

	@Test
	public void testRun() throws Exception {
		Ticket ticket1 = _addTicket(
			new Date(System.currentTimeMillis() - Time.DAY),
			JSONUtil.put(
				"userId", TestPropsValues.getUserId()
			).toString(),
			DSRTicketConstants.TYPE_EXPIRE_MEMBERSHIP);
		Ticket ticket2 = _addTicket(
			new Date(System.currentTimeMillis() + Time.DAY),
			JSONUtil.put(
				"userId", _user.getUserId()
			).toString(),
			DSRTicketConstants.TYPE_EXPIRE_MEMBERSHIP);
		Ticket ticket3 = _addTicket(
			new Date(System.currentTimeMillis() - Time.DAY), null,
			DSRTicketConstants.TYPE_INVITE_MEMBER);
		Ticket ticket4 = _addTicket(
			new Date(System.currentTimeMillis() + Time.DAY), null,
			DSRTicketConstants.TYPE_INVITE_MEMBER);

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		Assert.assertNull(_ticketLocalService.fetchTicket(ticket1.getKey()));
		Assert.assertNotNull(_ticketLocalService.fetchTicket(ticket2.getKey()));
		Assert.assertNull(_ticketLocalService.fetchTicket(ticket3.getKey()));
		Assert.assertNotNull(_ticketLocalService.fetchTicket(ticket4.getKey()));

		Assert.assertFalse(
			_userLocalService.hasGroupUser(
				_group.getGroupId(), TestPropsValues.getUserId()));
		Assert.assertTrue(
			_userLocalService.hasGroupUser(
				_group.getGroupId(), _user.getUserId()));
	}

	private Ticket _addTicket(Date expirationDate, String extraInfo, int type)
		throws Exception {

		return _ticketLocalService.addTicket(
			_group.getCompanyId(), Group.class.getName(), _group.getGroupId(),
			type, null, extraInfo, expirationDate,
			ServiceContextTestUtil.getServiceContext());
	}

	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.digital.sales.room.web.internal.scheduler.DeleteExpiredMembershipTicketsSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

	@Inject
	private TicketLocalService _ticketLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}