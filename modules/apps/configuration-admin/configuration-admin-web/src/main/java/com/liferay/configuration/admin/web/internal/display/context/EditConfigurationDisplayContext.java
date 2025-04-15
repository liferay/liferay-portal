/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.display.context;

import com.liferay.configuration.admin.menu.ConfigurationMenuItem;
import com.liferay.configuration.admin.web.internal.constants.ConfigurationAdminWebKeys;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.cm.Configuration;

/**
 * @author Evan Thibodeau
 */
public class EditConfigurationDisplayContext {

	public EditConfigurationDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_configurationModel =
			(ConfigurationModel)httpServletRequest.getAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_MODEL);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getDropdownItems() throws Exception {
		DropdownItemList dropdownItemList = DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "delete");

				PortletURL currentURL = PortletURLUtil.getCurrent(
					_renderRequest, _renderResponse);

				dropdownItem.putData(
					"deleteConfigActionURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/configuration_admin/delete_configuration"
					).setRedirect(
						currentURL
					).setParameter(
						"factoryPid", _configurationModel.getFactoryPid()
					).setParameter(
						"pid", _configurationModel.getID()
					).buildString());

				String label = LanguageUtil.get(
					_httpServletRequest, "reset-default-values");

				if (_configurationModel.isFactory()) {
					label = LanguageUtil.get(_httpServletRequest, "delete");
				}

				dropdownItem.setLabel(label);
			}
		).add(
			dropdownItem -> {
				dropdownItem.setHref(
					ResourceURLBuilder.createResourceURL(
						_renderResponse
					).setParameter(
						"factoryPid", _configurationModel.getFactoryPid()
					).setParameter(
						"pid", _configurationModel.getID()
					).setResourceID(
						"/configuration_admin/export_configuration"
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "export"));
			}
		).build();

		List<ConfigurationMenuItem> configurationMenuItems =
			(List<ConfigurationMenuItem>)_httpServletRequest.getAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_MENU_ITEMS);

		if (ListUtil.isNotEmpty(configurationMenuItems)) {
			for (ConfigurationMenuItem configurationMenuItem :
					configurationMenuItems) {

				Configuration configuration =
					_configurationModel.getConfiguration();

				dropdownItemList.add(
					dropdownItem -> {
						dropdownItem.putData("action", "customMenuItem");
						dropdownItem.putData(
							"url",
							configurationMenuItem.getURL(
								_renderRequest, _renderResponse,
								_configurationModel.getID(),
								_configurationModel.getFactoryPid(),
								configuration.getProperties()));

						dropdownItem.setLabel(
							configurationMenuItem.getLabel(
								_themeDisplay.getLocale()));
					});
			}
		}

		return dropdownItemList;
	}

	private final ConfigurationModel _configurationModel;
	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}