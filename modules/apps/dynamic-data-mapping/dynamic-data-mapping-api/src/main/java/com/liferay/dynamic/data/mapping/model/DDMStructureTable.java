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
 * The table class for the &quot;DDMStructure&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructure
 * @generated
 */
public class DDMStructureTable extends BaseTable<DDMStructureTable> {

	public static final DDMStructureTable INSTANCE = new DDMStructureTable();

	public final Column<DDMStructureTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<DDMStructureTable, Long> ctCollectionId = createColumn(
		"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<DDMStructureTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Long> structureId = createColumn(
		"structureId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<DDMStructureTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Long> versionUserId = createColumn(
		"versionUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, String> versionUserName =
		createColumn(
			"versionUserName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Long> parentStructureId =
		createColumn(
			"parentStructureId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Long> classNameId = createColumn(
		"classNameId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, String> structureKey = createColumn(
		"structureKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, String> version = createColumn(
		"version", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Clob> description = createColumn(
		"description", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Clob> definition = createColumn(
		"definition", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, String> storageType = createColumn(
		"storageType", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Integer> type = createColumn(
		"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<DDMStructureTable, Date> lastPublishDate = createColumn(
		"lastPublishDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private DDMStructureTable() {
		super("DDMStructure", DDMStructureTable::new);
	}

}