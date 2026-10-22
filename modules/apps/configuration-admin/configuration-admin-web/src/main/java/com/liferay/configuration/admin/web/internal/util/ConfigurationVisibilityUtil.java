/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.configuration.admin.display.ConfigurationVisibilityController;
import com.liferay.configuration.admin.util.ConfigurationPidUtil;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.settings.SettingsLocatorHelperUtil;
import com.liferay.portal.kernel.settings.definition.ConfigurationPidMapping;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Drew Brokke
 */
public class ConfigurationVisibilityUtil {

	public static boolean isVisible(
		ConfigurationModel configurationModel,
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		if (configurationModel.isStrictScope() &&
			!scope.equals(configurationModel.getScope())) {

			return false;
		}

		if (!isVisibleByFeatureFlagKey(
				configurationModel.getFeatureFlagKey(), scope, scopePK) ||
			!isVisibleByVisibilityControllerKey(
				configurationModel.getVisibilityControllerKey(), scope,
				scopePK)) {

			return false;
		}

		return true;
	}

	public static boolean isVisible(
		String pid, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK) {

		String rawPid = ConfigurationPidUtil.getRawPid(pid);

		Class<?> clazz = _getClass(rawPid);

		if (!isVisibleByFeatureFlagKey(
				_getFeatureFlagKey(clazz), scope, scopePK) ||
			!isVisibleByVisibilityControllerKey(
				_getVisibilityControllerKey(clazz, rawPid), scope, scopePK)) {

			return false;
		}

		return true;
	}

	public static boolean isVisibleByFeatureFlagKey(
		String featureFlagKey, ExtendedObjectClassDefinition.Scope scope,
		Serializable scopePK) {

		if (Validator.isBlank(featureFlagKey)) {
			return true;
		}

		if (scope.equals(ExtendedObjectClassDefinition.Scope.COMPANY)) {
			return FeatureFlagManagerUtil.isEnabled(
				(long)scopePK, featureFlagKey);
		}

		if (scope.equals(ExtendedObjectClassDefinition.Scope.GROUP)) {
			Group group = GroupLocalServiceUtil.fetchGroup((long)scopePK);

			if (group != null) {
				return FeatureFlagManagerUtil.isEnabled(
					group.getCompanyId(), featureFlagKey);
			}
		}

		return FeatureFlagManagerUtil.isEnabled(featureFlagKey);
	}

	public static boolean isVisibleByVisibilityControllerKey(
		String visibilityControllerKey,
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		if (Validator.isNull(visibilityControllerKey)) {
			return true;
		}

		ConfigurationVisibilityController configurationVisibilityController =
			_serviceTrackerMap.getService(visibilityControllerKey);

		if (configurationVisibilityController == null) {
			return true;
		}

		return configurationVisibilityController.isVisible(scope, scopePK);
	}

	private static Class<?> _getClass(String pid) {
		SettingsLocatorHelper settingsLocatorHelper =
			SettingsLocatorHelperUtil.getSettingsLocatorHelper();

		ConfigurationPidMapping configurationPidMapping =
			settingsLocatorHelper.getConfigurationPidMapping(pid);

		if (configurationPidMapping == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No class was found for PID " + pid);
			}

			return null;
		}

		return configurationPidMapping.getConfigurationBeanClass();
	}

	private static String _getFeatureFlagKey(Class<?> clazz) {
		if (clazz == null) {
			return StringPool.BLANK;
		}

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			clazz.getAnnotation(ExtendedObjectClassDefinition.class);

		if (extendedObjectClassDefinition == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Class " + clazz.getName() +
						" is missing ExtendedObjectClassDefinition");
			}

			return StringPool.BLANK;
		}

		String featureFlagKey = extendedObjectClassDefinition.featureFlagKey();

		if (Validator.isBlank(featureFlagKey)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Class " + clazz.getName() + " is missing featureFlagKey");
			}

			return StringPool.BLANK;
		}

		return featureFlagKey;
	}

	private static String _getVisibilityControllerKey(
		Class<?> clazz, String defaultValue) {

		if (clazz == null) {
			return defaultValue;
		}

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			clazz.getAnnotation(ExtendedObjectClassDefinition.class);

		if (extendedObjectClassDefinition == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Class " + clazz.getName() +
						" is missing ExtendedObjectClassDefinition");
			}

			return defaultValue;
		}

		String visibilityControllerKey =
			extendedObjectClassDefinition.visibilityControllerKey();

		if (Validator.isBlank(visibilityControllerKey)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Class " + clazz.getName() +
						" is missing visibilityControllerKey");
			}

			return defaultValue;
		}

		return visibilityControllerKey;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationVisibilityUtil.class);

	private static final ServiceTrackerMap
		<String, ConfigurationVisibilityController> _serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			ConfigurationVisibilityController.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceReferenceMapper<String, ConfigurationVisibilityController>
			configurationPidPropertyMapper =
				new PropertyServiceReferenceMapper<>("configuration.pid");
		ServiceReferenceMapper<String, ConfigurationVisibilityController>
			visibilityControllerKeyPropertyMapper =
				new PropertyServiceReferenceMapper<>(
					"visibility.controller.key");

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ConfigurationVisibilityController.class, null,
			(serviceReference, emitter) -> {
				configurationPidPropertyMapper.map(serviceReference, emitter);
				visibilityControllerKeyPropertyMapper.map(
					serviceReference, emitter);

				ConfigurationVisibilityController service =
					bundleContext.getService(serviceReference);

				String key = service.getKey();

				if (!Validator.isBlank(key)) {
					emitter.emit(key);
				}
			});
	}

}