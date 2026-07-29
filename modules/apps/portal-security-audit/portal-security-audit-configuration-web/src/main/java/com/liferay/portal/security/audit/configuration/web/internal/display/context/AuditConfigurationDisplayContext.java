/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.petra.string.StringPool;
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
		return _getOverriddenHelpMessage(
			isAuditMessageMaxQueueSizeOverridden());
	}

	public String getEnabledHelpMessage() {
		return _getOverriddenHelpMessage(isEnabledOverridden());
	}

	public boolean isAuditMessageMaxQueueSizeOverridden() {
		return AuditConfigurationOverrideUtil.isOverridden(
			"auditMessageMaxQueueSize");
	}

	public boolean isAuditMessageMaxQueueSizeVisible() {
		return _auditMessageMaxQueueSizeVisible;
	}

	public boolean isEnabled() {
		return _auditConfiguration.enabled();
	}

	public boolean isEnabledOverridden() {
		return AuditConfigurationOverrideUtil.isOverridden("enabled");
	}

	private String _getOverriddenHelpMessage(boolean overridden) {
		if (overridden) {
			return "this-field-has-been-set-by-a-portal-property-and-cannot-" +
				"be-changed-here";
		}

		return StringPool.BLANK;
	}

	private final AuditConfiguration _auditConfiguration;
	private final boolean _auditMessageMaxQueueSizeVisible;

}