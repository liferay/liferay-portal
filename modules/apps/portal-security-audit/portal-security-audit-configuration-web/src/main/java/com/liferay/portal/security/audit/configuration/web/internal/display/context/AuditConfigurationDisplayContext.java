/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;

/**
 * @author Christian Moura
 */
public class AuditConfigurationDisplayContext {

	public AuditConfigurationDisplayContext(
		AuditConfiguration auditConfiguration,
		PersistentAuditMessageProcessorConfiguration
			persistentAuditMessageProcessorConfiguration) {

		_auditConfiguration = auditConfiguration;
		_persistentAuditMessageProcessorConfiguration =
			persistentAuditMessageProcessorConfiguration;
	}

	public String getEnabledHelpMessage() {
		return _getHelpMessage(AuditConfiguration.class, "enabled");
	}

	public int getPersistentAuditMessageProcessorBufferSize() {
		return _persistentAuditMessageProcessorConfiguration.bufferSize();
	}

	public String getPersistentAuditMessageProcessorBufferSizeHelpMessage() {
		return _getHelpMessage(
			PersistentAuditMessageProcessorConfiguration.class, "bufferSize");
	}

	public String getPersistentAuditMessageProcessorEnabledHelpMessage() {
		return _getHelpMessage(
			PersistentAuditMessageProcessorConfiguration.class, "enabled");
	}

	public long getPersistentAuditMessageProcessorFlushInterval() {
		return _persistentAuditMessageProcessorConfiguration.flushInterval();
	}

	public String getPersistentAuditMessageProcessorFlushIntervalHelpMessage() {
		return _getHelpMessage(
			PersistentAuditMessageProcessorConfiguration.class,
			"flushInterval");
	}

	public boolean isEnabled() {
		return _auditConfiguration.enabled();
	}

	public boolean isEnabledOverridden() {
		return _isOverridden(AuditConfiguration.class, "enabled");
	}

	public boolean isPersistentAuditMessageProcessorBufferSizeOverridden() {
		return _isOverridden(
			PersistentAuditMessageProcessorConfiguration.class, "bufferSize");
	}

	public boolean isPersistentAuditMessageProcessorEnabled() {
		return _persistentAuditMessageProcessorConfiguration.enabled();
	}

	public boolean isPersistentAuditMessageProcessorEnabledOverridden() {
		return _isOverridden(
			PersistentAuditMessageProcessorConfiguration.class, "enabled");
	}

	public boolean isPersistentAuditMessageProcessorFlushIntervalOverridden() {
		return _isOverridden(
			PersistentAuditMessageProcessorConfiguration.class,
			"flushInterval");
	}

	private String _getHelpMessage(Class<?> clazz, String key) {
		return AuditConfigurationOverrideUtil.getHelpMessage(clazz, key);
	}

	private boolean _isOverridden(Class<?> clazz, String key) {
		return AuditConfigurationOverrideUtil.isOverridden(clazz, key);
	}

	private final AuditConfiguration _auditConfiguration;
	private final PersistentAuditMessageProcessorConfiguration
		_persistentAuditMessageProcessorConfiguration;

}