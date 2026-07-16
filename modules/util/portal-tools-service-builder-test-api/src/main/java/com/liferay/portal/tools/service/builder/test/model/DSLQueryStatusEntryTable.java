/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;DSLQueryStatusEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see DSLQueryStatusEntry
 * @generated
 */
public class DSLQueryStatusEntryTable
	extends BaseTable<DSLQueryStatusEntryTable> {

	public static final DSLQueryStatusEntryTable INSTANCE =
		new DSLQueryStatusEntryTable();

	public final Column<DSLQueryStatusEntryTable, Long> dslQueryStatusEntryId =
		createColumn(
			"dslQueryStatusEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<DSLQueryStatusEntryTable, Long> dslQueryEntryId =
		createColumn(
			"dslQueryEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DSLQueryStatusEntryTable, Double> weight = createColumn(
		"weight", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<DSLQueryStatusEntryTable, String> status = createColumn(
		"status", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DSLQueryStatusEntryTable, Date> statusDate =
		createColumn(
			"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private DSLQueryStatusEntryTable() {
		super("DSLQueryStatusEntry", DSLQueryStatusEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1837080576