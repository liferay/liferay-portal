/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.AddressCityException;
import com.liferay.portal.kernel.exception.AddressStreetException;
import com.liferay.portal.kernel.exception.AddressZipException;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.exception.NoSuchListTypeException;
import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.exception.NoSuchRegionException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/update_organization_addresses"
	},
	service = MVCActionCommand.class
)
public class UpdateOrganizationAddressesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateAddresses(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchOrganizationException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof AddressCityException ||
					 exception instanceof AddressStreetException ||
					 exception instanceof AddressZipException ||
					 exception instanceof NoSuchCountryException ||
					 exception instanceof NoSuchListTypeException ||
					 exception instanceof NoSuchRegionException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				actionResponse.setRenderParameter(
					"mvcPath", "/edit_organization.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private void _updateAddresses(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long organizationId = ParamUtil.getLong(
			actionRequest, "organizationId");

		OrganizationPermissionUtil.check(
			themeDisplay.getPermissionChecker(),
			_organizationService.getOrganization(organizationId),
			ActionKeys.UPDATE);

		List<Address> addresses = UsersAdminUtil.getAddresses(actionRequest);

		if (addresses != null) {
			UsersAdminUtil.updateAddresses(
				Organization.class.getName(), organizationId, addresses);
		}
	}

	@Reference
	private OrganizationService _organizationService;

}