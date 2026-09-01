/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.admin.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.admin.web.internal.constants.SearchAdminPortletKeys;
import com.liferay.portal.search.index.IndexInformation;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	property = {
		"panel.app.order:Integer=600",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_CONFIGURATION
	},
	service = PanelApp.class
)
public class SearchAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "search";
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		List<PanelAppNavigationItem> panelAppNavigationItems =
			new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isOmniadmin()) {
			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "connections", themeDisplay));
		}

		panelAppNavigationItems.add(
			_getPanelAppNavigationItem(
				httpServletRequest, "index-actions", themeDisplay));

		if ((_indexInformationSnapshot.get() != null) &&
			permissionChecker.isOmniadmin()) {

			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "field-mappings", themeDisplay));
		}

		if (panelAppNavigationItems.size() < 2) {
			return Collections.emptyList();
		}

		return panelAppNavigationItems;
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return SearchAdminPortletKeys.SEARCH_ADMIN;
	}

	private PanelAppNavigationItem _getPanelAppNavigationItem(
			HttpServletRequest httpServletRequest, String tabs1,
			ThemeDisplay themeDisplay)
		throws PortalException {

		return new PanelAppNavigationItem(
			_language.get(LocaleUtil.ENGLISH, tabs1),
			PortletURLBuilder.create(
				getPortletURL(httpServletRequest)
			).setTabs1(
				tabs1
			).buildString(),
			_language.get(themeDisplay.getLocale(), tabs1));
	}

	private static final Snapshot<IndexInformation> _indexInformationSnapshot =
		new Snapshot<>(
			SearchAdminPanelApp.class, IndexInformation.class, null, true);

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + SearchAdminPortletKeys.SEARCH_ADMIN + ")"
	)
	private Portlet _portlet;

}