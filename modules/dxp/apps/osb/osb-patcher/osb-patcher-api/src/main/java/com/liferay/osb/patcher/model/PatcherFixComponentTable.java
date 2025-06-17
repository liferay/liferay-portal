/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OSBPatcher_PatcherFixComponent&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixComponent
 * @generated
 */
public class PatcherFixComponentTable
	extends BaseTable<PatcherFixComponentTable> {

	public static final PatcherFixComponentTable INSTANCE =
		new PatcherFixComponentTable();

	public final Column<PatcherFixComponentTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<PatcherFixComponentTable, Long> patcherFixComponentId =
		createColumn(
			"patcherFixComponentId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<PatcherFixComponentTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherFixComponentTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherFixComponentTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixComponentTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherFixComponentTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherFixComponentTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private PatcherFixComponentTable() {
		super("OSBPatcher_PatcherFixComponent", PatcherFixComponentTable::new);
	}

}