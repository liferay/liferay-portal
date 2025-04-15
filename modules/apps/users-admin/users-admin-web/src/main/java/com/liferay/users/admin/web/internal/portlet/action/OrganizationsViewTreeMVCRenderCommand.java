/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/organizations_view_tree"
	},
	service = MVCRenderCommand.class
)
public class OrganizationsViewTreeMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			long organizationId = ParamUtil.getLong(
				renderRequest, "organizationId",
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID);

			Organization organization = null;

			if (organizationId != 0) {
				organization = _organizationService.getOrganization(
					organizationId);
			}

			renderRequest.setAttribute(
				"view.jsp-backURL",
				ParamUtil.getString(
					renderRequest, "backURL",
					ParamUtil.getString(renderRequest, "redirect")));
			renderRequest.setAttribute("view.jsp-organization", organization);
			renderRequest.setAttribute(
				"view.jsp-organizationId", organizationId);
			renderRequest.setAttribute(
				"view.jsp-portletURL",
				PortletURLBuilder.createRenderURL(
					renderResponse
				).setParameter(
					"screenNavigationCategoryKey",
					UserScreenNavigationEntryConstants.
						CATEGORY_KEY_ORGANIZATIONS
				).setParameter(
					"usersListView", UserConstants.LIST_VIEW_TREE
				).setParameter(
					"viewUsersRedirect",
					() -> {
						String viewUsersRedirect = ParamUtil.getString(
							renderRequest, "viewUsersRedirect");

						if (Validator.isNull(viewUsersRedirect)) {
							return null;
						}

						return viewUsersRedirect;
					}
				).buildPortletURL());
			renderRequest.setAttribute(
				"view.jsp-usersListView", UserConstants.LIST_VIEW_TREE);

			return "/view_tree.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	@Reference
	private OrganizationService _organizationService;

}