/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CPMeasurementUnit&quot; database table.
 *
 * @author Marco Leo
 * @see CPMeasurementUnit
 * @generated
 */
public class CPMeasurementUnitTable extends BaseTable<CPMeasurementUnitTable> {

	public static final CPMeasurementUnitTable INSTANCE =
		new CPMeasurementUnitTable();

	public final Column<CPMeasurementUnitTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CPMeasurementUnitTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CPMeasurementUnitTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Long> CPMeasurementUnitId =
		createColumn(
			"CPMeasurementUnitId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CPMeasurementUnitTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, String> key = createColumn(
		"key_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Double> rate = createColumn(
		"rate", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Boolean> primary = createColumn(
		"primary_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Double> priority = createColumn(
		"priority", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Integer> type = createColumn(
		"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<CPMeasurementUnitTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private CPMeasurementUnitTable() {
		super("CPMeasurementUnit", CPMeasurementUnitTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-999501138