/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Jorge Ferrer
 */
public class ConfigurationScreenConfigurationEntry
	implements ConfigurationEntry {

	public ConfigurationScreenConfigurationEntry(
		ConfigurationScreen configurationScreen, Locale locale) {

		_configurationScreen = configurationScreen;
		_locale = locale;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ConfigurationEntry)) {
			return false;
		}

		ConfigurationEntry configurationEntry = (ConfigurationEntry)object;

		if (Objects.equals(getCategory(), configurationEntry.getCategory()) &&
			Objects.equals(getKey(), configurationEntry.getKey()) &&
			Objects.equals(getScope(), configurationEntry.getScope())) {

			return true;
		}

		return false;
	}

	@Override
	public String getCategory() {
		return _configurationScreen.getCategoryKey();
	}

	@Override
	public String getEditURL(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCRenderCommandName(
			"/configuration_admin/view_configuration_screen"
		).setParameter(
			"configurationScreenKey", _configurationScreen.getKey()
		).buildString();
	}

	@Override
	public String getKey() {
		return _configurationScreen.getKey();
	}

	@Override
	public String getName() {
		return _configurationScreen.getName(_locale);
	}

	@Override
	public String getScope() {
		return _configurationScreen.getScope();
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, getCategory());

		hash = HashUtil.hash(hash, getKey());
		hash = HashUtil.hash(hash, getScope());

		return hash;
	}

	@Override
	public boolean isDeprecated() {
		return _configurationScreen.isDeprecated();
	}

	private final ConfigurationScreen _configurationScreen;
	private final Locale _locale;

}