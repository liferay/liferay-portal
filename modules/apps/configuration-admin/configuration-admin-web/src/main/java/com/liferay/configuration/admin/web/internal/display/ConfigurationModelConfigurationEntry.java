/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.display;

import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ResourceBundleLoaderProviderUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Jorge Ferrer
 */
public class ConfigurationModelConfigurationEntry
	implements ConfigurationEntry {

	public ConfigurationModelConfigurationEntry(
		ConfigurationModel configurationModel) {

		_configurationModel = configurationModel;
	}

	@Override
	public boolean equals(Object object) {
		ConfigurationEntry configurationEntry = (ConfigurationEntry)object;

		return Objects.equals(getKey(), configurationEntry.getKey());
	}

	@Override
	public String getCategory() {
		return _configurationModel.getCategory();
	}

	public ConfigurationModel getConfigurationModel() {
		return _configurationModel;
	}

	@Override
	public String getEditURL(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		Map<String, String> editURLParameters = getEditURLParameters();

		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			renderResponse
		).buildPortletURL();

		for (Map.Entry<String, String> entry : editURLParameters.entrySet()) {
			portletURL.setParameter(entry.getKey(), entry.getValue());
		}

		return portletURL.toString();
	}

	@Override
	public Map<String, String> getEditURLParameters() {
		if (_configurationModel.isFactory()) {
			return LinkedHashMapBuilder.put(
				"factoryPid", _configurationModel.getFactoryPid()
			).put(
				"mvcRenderCommandName",
				"/configuration_admin/view_factory_instances"
			).build();
		}

		return LinkedHashMapBuilder.put(
			"factoryPid", _configurationModel.getFactoryPid()
		).put(
			"mvcRenderCommandName", "/configuration_admin/edit_configuration"
		).put(
			"pid", _configurationModel.getID()
		).build();
	}

	@Override
	public String getKey() {
		return _configurationModel.getID();
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundleLoader curResourceBundleLoader =
			ResourceBundleLoaderProviderUtil.getResourceBundleLoader(
				_configurationModel.getBundleSymbolicName());

		ResourceBundle curComponentResourceBundle =
			curResourceBundleLoader.loadResourceBundle(locale);

		String curConfigurationModelName;

		if (curComponentResourceBundle != null) {
			curConfigurationModelName = LanguageUtil.get(
				curComponentResourceBundle, _configurationModel.getName());
		}
		else {
			curConfigurationModelName = _configurationModel.getName();
		}

		return curConfigurationModelName;
	}

	@Override
	public String getScope() {
		return _configurationModel.getScope();
	}

	@Override
	public int hashCode() {
		return Objects.hash(_configurationModel);
	}

	@Override
	public boolean isDeprecated() {
		return _configurationModel.isDeprecated();
	}

	private final ConfigurationModel _configurationModel;

}