/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration.admin.service;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.internal.configuration.util.ConfigurationUtil;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(
	property = {
		Constants.SERVICE_PID + "=com.liferay.portal.vulcan.internal.configuration.VulcanCompanyConfiguration",
		Constants.SERVICE_PID + "=com.liferay.portal.vulcan.internal.configuration.VulcanCompanyConfiguration.scoped",
		Constants.SERVICE_PID + "=com.liferay.portal.vulcan.internal.configuration.VulcanConfiguration"
	},
	service = ManagedServiceFactory.class
)
public class VulcanConfigurationManagedServiceFactory
	implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		String path = _paths.remove(pid);

		if (path != null) {
			ConfigurationUtil.clearRESTEnabledCache(path);
		}
	}

	@Override
	public String getName() {
		return "com.liferay.portal.vulcan.internal.configuration." +
			"VulcanConfiguration";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> dictionary) {
		String path = GetterUtil.getString(dictionary.get("path"));

		String oldPath = _paths.put(pid, path);

		if ((oldPath != null) && !oldPath.equals(path)) {
			ConfigurationUtil.clearRESTEnabledCache(oldPath);
		}

		ConfigurationUtil.clearRESTEnabledCache(path);
	}

	private final Map<String, String> _paths = new ConcurrentHashMap<>();

}