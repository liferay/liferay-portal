/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.sql.provider;

/**
 * @author Brian Wing Shun Chan
 * @author Mariano Álvaro Sáiz
 */
public interface SQLProvider {

	public String getIndexesSQL();

	public String getTablesSQL();

}