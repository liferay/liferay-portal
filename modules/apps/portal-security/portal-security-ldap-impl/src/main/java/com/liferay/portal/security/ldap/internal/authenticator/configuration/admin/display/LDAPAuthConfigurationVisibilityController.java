/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.authenticator.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationVisibilityController;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;

/**
 * @author Thiago Buarque
 */
@Component(service = ConfigurationVisibilityController.class)
public class LDAPAuthConfigurationVisibilityController
	implements ConfigurationVisibilityController {

	@Override
	public String getKey() {
		return "ldap-auth";
	}

	@Override
	public boolean isVisible(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		if (scope.equals(ExtendedObjectClassDefinition.Scope.SYSTEM) &&
			!FeatureFlagManagerUtil.isEnabled("LPD-45613")) {

			return true;
		}

		return false;
	}

}