/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;ObjectEntry&quot; database table.
 *
 * @author Marco Leo
 * @see ObjectEntry
 * @generated
 */
public class ObjectEntryTable extends BaseTable<ObjectEntryTable> {

	public static final ObjectEntryTable INSTANCE = new ObjectEntryTable();

	public final Column<ObjectEntryTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<ObjectEntryTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Long> objectEntryId = createColumn(
		"objectEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<ObjectEntryTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Long> headObjectEntryId =
		createColumn(
			"headObjectEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Long> objectDefinitionId =
		createColumn(
			"objectDefinitionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Long> objectEntryFolderId =
		createColumn(
			"objectEntryFolderId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Long> rootObjectEntryId =
		createColumn(
			"rootObjectEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, String> defaultLanguageId =
		createColumn(
			"defaultLanguageId", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Date> displayDate = createColumn(
		"displayDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Date> expirationDate = createColumn(
		"expirationDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Date> reviewDate = createColumn(
		"reviewDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, String> treePath = createColumn(
		"treePath", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Integer> version = createColumn(
		"version", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Date> lastPublishDate = createColumn(
		"lastPublishDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Long> statusByUserId = createColumn(
		"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, String> statusByUserName =
		createColumn(
			"statusByUserName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ObjectEntryTable, Date> statusDate = createColumn(
		"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private ObjectEntryTable() {
		super("ObjectEntry", ObjectEntryTable::new);
	}

}