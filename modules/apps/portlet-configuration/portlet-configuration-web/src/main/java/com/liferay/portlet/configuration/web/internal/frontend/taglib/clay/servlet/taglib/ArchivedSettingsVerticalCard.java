/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.settings.ArchivedSettings;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.web.internal.constants.PortletConfigurationWebKeys;
import com.liferay.portlet.configuration.web.internal.servlet.taglib.util.ArchivedSettingsActionDropdownItemsProvider;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ArchivedSettingsVerticalCard implements VerticalCard {

	public ArchivedSettingsVerticalCard(
		ArchivedSettings archivedSettings, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_archivedSettings = archivedSettings;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		ArchivedSettingsActionDropdownItemsProvider
			archivedSettingsActionDropdownItemsProvider =
				new ArchivedSettingsActionDropdownItemsProvider(
					_archivedSettings, _renderRequest, _renderResponse);

		return archivedSettingsActionDropdownItemsProvider.
			getActionDropdownItems();
	}

	@Override
	public String getDefaultEventHandler() {
		return PortletConfigurationWebKeys.
			ARCHIVED_SETUPS_DROPDOWN_DEFAULT_EVENT_HANDLER;
	}

	@Override
	public String getIcon() {
		return "archive";
	}

	@Override
	public String getSubtitle() {
		Date modifiedDate = _archivedSettings.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.getTimeDescription(
			_themeDisplay.getLocale(),
			System.currentTimeMillis() - modifiedDate.getTime(), true);

		return LanguageUtil.format(
			_httpServletRequest, "x-ago-by-x",
			new String[] {
				modifiedDateDescription,
				HtmlUtil.escape(_archivedSettings.getUserName())
			});
	}

	@Override
	public String getTitle() {
		return _archivedSettings.getName();
	}

	private final ArchivedSettings _archivedSettings;
	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}