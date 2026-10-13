/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.wiring.internal.servlet.filter;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.configuration.AuditConfiguration",
	service = {}
)
public class AuditFilterGatekeeper {

	@Activate
	protected void activate(ComponentContext componentContext) {
		AuditConfiguration auditConfiguration =
			ConfigurableUtil.createConfigurable(
				AuditConfiguration.class, componentContext.getProperties());

		if (auditConfiguration.enabled()) {
			componentContext.enableComponent(AuditFilter.class.getName());
		}
		else {
			componentContext.disableComponent(AuditFilter.class.getName());
		}
	}

}