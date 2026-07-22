/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.portlet.action.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchTicketException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
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
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.dsr.site.initializer.constants.DSRPortletKeys;
import com.liferay.site.dsr.site.initializer.constants.DSRTicketConstants;
import com.liferay.site.dsr.site.initializer.test.util.DSRTestUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
public class InviteMemberMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		DSRTestUtil.getOrAddGroup();
	}

	@Test
	public void testProcessAction() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, serviceContext);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", TestPropsValues.getCompanyId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToDSRRooms_accountEntryId",
				accountEntry.getAccountEntryId()
			).build(),
			serviceContext);

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		Group group = _groupLocalService.getGroup(
			GetterUtil.getLong(values.get("siteId")));

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
			group.getGroupId(), DSRTicketConstants.TYPE_INVITE_MEMBER, null,
			JSONUtil.put(
				"accountEntryId", accountEntry.getAccountEntryId()
			).put(
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
			_accountEntryUserRelLocalService.hasAccountEntryUserRel(
				accountEntry.getAccountEntryId(), user.getUserId()));
		Assert.assertTrue(
			ListUtil.exists(
				_userGroupRoleLocalService.getUserGroupRoles(
					user.getUserId(), group.getGroupId()),
				userGroupRole ->
					role.getRoleId() == userGroupRole.getRoleId()));
	}

	@Test
	public void testProcessActionMembershipExpirationDate() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, serviceContext);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", TestPropsValues.getCompanyId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToDSRRooms_accountEntryId",
				accountEntry.getAccountEntryId()
			).build(),
			serviceContext);

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Group group = _groupLocalService.getGroup(
			MapUtil.getLong(objectEntry.getValues(), "siteId"));

		String emailAddress = StringUtil.lowerCase(
			RandomTestUtil.randomString() + "@liferay.com");
		Date membershipExpirationDate = new Date(
			((System.currentTimeMillis() + Time.DAY) / 1000) * 1000);
		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), "Site Administrator");

		Ticket ticket = _ticketLocalService.addTicket(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			group.getGroupId(), DSRTicketConstants.TYPE_INVITE_MEMBER, null,
			JSONUtil.put(
				"accountEntryId", accountEntry.getAccountEntryId()
			).put(
				"emailAddress", emailAddress
			).put(
				"membershipExpirationDate", membershipExpirationDate.getTime()
			).put(
				"roleKey", role.getName()
			).toString(),
			new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48)),
			new ServiceContext());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(group);

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

		User user = _userLocalService.fetchUserByEmailAddress(
			TestPropsValues.getCompanyId(), emailAddress);

		List<Ticket> tickets = _ticketLocalService.getTickets(
			Group.class.getName(), group.getGroupId(),
			DSRTicketConstants.TYPE_EXPIRE_MEMBERSHIP);

		Assert.assertEquals(tickets.toString(), 1, tickets.size());

		Ticket expireMembershipTicket = tickets.get(0);

		Assert.assertEquals(
			membershipExpirationDate,
			expireMembershipTicket.getExpirationDate());

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			expireMembershipTicket.getExtraInfo());

		Assert.assertEquals(user.getUserId(), jsonObject.getLong("userId"));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Group group)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, DSRPortletKeys.DSR_INVITE_MEMBER);

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
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(filter = "mvc.command.name=/digital_sales_room/invite_member")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private TicketLocalService _ticketLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}