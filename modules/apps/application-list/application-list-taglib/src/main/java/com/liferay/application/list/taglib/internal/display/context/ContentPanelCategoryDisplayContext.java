/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.taglib.internal.display.context;

import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Pavel Savinov
 */
public class ContentPanelCategoryDisplayContext {

	public ContentPanelCategoryDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public DropdownItemList getScopesDropdownItemList() throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PanelCategoryHelper panelCategoryHelper =
			(PanelCategoryHelper)_httpServletRequest.getAttribute(
				ApplicationListWebKeys.PANEL_CATEGORY_HELPER);

		String portletId = themeDisplay.getPpid();

		if (Validator.isNull(portletId) ||
			!panelCategoryHelper.containsPortlet(
				portletId, PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
				themeDisplay.getPermissionChecker(),
				themeDisplay.getSiteGroup())) {

			portletId = panelCategoryHelper.getFirstPortletId(
				PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
				themeDisplay.getPermissionChecker(),
				themeDisplay.getSiteGroup());
		}

		PortletURL portletURL = PortalUtil.getControlPanelPortletURL(
			_httpServletRequest, themeDisplay.getSiteGroup(), portletId, 0, 0,
			PortletRequest.RENDER_PHASE);

		DropdownItemList dropdownItems = DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref(portletURL);
				dropdownItem.setLabel(
					LanguageUtil.get(
						themeDisplay.getLocale(), "default-scope"));
			}
		).build();

		List<Layout> scopeLayouts = LayoutLocalServiceUtil.getScopeGroupLayouts(
			themeDisplay.getSiteGroupId());

		for (Layout scopeLayout : scopeLayouts) {
			Group scopeGroup = scopeLayout.getScopeGroup();

			if (Validator.isNull(portletId) ||
				!panelCategoryHelper.containsPortlet(
					portletId, PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
					themeDisplay.getPermissionChecker(), scopeGroup)) {

				portletId = panelCategoryHelper.getFirstPortletId(
					PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
					themeDisplay.getPermissionChecker(), scopeGroup);
			}

			if (Validator.isNull(portletId)) {
				continue;
			}

			PortletURL layoutItemPortletURL =
				PortalUtil.getControlPanelPortletURL(
					_httpServletRequest, scopeGroup, portletId, 0, 0,
					PortletRequest.RENDER_PHASE);

			dropdownItems.add(
				dropdownItem -> {
					dropdownItem.setDeprecated(true);
					dropdownItem.setHref(layoutItemPortletURL);
					dropdownItem.setLabel(
						LanguageUtil.get(
							themeDisplay.getLocale(),
							HtmlUtil.escape(
								scopeLayout.getName(
									themeDisplay.getLocale()))));
				});
		}

		return dropdownItems;
	}

	private final HttpServletRequest _httpServletRequest;

}