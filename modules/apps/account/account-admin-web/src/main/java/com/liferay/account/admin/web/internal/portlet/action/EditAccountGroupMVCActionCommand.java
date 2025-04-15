/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.exception.DuplicateAccountGroupExternalReferenceCodeException;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

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
		"mvc.command.name=/account_admin/edit_account_group"
	},
	service = MVCActionCommand.class
)
public class EditAccountGroupMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (cmd.equals(Constants.ADD)) {
				AccountGroup accountGroup = _addAccountGroup(actionRequest);

				redirect = HttpComponentsUtil.setParameter(
					redirect, actionResponse.getNamespace() + "accountGroupId",
					accountGroup.getAccountGroupId());
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateAccountGroup(actionRequest);
			}

			if (Validator.isNotNull(redirect)) {
				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcPath", "/account_groups_admin/error.jsp");
			}
			else if (exception instanceof
						DuplicateAccountGroupExternalReferenceCodeException) {

				SessionErrors.add(actionRequest, exception.getClass());

				hideDefaultErrorMessage(actionRequest);

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/account_admin/edit_account_group");
			}

			throw exception;
		}
		catch (Throwable throwable) {
			throw new Exception(throwable);
		}
	}

	private AccountGroup _addAccountGroup(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String description = ParamUtil.getString(actionRequest, "description");
		String name = ParamUtil.getString(actionRequest, "name");

		return _accountGroupService.addAccountGroup(
			ParamUtil.getString(actionRequest, "externalReferenceCode"),
			themeDisplay.getUserId(), description, name,
			ServiceContextFactory.getInstance(
				AccountGroup.class.getName(), actionRequest));
	}

	private void _updateAccountGroup(ActionRequest actionRequest)
		throws Exception {

		long accountGroupId = ParamUtil.getLong(
			actionRequest, "accountGroupId");

		String description = ParamUtil.getString(actionRequest, "description");
		String name = ParamUtil.getString(actionRequest, "name");

		_accountGroupService.updateAccountGroup(
			ParamUtil.getString(actionRequest, "externalReferenceCode"),
			accountGroupId, description, name,
			ServiceContextFactory.getInstance(
				AccountGroup.class.getName(), actionRequest));
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private AccountGroupService _accountGroupService;

}