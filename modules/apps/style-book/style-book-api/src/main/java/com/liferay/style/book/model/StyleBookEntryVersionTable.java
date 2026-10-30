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
 * The table class for the &quot;StyleBookEntryVersion&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntryVersion
 * @generated
 */
public class StyleBookEntryVersionTable
	extends BaseTable<StyleBookEntryVersionTable> {

	public static final StyleBookEntryVersionTable INSTANCE =
		new StyleBookEntryVersionTable();

	public final Column<StyleBookEntryVersionTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<StyleBookEntryVersionTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<StyleBookEntryVersionTable, Long>
		styleBookEntryVersionId = createColumn(
			"styleBookEntryVersionId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<StyleBookEntryVersionTable, Integer> version =
		createColumn(
			"version", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, Long> styleBookEntryId =
		createColumn(
			"styleBookEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, Boolean>
		defaultStyleBookEntry = createColumn(
			"defaultStyleBookEntry", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, Clob>
		frontendTokenDefinition = createColumn(
			"frontendTokenDefinition", Clob.class, Types.CLOB,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, Clob> frontendTokensValues =
		createColumn(
			"frontendTokensValues", Clob.class, Types.CLOB,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, Long> previewFileEntryId =
		createColumn(
			"previewFileEntryId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, String> styleBookEntryKey =
		createColumn(
			"styleBookEntryKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookEntryVersionTable, String> themeId =
		createColumn(
			"themeId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private StyleBookEntryVersionTable() {
		super("StyleBookEntryVersion", StyleBookEntryVersionTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-946573344