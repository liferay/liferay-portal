/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action.test;

import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.account.service.test.util.AccountEntryArgs;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balazs Breier
 */
@RunWith(Arquillian.class)
public class DeleteAccountEntryAddressesMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testProcessAction() throws Exception {
		User user = UserTestUtil.addUser();

		AccountEntry accountEntry = AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withUsers(user));

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			accountEntry.getAccountEntryId(), RandomTestUtil.randomString(),
			null, null);

		RoleTestUtil.addResourcePermission(
			accountRole.getRole(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0", ActionKeys.UPDATE);

		_accountRoleLocalService.associateUser(
			accountEntry.getAccountEntryId(), accountRole.getAccountRoleId(),
			user.getUserId());

		Address address = _addAddress(AccountEntryTestUtil.addAccountEntry());

		try {
			_mvcActionCommand.processAction(
				_getMockLiferayPortletActionRequest(
					accountEntry, address, user),
				new MockLiferayPortletActionResponse());

			Assert.fail();
		}
		catch (PortletException portletException) {
			PrincipalException.MustHavePermission mustHavePermission =
				(PrincipalException.MustHavePermission)
					portletException.getCause();

			Assert.assertEquals(
				address.getAddressId(), mustHavePermission.resourceId);
		}

		address = _addAddress(accountEntry);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(accountEntry, address, user),
			new MockLiferayPortletActionResponse());

		Assert.assertNull(
			_addressLocalService.fetchAddress(address.getAddressId()));
	}

	private Address _addAddress(AccountEntry accountEntry) throws Exception {
		return _addressLocalService.addAddress(
			null, TestPropsValues.getUserId(), AccountEntry.class.getName(),
			accountEntry.getAccountEntryId(), 0,
			_listTypeLocalService.getListTypeId(
				accountEntry.getCompanyId(),
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING,
				AccountEntry.class.getName() + ListTypeConstants.ADDRESS),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			false, RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString(), null, null, null,
			RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			AccountEntry accountEntry, Address address, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setParameter(
			"accountEntryAddressIds", String.valueOf(address.getAddressId()));
		mockLiferayPortletActionRequest.setParameter(
			"accountEntryId", String.valueOf(accountEntry.getAccountEntryId()));
		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	@Inject(
		filter = "mvc.command.name=/account_admin/delete_account_entry_addresses",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

}