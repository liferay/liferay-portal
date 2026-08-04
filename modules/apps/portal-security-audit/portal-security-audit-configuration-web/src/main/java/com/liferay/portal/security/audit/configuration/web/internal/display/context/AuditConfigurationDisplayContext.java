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
		AuditConfiguration auditConfiguration,
		boolean auditMessageMaxQueueSizeVisible) {

		_auditConfiguration = auditConfiguration;
		_auditMessageMaxQueueSizeVisible = auditMessageMaxQueueSizeVisible;
	}

	@SuppressWarnings("deprecation")
	public int getAuditMessageMaxQueueSize() {
		return _auditConfiguration.auditMessageMaxQueueSize();
	}

	public String getAuditMessageMaxQueueSizeHelpMessage() {
		return _getHelpMessage("auditMessageMaxQueueSize");
	}

	public String getEnabledHelpMessage() {
		return _getHelpMessage("enabled");
	}

	public boolean isAuditMessageMaxQueueSizeOverridden() {
		return _isOverridden("auditMessageMaxQueueSize");
	}

	public boolean isAuditMessageMaxQueueSizeVisible() {
		return _auditMessageMaxQueueSizeVisible;
	}

	public boolean isEnabled() {
		return _auditConfiguration.enabled();
	}

	public boolean isEnabledOverridden() {
		return _isOverridden("enabled");
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
	private final boolean _auditMessageMaxQueueSizeVisible;

}