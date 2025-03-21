/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.account.service.AccountRoleService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_admin/edit_account_role"
	},
	service = MVCActionCommand.class
)
public class EditAccountRoleMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (cmd.equals(Constants.ADD)) {
				AccountRole accountRole = _addAccountRole(actionRequest);

				redirect = HttpComponentsUtil.setParameter(
					redirect, actionResponse.getNamespace() + "accountRoleId",
					accountRole.getAccountRoleId());
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateAccountRole(actionRequest);
			}

			if (Validator.isNotNull(redirect)) {
				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
		catch (Exception exception) {
			String mvcPath = "/account_entries_admin/edit_account_role.jsp";

			if (exception instanceof PrincipalException) {
				SessionErrors.add(actionRequest, exception.getClass());

				mvcPath = "/account_entries_admin/error.jsp";
			}
			else {
				SessionErrors.add(actionRequest, exception.getClass());
			}

			actionResponse.setRenderParameter("mvcPath", mvcPath);
		}
	}

	private AccountRole _addAccountRole(ActionRequest actionRequest)
		throws Exception {

		long accountEntryId = ParamUtil.getLong(
			actionRequest, "accountEntryId");
		String name = ParamUtil.getString(actionRequest, "name");
		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		_roleLocalService.validateName(name);

		return _accountRoleService.addAccountRole(
			null, accountEntryId, name, titleMap, descriptionMap);
	}

	private void _updateAccountRole(ActionRequest actionRequest)
		throws PortalException {

		long accountRoleId = ParamUtil.getLong(actionRequest, "accountRoleId");

		AccountRole accountRole = _accountRoleLocalService.fetchAccountRole(
			accountRoleId);

		String name = ParamUtil.getString(actionRequest, "name");

		_roleLocalService.validateName(name);

		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		_roleLocalService.updateRole(
			accountRole.getRoleId(), name, titleMap, descriptionMap, null,
			null);
	}

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private AccountRoleService _accountRoleService;

	@Reference
	private Localization _localization;

	@Reference
	private RoleLocalService _roleLocalService;

}