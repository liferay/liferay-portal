/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;SXPElement&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see SXPElement
 * @generated
 */
public class SXPElementTable extends BaseTable<SXPElementTable> {

	public static final SXPElementTable INSTANCE = new SXPElementTable();

	public final Column<SXPElementTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<SXPElementTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, Long> sxpElementId = createColumn(
		"sxpElementId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<SXPElementTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, Integer> compatibility = createColumn(
		"compatibility", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, String> description = createColumn(
		"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, Clob> elementDefinitionJSON =
		createColumn(
			"elementDefinitionJSON", Clob.class, Types.CLOB,
			Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, String> fallbackDescription =
		createColumn(
			"fallbackDescription", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, String> fallbackTitle = createColumn(
		"fallbackTitle", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, Boolean> hidden = createColumn(
		"hidden_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, Boolean> readOnly = createColumn(
		"readOnly", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, String> schemaVersion = createColumn(
		"schemaVersion", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, String> title = createColumn(
		"title", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, Integer> type = createColumn(
		"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<SXPElementTable, String> version = createColumn(
		"version", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private SXPElementTable() {
		super("SXPElement", SXPElementTable::new);
	}

}