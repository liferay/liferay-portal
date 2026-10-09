/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.configuration.internal.util;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.configuration.admin.exportimport.ConfigurationExportImportProcessor;
import com.liferay.configuration.admin.util.ConfigurationPidUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.file.install.constants.FileInstallConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.settings.definition.ConfigurationPidMapping;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.validation.ValidationException;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Thiago Buarque
 */
public class ConfigurationUtil {

	public static Configuration addOrUpdateConfiguration(
			Long classPK, ConfigurationAdmin configurationAdmin,
			String externalReferenceCode, String filterString,
			Map<String, Object> propertiesMap,
			ExtendedObjectClassDefinition.Scope scope,
			SettingsLocatorHelper settingsLocatorHelper)
		throws Exception {

		ConfigurationPidMapping configurationPidMapping =
			settingsLocatorHelper.getConfigurationPidMapping(
				ConfigurationPidUtil.getRawPid(externalReferenceCode));

		if (configurationPidMapping == null) {
			return null;
		}

		Class<?> configurationBeanClass =
			configurationPidMapping.getConfigurationBeanClass();

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			configurationBeanClass.getAnnotation(
				ExtendedObjectClassDefinition.class);

		if (!_isSameOrParentScope(
				scope, extendedObjectClassDefinition.scope(),
				extendedObjectClassDefinition.strictScope())) {

			return null;
		}

		Meta.OCD ocd = configurationBeanClass.getAnnotation(Meta.OCD.class);

		Configuration configuration = null;

		if (scope.equals(ExtendedObjectClassDefinition.Scope.SYSTEM)) {
			configuration = _getOrAddSystemConfiguration(
				configurationAdmin, externalReferenceCode, ocd.factory(),
				filterString);
		}
		else {
			configuration = _getOrAddScopedConfiguration(
				configurationAdmin, externalReferenceCode, ocd.factory(),
				filterString);
		}

		if (configuration == null) {
			return null;
		}

		Dictionary<String, Object> properties = _createProperties(
			configurationBeanClass, propertiesMap);

		String propertyKey = scope.getPropertyKey();

		if (propertyKey != null) {
			properties.put(propertyKey, classPK);

			if (scope.equals(ExtendedObjectClassDefinition.Scope.GROUP)) {
				properties.put(
					ExtendedObjectClassDefinition.Scope.COMPANY.
						getPropertyKey(),
					CompanyThreadLocal.getCompanyId());
			}
		}

		configuration.update(properties);

		return configuration;
	}

	public static Map<String, Object> getProperties(
			Configuration configuration,
			ConfigurationExportImportProcessor
				configurationExportImportProcessor,
			ExtendedObjectClassDefinition.Scope scope,
			SettingsLocatorHelper settingsLocatorHelper)
		throws Exception {

		ConfigurationPidMapping configurationPidMapping =
			settingsLocatorHelper.getConfigurationPidMapping(
				ConfigurationPidUtil.getRawPid(configuration.getPid()));

		if (configurationPidMapping == null) {
			return Collections.emptyMap();
		}

		Dictionary<String, Object> properties = configuration.getProperties();

		properties.remove(ConfigurationAdmin.SERVICE_FACTORYPID);
		properties.remove(Constants.SERVICE_PID);
		properties.remove(FileInstallConstants.FELIX_FILE_INSTALL_FILENAME);

		if (!scope.equals(ExtendedObjectClassDefinition.Scope.SYSTEM)) {
			configurationExportImportProcessor.prepareForExport(
				ConfigurationPidUtil.getRawPid(configuration.getPid()),
				properties);
		}

		if (GetterUtil.getBoolean(
				PropsUtil.get(
					PropsKeys.MODULE_FRAMEWORK_EXPORT_PASSWORD_ATTRIBUTES))) {

			return HashMapBuilder.<String, Object>putAll(
				properties
			).build();
		}

		Class<?> configurationBeanClass =
			configurationPidMapping.getConfigurationBeanClass();

		for (Method method : configurationBeanClass.getMethods()) {
			Meta.AD ad = method.getAnnotation(Meta.AD.class);

			if ((ad == null) || (ad.type() != Meta.Type.Password)) {
				continue;
			}

			properties.remove(method.getName());
		}

		return HashMapBuilder.<String, Object>putAll(
			properties
		).build();
	}

	private static Dictionary<String, Object> _createProperties(
		Class<?> configurationBeanClass, Map<String, Object> propertiesMap) {

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		for (Method method : configurationBeanClass.getMethods()) {
			Meta.AD ad = method.getAnnotation(Meta.AD.class);

			String key = method.getName();

			if (!_isNull(ad.id())) {
				key = ad.id();
			}

			Object value = propertiesMap.get(key);

			if (ad.required() && (value == null)) {
				throw new ValidationException(
					"The property \"" + key + "\" is required");
			}
			else if (value == null) {
				continue;
			}

			Class<?> clazz = value.getClass();
			Class<?> returnType = method.getReturnType();

			if ((clazz.isArray() || List.class.isAssignableFrom(clazz)) !=
					returnType.isArray()) {

				throw new ValidationException(
					StringBundler.concat(
						"The attribute \"", key, "\" expects \"", returnType,
						" and \" not \"", clazz, "\""));
			}

			if (value instanceof List<?>) {
				List<?> valueList = (List<?>)value;

				value = valueList.toArray(
					(Object[])Array.newInstance(
						returnType.getComponentType(), 0));
			}

			properties.put(key, value);
		}

		return properties;
	}

	private static Configuration _createScopedConfiguration(
			ConfigurationAdmin configurationAdmin, String externalReferenceCode)
		throws Exception {

		return configurationAdmin.createFactoryConfiguration(
			ConfigurationPidUtil.getRawPid(externalReferenceCode) + ".scoped",
			StringPool.QUESTION);
	}

	private static Configuration _createSystemConfiguration(
			ConfigurationAdmin configurationAdmin, String externalReferenceCode)
		throws Exception {

		return configurationAdmin.getConfiguration(
			ConfigurationPidUtil.getRawPid(externalReferenceCode),
			StringPool.QUESTION);
	}

	private static Configuration _createSystemFactoryConfiguration(
			ConfigurationAdmin configurationAdmin, String externalReferenceCode)
		throws Exception {

		return configurationAdmin.createFactoryConfiguration(
			ConfigurationPidUtil.getRawPid(externalReferenceCode),
			StringPool.QUESTION);
	}

	private static Configuration _getOrAddScopedConfiguration(
			ConfigurationAdmin configurationAdmin, String externalReferenceCode,
			boolean factory, String filterString)
		throws Exception {

		Configuration[] configurations = configurationAdmin.listConfigurations(
			filterString);

		if (factory) {
			if (ArrayUtil.isEmpty(configurations)) {
				if (externalReferenceCode.contains("~")) {
					return null;
				}

				return _createScopedConfiguration(
					configurationAdmin, externalReferenceCode);
			}
			else if (externalReferenceCode.contains("~")) {
				return configurations[0];
			}

			return _createScopedConfiguration(
				configurationAdmin, externalReferenceCode);
		}

		if (externalReferenceCode.contains("~") &&
			ArrayUtil.isEmpty(configurations)) {

			return null;
		}

		if (ArrayUtil.isNotEmpty(configurations)) {
			return configurations[0];
		}

		return _createScopedConfiguration(
			configurationAdmin, externalReferenceCode);
	}

	private static Configuration _getOrAddSystemConfiguration(
			ConfigurationAdmin configurationAdmin, String externalReferenceCode,
			boolean factory, String filterString)
		throws Exception {

		if (!factory && externalReferenceCode.contains("~")) {
			return null;
		}

		Configuration[] configurations = configurationAdmin.listConfigurations(
			filterString);

		if (factory) {
			if (ArrayUtil.isEmpty(configurations)) {
				if (externalReferenceCode.contains("~")) {
					return null;
				}

				return _createSystemFactoryConfiguration(
					configurationAdmin, externalReferenceCode);
			}
			else if (externalReferenceCode.contains("~")) {
				return configurations[0];
			}

			return _createSystemFactoryConfiguration(
				configurationAdmin, externalReferenceCode);
		}

		if (ArrayUtil.isNotEmpty(configurations)) {
			return configurations[0];
		}

		return _createSystemConfiguration(
			configurationAdmin, externalReferenceCode);
	}

	private static boolean _isNull(String value) {
		if (Objects.equals(Meta.NULL, value) || Validator.isNull(value)) {
			return true;
		}

		return false;
	}

	private static boolean _isSameOrParentScope(
		ExtendedObjectClassDefinition.Scope actualScope,
		ExtendedObjectClassDefinition.Scope expectedScope, boolean strict) {

		if (strict) {
			return actualScope.equals(expectedScope);
		}

		if (actualScope.equals(expectedScope)) {
			return true;
		}

		if (expectedScope.equals(ExtendedObjectClassDefinition.Scope.GROUP)) {
			if (actualScope.equals(
					ExtendedObjectClassDefinition.Scope.COMPANY) ||
				actualScope.equals(
					ExtendedObjectClassDefinition.Scope.SYSTEM)) {

				return true;
			}

			return false;
		}
		else if (expectedScope.equals(
					ExtendedObjectClassDefinition.Scope.COMPANY)) {

			return actualScope.equals(
				ExtendedObjectClassDefinition.Scope.SYSTEM);
		}

		return false;
	}

}