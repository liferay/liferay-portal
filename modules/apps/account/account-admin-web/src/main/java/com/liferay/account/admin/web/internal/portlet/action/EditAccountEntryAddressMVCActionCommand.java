/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_admin/edit_account_entry_address"
	},
	service = MVCActionCommand.class
)
public class EditAccountEntryAddressMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_checkPermission(actionRequest);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			Address accountEntryAddress = null;

			if (cmd.equals(Constants.ADD)) {
				accountEntryAddress = _addAccountEntryAddress(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateAccountEntryAddress(actionRequest);
			}

			String defaultType = ParamUtil.getString(
				actionRequest, "defaultType");

			if (Objects.equals(defaultType, "billing") ||
				Objects.equals(defaultType, "shipping")) {

				long accountEntryId = ParamUtil.getLong(
					actionRequest, "accountEntryId");

				AccountEntry accountEntry =
					_accountEntryService.getAccountEntry(accountEntryId);

				long addressId = 0;

				if (accountEntryAddress != null) {
					addressId = accountEntryAddress.getAddressId();
				}

				if (Objects.equals(defaultType, "billing")) {
					accountEntry.setDefaultBillingAddressId(addressId);
				}
				else if (Objects.equals(defaultType, "shipping")) {
					accountEntry.setDefaultShippingAddressId(addressId);
				}

				_accountEntryService.updateAccountEntry(accountEntry);
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

	private Address _addAccountEntryAddress(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _addressLocalService.addAddress(
			null, themeDisplay.getUserId(), AccountEntry.class.getName(),
			ParamUtil.getLong(actionRequest, "accountEntryId"),
			ParamUtil.getLong(actionRequest, "addressCountryId"),
			ParamUtil.getLong(actionRequest, "addressListTypeId"),
			ParamUtil.getLong(actionRequest, "addressRegionId"),
			ParamUtil.getString(actionRequest, "city"),
			ParamUtil.getString(actionRequest, "description"), false,
			ParamUtil.getString(actionRequest, "name"), false,
			ParamUtil.getString(actionRequest, "street1"),
			ParamUtil.getString(actionRequest, "street2"),
			ParamUtil.getString(actionRequest, "street3"),
			ParamUtil.getString(actionRequest, "subtype"),
			ParamUtil.getString(actionRequest, "zip"),
			ParamUtil.getString(actionRequest, "phoneNumber"),
			ServiceContextFactory.getInstance(
				User.class.getName(), actionRequest));
	}

	private void _checkPermission(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_accountEntryModelResourcePermission.check(
			themeDisplay.getPermissionChecker(),
			ParamUtil.getLong(actionRequest, "accountEntryId"),
			AccountActionKeys.MANAGE_ADDRESSES);
	}

	private void _updateAccountEntryAddress(ActionRequest actionRequest)
		throws Exception {

		long accountEntryAddressId = ParamUtil.getLong(
			actionRequest, "accountEntryAddressId");

		Address address = _addressLocalService.getAddress(
			accountEntryAddressId);

		_addressLocalService.updateAddress(
			address.getExternalReferenceCode(), accountEntryAddressId,
			ParamUtil.getLong(actionRequest, "addressCountryId"),
			ParamUtil.getLong(actionRequest, "addressListTypeId"),
			ParamUtil.getLong(actionRequest, "addressRegionId"),
			ParamUtil.getString(actionRequest, "city"),
			ParamUtil.getString(actionRequest, "description"),
			address.isMailing(), ParamUtil.getString(actionRequest, "name"),
			address.isPrimary(), ParamUtil.getString(actionRequest, "street1"),
			ParamUtil.getString(actionRequest, "street2"),
			ParamUtil.getString(actionRequest, "street3"),
			ParamUtil.getString(actionRequest, "subtype"),
			ParamUtil.getString(actionRequest, "zip"),
			ParamUtil.getString(actionRequest, "phoneNumber"));
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
	private AddressLocalService _addressLocalService;

}