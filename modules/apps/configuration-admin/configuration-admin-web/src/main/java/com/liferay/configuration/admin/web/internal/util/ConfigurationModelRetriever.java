/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import java.io.IOException;
import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.service.cm.Configuration;

/**
 * @author Michael C. Han
 */
public interface ConfigurationModelRetriever {

	public Map<String, Set<ConfigurationModel>> categorizeConfigurationModels(
		Map<String, ConfigurationModel> configurationModels);

	public Configuration getConfiguration(
		String pid, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK);

	public Configuration getConfiguration(
		String pid, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK, boolean strictScope);

	public Map<String, ConfigurationModel> getConfigurationModels(
		Bundle bundle, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK);

	public Map<String, ConfigurationModel> getConfigurationModels(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK);

	public Map<String, ConfigurationModel> getConfigurationModels(
		String locale, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK);

	public Set<ConfigurationModel> getConfigurationModels(
		String configurationCategory, String languageId,
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK);

	public List<ConfigurationModel> getFactoryInstances(
			ConfigurationModel factoryConfigurationModel,
			ExtendedObjectClassDefinition.Scope scope, Serializable scopePK)
		throws IOException;

}