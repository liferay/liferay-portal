/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.settings.ArchivedSettings;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ArchivedSettingsActionDropdownItemsProvider {

	public ArchivedSettingsActionDropdownItemsProvider(
		ArchivedSettings archivedSettings, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_archivedSettings = archivedSettings;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			_getRestoreArchivedSetupActionUnsafeConsumer()
		).add(
			_getDeleteArchivedSetupActionUnsafeConsumer()
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteArchivedSetupActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteArchivedSetups");
			dropdownItem.putData(
				"deleteArchivedSetupsURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"deleteArchivedSetups"
				).setMVCPath(
					"/edit_configuration_templates.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setPortletResource(
					_getPortletResource()
				).setParameter(
					"name", _archivedSettings.getName()
				).setParameter(
					"portletConfiguration", true
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private String _getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		_portletResource = ParamUtil.getString(
			_httpServletRequest, "portletResource");

		return _portletResource;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getRestoreArchivedSetupActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "restoreArchivedSetup");
			dropdownItem.putData(
				"restoreArchivedSetupURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"restoreArchivedSetup"
				).setMVCPath(
					"/edit_configuration_templates.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setPortletResource(
					_getPortletResource()
				).setParameter(
					"name", _archivedSettings.getName()
				).setParameter(
					"portletConfiguration", true
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "apply"));
		};
	}

	private final ArchivedSettings _archivedSettings;
	private final HttpServletRequest _httpServletRequest;
	private String _portletResource;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}