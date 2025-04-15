/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_admin/invite_account_users"
	},
	service = MVCActionCommand.class
)
public class InviteAccountUsersMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			long accountEntryId = ParamUtil.getLong(
				actionRequest, "accountEntryId");

			_accountEntryLocalService.getAccountEntry(accountEntryId);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				User.class.getName(), actionRequest);

			int count = ParamUtil.getInteger(actionRequest, "count");

			for (int index = 0; index < count; index++) {
				long[] accountRoleIds = ParamUtil.getLongValues(
					actionRequest, "accountRoleIds" + index);
				String[] emailAddresses = ParamUtil.getStringValues(
					actionRequest, "emailAddresses" + index);

				_inviteUsers(
					accountEntryId, accountRoleIds, emailAddresses,
					themeDisplay.getUser(), serviceContext);
			}

			jsonObject.put("success", true);
		}
		catch (Exception exception) {
			jsonObject.put("success", false);

			throw exception;
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private void _inviteUsers(
			long accountEntryId, long[] accountRoleIds, String[] emailAddresses,
			User user, ServiceContext serviceContext)
		throws PortalException {

		for (String emailAddress : emailAddresses) {
			User existingUser = _userLocalService.fetchUserByEmailAddress(
				user.getCompanyId(), emailAddress);

			if (existingUser != null) {
				continue;
			}

			_accountEntryUserRelService.inviteUser(
				accountEntryId, accountRoleIds, emailAddress, user,
				serviceContext);
		}
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelService _accountEntryUserRelService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private UserLocalService _userLocalService;

}