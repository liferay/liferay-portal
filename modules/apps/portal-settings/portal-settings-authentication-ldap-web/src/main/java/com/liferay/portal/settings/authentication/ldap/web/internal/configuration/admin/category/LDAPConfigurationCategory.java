/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.configuration.admin.category;

import com.liferay.configuration.admin.category.ConfigurationCategory;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.settings.authentication.ldap.web.internal.configuration.admin.display.ExportLDAPPortalSettingsConfigurationScreenWrapper;
import com.liferay.portal.settings.authentication.ldap.web.internal.configuration.admin.display.GeneralLDAPPortalSettingsConfigurationScreenWrapper;
import com.liferay.portal.settings.authentication.ldap.web.internal.configuration.admin.display.ImportLDAPPortalSettingsConfigurationScreenWrapper;
import com.liferay.portal.settings.authentication.ldap.web.internal.configuration.admin.display.ServersLDAPPortalSettingsConfigurationScreenWrapper;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = ConfigurationCategory.class)
public class LDAPConfigurationCategory implements ConfigurationCategory {

	@Override
	public String getCategoryIcon() {
		return "diary";
	}

	@Override
	public String getCategoryKey() {
		return "ldap";
	}

	@Override
	public String getCategorySection() {
		return "security";
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_registerLDAPConfigurationScreen(
			bundleContext,
			scope -> new ExportLDAPPortalSettingsConfigurationScreenWrapper(
				_portalSettingsConfigurationScreenFactory, scope,
				_servletContext));
		_registerLDAPConfigurationScreen(
			bundleContext,
			scope -> new GeneralLDAPPortalSettingsConfigurationScreenWrapper(
				_portalSettingsConfigurationScreenFactory, scope,
				_servletContext));
		_registerLDAPConfigurationScreen(
			bundleContext,
			scope -> new ImportLDAPPortalSettingsConfigurationScreenWrapper(
				_portalSettingsConfigurationScreenFactory, scope,
				_servletContext));
		_registerLDAPConfigurationScreen(
			bundleContext,
			scope -> new ServersLDAPPortalSettingsConfigurationScreenWrapper(
				_portalSettingsConfigurationScreenFactory, scope,
				_servletContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);

		_serviceRegistrations.clear();
	}

	private void _registerLDAPConfigurationScreen(
		BundleContext bundleContext,
		Function<ExtendedObjectClassDefinition.Scope, ConfigurationScreen>
			function) {

		_serviceRegistrations.add(
			bundleContext.registerService(
				ConfigurationScreen.class,
				function.apply(ExtendedObjectClassDefinition.Scope.SYSTEM),
				new HashMapDictionary<>()));
		_serviceRegistrations.add(
			bundleContext.registerService(
				ConfigurationScreen.class,
				function.apply(ExtendedObjectClassDefinition.Scope.COMPANY),
				new HashMapDictionary<>()));
	}

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	private final List<ServiceRegistration<ConfigurationScreen>>
		_serviceRegistrations = new ArrayList<>();

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.settings.authentication.ldap.web)"
	)
	private ServletContext _servletContext;

}