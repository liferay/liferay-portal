/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.sales.room.constants.DigitalSalesRoomPortletKeys;
import com.liferay.digital.sales.room.constants.DigitalSalesRoomTicketConstants;
import com.liferay.digital.sales.room.test.util.DigitalSalesRoomTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.exception.NoSuchTicketException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@FeatureFlag("LPD-66359")
@Ignore
@RunWith(Arquillian.class)
public class InviteMemberMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		DigitalSalesRoomTestUtil.getObjectDefinition(
			InviteMemberMVCActionCommandTest.class);
	}

	@Test
	public void testProcessAction() throws Exception {
		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(group);

		Assert.assertFalse(
			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest, NoSuchTicketException.class));

		String emailAddress = StringUtil.lowerCase(
			RandomTestUtil.randomString() + "@liferay.com");
		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), "Site Administrator");

		Ticket ticket = _ticketLocalService.addTicket(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			group.getGroupId(),
			DigitalSalesRoomTicketConstants.TYPE_INVITE_MEMBER,
			JSONUtil.put(
				"emailAddress", emailAddress
			).put(
				"roleKey", role.getName()
			).toString(),
			new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48)),
			new ServiceContext());

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest(
			group);

		mockLiferayPortletActionRequest.addParameter(
			"firstName", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"lastName", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter("password1", "test");
		mockLiferayPortletActionRequest.addParameter("password2", "test");
		mockLiferayPortletActionRequest.addParameter(
			"screenName", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"ticketKey", ticket.getKey());

		Assert.assertTrue(
			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));

		Assert.assertEquals(
			emailAddress,
			SessionMessages.get(
				mockLiferayPortletActionRequest.getHttpServletRequest(),
				"userAdded"));

		Assert.assertNull(
			_ticketLocalService.fetchTicket(ticket.getTicketId()));

		User user = _userLocalService.fetchUserByEmailAddress(
			TestPropsValues.getCompanyId(), emailAddress);

		Assert.assertTrue(
			ListUtil.exists(
				_userGroupRoleLocalService.getUserGroupRoles(
					user.getUserId(), group.getGroupId()),
				userGroupRole ->
					role.getRoleId() == userGroupRole.getRoleId()));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Group group)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID,
			DigitalSalesRoomPortletKeys.DIGITAL_SALES_ROOM_INVITE_MEMBER);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.fetchCompany(
				TestPropsValues.getCompanyId()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());

		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(filter = "mvc.command.name=/digital_sales_room/invite_member")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private TicketLocalService _ticketLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}