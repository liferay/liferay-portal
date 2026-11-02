/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;DLFileEntryType&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryType
 * @generated
 */
public class DLFileEntryTypeTable extends BaseTable<DLFileEntryTypeTable> {

	public static final DLFileEntryTypeTable INSTANCE =
		new DLFileEntryTypeTable();

	public final Column<DLFileEntryTypeTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<DLFileEntryTypeTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<DLFileEntryTypeTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, Long> fileEntryTypeId =
		createColumn(
			"fileEntryTypeId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<DLFileEntryTypeTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, Long> dataDefinitionId =
		createColumn(
			"dataDefinitionId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, String> fileEntryTypeKey =
		createColumn(
			"fileEntryTypeKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, Clob> name = createColumn(
		"name", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, String> description =
		createColumn(
			"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, Integer> scope = createColumn(
		"scope", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<DLFileEntryTypeTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);

	private DLFileEntryTypeTable() {
		super("DLFileEntryType", DLFileEntryTypeTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2094216908