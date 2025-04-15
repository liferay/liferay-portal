/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.utility.internal.layout.type.controller;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorWebKeys;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.security.permission.resource.LayoutContentModelResourcePermission;
import com.liferay.layout.type.controller.BaseLayoutTypeControllerImpl;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.TransferHeadersHelperUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "layout.type=" + LayoutConstants.TYPE_UTILITY,
	service = LayoutTypeController.class
)
public class UtilityLayoutTypeController extends BaseLayoutTypeControllerImpl {

	@Override
	public String getType() {
		return LayoutConstants.TYPE_UTILITY;
	}

	@Override
	public String getURL() {
		return _URL;
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

		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		Boolean hasUpdatePermissions = null;

		if (layout.isDraftLayout()) {
			Layout curLayout = _layoutLocalService.fetchLayout(
				layout.getClassPK());

			if (curLayout.isPending()) {
				curLayout = layout;
			}

			if (layoutMode.equals(Constants.PREVIEW) ||
				layoutMode.equals(Constants.VIEW)) {

				if (!_hasUpdatePermissions(
						themeDisplay.getPermissionChecker(), curLayout)) {

					throw new PrincipalException.MustHavePermission(
						themeDisplay.getPermissionChecker(),
						Layout.class.getName(), layout.getLayoutId(),
						ActionKeys.UPDATE);
				}
			}
			else {
				hasUpdatePermissions = _hasUpdatePermissions(
					themeDisplay.getPermissionChecker(), curLayout);

				if (!hasUpdatePermissions) {
					throw new PrincipalException.MustHavePermission(
						themeDisplay.getPermissionChecker(),
						Layout.class.getName(), layout.getLayoutId(),
						ActionKeys.UPDATE);
				}
			}
		}

		String redirect = StringPool.BLANK;

		if (layoutMode.equals(Constants.EDIT)) {
			if (hasUpdatePermissions == null) {
				hasUpdatePermissions = _hasUpdatePermissions(
					themeDisplay.getPermissionChecker(), layout);
			}

			if (!hasUpdatePermissions) {
				layoutMode = Constants.VIEW;
			}
			else if (!layout.isUnlocked(layoutMode, themeDisplay.getUserId())) {
				redirect = _layoutLockManager.getLockedLayoutURL(
					httpServletRequest);
			}
			else {
				redirect = _getDraftLayoutFullURL(
					httpServletRequest, layout, themeDisplay);
			}
		}

		if (!layout.isPublished()) {
			if (hasUpdatePermissions == null) {
				hasUpdatePermissions = _hasUpdatePermissions(
					themeDisplay.getPermissionChecker(), layout);
			}

			if (!hasUpdatePermissions) {
				throw new NoSuchLayoutException();
			}
		}

		String page = getViewPage();

		if (layoutMode.equals(Constants.EDIT)) {
			page = _EDIT_LAYOUT_PAGE;
		}

		RequestDispatcher requestDispatcher =
			TransferHeadersHelperUtil.getTransferHeadersRequestDispatcher(
				_servletContext.getRequestDispatcher(page));

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		ServletResponse servletResponse = createServletResponse(
			httpServletResponse, unsyncStringWriter);

		String contentType = servletResponse.getContentType();

		String includeServletPath = (String)httpServletRequest.getAttribute(
			RequestDispatcher.INCLUDE_SERVLET_PATH);

		try {
			if (Validator.isNotNull(redirect)) {
				httpServletResponse.sendRedirect(redirect);
			}
			else {
				httpServletRequest.setAttribute(
					ContentPageEditorWebKeys.CLASS_NAME,
					LayoutUtilityPageEntry.class.getName());
				httpServletRequest.setAttribute(
					ContentPageEditorWebKeys.CLASS_PK, layout.getPlid());

				addAttributes(httpServletRequest);

				requestDispatcher.include(httpServletRequest, servletResponse);
			}
		}
		finally {
			removeAttributes(httpServletRequest);

			httpServletRequest.setAttribute(
				RequestDispatcher.INCLUDE_SERVLET_PATH, includeServletPath);
		}

		if (contentType != null) {
			httpServletResponse.setContentType(contentType);
		}

		httpServletRequest.setAttribute(
			WebKeys.LAYOUT_CONTENT, unsyncStringWriter.getStringBundler());

		return false;
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
		return false;
	}

	@Override
	public boolean isSitemapable() {
		return false;
	}

	@Override
	public boolean isURLFriendliable() {
		return true;
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

	private String _getDraftLayoutFullURL(
			HttpServletRequest httpServletRequest, Layout layout,
			ThemeDisplay themeDisplay)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			return StringPool.BLANK;
		}

		String layoutFullURL = _portal.getLayoutFullURL(
			draftLayout, themeDisplay);

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String backURL = originalHttpServletRequest.getParameter(
			"p_l_back_url");

		if (Validator.isNotNull(backURL)) {
			layoutFullURL = HttpComponentsUtil.addParameters(
				layoutFullURL, "p_l_back_url", backURL, "p_l_back_url_title",
				draftLayout.getName(themeDisplay.getLocale()));
		}

		return HttpComponentsUtil.addParameter(
			layoutFullURL, "p_l_mode", Constants.EDIT);
	}

	private boolean _hasUpdatePermissions(
		PermissionChecker permissionChecker, Layout layout) {

		try {
			if (_layoutPermission.containsLayoutUpdatePermission(
					permissionChecker, layout) ||
				_modelResourcePermission.contains(
					permissionChecker, layout.getPlid(), ActionKeys.UPDATE)) {

				return true;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	private static final String _EDIT_LAYOUT_PAGE =
		"/layout/edit_layout/utility.jsp";

	private static final String _URL =
		"${liferay:mainPath}/portal/layout?p_l_id=${liferay:plid}" +
			"&p_v_l_s_g_id=${liferay:pvlsgid}";

	private static final String _VIEW_PAGE = "/layout/view/utility.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		UtilityLayoutTypeController.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutLockManager _layoutLockManager;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private LayoutContentModelResourcePermission _modelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.type.controller.utility)"
	)
	private ServletContext _servletContext;

}