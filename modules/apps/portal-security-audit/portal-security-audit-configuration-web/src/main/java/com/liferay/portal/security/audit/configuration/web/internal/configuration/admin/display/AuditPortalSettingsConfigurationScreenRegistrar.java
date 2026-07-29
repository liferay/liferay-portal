/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

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
 * @author Christian Moura
 */
@Component(service = {})
public class AuditPortalSettingsConfigurationScreenRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistrations.add(
			bundleContext.registerService(
				ConfigurationScreen.class,
				new AuditPortalSettingsConfigurationScreenWrapper(
					_configurationProvider, _language,
					_portalSettingsConfigurationScreenFactory,
					ExtendedObjectClassDefinition.Scope.COMPANY,
					_servletContext),
				new HashMapDictionary<>()));
		_serviceRegistrations.add(
			bundleContext.registerService(
				ConfigurationScreen.class,
				new AuditPortalSettingsConfigurationScreenWrapper(
					_configurationProvider, _language,
					_portalSettingsConfigurationScreenFactory,
					ExtendedObjectClassDefinition.Scope.SYSTEM,
					_servletContext),
				new HashMapDictionary<>()));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);

		_serviceRegistrations.clear();
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	private final List<ServiceRegistration<ConfigurationScreen>>
		_serviceRegistrations = new ArrayList<>();

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.security.audit.configuration.web)"
	)
	private ServletContext _servletContext;

}