/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;DateEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see DateEntry
 * @generated
 */
public class DateEntryTable extends BaseTable<DateEntryTable> {

	public static final DateEntryTable INSTANCE = new DateEntryTable();

	public final Column<DateEntryTable, Long> dateEntryId = createColumn(
		"dateEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<DateEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DateEntryTable, Date> snapshotDate = createColumn(
		"snapshotDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private DateEntryTable() {
		super("DateEntry", DateEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1975297974