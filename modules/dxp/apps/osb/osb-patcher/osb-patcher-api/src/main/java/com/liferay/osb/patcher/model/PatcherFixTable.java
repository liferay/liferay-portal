/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OSBPatcher_PatcherFix&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFix
 * @generated
 */
public class PatcherFixTable extends BaseTable<PatcherFixTable> {

	public static final PatcherFixTable INSTANCE = new PatcherFixTable();

	public final Column<PatcherFixTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<PatcherFixTable, Long> patcherFixId = createColumn(
		"patcherFixId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherFixTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Long> patcherProductVersionId =
		createColumn(
			"patcherProductVersionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Long> patcherProjectVersionId =
		createColumn(
			"patcherProjectVersionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Clob> comments = createColumn(
		"comments", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, String> committish = createColumn(
		"committish", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, String> dependencies = createColumn(
		"dependencies", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Integer> fixPackStatus = createColumn(
		"fixPackStatus", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, String> gitHash = createColumn(
		"gitHash", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, String> gitRemoteURL = createColumn(
		"gitRemoteURL", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Clob> jenkinsResults = createColumn(
		"jenkinsResults", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, String> key = createColumn(
		"key_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Double> keyVersion = createColumn(
		"keyVersion", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Boolean> latestFix = createColumn(
		"latestFix", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Clob> name = createColumn(
		"name", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Boolean> notified = createColumn(
		"notified", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Boolean> obsolete = createColumn(
		"obsolete", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Integer> productVersion = createColumn(
		"productVersion", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, String> requestKey = createColumn(
		"requestKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, String> requirements = createColumn(
		"requirements", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Integer> type = createColumn(
		"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Long> statusByUserId = createColumn(
		"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, String> statusByUserName =
		createColumn(
			"statusByUserName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<PatcherFixTable, Date> statusDate = createColumn(
		"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private PatcherFixTable() {
		super("OSBPatcher_PatcherFix", PatcherFixTable::new);
	}

}