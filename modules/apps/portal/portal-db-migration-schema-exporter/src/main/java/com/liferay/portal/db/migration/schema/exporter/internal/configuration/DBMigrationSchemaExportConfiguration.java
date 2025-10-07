/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Mariano Álvaro Sáiz
 */
@ExtendedObjectClassDefinition(
	category = "upgrades", featureFlagKey = "LPD-23840"
)
@Meta.OCD(
	description = "db-migration-schema-export-configuration-description",
	id = "com.liferay.portal.db.migration.schema.exporter.internal.configuration.DBMigrationSchemaExportConfiguration",
	localization = "content/Language",
	name = "db-migration-schema-export-configuration-name"
)
public interface DBMigrationSchemaExportConfiguration {

	@Meta.AD(name = "export-files-path")
	public String exportFilesPath();

}