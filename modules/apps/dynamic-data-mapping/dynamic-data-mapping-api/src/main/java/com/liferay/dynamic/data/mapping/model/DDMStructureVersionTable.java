/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;DDMStructureVersion&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureVersion
 * @generated
 */
public class DDMStructureVersionTable
	extends BaseTable<DDMStructureVersionTable> {

	public static final DDMStructureVersionTable INSTANCE =
		new DDMStructureVersionTable();

	public final Column<DDMStructureVersionTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<DDMStructureVersionTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<DDMStructureVersionTable, Long> structureVersionId =
		createColumn(
			"structureVersionId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<DDMStructureVersionTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Long> structureId =
		createColumn(
			"structureId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, String> version =
		createColumn(
			"version", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Long> parentStructureId =
		createColumn(
			"parentStructureId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Clob> name = createColumn(
		"name", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Clob> description =
		createColumn(
			"description", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Clob> definition =
		createColumn("definition", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, String> storageType =
		createColumn(
			"storageType", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Integer> type = createColumn(
		"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Integer> status =
		createColumn(
			"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Long> statusByUserId =
		createColumn(
			"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, String> statusByUserName =
		createColumn(
			"statusByUserName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<DDMStructureVersionTable, Date> statusDate =
		createColumn(
			"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private DDMStructureVersionTable() {
		super("DDMStructureVersion", DDMStructureVersionTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2028673286