/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.plugin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.plugin.PluginPackageNameAndContextComparator;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jorge Ferrer
 */
public class LocalPluginPackageRepository {

	public void addPluginPackage(PluginPackage pluginPackage) {
		if (pluginPackage.getContext() == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Plugin package cannot be registered because it does not " +
						"have an installation context");
			}

			return;
		}

		_pluginPackages.remove(pluginPackage.getContext());
		_pluginPackages.put(pluginPackage.getContext(), pluginPackage);
	}

	public PluginPackage getPluginPackage(String context) {
		return _pluginPackages.get(context);
	}

	public List<PluginPackage> getSortedPluginPackages() {
		List<PluginPackage> pluginPackages = new ArrayList<>();

		pluginPackages.addAll(_pluginPackages.values());

		return ListUtil.sort(
			pluginPackages, new PluginPackageNameAndContextComparator());
	}

	public void registerPluginPackageInstallation(PluginPackage pluginPackage) {
		if (pluginPackage.getContext() != null) {
			PluginPackage previousPluginPackage = _pluginPackages.get(
				pluginPackage.getContext());

			if (previousPluginPackage == null) {
				addPluginPackage(pluginPackage);
			}
		}
	}

	public void removePluginPackage(PluginPackage pluginPackage) {
		_pluginPackages.remove(pluginPackage.getContext());
	}

	public void unregisterPluginPackageInstallation(String context) {
		_pluginPackages.remove(context);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LocalPluginPackageRepository.class);

	private final Map<String, PluginPackage> _pluginPackages = new HashMap<>();

}