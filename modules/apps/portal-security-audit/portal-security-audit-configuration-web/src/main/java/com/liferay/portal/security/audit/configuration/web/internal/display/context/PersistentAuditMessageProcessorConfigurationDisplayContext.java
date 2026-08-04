/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;

/**
 * @author Christian Moura
 */
public class PersistentAuditMessageProcessorConfigurationDisplayContext {

	public PersistentAuditMessageProcessorConfigurationDisplayContext(
		PersistentAuditMessageProcessorConfiguration
			persistentAuditMessageProcessorConfiguration) {

		_persistentAuditMessageProcessorConfiguration =
			persistentAuditMessageProcessorConfiguration;
	}

	public int getBufferSize() {
		return _persistentAuditMessageProcessorConfiguration.bufferSize();
	}

	public String getBufferSizeHelpMessage() {
		return _getHelpMessage("bufferSize");
	}

	public String getEnabledHelpMessage() {
		return _getHelpMessage("enabled");
	}

	public long getFlushInterval() {
		return _persistentAuditMessageProcessorConfiguration.flushInterval();
	}

	public String getFlushIntervalHelpMessage() {
		return _getHelpMessage("flushInterval");
	}

	public boolean isBufferSizeOverridden() {
		return _isOverridden("bufferSize");
	}

	public boolean isEnabled() {
		return _persistentAuditMessageProcessorConfiguration.enabled();
	}

	public boolean isEnabledOverridden() {
		return _isOverridden("enabled");
	}

	public boolean isFlushIntervalOverridden() {
		return _isOverridden("flushInterval");
	}

	private String _getHelpMessage(String key) {
		return AuditConfigurationOverrideUtil.getHelpMessage(
			PersistentAuditMessageProcessorConfiguration.class, key);
	}

	private boolean _isOverridden(String key) {
		return AuditConfigurationOverrideUtil.isOverridden(
			PersistentAuditMessageProcessorConfiguration.class, key);
	}

	private final PersistentAuditMessageProcessorConfiguration
		_persistentAuditMessageProcessorConfiguration;

}