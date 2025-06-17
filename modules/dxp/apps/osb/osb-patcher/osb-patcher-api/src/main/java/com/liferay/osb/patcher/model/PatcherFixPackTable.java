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
 * The table class for the &quot;OSBPatcher_PatcherFixPack&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixPack
 * @generated
 */
public class PatcherFixPackTable extends BaseTable<PatcherFixPackTable> {

	public static final PatcherFixPackTable INSTANCE =
		new PatcherFixPackTable();

	public final Column<PatcherFixPackTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<PatcherFixPackTable, Long> patcherFixPackId =
		createColumn(
			"patcherFixPackId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherFixPackTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, Long> patcherBuildId =
		createColumn(
			"patcherBuildId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, Long> patcherFixComponentId =
		createColumn(
			"patcherFixComponentId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, Long> patcherProjectVersionId =
		createColumn(
			"patcherProjectVersionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, Date> releasedDate = createColumn(
		"releasedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, String> requirements =
		createColumn(
			"requirements", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, Integer> version = createColumn(
		"version", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<PatcherFixPackTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private PatcherFixPackTable() {
		super("OSBPatcher_PatcherFixPack", PatcherFixPackTable::new);
	}

}