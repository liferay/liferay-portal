/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;FilterPrimaryEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see FilterPrimaryEntry
 * @generated
 */
public class FilterPrimaryEntryTable
	extends BaseTable<FilterPrimaryEntryTable> {

	public static final FilterPrimaryEntryTable INSTANCE =
		new FilterPrimaryEntryTable();

	public final Column<FilterPrimaryEntryTable, Long> filterPrimaryEntryId =
		createColumn(
			"filterPrimaryEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<FilterPrimaryEntryTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FilterPrimaryEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FilterPrimaryEntryTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FilterPrimaryEntryTable, Long> resourcePrimKey =
		createColumn(
			"resourcePrimKey", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);

	private FilterPrimaryEntryTable() {
		super("FilterPrimaryEntry", FilterPrimaryEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1176837855