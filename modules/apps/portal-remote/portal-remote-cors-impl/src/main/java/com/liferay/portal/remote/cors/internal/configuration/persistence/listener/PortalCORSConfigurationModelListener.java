/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.remote.cors.configuration.PortalCORSConfiguration;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Arthur Chan
 * @author Carlos Sierra Andrés
 */
public class PortalCORSConfigurationModelListener
	implements ConfigurationModelListener {

	public PortalCORSConfigurationModelListener(
		Map<String, Dictionary<String, ?>> configurationPidsProperties) {

		_configurationPidsProperties = configurationPidsProperties;
	}

	@Override
	public void onBeforeSave(
			String pid, Dictionary<String, Object> newProperties)
		throws ConfigurationModelListenerException {

		Set<String> urlPatterns = new HashSet<>();

		PortalCORSConfiguration portalCORSConfiguration =
			ConfigurableUtil.createConfigurable(
				PortalCORSConfiguration.class, newProperties);

		for (String urlPattern :
				portalCORSConfiguration.filterMappingURLPatterns()) {

			if (urlPatterns.contains(urlPattern)) {
				throw new ConfigurationModelListenerException(
					"Duplicate URL pattern " + urlPattern,
					PortalCORSConfiguration.class,
					PortalCORSConfigurationModelListener.class, newProperties);
			}

			urlPatterns.add(urlPattern);
		}

		Set<String> duplicateURLPatterns = new HashSet<>();

		for (Map.Entry<String, Dictionary<String, ?>> entry :
				_configurationPidsProperties.entrySet()) {

			if (StringUtil.equals(pid, entry.getKey())) {
				continue;
			}

			Dictionary<String, ?> properties = entry.getValue();

			if (!Objects.equals(
					GetterUtil.getLong(newProperties.get("companyId")),
					GetterUtil.getLong(properties.get("companyId")))) {

				continue;
			}

			portalCORSConfiguration = ConfigurableUtil.createConfigurable(
				PortalCORSConfiguration.class, properties);

			for (String urlPattern :
					portalCORSConfiguration.filterMappingURLPatterns()) {

				if (urlPatterns.add(urlPattern)) {
					continue;
				}

				duplicateURLPatterns.add(urlPattern);
			}
		}

		if (!duplicateURLPatterns.isEmpty()) {
			throw new ConfigurationModelListenerException(
				"Duplicate URL patterns " + duplicateURLPatterns,
				PortalCORSConfiguration.class,
				PortalCORSConfigurationModelListener.class, newProperties);
		}
	}

	private final Map<String, Dictionary<String, ?>>
		_configurationPidsProperties;

}