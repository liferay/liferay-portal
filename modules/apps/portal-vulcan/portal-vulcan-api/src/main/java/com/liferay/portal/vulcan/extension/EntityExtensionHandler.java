/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.extension;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.extension.validation.PropertyValidator;

import jakarta.validation.ValidationException;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Javier de Arcos
 */
public class EntityExtensionHandler {

	public EntityExtensionHandler(
		String className, List<ExtensionProvider> extensionProviders) {

		_className = className;
		_extensionProviders = extensionProviders;
	}

	public Map<String, Serializable> getExtendedProperties(
			long companyId, long userId, Object entity)
		throws Exception {

		Map<String, Serializable> extendedProperties = new HashMap<>();

		for (ExtensionProvider extensionProvider : _extensionProviders) {
			Map<String, Serializable> extensionProviderExtendedProperties =
				extensionProvider.getExtendedProperties(
					companyId, userId, _className, entity);

			if (extensionProviderExtendedProperties != null) {
				extendedProperties.putAll(extensionProviderExtendedProperties);
			}
		}

		return extendedProperties;
	}

	public Map<String, PropertyDefinition> getExtendedPropertyDefinitions(
			long companyId, String className)
		throws Exception {

		Map<String, PropertyDefinition> propertyDefinitions = new HashMap<>();

		for (ExtensionProvider extensionProvider : _extensionProviders) {
			Map<String, PropertyDefinition>
				extensionProviderExtendedProperties =
					extensionProvider.getExtendedPropertyDefinitions(
						companyId, className);

			if (extensionProviderExtendedProperties != null) {
				propertyDefinitions.putAll(extensionProviderExtendedProperties);
			}
		}

		return propertyDefinitions;
	}

	public Set<String> getFilteredPropertyNames(long companyId, Object entity) {
		Set<String> filteredPropertyNames = new HashSet<>();

		for (ExtensionProvider extensionProvider : _extensionProviders) {
			Collection<String> extensionProviderFilteredPropertyNames =
				extensionProvider.getFilteredPropertyNames(companyId, entity);

			if (extensionProviderFilteredPropertyNames != null) {
				filteredPropertyNames.addAll(
					extensionProviderFilteredPropertyNames);
			}
		}

		return filteredPropertyNames;
	}

	public void setExtendedProperties(
			long companyId, long userId, Object entity,
			Map<String, Serializable> extendedProperties)
		throws Exception {

		for (ExtensionProvider extensionProvider : _extensionProviders) {
			Map<String, PropertyDefinition> extendedPropertyDefinitions =
				extensionProvider.getExtendedPropertyDefinitions(
					companyId, _className);

			if (extendedPropertyDefinitions == null) {
				continue;
			}

			Map<String, Serializable> extensionProviderExtendedProperties =
				new HashMap<>();

			for (Map.Entry<String, Serializable> entry :
					extendedProperties.entrySet()) {

				if (extendedPropertyDefinitions.containsKey(entry.getKey())) {
					extensionProviderExtendedProperties.put(
						entry.getKey(), entry.getValue());
				}
			}

			extensionProvider.setExtendedProperties(
				companyId, userId, _className, entity,
				extensionProviderExtendedProperties);
		}
	}

	public void validate(
			long companyId, Map<String, Serializable> extendedProperties,
			boolean partialUpdate)
		throws Exception {

		Map<String, PropertyDefinition> propertyDefinitions = new HashMap<>();

		for (ExtensionProvider extensionProvider : _extensionProviders) {
			Map<String, PropertyDefinition>
				extensionProviderPropertyDefinitions =
					extensionProvider.getExtendedPropertyDefinitions(
						companyId, _className);

			if (extensionProviderPropertyDefinitions != null) {
				propertyDefinitions.putAll(
					extensionProviderPropertyDefinitions);
			}
		}

		List<String> unknownPropertyNames = new ArrayList<>();

		for (Map.Entry<String, Serializable> entry :
				extendedProperties.entrySet()) {

			String extendedPropertyName = entry.getKey();

			if (!propertyDefinitions.containsKey(extendedPropertyName)) {
				unknownPropertyNames.add(extendedPropertyName);

				continue;
			}

			PropertyDefinition propertyDefinition = propertyDefinitions.get(
				extendedPropertyName);

			PropertyValidator propertyValidator =
				propertyDefinition.getPropertyValidator();

			propertyValidator.validate(propertyDefinition, entry.getValue());

			propertyDefinitions.remove(extendedPropertyName);
		}

		if (ListUtil.isNotEmpty(unknownPropertyNames)) {
			throw new ValidationException(
				"The properties [" +
					ListUtil.toString(unknownPropertyNames, "") +
						"] are unknown");
		}

		if (partialUpdate) {
			return;
		}

		List<String> missingRequiredPropertyNames = new ArrayList<>();

		for (PropertyDefinition propertyDefinition :
				propertyDefinitions.values()) {

			if (propertyDefinition.isRequired()) {
				missingRequiredPropertyNames.add(
					propertyDefinition.getPropertyName());
			}
		}

		if (ListUtil.isNotEmpty(missingRequiredPropertyNames)) {
			throw new ValidationException(
				"The properties [" +
					ListUtil.toString(missingRequiredPropertyNames, "") +
						"] are required");
		}
	}

	private final String _className;
	private final List<ExtensionProvider> _extensionProviders;

}