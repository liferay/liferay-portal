/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.display.context;

import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationOverrideUtil;
import com.liferay.portal.security.audit.router.configuration.FileSystemAuditMessageProcessorConfiguration;

/**
 * @author Álvaro Saugar
 */
public class FileSystemAuditMessageProcessorConfigurationDisplayContext {

	public FileSystemAuditMessageProcessorConfigurationDisplayContext(
		FileSystemAuditMessageProcessorConfiguration
			fileSystemAuditMessageProcessorConfiguration) {

		_fileSystemAuditMessageProcessorConfiguration =
			fileSystemAuditMessageProcessorConfiguration;
	}

	public String getEnabledHelpMessage() {
		return _getHelpMessage("enabled");
	}

	public String getGenerateChecksumHelpMessage() {
		return _getHelpMessage("generateChecksum");
	}

	public String getOutputDirectory() {
		return _fileSystemAuditMessageProcessorConfiguration.outputDirectory();
	}

	public String getOutputDirectoryHelpMessage() {
		return _getHelpMessage("outputDirectory");
	}

	public String getOutputFormat() {
		return _fileSystemAuditMessageProcessorConfiguration.outputFormat();
	}

	public String getOutputFormatHelpMessage() {
		return _getHelpMessage("outputFormat");
	}

	public boolean isEnabled() {
		return _fileSystemAuditMessageProcessorConfiguration.enabled();
	}

	public boolean isEnabledOverridden() {
		return _isOverridden("enabled");
	}

	public boolean isGenerateChecksum() {
		return _fileSystemAuditMessageProcessorConfiguration.generateChecksum();
	}

	public boolean isGenerateChecksumOverridden() {
		return _isOverridden("generateChecksum");
	}

	public boolean isOutputDirectoryOverridden() {
		return _isOverridden("outputDirectory");
	}

	public boolean isOutputFormatOverridden() {
		return _isOverridden("outputFormat");
	}

	private String _getHelpMessage(String key) {
		return AuditConfigurationOverrideUtil.getHelpMessage(
			FileSystemAuditMessageProcessorConfiguration.class, key);
	}

	private boolean _isOverridden(String key) {
		return AuditConfigurationOverrideUtil.isOverridden(
			FileSystemAuditMessageProcessorConfiguration.class, key);
	}

	private final FileSystemAuditMessageProcessorConfiguration
		_fileSystemAuditMessageProcessorConfiguration;

}