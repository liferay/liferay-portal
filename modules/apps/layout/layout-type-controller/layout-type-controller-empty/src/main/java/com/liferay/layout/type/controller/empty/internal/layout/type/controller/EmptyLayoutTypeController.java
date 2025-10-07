/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.empty.internal.layout.type.controller;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.type.controller.BaseLayoutTypeControllerImpl;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(
	property = "layout.type=" + LayoutConstants.TYPE_EMPTY,
	service = LayoutTypeController.class
)
public class EmptyLayoutTypeController extends BaseLayoutTypeControllerImpl {

	@Override
	public String getType() {
		return LayoutConstants.TYPE_EMPTY;
	}

	@Override
	public String getURL() {
		return null;
	}

	@Override
	public String includeEditContent(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Layout layout) {

		return StringPool.BLANK;
	}

	@Override
	public boolean includeLayoutContent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!layout.isTypeEmpty() ||
			!_layoutPermission.containsLayoutUpdatePermission(
				themeDisplay.getPermissionChecker(), layout)) {

			throw new NoSuchLayoutException();
		}

		String backURL = themeDisplay.getURLCurrent();

		httpServletRequest.setAttribute(
			"editURL",
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/layout_admin/select_layout_page_template_entry"
			).setRedirect(
				() -> {
					String redirect = ParamUtil.getString(
						httpServletRequest, "redirect");

					if (Validator.isNull(redirect)) {
						redirect = _portal.escapeRedirect(backURL);
					}

					return redirect;
				}
			).setBackURL(
				backURL
			).setParameter(
				"emptyLayout", Boolean.TRUE
			).setParameter(
				"externalReferenceCode", layout.getExternalReferenceCode()
			).setParameter(
				"groupId", layout.getGroupId()
			).setParameter(
				"privateLayout", layout.isPrivateLayout()
			).setParameter(
				"selPlid", layout.getPlid()
			).buildString());

		return super.includeLayoutContent(
			httpServletRequest, httpServletResponse, layout);
	}

	@Override
	public boolean isFirstPageable() {
		return true;
	}

	@Override
	public boolean isInstanceable() {
		return false;
	}

	@Override
	public boolean isParentable() {
		return true;
	}

	@Override
	public boolean isSitemapable() {
		return false;
	}

	@Override
	public boolean isURLFriendliable() {
		return false;
	}

	@Override
	protected ServletResponse createServletResponse(
		HttpServletResponse httpServletResponse,
		UnsyncStringWriter unsyncStringWriter) {

		return new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);
	}

	@Override
	protected String getEditPage() {
		return null;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	protected String getViewPage() {
		return _VIEW_PAGE;
	}

	private static final String _VIEW_PAGE = "/layout/view/empty.jsp";

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.type.controller.empty)"
	)
	private ServletContext _servletContext;

}