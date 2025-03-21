/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.exception.NoSuchGroupAccountEntryRelException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountGroupRelService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Albert Lee
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_GROUPS_ADMIN,
		"mvc.command.name=/account_admin/remove_account_group_account_entries"
	},
	service = MVCActionCommand.class
)
public class RemoveAccountGroupAccountEntriesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			long accountGroupId = ParamUtil.getLong(
				actionRequest, "accountGroupId");
			long[] accountEntryIds = ParamUtil.getLongValues(
				actionRequest, "accountEntryIds");

			_accountGroupRelService.deleteAccountGroupRels(
				accountGroupId, AccountEntry.class.getName(), accountEntryIds);

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (Validator.isNotNull(redirect)) {
				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchGroupAccountEntryRelException) {
				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}

			actionResponse.setRenderParameter(
				"mvcPath", "/account_groups_admin/view.jsp");
		}
	}

	@Reference
	private AccountGroupRelService _accountGroupRelService;

}