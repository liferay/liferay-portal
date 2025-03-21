/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.web.internal;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.IconTag;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.security.permission.resource.LayoutContentModelResourcePermission;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;
import java.io.Writer;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.TOOLS,
		"product.navigation.control.menu.entry.order:Integer=100"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class LayoutHeaderProductNavigationControlMenuEntry
	extends BaseProductNavigationControlMenuEntry {

	@Override
	public String getLabel(Locale locale) {
		return null;
	}

	@Override
	public String getURL(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		Writer writer = httpServletResponse.getWriter();

		StringBundler sb = new StringBundler(29);

		sb.append("<div class=\"control-menu-nav-item\"><span ");
		sb.append("class=\"align-items-center control-menu-level-1-heading ");
		sb.append("d-flex mr-1\" data-qa-id=\"headerTitle\"><h1 class=\"");
		sb.append("lfr-portal-tooltip h4 mb-0\" title=\"");

		String headerTitle = _getHeaderTitle(httpServletRequest);

		sb.append(HtmlUtil.escapeAttribute(headerTitle));

		sb.append("\">");
		sb.append(HtmlUtil.escape(headerTitle));

		if (_hasDraftLayout(httpServletRequest) &&
			_hasEditPermission(httpServletRequest)) {

			sb.append("<span class=\"sr-only\">");
			sb.append(_language.get(httpServletRequest, "draft"));
			sb.append("</span>");
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		try {
			if (!_hasGuestViewPermission(layout) && !layout.isPrivateLayout()) {
				sb.append("<span class=\"sr-only\">");
				sb.append(_language.get(httpServletRequest, "restricted-page"));
				sb.append("</span>");
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		sb.append("</h1>");

		if (_hasDraftLayout(httpServletRequest) &&
			_hasEditPermission(httpServletRequest)) {

			sb.append("<sup aria-hidden=\"true\" ");
			sb.append("class=\"flex-shrink-0 small\">*</sup>");
		}

		sb.append("</span>");

		try {
			if (layout.isDraftLayout()) {
				layout = _layoutLocalService.fetchLayout(layout.getClassPK());
			}

			if (!_hasGuestViewPermission(layout) && !layout.isPrivateLayout()) {
				sb.append("<span class=\"align-items-center c-ml-3 d-flex ");
				sb.append("lfr-portal-tooltip text-white\" title=\"");
				sb.append(_language.get(httpServletRequest, "restricted-page"));
				sb.append("\">");

				IconTag iconTag = new IconTag();

				iconTag.setCssClass("c-mt-0");
				iconTag.setSymbol("password-policies");

				try {
					sb.append(
						iconTag.doTagAsString(
							httpServletRequest, httpServletResponse));
				}
				catch (JspException jspException) {
					if (_log.isDebugEnabled()) {
						_log.debug(jspException);
					}
				}

				sb.append("</span>");
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		if (_isDraftLayout(httpServletRequest)) {
			sb.append("<span class=\"bg-transparent flex-shrink-0 label ");
			sb.append("label-inverse-secondary ml-3 mr-0\">");
			sb.append("<span class=\"label-item label-item-expand\">");
			sb.append(_language.get(httpServletRequest, "draft"));
			sb.append("</span></span>");
		}

		sb.append("</div>");

		writer.write(sb.toString());

		return true;
	}

	@Override
	public boolean isRelevant(HttpServletRequest httpServletRequest) {
		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		return layoutMode.equals(Constants.EDIT);
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isTypeControlPanel()) {
			return false;
		}

		return super.isShow(httpServletRequest);
	}

	private String _getHeaderTitle(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String portletId = ParamUtil.getString(httpServletRequest, "p_p_id");

		Layout layout = themeDisplay.getLayout();

		if (Validator.isNotNull(portletId) && layout.isSystem() &&
			!layout.isTypeControlPanel() &&
			Objects.equals(
				layout.getFriendlyURL(),
				PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL)) {

			return _portal.getPortletTitle(portletId, themeDisplay.getLocale());
		}

		if (layout.isTypeAssetDisplay()) {
			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
				(LayoutDisplayPageObjectProvider<?>)
					httpServletRequest.getAttribute(
						LayoutDisplayPageWebKeys.
							LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

			if (layoutDisplayPageObjectProvider != null) {
				return layoutDisplayPageObjectProvider.getTitle(
					themeDisplay.getLocale());
			}

			AssetEntry assetEntry = (AssetEntry)httpServletRequest.getAttribute(
				WebKeys.LAYOUT_ASSET_ENTRY);

			if (assetEntry != null) {
				return assetEntry.getTitle(themeDisplay.getLanguageId());
			}
		}

		return layout.getName(themeDisplay.getLocale());
	}

	private boolean _hasDraftLayout(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			return false;
		}

		Layout draftLayout = null;

		if (layout.isDraftLayout()) {
			draftLayout = layout;

			layout = _layoutLocalService.fetchLayout(draftLayout.getClassPK());
		}
		else {
			draftLayout = layout.fetchDraftLayout();
		}

		if (((draftLayout != null) && draftLayout.isDraft()) ||
			!layout.isPublished()) {

			return true;
		}

		return false;
	}

	private boolean _hasEditPermission(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		try {
			if (_layoutContentModelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), layout.getPlid(),
					ActionKeys.UPDATE) ||
				_layoutPermission.containsLayoutUpdatePermission(
					themeDisplay.getPermissionChecker(), layout)) {

				return true;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	private boolean _hasGuestViewPermission(Layout layout)
		throws PortalException {

		Role role = _roleLocalService.getRole(
			layout.getCompanyId(), RoleConstants.GUEST);

		return _resourcePermissionLocalService.hasResourcePermission(
			layout.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(layout.getPlid()), role.getRoleId(),
			ActionKeys.VIEW);
	}

	private boolean _isDraftLayout(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (!layout.isTypeContent()) {
			return false;
		}

		String mode = ParamUtil.getString(httpServletRequest, "p_l_mode");

		if (Objects.equals(mode, Constants.EDIT) || !layout.isDraftLayout()) {
			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutHeaderProductNavigationControlMenuEntry.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutContentModelResourcePermission
		_layoutContentModelResourcePermission;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private Portal _portal;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}