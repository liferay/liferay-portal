/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorCompanyConfiguration;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;

/**
 * @author Lucas Miranda
 */
public class PersistentAuditMessageProcessorCompanyConfigurationDisplayContext {

	public PersistentAuditMessageProcessorCompanyConfigurationDisplayContext(
		PersistentAuditMessageProcessorCompanyConfiguration
			persistentAuditMessageProcessorCompanyConfiguration,
		PersistentAuditMessageProcessorConfiguration
			persistentAuditMessageProcessorConfiguration) {

		_persistentAuditMessageProcessorCompanyConfiguration =
			persistentAuditMessageProcessorCompanyConfiguration;
		_persistentAuditMessageProcessorConfiguration =
			persistentAuditMessageProcessorConfiguration;
	}

	public String getEnabledHelpMessage() {
		if (!_isEnabledOverridden() &&
			_isPersistentAuditMessageProcessorDisabled()) {

			return "the-database-processor-is-disabled-in-system-settings";
		}

		return AuditConfigurationOverrideUtil.getHelpMessage(
			PersistentAuditMessageProcessorCompanyConfiguration.class,
			"enabled");
	}

	public boolean isEnabled() {
		return _persistentAuditMessageProcessorCompanyConfiguration.enabled();
	}

	public boolean isEnabledInputDisabled() {
		if (_isEnabledOverridden() ||
			_isPersistentAuditMessageProcessorDisabled()) {

			return true;
		}

		return false;
	}

	private boolean _isEnabledOverridden() {
		return AuditConfigurationOverrideUtil.isOverridden(
			PersistentAuditMessageProcessorCompanyConfiguration.class,
			"enabled");
	}

	private boolean _isPersistentAuditMessageProcessorDisabled() {
		return !_persistentAuditMessageProcessorConfiguration.enabled();
	}

	private final PersistentAuditMessageProcessorCompanyConfiguration
		_persistentAuditMessageProcessorCompanyConfiguration;
	private final PersistentAuditMessageProcessorConfiguration
		_persistentAuditMessageProcessorConfiguration;

}