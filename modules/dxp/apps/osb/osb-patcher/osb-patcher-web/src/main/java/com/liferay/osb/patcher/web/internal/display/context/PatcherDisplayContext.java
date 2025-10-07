/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class PatcherDisplayContext {

	public PatcherDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<NavigationItem> getNavigationItems(String tabs1) {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(tabs1, "accounts"));
				navigationItem.setHref(
					_getPortletURL(), "mvcRenderCommandName",
					"/patcher/index_accounts");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "accounts"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(tabs1, "fixes"));
				navigationItem.setHref(
					_getPortletURL(), "mvcRenderCommandName",
					"/patcher/index_fixes");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "fixes"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(tabs1, "qa-builds"));
				navigationItem.setHref(
					_getPortletURL(), "mvcRenderCommandName",
					"/patcher/index_builds");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "qa-builds"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(tabs1, "fix-components"));
				navigationItem.setHref(
					_getPortletURL(), "mvcRenderCommandName",
					"/patcher/index_fix_components");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "fix-components"));
			}
		).add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), "fix_packs",
				PatcherActionKeys.INDEX),
			navigationItem -> {
				navigationItem.setActive(Objects.equals(tabs1, "fix-packs"));
				navigationItem.setHref(
					_getPortletURL(), "mvcRenderCommandName",
					"/patcher/index_fix_packs");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "fix-packs"));
			}
		).add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), "product_versions",
				PatcherActionKeys.INDEX),
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(tabs1, "product-versions"));
				navigationItem.setHref(
					_getPortletURL(), "mvcRenderCommandName",
					"/patcher/index_product_versions");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "product-versions"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(tabs1, "project-versions"));
				navigationItem.setHref(
					_getPortletURL(), "mvcRenderCommandName",
					"/patcher/index_project_versions");
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "project-versions"));
			}
		).add(
			navigationItem -> {
				navigationItem.setHref(
					_themeDisplay.getCDNBaseURL() +
						"/api/jsonws/?contextPath=" +
							PortalUtil.getPathContext(_httpServletRequest));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "jsonws-api"));
			}
		).build();
	}

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setParameter(
			"patcherProductVersionId",
			() -> {
				long patcherProductVersionId = ParamUtil.getLong(
					_httpServletRequest, "patcherProductVersionId");

				if (patcherProductVersionId > 0) {
					return patcherProductVersionId;
				}

				return null;
			}
		).buildRenderURL();
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}