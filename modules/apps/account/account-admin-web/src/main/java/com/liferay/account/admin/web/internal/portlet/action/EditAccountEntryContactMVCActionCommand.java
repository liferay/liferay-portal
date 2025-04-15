/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ContactService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_admin/edit_account_entry_contact"
	},
	service = MVCActionCommand.class
)
public class EditAccountEntryContactMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD)) {
				_addAccountEntryContact(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateAccountEntryContact(actionRequest);
			}

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (Validator.isNotNull(redirect)) {
				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
		catch (Exception exception) {
			if ((exception instanceof ModelListenerException) &&
				(exception.getCause() instanceof PortalException)) {

				throw (PortalException)exception.getCause();
			}

			throw exception;
		}
	}

	private void _addAccountEntryContact(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long accountEntryId = ParamUtil.getLong(
			actionRequest, "accountEntryId");
		String smsSn = ParamUtil.getString(actionRequest, "smsSn");
		String facebookSn = ParamUtil.getString(actionRequest, "facebookSn");
		String jabberSn = ParamUtil.getString(actionRequest, "jabberSn");
		String skypeSn = ParamUtil.getString(actionRequest, "skypeSn");
		String twitterSn = ParamUtil.getString(actionRequest, "twitterSn");

		_contactService.addContact(
			themeDisplay.getUserId(), AccountEntry.class.getName(),
			accountEntryId, null, null, null, null, 0, 0, true, 0, 1, 1970,
			smsSn, facebookSn, jabberSn, skypeSn, twitterSn, null);
	}

	private void _updateAccountEntryContact(ActionRequest actionRequest)
		throws Exception {

		long accountEntryId = ParamUtil.getLong(
			actionRequest, "accountEntryId");

		AccountEntry accountEntry = _accountEntryService.getAccountEntry(
			accountEntryId);

		Contact contact = accountEntry.fetchContact();

		String smsSn = ParamUtil.getString(actionRequest, "smsSn");
		String facebookSn = ParamUtil.getString(actionRequest, "facebookSn");
		String jabberSn = ParamUtil.getString(actionRequest, "jabberSn");
		String skypeSn = ParamUtil.getString(actionRequest, "skypeSn");
		String twitterSn = ParamUtil.getString(actionRequest, "twitterSn");

		_contactService.updateContact(
			contact.getContactId(), null, null, null, null, 0, 0, true, 0, 1,
			1970, smsSn, facebookSn, jabberSn, skypeSn, twitterSn, null);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private ContactService _contactService;

}