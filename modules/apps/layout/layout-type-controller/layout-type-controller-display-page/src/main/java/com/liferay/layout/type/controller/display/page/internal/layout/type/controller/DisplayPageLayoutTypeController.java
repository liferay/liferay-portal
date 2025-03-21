/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.display.page.internal.layout.type.controller;

import com.liferay.info.display.request.attributes.contributor.InfoDisplayRequestAttributesContributor;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorWebKeys;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.type.controller.BaseLayoutTypeControllerImpl;
import com.liferay.layout.type.controller.display.page.internal.constants.DisplayPageLayoutTypeControllerWebKeys;
import com.liferay.layout.type.controller.display.page.internal.display.context.DisplayPageLayoutTypeControllerDisplayContext;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.login.AuthLoginGroupSettingsUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.TransferHeadersHelperUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
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

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juergen Kappler
 */
@Component(
	property = "layout.type=" + LayoutConstants.TYPE_ASSET_DISPLAY,
	service = LayoutTypeController.class
)
public class DisplayPageLayoutTypeController
	extends BaseLayoutTypeControllerImpl {

	@Override
	public String getFriendlyURL(
			HttpServletRequest httpServletRequest, Layout layout)
		throws PortalException {

		if (layout.isDraftLayout()) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String friendlyURL = _portal.getCurrentURL(httpServletRequest);

		if (!Validator.isBlank(themeDisplay.getPathMain()) &&
			friendlyURL.startsWith(themeDisplay.getPathMain())) {

			return null;
		}

		if (friendlyURL.contains(Portal.FRIENDLY_URL_SEPARATOR)) {
			friendlyURL = friendlyURL.substring(
				0, friendlyURL.indexOf(Portal.FRIENDLY_URL_SEPARATOR));
		}
		else if (friendlyURL.contains(StringPool.QUESTION)) {
			friendlyURL = friendlyURL.substring(
				0, friendlyURL.lastIndexOf(StringPool.QUESTION));
		}

		return HtmlUtil.escape(friendlyURL);
	}

	@Override
	public String getType() {
		return LayoutConstants.TYPE_PORTLET;
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

		if (layout.isDraftLayout()) {
			if (!themeDisplay.isSignedIn()) {
				throw new NoSuchLayoutException();
			}

			Layout curLayout = _layoutLocalService.fetchLayout(
				layout.getClassPK());

			if (!_hasUpdatePermissions(
					themeDisplay.getPermissionChecker(), curLayout)) {

				throw new PrincipalException.MustHavePermission(
					themeDisplay.getPermissionChecker(), Layout.class.getName(),
					layout.getLayoutId(), ActionKeys.UPDATE);
			}
		}

		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);
		String redirect = StringPool.BLANK;

		if (layoutMode.equals(Constants.EDIT) &&
			!_hasUpdatePermissions(
				themeDisplay.getPermissionChecker(), layout)) {

			layoutMode = Constants.VIEW;
		}
		else if (!layout.isUnlocked(layoutMode, themeDisplay.getUserId())) {
			redirect = _layoutLockManager.getLockedLayoutURL(
				httpServletRequest);
		}

		DisplayPageLayoutTypeControllerDisplayContext
			displayPageLayoutTypeControllerDisplayContext =
				new DisplayPageLayoutTypeControllerDisplayContext(
					httpServletRequest, _infoItemServiceRegistry,
					_infoSearchClassMapperRegistry);

		httpServletRequest.setAttribute(
			DisplayPageLayoutTypeControllerWebKeys.
				DISPLAY_PAGE_LAYOUT_TYPE_CONTROLLER_DISPLAY_CONTEXT,
			displayPageLayoutTypeControllerDisplayContext);

		if (!displayPageLayoutTypeControllerDisplayContext.hasInfoItem() &&
			!themeDisplay.isSignedIn()) {

			throw new NoSuchLayoutException();
		}

		String page = getViewPage();

		if (layoutMode.equals(Constants.EDIT)) {
			page = _EDIT_PAGE;
		}

		RequestDispatcher requestDispatcher =
			TransferHeadersHelperUtil.getTransferHeadersRequestDispatcher(
				_servletContext.getRequestDispatcher(page));

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		ServletResponse servletResponse = createServletResponse(
			httpServletResponse, unsyncStringWriter);

		String includeServletPath = (String)httpServletRequest.getAttribute(
			RequestDispatcher.INCLUDE_SERVLET_PATH);

		try {
			boolean hasViewPermission =
				displayPageLayoutTypeControllerDisplayContext.hasPermission(
					themeDisplay.getPermissionChecker(), ActionKeys.VIEW);

			if (!hasViewPermission && themeDisplay.isSignedIn()) {
				httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
			else if (!hasViewPermission) {
				if (themeDisplay.isSignedIn()) {
					httpServletResponse.setStatus(
						HttpServletResponse.SC_FORBIDDEN);
				}
				else if (AuthLoginGroupSettingsUtil.isPromptEnabled(
							layout.getGroupId())) {

					redirect = HttpComponentsUtil.setParameter(
						themeDisplay.getURLSignIn(), "redirect",
						themeDisplay.getURLCurrent());
				}
				else {
					throw new NoSuchLayoutException();
				}
			}

			if (Validator.isNotNull(redirect)) {
				httpServletResponse.sendRedirect(redirect);
			}
			else {
				LayoutPageTemplateEntry layoutPageTemplateEntry =
					_fetchLayoutPageTemplateEntry(layout);

				if (layoutPageTemplateEntry != null) {
					httpServletRequest.setAttribute(
						ContentPageEditorWebKeys.CLASS_NAME,
						LayoutPageTemplateEntry.class.getName());
					httpServletRequest.setAttribute(
						ContentPageEditorWebKeys.CLASS_PK,
						layoutPageTemplateEntry.getLayoutPageTemplateEntryId());
				}

				addAttributes(httpServletRequest);

				requestDispatcher.include(httpServletRequest, servletResponse);
			}
		}
		finally {
			removeAttributes(httpServletRequest);

			httpServletRequest.setAttribute(
				RequestDispatcher.INCLUDE_SERVLET_PATH, includeServletPath);
		}

		httpServletRequest.setAttribute(
			WebKeys.LAYOUT_CONTENT, unsyncStringWriter.getStringBundler());

		String contentType = servletResponse.getContentType();

		if (contentType != null) {
			httpServletResponse.setContentType(contentType);
		}

		return false;
	}

	@Override
	public boolean isFirstPageable() {
		return true;
	}

	@Override
	public boolean isFullPageDisplayable() {
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
		return true;
	}

	@Override
	public boolean isURLFriendliable() {
		return true;
	}

	@Override
	protected void addAttributes(HttpServletRequest httpServletRequest) {
		for (InfoDisplayRequestAttributesContributor
				infoDisplayRequestAttributesContributor :
					_infoDisplayRequestAttributesContributors) {

			infoDisplayRequestAttributesContributor.addAttributes(
				httpServletRequest);
		}
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

	private LayoutPageTemplateEntry _fetchLayoutPageTemplateEntry(
		Layout layout) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry != null) {
			return layoutPageTemplateEntry;
		}

		if (!layout.isDraftLayout()) {
			return null;
		}

		Layout publishedLayout = _layoutLocalService.fetchLayout(
			layout.getClassPK());

		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntryByPlid(publishedLayout.getPlid());
	}

	private boolean _hasUpdatePermissions(
		PermissionChecker permissionChecker, Layout layout) {

		try {
			if (LayoutPermissionUtil.containsLayoutUpdatePermission(
					permissionChecker, layout)) {

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

	private static final String _EDIT_PAGE = "/layout/edit/display_page.jsp";

	private static final String _URL =
		"${liferay:mainPath}/portal/layout?p_l_id=${liferay:plid}" +
			"&p_v_l_s_g_id=${liferay:pvlsgid}";

	private static final String _VIEW_PAGE = "/layout/view/display_page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		DisplayPageLayoutTypeController.class);

	@Reference
	private volatile List<InfoDisplayRequestAttributesContributor>
		_infoDisplayRequestAttributesContributors;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutLockManager _layoutLockManager;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.type.controller.display.page)"
	)
	private ServletContext _servletContext;

}