/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Roberto Díaz
 */
@ExtendedObjectClassDefinition(
	category = "documents-and-media",
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.document.library.configuration.DLFileEntryRawMetadataProcessorConfiguration",
	localization = "content/Language",
	name = "dl-file-entry-raw-metadata-processor-configuration-name"
)
public interface DLFileEntryRawMetadataProcessorConfiguration {

	@Meta.AD(
		deflt = "application/zip", description = "excluded-mime-types-help",
		name = "excluded-mime-types", required = false
	)
	public String[] excludedMimeTypes();

}