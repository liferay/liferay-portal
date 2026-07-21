/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.math.BigDecimal;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CommerceCurrency&quot; database table.
 *
 * @author Andrea Di Giorgi
 * @see CommerceCurrency
 * @generated
 */
public class CommerceCurrencyTable extends BaseTable<CommerceCurrencyTable> {

	public static final CommerceCurrencyTable INSTANCE =
		new CommerceCurrencyTable();

	public final Column<CommerceCurrencyTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CommerceCurrencyTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Long> commerceCurrencyId =
		createColumn(
			"commerceCurrencyId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CommerceCurrencyTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, String> code = createColumn(
		"code_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, String> symbol = createColumn(
		"symbol", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, BigDecimal> rate = createColumn(
		"rate", BigDecimal.class, Types.DECIMAL, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, String> formatPattern =
		createColumn(
			"formatPattern", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Integer> maxFractionDigits =
		createColumn(
			"maxFractionDigits", Integer.class, Types.INTEGER,
			Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Integer> minFractionDigits =
		createColumn(
			"minFractionDigits", Integer.class, Types.INTEGER,
			Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, String> roundingMode =
		createColumn(
			"roundingMode", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Boolean> primary = createColumn(
		"primary_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Double> priority = createColumn(
		"priority", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Boolean> active = createColumn(
		"active_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<CommerceCurrencyTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private CommerceCurrencyTable() {
		super("CommerceCurrency", CommerceCurrencyTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:976757882