/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.AddressCityException;
import com.liferay.portal.kernel.exception.AddressStreetException;
import com.liferay.portal.kernel.exception.AddressZipException;
import com.liferay.portal.kernel.exception.EmailAddressException;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.exception.NoSuchListTypeException;
import com.liferay.portal.kernel.exception.NoSuchOrgLaborException;
import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.exception.NoSuchRegionException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PhoneNumberException;
import com.liferay.portal.kernel.exception.PhoneNumberExtensionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserSmsException;
import com.liferay.portal.kernel.exception.WebsiteURLException;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.AddressService;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.EmailAddressService;
import com.liferay.portal.kernel.service.OrgLaborLocalService;
import com.liferay.portal.kernel.service.OrgLaborService;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.PhoneService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WebsiteLocalService;
import com.liferay.portal.kernel.service.WebsiteService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;
import com.liferay.users.admin.web.internal.manager.AddressContactInfoManager;
import com.liferay.users.admin.web.internal.manager.ContactInfoManager;
import com.liferay.users.admin.web.internal.manager.EmailAddressContactInfoManager;
import com.liferay.users.admin.web.internal.manager.OrgLaborContactInfoManager;
import com.liferay.users.admin.web.internal.manager.PhoneContactInfoManager;
import com.liferay.users.admin.web.internal.manager.WebsiteContactInfoManager;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ACCOUNT,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.SERVICE_ACCOUNTS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/update_contact_information"
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
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			_checkPermission(
				themeDisplay.getPermissionChecker(), className, classPK);

			_updateContactInformation(actionRequest, className, classPK);

			String redirect = _portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchOrganizationException ||
				exception instanceof NoSuchUserException ||
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
					 exception instanceof NoSuchOrgLaborException ||
					 exception instanceof NoSuchRegionException ||
					 exception instanceof PhoneNumberException ||
					 exception instanceof PhoneNumberExtensionException ||
					 exception instanceof UserEmailAddressException ||
					 exception instanceof UserSmsException ||
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
						"mvcPath", _getEditMVCPath(className));
				}
			}
			else {
				throw exception;
			}
		}
	}

	private void _checkPermission(
			PermissionChecker permissionChecker, String className, long classPK)
		throws PortalException {

		if (Objects.equals(className, Organization.class.getName())) {
			OrganizationPermissionUtil.check(
				permissionChecker, classPK, ActionKeys.UPDATE);
		}
		else {
			User user = _userLocalService.getUserByContactId(classPK);

			UserPermissionUtil.check(
				permissionChecker, user.getUserId(), ActionKeys.UPDATE);
		}
	}

	private ContactInfoManager _getContactInformationHelper(
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
		else if (listType.equals(ListTypeConstants.ORGANIZATION_SERVICE)) {
			return new OrgLaborContactInfoManager(
				classPK, _orgLaborLocalService, _orgLaborService);
		}
		else if (listType.equals(ListTypeConstants.WEBSITE)) {
			return new WebsiteContactInfoManager(
				className, classPK, _websiteLocalService, _websiteService);
		}

		return null;
	}

	private String _getEditMVCPath(String className) {
		if (Objects.equals(className, Organization.class.getName())) {
			return "/edit_organization.jsp";
		}

		return "/edit_user.jsp";
	}

	private void _updateContactInformation(
			ActionRequest actionRequest, String className, long classPK)
		throws Exception {

		String listType = ParamUtil.getString(actionRequest, "listType");

		ContactInfoManager contactInformationHelper =
			_getContactInformationHelper(className, classPK, listType);

		if (contactInformationHelper == null) {
			throw new NoSuchListTypeException();
		}

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long primaryKey = ParamUtil.getLong(actionRequest, "primaryKey");

		if (cmd.equals(Constants.DELETE)) {
			contactInformationHelper.delete(primaryKey);
		}
		else if (cmd.equals(Constants.EDIT)) {
			contactInformationHelper.edit(actionRequest);
		}
		else if (cmd.equals("makePrimary")) {
			contactInformationHelper.makePrimary(primaryKey);
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
	private OrgLaborLocalService _orgLaborLocalService;

	@Reference
	private OrgLaborService _orgLaborService;

	@Reference
	private PhoneLocalService _phoneLocalService;

	@Reference
	private PhoneService _phoneService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WebsiteLocalService _websiteLocalService;

	@Reference
	private WebsiteService _websiteService;

}