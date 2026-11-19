/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;

/**
 * @author Christian Moura
 */
public class AuditConfigurationDisplayContext {

	public AuditConfigurationDisplayContext(
		AuditConfiguration auditConfiguration, boolean systemScope) {

		_auditConfiguration = auditConfiguration;
		_systemScope = systemScope;
	}

	public String getEnabledHelpMessage() {
		return _getHelpMessage("enabled");
	}

	public String getPseudonymizationEnabledHelpMessage() {
		return _getHelpMessage("pseudonymizationEnabled");
	}

	public boolean isEnabled() {
		return _auditConfiguration.enabled();
	}

	public boolean isEnabledOverridden() {
		return _isOverridden("enabled");
	}

	public boolean isPseudonymizationEnabled() {
		return _auditConfiguration.pseudonymizationEnabled();
	}

	public boolean isPseudonymizationEnabledOverridden() {
		return _isOverridden("pseudonymizationEnabled");
	}

	public boolean isSystemScope() {
		return _systemScope;
	}

	private String _getHelpMessage(String key) {
		return AuditConfigurationOverrideUtil.getHelpMessage(
			AuditConfiguration.class, key);
	}

	private boolean _isOverridden(String key) {
		return AuditConfigurationOverrideUtil.isOverridden(
			AuditConfiguration.class, key);
	}

	private final AuditConfiguration _auditConfiguration;
	private final boolean _systemScope;

}