/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;StyleBookEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntry
 * @generated
 */
public class StyleBookEntryTable extends BaseTable<StyleBookEntryTable> {

	public static final StyleBookEntryTable INSTANCE =
		new StyleBookEntryTable();

	public final Column<StyleBookEntryTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<StyleBookEntryTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<StyleBookEntryTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Long> headId = createColumn(
		"headId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Boolean> head = createColumn(
		"head", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Long> styleBookEntryId =
		createColumn(
			"styleBookEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<StyleBookEntryTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Boolean> defaultStyleBookEntry =
		createColumn(
			"defaultStyleBookEntry", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Clob> frontendTokenDefinition =
		createColumn(
			"frontendTokenDefinition", Clob.class, Types.CLOB,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Clob> frontendTokensValues =
		createColumn(
			"frontendTokensValues", Clob.class, Types.CLOB,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, Long> previewFileEntryId =
		createColumn(
			"previewFileEntryId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, String> styleBookEntryKey =
		createColumn(
			"styleBookEntryKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryTable, String> themeId = createColumn(
		"themeId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private StyleBookEntryTable() {
		super("StyleBookEntry", StyleBookEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1554132384