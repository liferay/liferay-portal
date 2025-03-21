/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.admin.web.internal.portlet.action.util.TicketUtil;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.exception.NoSuchTicketException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.time.Month;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_USERS_REGISTRATION,
		"mvc.command.name=/account_admin/create_account_user"
	},
	service = MVCActionCommand.class
)
public class CreateAccountUserMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Ticket ticket = TicketUtil.getTicket(
			actionRequest, _ticketLocalService);

		if (ticket == null) {
			SessionErrors.add(actionRequest, NoSuchTicketException.class);

			actionResponse.setRenderParameter(
				"mvcPath", "/account_user_registration/error.jsp");

			return;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			ticket.getExtraInfo());

		JSONArray jsonArray = jsonObject.getJSONArray("accountRoleIds");

		long[] accountRolesIds = new long[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			accountRolesIds[i] = jsonArray.getLong(i);
		}

		User user = _addUser(
			actionRequest, ticket.getClassPK(), accountRolesIds,
			jsonObject.getString("emailAddress"));

		_ticketLocalService.deleteTicket(ticket);

		if (user.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			SessionMessages.add(
				_portal.getHttpServletRequest(actionRequest), "userAdded",
				user.getEmailAddress());
		}
		else {
			SessionMessages.add(
				_portal.getHttpServletRequest(actionRequest), "userPending",
				user.getEmailAddress());
		}

		sendRedirect(actionRequest, actionResponse);
	}

	private User _addUser(
			ActionRequest actionRequest, long accountEntryId,
			long[] accountRoleIds, String emailAddress)
		throws Exception {

		boolean autoPassword = true;
		String password1 = null;
		String password2 = null;
		boolean autoScreenName = false;
		String screenName = ParamUtil.getString(actionRequest, "screenName");
		String languageId = ParamUtil.getString(actionRequest, "languageId");
		String firstName = ParamUtil.getString(actionRequest, "firstName");
		String middleName = ParamUtil.getString(actionRequest, "middleName");
		String lastName = ParamUtil.getString(actionRequest, "lastName");
		long prefixListTypeId = ParamUtil.getInteger(
			actionRequest, "prefixListTypeId");
		long suffixListTypeId = ParamUtil.getInteger(
			actionRequest, "suffixListTypeId");
		boolean male = true;
		int birthdayMonth = Month.JANUARY.getValue();
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = ParamUtil.getString(actionRequest, "jobTitle");
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = true;

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (PrefsPropsUtil.getBoolean(
				themeDisplay.getCompanyId(),
				PropsKeys.LOGIN_CREATE_ACCOUNT_ALLOW_CUSTOM_PASSWORD,
				PropsValues.LOGIN_CREATE_ACCOUNT_ALLOW_CUSTOM_PASSWORD)) {

			autoPassword = false;

			password1 = ParamUtil.getString(actionRequest, "password1");
			password2 = ParamUtil.getString(actionRequest, "password2");
		}

		User user = _userService.addUser(
			themeDisplay.getCompanyId(), autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress,
			LocaleUtil.fromLanguageId(languageId), firstName, middleName,
			lastName, prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds,
			roleIds, userGroupIds, sendEmail, serviceContext);

		byte[] portraitBytes = null;

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId > 0) {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			portraitBytes = _file.getBytes(fileEntry.getContentStream());
		}

		if (portraitBytes != null) {
			_userService.updatePortrait(user.getUserId(), portraitBytes);
		}

		_accountEntryUserRelLocalService.addAccountEntryUserRels(
			accountEntryId, new long[] {user.getUserId()});

		if (ArrayUtil.isNotEmpty(accountRoleIds)) {
			_accountRoleLocalService.associateUser(
				accountEntryId, accountRoleIds, user.getUserId());
		}

		return user;
	}

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private File _file;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserService _userService;

}