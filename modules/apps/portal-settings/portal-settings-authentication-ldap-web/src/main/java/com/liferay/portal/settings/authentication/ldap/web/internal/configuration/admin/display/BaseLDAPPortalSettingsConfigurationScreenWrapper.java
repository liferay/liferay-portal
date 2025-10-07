/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;

/**
 * @author Rafael Praxedes
 */
public abstract class BaseLDAPPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	public BaseLDAPPortalSettingsConfigurationScreenWrapper(
		PortalSettingsConfigurationScreenFactory
			portalSettingsConfigurationScreenFactory,
		ExtendedObjectClassDefinition.Scope scope) {

		_portalSettingsConfigurationScreenFactory =
			portalSettingsConfigurationScreenFactory;
		_scope = scope;
	}

	@Override
	public String getScope() {
		return _scope.getValue();
	}

	@Override
	public boolean isVisible() {
		if (_scope.equals(ExtendedObjectClassDefinition.Scope.SYSTEM)) {
			return FeatureFlagManagerUtil.isEnabled("LPD-45613");
		}

		return super.isVisible();
	}

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			getPortalSettingsConfigurationScreenContributor());
	}

	protected abstract PortalSettingsConfigurationScreenContributor
		getPortalSettingsConfigurationScreenContributor();

	private final PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;
	private final ExtendedObjectClassDefinition.Scope _scope;

}