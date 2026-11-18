/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.dao.search.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.account.service.test.util.AccountEntryArgs;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
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

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Balazs Breier
 */
@RunWith(Arquillian.class)
public class AccountEntryAddressDisplaySearchContainerFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testCreate() throws Exception {
		User user = UserTestUtil.addUser();

		AccountEntry accountEntry = AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withUsers(user));

		_addressLocalService.addAddress(
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

		SearchContainer<?> searchContainer = _create(accountEntry, user);

		Assert.assertEquals(0, searchContainer.getTotal());

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			accountEntry.getAccountEntryId(), RandomTestUtil.randomString(),
			null, null);

		RoleTestUtil.addResourcePermission(
			accountRole.getRole(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			AccountActionKeys.VIEW_ADDRESSES);

		_accountRoleLocalService.associateUser(
			accountEntry.getAccountEntryId(), accountRole.getAccountRoleId(),
			user.getUserId());

		searchContainer = _create(accountEntry, user);

		Assert.assertEquals(1, searchContainer.getTotal());
	}

	private SearchContainer<?> _create(AccountEntry accountEntry, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(accountEntry.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"accountEntryId", String.valueOf(accountEntry.getAccountEntryId()));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderRequest.getPortletName(), "-",
				WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		Class<?> clazz = _mvcRenderCommand.getClass();

		return ReflectionTestUtil.invoke(
			Class.forName(
				"com.liferay.account.admin.web.internal.dao.search." +
					"AccountEntryAddressDisplaySearchContainerFactory",
				true, clazz.getClassLoader()),
			"create",
			new Class<?>[] {
				LiferayPortletRequest.class, LiferayPortletResponse.class
			},
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());
	}

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	@Inject(
		filter = "mvc.command.name=/account_admin/edit_account_entry_address",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

}