/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;FinderWhereClauseEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see FinderWhereClauseEntry
 * @generated
 */
public class FinderWhereClauseEntryTable
	extends BaseTable<FinderWhereClauseEntryTable> {

	public static final FinderWhereClauseEntryTable INSTANCE =
		new FinderWhereClauseEntryTable();

	public final Column<FinderWhereClauseEntryTable, Long>
		finderWhereClauseEntryId = createColumn(
			"finderWhereClauseEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<FinderWhereClauseEntryTable, Long> headId =
		createColumn("headId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FinderWhereClauseEntryTable, String> name =
		createColumn("name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FinderWhereClauseEntryTable, String> nickname =
		createColumn(
			"nickname", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FinderWhereClauseEntryTable, Integer> status =
		createColumn(
			"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private FinderWhereClauseEntryTable() {
		super("FinderWhereClauseEntry", FinderWhereClauseEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-873587444