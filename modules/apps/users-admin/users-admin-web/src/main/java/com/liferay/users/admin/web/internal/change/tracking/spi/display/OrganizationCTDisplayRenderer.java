/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class OrganizationCTDisplayRenderer
	extends BaseCTDisplayRenderer<Organization> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, Organization organization)
		throws PortalException {

		Group group = _groupLocalService.getGroup(organization.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, UsersAdminPortletKeys.USERS_ADMIN, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/users_admin/edit_organization"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"organizationId", organization.getOrganizationId()
		).buildString();
	}

	@Override
	public Class<Organization> getModelClass() {
		return Organization.class;
	}

	@Override
	public String getTitle(Locale locale, Organization organization) {
		return organization.getName();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Organization> displayBuilder)
		throws PortalException {

		Organization organization = displayBuilder.getModel();

		Address address = organization.getAddress();

		displayBuilder.display(
			"name", organization.getName()
		).display(
			"created-by",
			() -> {
				String userName = organization.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", organization.getCreateDate()
		).display(
			"last-modified", organization.getModifiedDate()
		).display(
			"parent-organization", organization.getParentOrganizationName()
		).display(
			"organization-type", organization.getType()
		).display(
			"city", address.getCity()
		).display(
			"region",
			UsersAdminUtil.ORGANIZATION_REGION_NAME_ACCESSOR.get(organization)
		).display(
			"country",
			UsersAdminUtil.ORGANIZATION_COUNTRY_NAME_ACCESSOR.get(organization)
		).display(
			"num-of-users",
			_userLocalService.getOrganizationUsersCount(
				organization.getOrganizationId(),
				WorkflowConstants.STATUS_APPROVED)
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}