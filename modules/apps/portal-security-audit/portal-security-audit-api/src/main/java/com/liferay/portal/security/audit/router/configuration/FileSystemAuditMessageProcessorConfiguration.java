/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Christian Moura
 */
@ExtendedObjectClassDefinition(
	category = "audit", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.portal.security.audit.router.configuration.FileSystemAuditMessageProcessorConfiguration",
	localization = "content/Language",
	name = "file-system-audit-message-processor-configuration-name"
)
@ProviderType
public interface FileSystemAuditMessageProcessorConfiguration {

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(deflt = "false", name = "generate-checksum", required = false)
	public boolean generateChecksum();

	@Meta.AD(
		deflt = "${liferay.home}/data/audit", name = "output-directory",
		required = false
	)
	public String outputDirectory();

	@Meta.AD(
		deflt = "NDJSON", name = "output-format",
		optionValues = {"CSV", "NDJSON"}, required = false
	)
	public String outputFormat();

}