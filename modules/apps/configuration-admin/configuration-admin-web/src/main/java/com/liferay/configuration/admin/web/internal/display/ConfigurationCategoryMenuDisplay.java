/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.display;

import com.liferay.configuration.admin.util.ConfigurationPidUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Jorge Ferrer
 */
public class ConfigurationCategoryMenuDisplay {

	public ConfigurationCategoryMenuDisplay(
		ConfigurationCategoryDisplay configurationCategoryDisplay,
		Set<ConfigurationEntry> configurationEntries,
		ExtendedObjectClassDefinition.Scope scope) {

		_configurationCategoryDisplay = configurationCategoryDisplay;

		_configurationScopeDisplay = new ConfigurationScopeDisplay(
			scope.toString());

		for (ConfigurationEntry configurationEntry : configurationEntries) {
			_configurationScopeDisplay.add(configurationEntry);
		}
	}

	public ConfigurationCategoryDisplay getConfigurationCategoryDisplay() {
		return _configurationCategoryDisplay;
	}

	public Collection<ConfigurationScopeDisplay>
		getConfigurationScopeDisplays() {

		return Collections.singletonList(_configurationScopeDisplay);
	}

	public ConfigurationEntry getFirstConfigurationEntry() {
		List<ConfigurationEntry> configurationEntries =
			_configurationScopeDisplay.getConfigurationEntries();

		if (!configurationEntries.isEmpty()) {
			return configurationEntries.get(0);
		}

		return null;
	}

	public VerticalNavItemList getVerticalNavItemList(
		ConfigurationEntry configurationEntry,
		ConfigurationScopeDisplay configurationScopeDisplay,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		VerticalNavItemList verticalNavItemList = new VerticalNavItemList();

		for (ConfigurationEntry curConfigurationEntry :
				configurationScopeDisplay.getConfigurationEntries()) {

			verticalNavItemList.add(
				verticalNavItem -> {
					String name = curConfigurationEntry.getName();

					verticalNavItem.put(
						"deprecated", curConfigurationEntry.isDeprecated());

					String key = ConfigurationPidUtil.getRawPid(
						configurationEntry.getKey());

					verticalNavItem.setActive(
						key.equals(
							ConfigurationPidUtil.getRawPid(
								curConfigurationEntry.getKey())));

					verticalNavItem.setHref(
						curConfigurationEntry.getEditURL(
							renderRequest, renderResponse));
					verticalNavItem.setId(name);
					verticalNavItem.setLabel(name);
				});
		}

		return verticalNavItemList;
	}

	public boolean isEmpty() {
		return _configurationScopeDisplay.isEmpty();
	}

	private final ConfigurationCategoryDisplay _configurationCategoryDisplay;
	private final ConfigurationScopeDisplay _configurationScopeDisplay;

}