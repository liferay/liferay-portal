/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;ExportImportReportEntry&quot; database table.
 *
 * @author Carlos Correa
 * @see ExportImportReportEntry
 * @generated
 */
public class ExportImportReportEntryTable
	extends BaseTable<ExportImportReportEntryTable> {

	public static final ExportImportReportEntryTable INSTANCE =
		new ExportImportReportEntryTable();

	public final Column<ExportImportReportEntryTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<ExportImportReportEntryTable, Long>
		exportImportReportEntryId = createColumn(
			"exportImportReportEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<ExportImportReportEntryTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, String>
		classExternalReferenceCode = createColumn(
			"classExternalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Long> classNameId =
		createColumn(
			"classNameId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Long> classPK =
		createColumn("classPK", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Long>
		exportImportConfigurationId = createColumn(
			"exportImportConfigurationId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Clob> errorMessage =
		createColumn(
			"errorMessage", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Clob> errorStacktrace =
		createColumn(
			"errorStacktrace", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, String> modelName =
		createColumn(
			"modelName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Integer> origin =
		createColumn(
			"origin", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, String> scope =
		createColumn("scope", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, String> scopeKey =
		createColumn(
			"scopeKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Integer> type =
		createColumn(
			"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<ExportImportReportEntryTable, Integer> status =
		createColumn(
			"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private ExportImportReportEntryTable() {
		super("ExportImportReportEntry", ExportImportReportEntryTable::new);
	}

}