/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.display;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jorge Ferrer
 */
public class ConfigurationCategoryMenuDisplay {

	public ConfigurationCategoryMenuDisplay(
		ConfigurationCategoryDisplay configurationCategoryDisplay,
		Set<ConfigurationEntry> configurationEntries) {

		_configurationCategoryDisplay = configurationCategoryDisplay;

		for (ConfigurationEntry configurationEntry : configurationEntries) {
			_addConfigurationEntry(configurationEntry);
		}
	}

	public ConfigurationCategoryDisplay getConfigurationCategoryDisplay() {
		return _configurationCategoryDisplay;
	}

	public Collection<ConfigurationScopeDisplay>
		getConfigurationScopeDisplays() {

		return _configurationScopeDisplays.values();
	}

	public ConfigurationEntry getFirstConfigurationEntry() {
		for (ConfigurationScopeDisplay configurationScopeDisplay :
				_configurationScopeDisplays.values()) {

			List<ConfigurationEntry> configurationEntries =
				configurationScopeDisplay.getConfigurationEntries();

			if (!configurationEntries.isEmpty()) {
				return configurationEntries.get(0);
			}
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
					verticalNavItem.setActive(
						configurationEntry.equals(curConfigurationEntry));
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
		for (ConfigurationScopeDisplay configurationScopeDisplay :
				_configurationScopeDisplays.values()) {

			if (!configurationScopeDisplay.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	private void _addConfigurationEntry(ConfigurationEntry configurationEntry) {
		ConfigurationScopeDisplay configurationScopeDisplay =
			_configurationScopeDisplays.get(configurationEntry.getScope());

		if (configurationScopeDisplay == null) {
			configurationScopeDisplay = new ConfigurationScopeDisplay(
				configurationEntry.getScope());

			_configurationScopeDisplays.put(
				configurationEntry.getScope(), configurationScopeDisplay);
		}

		configurationScopeDisplay.add(configurationEntry);
	}

	private static final String[] _UI_ORDERED_SCOPES = {
		ExtendedObjectClassDefinition.Scope.SYSTEM.toString(),
		ExtendedObjectClassDefinition.Scope.COMPANY.toString(),
		ExtendedObjectClassDefinition.Scope.GROUP.toString(),
		ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.toString()
	};

	private final ConfigurationCategoryDisplay _configurationCategoryDisplay;
	private final Map<String, ConfigurationScopeDisplay>
		_configurationScopeDisplays = LinkedHashMapBuilder.put(
			Arrays.asList(_UI_ORDERED_SCOPES), ConfigurationScopeDisplay::new
		).build();

}