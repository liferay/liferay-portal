/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;ColumnNameEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see ColumnNameEntry
 * @generated
 */
public class ColumnNameEntryTable extends BaseTable<ColumnNameEntryTable> {

	public static final ColumnNameEntryTable INSTANCE =
		new ColumnNameEntryTable();

	public final Column<ColumnNameEntryTable, Long> columnNameEntryId =
		createColumn(
			"cNameEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<ColumnNameEntryTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private ColumnNameEntryTable() {
		super("ColumnNameEntry", ColumnNameEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1192441409