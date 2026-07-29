/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationVisibilityController;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;

/**
 * @author Christian Moura
 */
@Component(
	property = "configuration.pid=com.liferay.portal.security.audit.configuration.AuditConfiguration",
	service = ConfigurationVisibilityController.class
)
public class AuditConfigurationVisibilityController
	implements ConfigurationVisibilityController {

	@Override
	public boolean isVisible(
		ExtendedObjectClassDefinition.Scope scope, Serializable scopePK) {

		if (ExtendedObjectClassDefinition.Scope.COMPANY.equals(scope)) {
			return FeatureFlagManagerUtil.isEnabled((long)scopePK, "LPD-6417");
		}

		return true;
	}

}