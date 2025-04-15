/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.configuration.admin.category;

import com.liferay.configuration.admin.category.ConfigurationCategory;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.feature.flag.web.internal.configuration.admin.display.FeatureFlagConfigurationScreen;
import com.liferay.feature.flag.web.internal.display.FeatureFlagsDisplayContextFactory;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.util.HashMapDictionary;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = ConfigurationCategory.class)
public class FeatureFlagConfigurationCategory implements ConfigurationCategory {

	public static final String CATEGORY_KEY = "feature-flags";

	@Override
	public String getCategoryIcon() {
		return "flag-full";
	}

	@Override
	public String getCategoryKey() {
		return CATEGORY_KEY;
	}

	@Override
	public String getCategorySection() {
		return "platform";
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		for (int i = 0; i < _orderedFeatureFlagTypes.length; i++) {
			FeatureFlagType featureFlagType = _orderedFeatureFlagTypes[i];

			_serviceRegistrations.add(
				bundleContext.registerService(
					ConfigurationScreen.class,
					new FeatureFlagConfigurationScreen(
						featureFlagType, _featureFlagsDisplayContextFactory, i,
						ExtendedObjectClassDefinition.Scope.SYSTEM.getValue(),
						_servletContext),
					new HashMapDictionary<>()));

			_serviceRegistrations.add(
				bundleContext.registerService(
					ConfigurationScreen.class,
					new FeatureFlagConfigurationScreen(
						featureFlagType, _featureFlagsDisplayContextFactory, i,
						ExtendedObjectClassDefinition.Scope.COMPANY.getValue(),
						_servletContext),
					new HashMapDictionary<>()));
		}
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);

		_serviceRegistrations.clear();
	}

	private static final FeatureFlagType[] _orderedFeatureFlagTypes = {
		FeatureFlagType.RELEASE, FeatureFlagType.BETA,
		FeatureFlagType.DEPRECATION, FeatureFlagType.DEV
	};

	@Reference
	private FeatureFlagsDisplayContextFactory
		_featureFlagsDisplayContextFactory;

	private final List<ServiceRegistration<ConfigurationScreen>>
		_serviceRegistrations = new ArrayList<>();

	@Reference(target = "(osgi.web.symbolicname=com.liferay.feature.flag.web)")
	private ServletContext _servletContext;

}