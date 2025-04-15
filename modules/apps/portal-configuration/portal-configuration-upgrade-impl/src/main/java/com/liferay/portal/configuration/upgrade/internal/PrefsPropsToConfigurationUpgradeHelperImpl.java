/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.upgrade.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.upgrade.PrefsPropsToConfigurationUpgradeHelper;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Objects;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = PrefsPropsToConfigurationUpgradeHelper.class)
public class PrefsPropsToConfigurationUpgradeHelperImpl
	implements PrefsPropsToConfigurationUpgradeHelper {

	@Override
	public void mapConfigurations(
			Class<?> configurationClass, KeyValuePair... keyValuePairs)
		throws Exception {

		String filterString = StringBundler.concat(
			"(", Constants.SERVICE_PID, "=", configurationClass.getName(), ")");

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			filterString);

		if (configurations != null) {
			return;
		}

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		PortletPreferences portletPreferences = _prefsProps.getPreferences();

		Object defaultConfiguration = ConfigurableUtil.createConfigurable(
			configurationClass, properties);

		for (KeyValuePair keyValuePair : keyValuePairs) {
			String valueString = _prefsProps.getString(
				keyValuePair.getKey(), null);

			if (Validator.isNull(valueString)) {
				continue;
			}

			Method method = configurationClass.getMethod(
				keyValuePair.getValue());

			Object defaultValueObject = method.invoke(defaultConfiguration);

			Class<?> returnType = method.getReturnType();

			Object value = null;

			if (returnType == boolean.class) {
				value = GetterUtil.getBoolean(valueString);
			}
			else if (returnType == double.class) {
				value = GetterUtil.getDouble(valueString);
			}
			else if (returnType == int.class) {
				value = GetterUtil.getInteger(valueString);
			}
			else if (returnType == float.class) {
				value = GetterUtil.getFloat(valueString);
			}
			else if (returnType == long.class) {
				value = GetterUtil.getLong(valueString);
			}
			else if (returnType == short.class) {
				value = GetterUtil.getShort(valueString);
			}
			else if (returnType == String.class) {
				value = GetterUtil.getString(valueString);
			}
			else if (returnType == String[].class) {
				value = StringUtil.split(valueString);

				if (!Arrays.equals(
						(Object[])value, (Object[])defaultValueObject)) {

					_writeProperty(
						properties, portletPreferences, keyValuePair, value);
				}

				continue;
			}
			else {
				throw new IllegalArgumentException(
					"No valid return type found: " + method);
			}

			if (!Objects.equals(value, defaultValueObject)) {
				_writeProperty(
					properties, portletPreferences, keyValuePair, value);
			}
		}

		if (properties.isEmpty()) {
			return;
		}

		Configuration configuration = _configurationAdmin.getConfiguration(
			configurationClass.getName(), StringPool.QUESTION);

		configuration.update(properties);

		portletPreferences.store();
	}

	private void _writeProperty(
		Dictionary<String, Object> properties,
		PortletPreferences portletPreferences, KeyValuePair keyValuePair,
		Object value) {

		properties.put(keyValuePair.getValue(), value);

		if (!portletPreferences.isReadOnly(keyValuePair.getKey())) {
			try {
				portletPreferences.reset(keyValuePair.getKey());
			}
			catch (ReadOnlyException readOnlyException) {
				throw new RuntimeException(readOnlyException);
			}
		}
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private PrefsProps _prefsProps;

}