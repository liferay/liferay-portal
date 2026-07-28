/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.portlet.action.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.dsr.site.initializer.constants.DSRRoleConstants;

import java.io.ByteArrayOutputStream;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class GetEmailNotificationRolesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetEmailNotificationRoles() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		User user = UserTestUtil.getAdminUser(company.getCompanyId());

		Role accountRole1 = _addAccountRole(user);
		Role accountRole2 = _addAccountRole(user);
		Role accountRole3 = _roleLocalService.addRole(
			RandomTestUtil.randomString(), user.getUserId(),
			AccountRole.class.getName(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), RoleConstants.TYPE_ACCOUNT,
			null, null);
		Role organizationRole1 = _addRole(
			RoleConstants.TYPE_ORGANIZATION, user);
		Role organizationRole2 = _addRole(
			RoleConstants.TYPE_ORGANIZATION, user);
		Role regularRole1 = _addRole(RoleConstants.TYPE_REGULAR, user);
		Role regularRole2 = _addRole(RoleConstants.TYPE_REGULAR, user);

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(company);
		themeDisplay.setLocale(LocaleUtil.getDefault());

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"accountRoles",
				JSONUtil.putAll(
					JSONUtil.put(
						"name",
						AccountRoleConstants.
							REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR),
					JSONUtil.put(
						"name",
						AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER),
					JSONUtil.put(
						"name", AccountRoleConstants.ROLE_NAME_ACCOUNT_BUYER),
					JSONUtil.put(
						"name",
						AccountRoleConstants.ROLE_NAME_ACCOUNT_ORDER_MANAGER),
					JSONUtil.put(
						"name",
						AccountRoleConstants.ROLE_NAME_ACCOUNT_SUPPLIER),
					JSONUtil.put(
						"label", accountRole1.getTitle(LocaleUtil.getDefault())
					).put(
						"name", accountRole1.getName()
					),
					JSONUtil.put(
						"label", accountRole2.getTitle(LocaleUtil.getDefault())
					).put(
						"name", accountRole2.getName()
					),
					JSONUtil.put(
						"label", accountRole3.getTitle(LocaleUtil.getDefault())
					).put(
						"name", accountRole3.getName()
					))
			).put(
				"organizationRoles",
				JSONUtil.putAll(
					JSONUtil.put(
						"name", RoleConstants.ORGANIZATION_ADMINISTRATOR),
					JSONUtil.put(
						"name", RoleConstants.ORGANIZATION_CONTENT_REVIEWER),
					JSONUtil.put("name", RoleConstants.ORGANIZATION_OWNER),
					JSONUtil.put("name", RoleConstants.ORGANIZATION_USER),
					JSONUtil.put(
						"name",
						AccountRoleConstants.
							REQUIRED_ROLE_NAME_ACCOUNT_MANAGER),
					JSONUtil.put(
						"label",
						organizationRole1.getTitle(LocaleUtil.getDefault())
					).put(
						"name", organizationRole1.getName()
					),
					JSONUtil.put(
						"label",
						organizationRole2.getTitle(LocaleUtil.getDefault())
					).put(
						"name", organizationRole2.getName()
					))
			).put(
				"regularRoles",
				JSONUtil.putAll(
					JSONUtil.put(
						"name",
						AccountRoleConstants.ROLE_NAME_ORDER_ADMINISTRATOR),
					JSONUtil.put(
						"name", AccountRoleConstants.ROLE_NAME_SUPPLIER),
					JSONUtil.put("name", DSRRoleConstants.NAME_DSR_SELLER),
					JSONUtil.put("name", RoleConstants.ADMINISTRATOR),
					JSONUtil.put("name", RoleConstants.ANALYTICS_ADMINISTRATOR),
					JSONUtil.put("name", RoleConstants.OWNER),
					JSONUtil.put("name", RoleConstants.PORTAL_CONTENT_REVIEWER),
					JSONUtil.put("name", RoleConstants.POWER_USER),
					JSONUtil.put("name", RoleConstants.PUBLICATIONS_ADMIN),
					JSONUtil.put("name", RoleConstants.PUBLICATIONS_EDITOR),
					JSONUtil.put("name", RoleConstants.PUBLICATIONS_PUBLISHER),
					JSONUtil.put("name", RoleConstants.PUBLICATIONS_USER),
					JSONUtil.put("name", RoleConstants.PUBLICATIONS_VIEWER),
					JSONUtil.put("name", RoleConstants.USER),
					JSONUtil.put(
						"label", regularRole1.getTitle(LocaleUtil.getDefault())
					).put(
						"name", regularRole1.getName()
					),
					JSONUtil.put(
						"label", regularRole2.getTitle(LocaleUtil.getDefault())
					).put(
						"name", regularRole2.getName()
					))
			).toString(),
			byteArrayOutputStream.toString(), JSONCompareMode.LENIENT);

		_companyLocalService.deleteCompany(company);
	}

	private Role _addAccountRole(User user) throws Exception {
		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, user.getUserId(), 0L,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), user.getUserId(),
			accountEntry.getAccountEntryId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap());

		return accountRole.getRole();
	}

	private Role _addRole(int type, User user) throws Exception {
		return _roleLocalService.addRole(
			RandomTestUtil.randomString(), user.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null, type, null, null);
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "mvc.command.name=/notification_templates/get_email_notification_roles"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}