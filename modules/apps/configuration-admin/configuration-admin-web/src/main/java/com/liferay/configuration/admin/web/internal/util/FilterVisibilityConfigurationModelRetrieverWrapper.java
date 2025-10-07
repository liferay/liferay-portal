/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import java.io.IOException;
import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.service.cm.Configuration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = "filter.visibility=true",
	service = ConfigurationModelRetriever.class
)
public class FilterVisibilityConfigurationModelRetrieverWrapper
	implements ConfigurationModelRetriever {

	@Override
	public Map<String, Set<ConfigurationModel>> categorizeConfigurationModels(
		Map<String, ConfigurationModel> configurationModels) {

		return _configurationModelRetriever.categorizeConfigurationModels(
			configurationModels);
	}

	@Override
	public Configuration getConfiguration(
		String pid, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK) {

		return getConfiguration(pid, scope, scopePK, true);
	}

	@Override
	public Configuration getConfiguration(
		String pid, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK, boolean strictScope) {

		if (!ConfigurationVisibilityUtil.isVisible(pid, scope, scopePK)) {
			return null;
		}

		return _configurationModelRetriever.getConfiguration(
			pid, scope, scopePK, strictScope);
	}

	@Override
	public Map<String, ConfigurationModel> getConfigurationModels(
		Bundle bundle, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK) {

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				bundle, scope, scopePK);

		_filterVisibility(scope, scopePK, configurationModels);

		return configurationModels;
	}

	@Override
	public Map<String, ConfigurationModel> getConfigurationModels(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(scope, scopePK);

		_filterVisibility(scope, scopePK, configurationModels);

		return configurationModels;
	}

	@Override
	public Map<String, ConfigurationModel> getConfigurationModels(
		String locale, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK) {

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				locale, scope, scopePK);

		_filterVisibility(scope, scopePK, configurationModels);

		return configurationModels;
	}

	@Override
	public Set<ConfigurationModel> getConfigurationModels(
		String configurationCategory, String languageId,
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		Set<ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				configurationCategory, languageId, scope, scopePK);

		configurationModels.removeIf(
			configurationModel -> !ConfigurationVisibilityUtil.isVisible(
				configurationModel, scope, scopePK));

		return configurationModels;
	}

	@Override
	public List<ConfigurationModel> getFactoryInstances(
			ConfigurationModel factoryConfigurationModel,
			ExtendedObjectClassDefinition.Scope scope, Serializable scopePK)
		throws IOException {

		if (ConfigurationVisibilityUtil.isVisible(
				factoryConfigurationModel, scope, scopePK)) {

			return _configurationModelRetriever.getFactoryInstances(
				factoryConfigurationModel, scope, scopePK);
		}

		return Collections.emptyList();
	}

	private void _filterVisibility(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK,
		Map<String, ConfigurationModel> configurationModelMap) {

		Set<Map.Entry<String, ConfigurationModel>> entries =
			configurationModelMap.entrySet();

		entries.removeIf(
			entry -> !ConfigurationVisibilityUtil.isVisible(
				entry.getValue(), scope, scopePK));
	}

	@Reference(target = "(!(filter.visibility=*))")
	private ConfigurationModelRetriever _configurationModelRetriever;

}