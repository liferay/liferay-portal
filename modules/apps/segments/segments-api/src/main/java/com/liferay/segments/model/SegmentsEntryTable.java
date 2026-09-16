/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;SegmentsEntry&quot; database table.
 *
 * @author Eduardo Garcia
 * @see SegmentsEntry
 * @generated
 */
public class SegmentsEntryTable extends BaseTable<SegmentsEntryTable> {

	public static final SegmentsEntryTable INSTANCE = new SegmentsEntryTable();

	public final Column<SegmentsEntryTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<SegmentsEntryTable, Long> ctCollectionId = createColumn(
		"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<SegmentsEntryTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, Long> segmentsEntryId =
		createColumn(
			"segmentsEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<SegmentsEntryTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, String> segmentsEntryKey =
		createColumn(
			"segmentsEntryKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, String> description = createColumn(
		"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, Boolean> active = createColumn(
		"active_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, Clob> criteria = createColumn(
		"criteria", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, String> source = createColumn(
		"source", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, Integer> type = createColumn(
		"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<SegmentsEntryTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);

	private SegmentsEntryTable() {
		super("SegmentsEntry", SegmentsEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1643238388