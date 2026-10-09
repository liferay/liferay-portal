/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.scope.internal.spi.prefix.handler;

import com.liferay.oauth2.provider.scope.internal.configuration.BundlePrefixHandlerFactoryConfiguration;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandler;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * @author Carlos Sierra Andrés
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.scope.internal.configuration.BundlePrefixHandlerFactoryConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = PrefixHandlerFactory.class
)
public class PrefixHandlerFactoryImpl implements PrefixHandlerFactory {

	@Override
	public PrefixHandler create(
		Function<String, Object> propertyAccessorFunction) {

		List<String> strings = new ArrayList<>(_serviceProperties.size() + 1);

		if (_includeBundleSymbolicName) {
			Bundle bundle = _getBundle(propertyAccessorFunction);

			if (bundle != null) {
				strings.add(bundle.getSymbolicName());
			}
			else {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"bundle.id in " + _serviceProperties + " is not valid");
				}
			}
		}

		for (String serviceProperty : _serviceProperties) {
			Object value = propertyAccessorFunction.apply(serviceProperty);

			if (Validator.isNotNull(value)) {
				strings.add(value.toString());
			}
			else {
				String defaultValue = _defaults.get(serviceProperty);

				if (Validator.isNotNull(defaultValue)) {
					strings.add(defaultValue);
				}
			}
		}

		String prefix = StringUtil.merge(strings, _delimiter);

		return input -> {
			if (_excludedInputs.contains(input)) {
				return input;
			}

			return StringBundler.concat(prefix, _delimiter, input);
		};
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		BundlePrefixHandlerFactoryConfiguration
			bundlePrefixHandlerFactoryConfiguration =
				ConfigurableUtil.createConfigurable(
					BundlePrefixHandlerFactoryConfiguration.class, properties);

		_delimiter = bundlePrefixHandlerFactoryConfiguration.delimiter();

		List<String> excludedInputs = new ArrayList<>();

		for (String excludedScope :
				bundlePrefixHandlerFactoryConfiguration.excludedScopes()) {

			if (Validator.isBlank(excludedScope)) {
				continue;
			}

			excludedInputs.add(excludedScope);
		}

		_excludedInputs = excludedInputs;

		_includeBundleSymbolicName =
			bundlePrefixHandlerFactoryConfiguration.includeBundleSymbolicName();

		for (String serviceProperty :
				bundlePrefixHandlerFactoryConfiguration.serviceProperties()) {

			_serviceProperties.add(_initializeServiceProperty(serviceProperty));
		}
	}

	private Bundle _getBundle(
		Function<String, Object> propertyAccessorFunction) {

		long bundleId = GetterUtil.getLong(
			propertyAccessorFunction.apply("original.service.bundleid"),
			GetterUtil.getLong(
				propertyAccessorFunction.apply("service.bundleid"), -1L));

		if (bundleId == -1) {
			return null;
		}

		return _bundleContext.getBundle(bundleId);
	}

	private String _initializeServiceProperty(String serviceProperty) {
		int indexOfSpace = serviceProperty.indexOf(StringPool.SPACE);

		if (indexOfSpace == -1) {
			return serviceProperty;
		}

		String defaultsKey = serviceProperty.substring(0, indexOfSpace);

		Properties modifiers = new Properties();

		try {
			modifiers.load(
				new StringReader(serviceProperty.substring(indexOfSpace)));
		}
		catch (IOException ioException) {
			throw new IllegalArgumentException(ioException);
		}

		_defaults.put(
			defaultsKey,
			GetterUtil.getString(modifiers.getProperty("default")));

		return defaultsKey;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PrefixHandlerFactoryImpl.class);

	private BundleContext _bundleContext;
	private final Map<String, String> _defaults = new HashMap<>();
	private String _delimiter = StringPool.SLASH;
	private List<String> _excludedInputs;
	private boolean _includeBundleSymbolicName;
	private final Collection<String> _serviceProperties = new ArrayList<>();

}