/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.server.internal.resource.v1_0;

import com.liferay.headless.admin.server.resource.v1_0.DatabaseSchemaExportResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Luis Ortiz
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/database-schema-export.properties",
	scope = ServiceScope.PROTOTYPE, service = DatabaseSchemaExportResource.class
)
public class DatabaseSchemaExportResourceImpl
	extends BaseDatabaseSchemaExportResourceImpl {
}