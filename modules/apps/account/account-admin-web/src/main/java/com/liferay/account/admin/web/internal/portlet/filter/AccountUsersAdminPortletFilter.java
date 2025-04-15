/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.filter;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.FilterChain;
import jakarta.portlet.filter.FilterConfig;
import jakarta.portlet.filter.PortletFilter;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_USERS_ADMIN
	},
	service = PortletFilter.class
)
public class AccountUsersAdminPortletFilter
	implements ActionFilter, RenderFilter, ResourceFilter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			ActionRequest actionRequest, ActionResponse actionResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		String actionName = ParamUtil.getString(
			actionRequest, ActionRequest.ACTION_NAME);

		if (Validator.isNotNull(actionName) &&
			actionName.startsWith("/users_admin/")) {

			_portlet.processAction(actionRequest, actionResponse);

			if (actionName.equals("/users_admin/edit_user") &&
				!SessionErrors.isEmpty(actionRequest)) {

				actionResponse.setRenderParameter(
					"mvcPath", "/account_users_admin/edit_account_user.jsp");
			}

			return;
		}

		filterChain.doFilter(actionRequest, actionResponse);
	}

	@Override
	public void doFilter(
			RenderRequest renderRequest, RenderResponse renderResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		String mvcPath = ParamUtil.getString(renderRequest, "mvcPath");

		if (mvcPath.startsWith("/common/") || mvcPath.startsWith("/user/")) {
			_portlet.render(renderRequest, renderResponse);

			return;
		}

		filterChain.doFilter(renderRequest, renderResponse);
	}

	@Override
	public void doFilter(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		String resourceID = resourceRequest.getResourceID();

		if (resourceID.startsWith("/users_admin/")) {
			MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

			mvcPortlet.serveResource(resourceRequest, resourceResponse);

			return;
		}

		filterChain.doFilter(resourceRequest, resourceResponse);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Reference(
		target = "(jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN + ")",
		unbind = "-"
	)
	private Portlet _portlet;

}