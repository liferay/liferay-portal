/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.admin.web.internal.manager.AddressContactInfoManager;
import com.liferay.account.admin.web.internal.manager.ContactInfoManager;
import com.liferay.account.admin.web.internal.manager.EmailAddressContactInfoManager;
import com.liferay.account.admin.web.internal.manager.PhoneContactInfoManager;
import com.liferay.account.admin.web.internal.manager.WebsiteContactInfoManager;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.portal.kernel.exception.AddressCityException;
import com.liferay.portal.kernel.exception.AddressStreetException;
import com.liferay.portal.kernel.exception.AddressZipException;
import com.liferay.portal.kernel.exception.EmailAddressException;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.exception.NoSuchListTypeException;
import com.liferay.portal.kernel.exception.NoSuchRegionException;
import com.liferay.portal.kernel.exception.PhoneNumberException;
import com.liferay.portal.kernel.exception.PhoneNumberExtensionException;
import com.liferay.portal.kernel.exception.WebsiteURLException;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.AddressService;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.EmailAddressService;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.PhoneService;
import com.liferay.portal.kernel.service.WebsiteLocalService;
import com.liferay.portal.kernel.service.WebsiteService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_admin/update_contact_information"
	},
	service = MVCActionCommand.class
)
public class UpdateContactInformationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String className = ParamUtil.getString(actionRequest, "className");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");

		try {
			_updateContactInformation(actionRequest, className, classPK);

			String redirect = _portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof AddressCityException ||
					 exception instanceof AddressStreetException ||
					 exception instanceof AddressZipException ||
					 exception instanceof EmailAddressException ||
					 exception instanceof NoSuchCountryException ||
					 exception instanceof NoSuchListTypeException ||
					 exception instanceof NoSuchRegionException ||
					 exception instanceof PhoneNumberException ||
					 exception instanceof PhoneNumberExtensionException ||
					 exception instanceof WebsiteURLException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				String errorMVCPath = ParamUtil.getString(
					actionRequest, "errorMVCPath");

				if (Validator.isNotNull(errorMVCPath)) {
					actionResponse.setRenderParameter("mvcPath", errorMVCPath);
				}
				else {
					actionResponse.setRenderParameter(
						"mvcPath", "/edit_account_entry.jsp");
				}
			}
			else {
				throw exception;
			}
		}
	}

	private ContactInfoManager _getContactInfoManager(
		String className, long classPK, String listType) {

		if (listType.equals(ListTypeConstants.ADDRESS)) {
			return new AddressContactInfoManager(
				_addressLocalService, _addressService, className, classPK);
		}
		else if (listType.equals(ListTypeConstants.EMAIL_ADDRESS)) {
			return new EmailAddressContactInfoManager(
				className, classPK, _emailAddressLocalService,
				_emailAddressService);
		}
		else if (listType.equals(ListTypeConstants.PHONE)) {
			return new PhoneContactInfoManager(
				className, classPK, _phoneLocalService, _phoneService);
		}
		else if (listType.equals(ListTypeConstants.WEBSITE)) {
			return new WebsiteContactInfoManager(
				className, classPK, _websiteLocalService, _websiteService);
		}

		return null;
	}

	private void _updateContactInformation(
			ActionRequest actionRequest, String className, long classPK)
		throws Exception {

		String listType = ParamUtil.getString(actionRequest, "listType");

		ContactInfoManager contactInfoManager = _getContactInfoManager(
			className, classPK, listType);

		if (contactInfoManager == null) {
			throw new NoSuchListTypeException();
		}

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long primaryKey = ParamUtil.getLong(actionRequest, "primaryKey");

		if (cmd.equals(Constants.DELETE)) {
			contactInfoManager.delete(primaryKey);
		}
		else if (cmd.equals(Constants.EDIT)) {
			contactInfoManager.edit(actionRequest);
		}
		else if (cmd.equals("makePrimary")) {
			contactInfoManager.makePrimary(primaryKey);
		}
	}

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private AddressService _addressService;

	@Reference
	private EmailAddressLocalService _emailAddressLocalService;

	@Reference
	private EmailAddressService _emailAddressService;

	@Reference
	private PhoneLocalService _phoneLocalService;

	@Reference
	private PhoneService _phoneService;

	@Reference
	private Portal _portal;

	@Reference
	private WebsiteLocalService _websiteLocalService;

	@Reference
	private WebsiteService _websiteService;

}