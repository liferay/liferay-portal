/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.filter;

import com.liferay.account.admin.web.internal.constants.AccountScreenNavigationEntryConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
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

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
	service = PortletFilter.class
)
public class AccountEntriesAdminPortletFilter
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
			(actionName.equals("deletePermission") ||
			 actionName.equals("updateActions"))) {

			_portlet.processAction(actionRequest, actionResponse);

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

		if (Validator.isNotNull(mvcPath) &&
			(mvcPath.startsWith("/edit_role") ||
			 mvcPath.equals("/view_resources.jsp"))) {

			if (mvcPath.equals("/edit_role_permissions.jsp")) {
				renderRequest.removeAttribute("mvcPath");

				PortletURL portletURL = PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						renderRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/account_admin/edit_account_role"
				).setBackURL(
					ParamUtil.getString(renderRequest, "backURL")
				).setParameter(
					"cur", ParamUtil.getString(renderRequest, "cur")
				).setParameter(
					"delta", ParamUtil.getString(renderRequest, "delta")
				).setParameter(
					"resetCur", ParamUtil.getString(renderRequest, "resetCur")
				).setParameter(
					"screenNavigationCategoryKey",
					AccountScreenNavigationEntryConstants.
						CATEGORY_KEY_DEFINE_PERMISSIONS
				).buildPortletURL();

				long roleId = ParamUtil.getLong(renderRequest, "roleId");

				AccountRole accountRole =
					_accountRoleLocalService.fetchAccountRoleByRoleId(roleId);

				if (accountRole != null) {
					portletURL.setParameter(
						"accountEntryId",
						String.valueOf(accountRole.getAccountEntryId()));
					portletURL.setParameter(
						"accountRoleId",
						String.valueOf(accountRole.getAccountRoleId()));
				}

				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(renderResponse);

				httpServletResponse.sendRedirect(portletURL.toString());

				return;
			}

			_jspRenderer.renderJSP(
				_servletContext, _portal.getHttpServletRequest(renderRequest),
				_portal.getHttpServletResponse(renderResponse), mvcPath);

			return;
		}

		filterChain.doFilter(renderRequest, renderResponse);
	}

	@Override
	public void doFilter(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		String mvcPath = ParamUtil.getString(resourceRequest, "mvcPath");

		if (Validator.isNotNull(mvcPath) &&
			mvcPath.equals("/view_resources.jsp")) {

			MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

			mvcPortlet.serveResource(resourceRequest, resourceResponse);

			return;
		}

		filterChain.doFilter(resourceRequest, resourceResponse);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(jakarta.portlet.name=" + RolesAdminPortletKeys.ROLES_ADMIN + ")",
		unbind = "-"
	)
	private Portlet _portlet;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.roles.admin.web)")
	private ServletContext _servletContext;

}