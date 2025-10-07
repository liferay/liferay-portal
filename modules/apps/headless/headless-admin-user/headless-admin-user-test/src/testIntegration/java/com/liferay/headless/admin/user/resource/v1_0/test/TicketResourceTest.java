/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.client.dto.v1_0.Ticket;
import com.liferay.headless.admin.user.client.problem.Problem;
import com.liferay.headless.admin.user.client.resource.v1_0.TicketResource;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class TicketResourceTest extends BaseTicketResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser();
	}

	@Override
	@Test
	public void testGetUserAccountEmailVerificationTicket() throws Exception {
		Ticket ticket = ticketResource.getUserAccountEmailVerificationTicket(
			testGetUserAccountEmailVerificationTicket_getUserAccountId());

		assertValid(ticket);

		User requestingUser = _addUser(RandomTestUtil.randomString());

		_assertGetUserAccountTicketWithPermission(
			requestingUser,
			ticketResource::getUserAccountEmailVerificationTicket);
	}

	@Override
	@Test
	public void testGetUserAccountPasswordResetTicket() throws Exception {
		Ticket ticket = ticketResource.getUserAccountPasswordResetTicket(
			testGetUserAccountPasswordResetTicket_getUserAccountId());

		assertValid(ticket);

		User requestingUser = _addUser(RandomTestUtil.randomString());

		_assertGetUserAccountTicketWithPermission(
			requestingUser, ticketResource::getUserAccountPasswordResetTicket);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetUserAccountEmailVerificationTicket()
		throws Exception {

		super.testGraphQLGetUserAccountEmailVerificationTicket();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetUserAccountPasswordResetTicket()
		throws Exception {

		super.testGraphQLGetUserAccountPasswordResetTicket();
	}

	@Override
	protected void assertValid(Ticket ticket) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(additionalAssertFieldName, "expirationDate")) {
				if (ticket.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "extraInfo")) {
				if (ticket.getExtraInfo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "id")) {
				if (ticket.getId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "key")) {
				if (ticket.getKey() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"extraInfo", "id", "key"};
	}

	@Override
	protected Long
		testGetUserAccountEmailVerificationTicket_getUserAccountId() {

		return _user.getUserId();
	}

	@Override
	protected Long testGetUserAccountPasswordResetTicket_getUserAccountId()
		throws Exception {

		return _user.getUserId();
	}

	private User _addUser(String password) throws Exception {
		User user = UserTestUtil.addUser(
			testCompany.getCompanyId(), TestPropsValues.getUserId(), password,
			RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[] {TestPropsValues.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		ticketResource = _getTicketResource(
			user.getEmailAddress(), user.getPasswordUnencrypted());

		user.setEmailAddressVerified(true);

		return _userLocalService.updateUser(user);
	}

	private void _assertGetUserAccountTicketWithPermission(
			User requestingUser,
			UnsafeFunction<Long, Ticket, Exception> unsafeFunction)
		throws Exception {

		try {
			unsafeFunction.apply(_user.getUserId());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, User.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(testCompany.getCompanyId()), ActionKeys.UPDATE);

		_userLocalService.addRoleUser(role.getRoleId(), requestingUser);

		Ticket ticket = unsafeFunction.apply(_user.getUserId());

		assertValid(ticket);
	}

	private TicketResource _getTicketResource(String login, String password) {
		TicketResource.Builder builder = TicketResource.builder();

		return builder.authentication(
			login, password
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}