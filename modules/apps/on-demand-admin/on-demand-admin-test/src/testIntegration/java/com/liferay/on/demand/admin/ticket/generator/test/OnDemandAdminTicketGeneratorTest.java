/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.ticket.generator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.on.demand.admin.constants.OnDemandAdminActionKeys;
import com.liferay.on.demand.admin.constants.OnDemandAdminConstants;
import com.liferay.on.demand.admin.constants.OnDemandAdminPortletKeys;
import com.liferay.on.demand.admin.ticket.generator.OnDemandAdminTicketGenerator;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class OnDemandAdminTicketGeneratorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGenerate() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, OnDemandAdminPortletKeys.ON_DEMAND_ADMIN,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			OnDemandAdminActionKeys.REQUEST_ADMINISTRATOR_ACCESS);

		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user);

		Company company = CompanyTestUtil.addCompany();

		List<AuditMessage> auditMessages = new ArrayList<>();

		AuditRouter auditRouter =
			(AuditRouter)ReflectionTestUtil.getAndSetFieldValue(
				_onDemandAdminTicketGenerator, "_auditRouter",
				ProxyUtil.newProxyInstance(
					AuditRouter.class.getClassLoader(),
					new Class<?>[] {AuditRouter.class},
					(proxy, method, arguments) -> {
						auditMessages.add((AuditMessage)arguments[0]);

						return null;
					}));

		Ticket ticket = null;

		try {
			ticket = _onDemandAdminTicketGenerator.generate(
				company, null, user);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_onDemandAdminTicketGenerator, "_auditRouter", auditRouter);
		}

		AuditMessage auditMessage = auditMessages.get(0);

		Assert.assertEquals("grant_on_demand_access", auditMessage.getAction());

		Assert.assertEquals(auditMessages.toString(), 1, auditMessages.size());

		Assert.assertEquals(
			TicketConstants.TYPE_ON_DEMAND_ADMIN_LOGIN, ticket.getType());

		User onDemandAdminUser = _userLocalService.getUser(ticket.getClassPK());

		Assert.assertEquals(
			company.getCompanyId(), onDemandAdminUser.getCompanyId());
		Assert.assertTrue(
			StringUtil.startsWith(
				onDemandAdminUser.getScreenName(),
				OnDemandAdminConstants.SCREEN_NAME_PREFIX_ON_DEMAND_ADMIN));
		Assert.assertTrue(
			_userLocalService.hasRoleUser(
				company.getCompanyId(), RoleConstants.ADMINISTRATOR,
				onDemandAdminUser.getUserId(), false));
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testGenerateWithoutPermission() throws Exception {
		Company company = CompanyTestUtil.addCompany();
		User user = UserTestUtil.addUser();

		_onDemandAdminTicketGenerator.generate(company, null, user);
	}

	@Inject
	private OnDemandAdminTicketGenerator _onDemandAdminTicketGenerator;

	@Inject
	private TicketLocalService _ticketLocalService;

	@Inject
	private UserLocalService _userLocalService;

}