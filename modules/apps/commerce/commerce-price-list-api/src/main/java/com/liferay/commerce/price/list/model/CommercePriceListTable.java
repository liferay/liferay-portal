/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CommercePriceList&quot; database table.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceList
 * @generated
 */
public class CommercePriceListTable extends BaseTable<CommercePriceListTable> {

	public static final CommercePriceListTable INSTANCE =
		new CommercePriceListTable();

	public final Column<CommercePriceListTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CommercePriceListTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CommercePriceListTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Long> commercePriceListId =
		createColumn(
			"commercePriceListId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CommercePriceListTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Boolean> catalogBasePriceList =
		createColumn(
			"catalogBasePriceList", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, String> commerceCurrencyCode =
		createColumn(
			"commerceCurrencyCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Date> displayDate =
		createColumn(
			"displayDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Date> expirationDate =
		createColumn(
			"expirationDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Boolean> netPrice =
		createColumn(
			"netPrice", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Long>
		parentCommercePriceListId = createColumn(
			"parentCommercePriceListId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Double> priority = createColumn(
		"priority", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, String> type = createColumn(
		"type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Long> statusByUserId =
		createColumn(
			"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, String> statusByUserName =
		createColumn(
			"statusByUserName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommercePriceListTable, Date> statusDate = createColumn(
		"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private CommercePriceListTable() {
		super("CommercePriceList", CommercePriceListTable::new);
	}

}