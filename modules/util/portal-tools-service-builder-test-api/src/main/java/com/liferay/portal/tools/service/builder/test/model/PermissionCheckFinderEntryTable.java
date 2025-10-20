/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;PermissionCheckFinderEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PermissionCheckFinderEntry
 * @generated
 */
public class PermissionCheckFinderEntryTable
	extends BaseTable<PermissionCheckFinderEntryTable> {

	public static final PermissionCheckFinderEntryTable INSTANCE =
		new PermissionCheckFinderEntryTable();

	public final Column<PermissionCheckFinderEntryTable, Long>
		permissionCheckFinderEntryId = createColumn(
			"permissionCheckFinderEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<PermissionCheckFinderEntryTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PermissionCheckFinderEntryTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PermissionCheckFinderEntryTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PermissionCheckFinderEntryTable, Integer> integer =
		createColumn(
			"integer_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<PermissionCheckFinderEntryTable, String> name =
		createColumn("name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PermissionCheckFinderEntryTable, String> type =
		createColumn("type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private PermissionCheckFinderEntryTable() {
		super(
			"PermissionCheckFinderEntry", PermissionCheckFinderEntryTable::new);
	}

}