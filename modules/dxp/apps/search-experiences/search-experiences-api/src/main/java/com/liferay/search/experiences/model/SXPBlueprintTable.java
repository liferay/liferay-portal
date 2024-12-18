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
 * The table class for the &quot;SXPBlueprint&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see SXPBlueprint
 * @generated
 */
public class SXPBlueprintTable extends BaseTable<SXPBlueprintTable> {

	public static final SXPBlueprintTable INSTANCE = new SXPBlueprintTable();

	public final Column<SXPBlueprintTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<SXPBlueprintTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Long> sxpBlueprintId = createColumn(
		"sxpBlueprintId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<SXPBlueprintTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Boolean> collectionProvider =
		createColumn(
			"collectionProvider", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Integer> compatibility =
		createColumn(
			"compatibility", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Clob> configurationJSON =
		createColumn(
			"configurationJSON", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, String> description = createColumn(
		"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Clob> elementInstancesJSON =
		createColumn(
			"elementInstancesJSON", Clob.class, Types.CLOB,
			Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, String> fallbackDescription =
		createColumn(
			"fallbackDescription", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, String> fallbackTitle = createColumn(
		"fallbackTitle", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, String> schemaVersion = createColumn(
		"schemaVersion", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, String> title = createColumn(
		"title", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, String> version = createColumn(
		"version", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Long> statusByUserId = createColumn(
		"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, String> statusByUserName =
		createColumn(
			"statusByUserName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SXPBlueprintTable, Date> statusDate = createColumn(
		"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private SXPBlueprintTable() {
		super("SXPBlueprint", SXPBlueprintTable::new);
	}

}