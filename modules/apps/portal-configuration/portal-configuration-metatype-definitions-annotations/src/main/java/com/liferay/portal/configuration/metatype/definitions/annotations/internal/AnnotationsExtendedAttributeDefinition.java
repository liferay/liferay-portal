/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.metatype.definitions.annotations.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Method;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.service.metatype.AttributeDefinition;

/**
 * @author Iván Zaera
 */
public class AnnotationsExtendedAttributeDefinition
	implements com.liferay.portal.configuration.metatype.definitions.
				   ExtendedAttributeDefinition {

	public AnnotationsExtendedAttributeDefinition(
		Class<?> configurationBeanClass,
		AttributeDefinition attributeDefinition) {

		_configurationBeanClass = configurationBeanClass;
		_attributeDefinition = attributeDefinition;

		if (configurationBeanClass != null) {
			_processExtendedMetatypeFields();
		}
	}

	@Override
	public int getCardinality() {
		return _attributeDefinition.getCardinality();
	}

	@Override
	public String[] getDefaultValue() {
		return _attributeDefinition.getDefaultValue();
	}

	@Override
	public String getDescription() {
		return _attributeDefinition.getDescription();
	}

	@Override
	public Map<String, String> getExtensionAttributes(String uri) {
		Map<String, String> extensionAttributes = _extensionAttributes.get(uri);

		if (extensionAttributes == null) {
			extensionAttributes = Collections.emptyMap();
		}

		return extensionAttributes;
	}

	@Override
	public Set<String> getExtensionUris() {
		return _extensionAttributes.keySet();
	}

	@Override
	public String getID() {
		return _attributeDefinition.getID();
	}

	@Override
	public String getName() {
		return _attributeDefinition.getName();
	}

	@Override
	public String[] getOptionLabels() {
		return _attributeDefinition.getOptionLabels();
	}

	@Override
	public String[] getOptionValues() {
		return _attributeDefinition.getOptionValues();
	}

	@Override
	public int getType() {
		if (_configurationBeanClass != null) {
			try {
				Method method = _configurationBeanClass.getMethod(
					_attributeDefinition.getID());

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(LocalizedValuesMap.class)) {
					return LOCALIZED_VALUES_MAP;
				}
			}
			catch (NoSuchMethodException noSuchMethodException) {
				_handleNoSuchMethodException(noSuchMethodException);
			}
		}

		return _attributeDefinition.getType();
	}

	@Override
	public String validate(String value) {
		return _attributeDefinition.validate(value);
	}

	private void _handleNoSuchMethodException(
		NoSuchMethodException noSuchMethodException) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"The configuration bean class ",
					_configurationBeanClass.getName(),
					" does not have a method for the attribute definition ",
					_attributeDefinition.getID()),
				noSuchMethodException);
		}
	}

	private void _processExtendedMetatypeFields() {
		try {
			Method method = _configurationBeanClass.getMethod(
				_attributeDefinition.getID());

			ExtendedAttributeDefinition extendedAttributeDefinition =
				method.getAnnotation(ExtendedAttributeDefinition.class);

			if (extendedAttributeDefinition != null) {
				_extensionAttributes.put(
					ExtendedAttributeDefinition.XML_NAMESPACE,
					HashMapBuilder.put(
						"description-arguments",
						StringUtil.merge(
							extendedAttributeDefinition.descriptionArguments())
					).put(
						"feature.flag.key",
						extendedAttributeDefinition.featureFlagKey()
					).put(
						"name-arguments",
						StringUtil.merge(
							extendedAttributeDefinition.nameArguments())
					).put(
						"required-input",
						String.valueOf(
							extendedAttributeDefinition.requiredInput())
					).put(
						"visibility-controller-key",
						extendedAttributeDefinition.visibilityControllerKey()
					).build());
			}
		}
		catch (NoSuchMethodException noSuchMethodException) {
			_handleNoSuchMethodException(noSuchMethodException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnnotationsExtendedAttributeDefinition.class);

	private final AttributeDefinition _attributeDefinition;
	private final Class<?> _configurationBeanClass;
	private final Map<String, Map<String, String>> _extensionAttributes =
		new HashMap<>();

}