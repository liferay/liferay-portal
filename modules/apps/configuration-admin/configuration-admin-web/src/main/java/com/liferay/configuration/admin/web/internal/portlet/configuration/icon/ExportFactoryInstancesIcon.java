/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.portlet.configuration.icon;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.web.internal.constants.ConfigurationAdminWebKeys;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelIterator;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = "path=/view_factory_instances",
	service = PortletConfigurationIcon.class
)
public class ExportFactoryInstancesIcon extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _language.get(themeDisplay.getLocale(), "export-entries");
	}

	@Override
	public String getMethod() {
		return "GET";
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		LiferayPortletURL liferayPortletURL =
			(LiferayPortletURL)_portal.getControlPanelPortletURL(
				portletRequest, ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
				PortletRequest.RESOURCE_PHASE);

		ConfigurationModel factoryConfigurationModel =
			(ConfigurationModel)portletRequest.getAttribute(
				ConfigurationAdminWebKeys.FACTORY_CONFIGURATION_MODEL);

		liferayPortletURL.setParameter(
			"factoryPid", factoryConfigurationModel.getFactoryPid());

		liferayPortletURL.setResourceID(
			"/configuration_admin/export_configuration");

		return liferayPortletURL.toString();
	}

	@Override
	public double getWeight() {
		return 1;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		String portletId = _portal.getPortletId(portletRequest);

		if (!portletId.equals(
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS) &&
			!portletId.equals(ConfigurationAdminPortletKeys.SYSTEM_SETTINGS)) {

			return false;
		}

		ConfigurationModelIterator configurationModelIterator =
			(ConfigurationModelIterator)portletRequest.getAttribute(
				ConfigurationAdminWebKeys.CONFIGURATION_MODEL_ITERATOR);

		if (configurationModelIterator.getTotal() > 0) {
			return true;
		}

		return false;
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}