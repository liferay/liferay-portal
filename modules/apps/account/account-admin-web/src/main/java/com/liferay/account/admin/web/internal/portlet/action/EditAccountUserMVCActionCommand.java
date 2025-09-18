/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.admin.web.internal.security.permission.resource.AccountUserPermission;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_admin/edit_account_user"
	},
	service = MVCActionCommand.class
)
public class EditAccountUserMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		User accountUser = _userLocalService.fetchUser(
			ParamUtil.getLong(actionRequest, "accountUserId"));
		AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
			ParamUtil.getLong(actionRequest, "accountEntryId"));

		AccountUserPermission.checkEditUserPermission(
			_permissionCheckerFactory.create(_portal.getUser(actionRequest)),
			AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT, accountEntry,
			accountUser);

		_editAccountUser(accountUser, actionRequest);
	}

	private void _editAccountUser(User accountUser, ActionRequest actionRequest)
		throws Exception {

		String firstName = ParamUtil.getString(
			actionRequest, "firstName", accountUser.getFirstName());
		String languageId = ParamUtil.getString(
			actionRequest, "languageId", accountUser.getLanguageId());
		String lastName = ParamUtil.getString(
			actionRequest, "lastName", accountUser.getLastName());
		String middleName = ParamUtil.getString(
			actionRequest, "middleName", accountUser.getMiddleName());

		accountUser.setLanguageId(languageId);
		accountUser.setFirstName(firstName);
		accountUser.setMiddleName(middleName);
		accountUser.setLastName(lastName);

		accountUser = _userLocalService.updateUser(accountUser);

		Contact accountUserContact = accountUser.getContact();

		accountUserContact.setPrefixListTypeId(
			_getListTypeId(
				accountUser, actionRequest, "prefixListTypeValue",
				ListTypeConstants.CONTACT_PREFIX));
		accountUserContact.setSuffixListTypeId(
			_getListTypeId(
				accountUser, actionRequest, "suffixListTypeValue",
				ListTypeConstants.CONTACT_SUFFIX));

		_contactLocalService.updateContact(accountUserContact);
	}

	private long _getListTypeId(
			User accountUser, PortletRequest portletRequest,
			String parameterName, String type)
		throws Exception {

		String parameterValue = ParamUtil.getString(
			portletRequest, parameterName);

		if (Validator.isNull(parameterValue)) {
			User currentUser = _portal.getUser(portletRequest);

			if (!UsersAdminUtil.hasUpdateFieldPermission(
					_permissionCheckerFactory.create(currentUser), currentUser,
					accountUser, parameterName)) {

				Contact contact = accountUser.getContact();

				if (parameterName.equals("prefixListTypeValue")) {
					return contact.getPrefixListTypeId();
				}

				return contact.getSuffixListTypeId();
			}

			return 0;
		}

		ListType listType = _listTypeLocalService.addListType(
			accountUser.getCompanyId(), parameterValue, type);

		return listType.getListTypeId();
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private ContactLocalService _contactLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}