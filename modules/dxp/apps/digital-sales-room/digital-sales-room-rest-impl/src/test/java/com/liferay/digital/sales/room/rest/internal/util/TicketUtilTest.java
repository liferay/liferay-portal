/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.rest.internal.util;

import com.liferay.digital.sales.room.constants.DSRTicketConstants;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class TicketUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFetchExpireMembershipTicket() throws Exception {
		long groupId = RandomTestUtil.randomLong();
		Ticket ticket1 = _mockTicket(RandomTestUtil.randomLong());

		long userId = RandomTestUtil.randomLong();

		Ticket ticket2 = _mockTicket(userId);

		Mockito.when(
			_ticketLocalService.getTickets(
				Group.class.getName(), groupId,
				DSRTicketConstants.TYPE_EXPIRE_MEMBERSHIP)
		).thenReturn(
			Arrays.asList(ticket1, ticket2)
		);

		Assert.assertNull(
			TicketUtil.fetchExpireMembershipTicket(
				RandomTestUtil.randomLong(), _jsonFactory, _ticketLocalService,
				userId));
		Assert.assertNull(
			TicketUtil.fetchExpireMembershipTicket(
				groupId, _jsonFactory, _ticketLocalService,
				RandomTestUtil.randomLong()));
		Assert.assertSame(
			ticket2,
			TicketUtil.fetchExpireMembershipTicket(
				groupId, _jsonFactory, _ticketLocalService, userId));

		Mockito.when(
			_ticketLocalService.getTickets(
				Group.class.getName(), groupId,
				DSRTicketConstants.TYPE_EXPIRE_MEMBERSHIP)
		).thenReturn(
			Collections.emptyList()
		);

		Assert.assertNull(
			TicketUtil.fetchExpireMembershipTicket(
				groupId, _jsonFactory, _ticketLocalService, userId));
	}

	private Ticket _mockTicket(long userId) {
		Ticket ticket = Mockito.mock(Ticket.class);

		Mockito.when(
			ticket.getExtraInfo()
		).thenReturn(
			_jsonFactory.createJSONObject(
			).put(
				"userId", userId
			).toString()
		);

		return ticket;
	}

	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private final TicketLocalService _ticketLocalService = Mockito.mock(
		TicketLocalService.class);

}