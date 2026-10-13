/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.security.audit.router.configuration.FileSystemAuditMessageProcessorConfiguration;

/**
 * @author Christian Moura
 */
public class AuditConfigurationDisplayContext {

	public AuditConfigurationDisplayContext(
		AuditConfiguration auditConfiguration,
		FileSystemAuditMessageProcessorConfiguration
			fileSystemAuditMessageProcessorConfiguration,
		boolean systemScope) {

		_auditConfiguration = auditConfiguration;
		_fileSystemAuditMessageProcessorConfiguration =
			fileSystemAuditMessageProcessorConfiguration;
		_systemScope = systemScope;
	}

	public String getEnabledHelpMessage() {
		return _getHelpMessage(AuditConfiguration.class, "enabled");
	}

	public String getFileSystemAuditMessageProcessorEnabledHelpMessage() {
		return _getHelpMessage(
			FileSystemAuditMessageProcessorConfiguration.class, "enabled");
	}

	public String
		getFileSystemAuditMessageProcessorGenerateChecksumHelpMessage() {

		return _getHelpMessage(
			FileSystemAuditMessageProcessorConfiguration.class,
			"generateChecksum");
	}

	public String getFileSystemAuditMessageProcessorOutputDirectory() {
		return _fileSystemAuditMessageProcessorConfiguration.outputDirectory();
	}

	public String
		getFileSystemAuditMessageProcessorOutputDirectoryHelpMessage() {

		return _getHelpMessage(
			FileSystemAuditMessageProcessorConfiguration.class,
			"outputDirectory");
	}

	public String getFileSystemAuditMessageProcessorOutputFormat() {
		return _fileSystemAuditMessageProcessorConfiguration.outputFormat();
	}

	public String getFileSystemAuditMessageProcessorOutputFormatHelpMessage() {
		return _getHelpMessage(
			FileSystemAuditMessageProcessorConfiguration.class, "outputFormat");
	}

	public String getPseudonymizationEnabledHelpMessage() {
		return _getHelpMessage(
			AuditConfiguration.class, "pseudonymizationEnabled");
	}

	public boolean isEnabled() {
		return _auditConfiguration.enabled();
	}

	public boolean isEnabledOverridden() {
		return _isOverridden(AuditConfiguration.class, "enabled");
	}

	public boolean isFileSystemAuditMessageProcessorEnabled() {
		return _fileSystemAuditMessageProcessorConfiguration.enabled();
	}

	public boolean isFileSystemAuditMessageProcessorEnabledOverridden() {
		return _isOverridden(
			FileSystemAuditMessageProcessorConfiguration.class, "enabled");
	}

	public boolean isFileSystemAuditMessageProcessorGenerateChecksum() {
		return _fileSystemAuditMessageProcessorConfiguration.generateChecksum();
	}

	public boolean
		isFileSystemAuditMessageProcessorGenerateChecksumOverridden() {

		return _isOverridden(
			FileSystemAuditMessageProcessorConfiguration.class,
			"generateChecksum");
	}

	public boolean
		isFileSystemAuditMessageProcessorOutputDirectoryOverridden() {

		return _isOverridden(
			FileSystemAuditMessageProcessorConfiguration.class,
			"outputDirectory");
	}

	public boolean isFileSystemAuditMessageProcessorOutputFormatOverridden() {
		return _isOverridden(
			FileSystemAuditMessageProcessorConfiguration.class, "outputFormat");
	}

	public boolean isPseudonymizationEnabled() {
		return _auditConfiguration.pseudonymizationEnabled();
	}

	public boolean isPseudonymizationEnabledOverridden() {
		return _isOverridden(
			AuditConfiguration.class, "pseudonymizationEnabled");
	}

	public boolean isSystemScope() {
		return _systemScope;
	}

	private String _getHelpMessage(Class<?> clazz, String key) {
		return AuditConfigurationOverrideUtil.getHelpMessage(clazz, key);
	}

	private boolean _isOverridden(Class<?> clazz, String key) {
		return AuditConfigurationOverrideUtil.isOverridden(clazz, key);
	}

	private final AuditConfiguration _auditConfiguration;
	private final FileSystemAuditMessageProcessorConfiguration
		_fileSystemAuditMessageProcessorConfiguration;
	private final boolean _systemScope;

}