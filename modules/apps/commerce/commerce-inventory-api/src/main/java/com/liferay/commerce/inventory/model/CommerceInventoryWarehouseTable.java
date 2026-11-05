/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CIWarehouse&quot; database table.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryWarehouse
 * @generated
 */
public class CommerceInventoryWarehouseTable
	extends BaseTable<CommerceInventoryWarehouseTable> {

	public static final CommerceInventoryWarehouseTable INSTANCE =
		new CommerceInventoryWarehouseTable();

	public final Column<CommerceInventoryWarehouseTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CommerceInventoryWarehouseTable, String> uuid =
		createColumn("uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, Long>
		commerceInventoryWarehouseId = createColumn(
			"CIWarehouseId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CommerceInventoryWarehouseTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String> name =
		createColumn("name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String> description =
		createColumn(
			"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, Boolean> active =
		createColumn(
			"active_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String> street1 =
		createColumn(
			"street1", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String> street2 =
		createColumn(
			"street2", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String> street3 =
		createColumn(
			"street3", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String> city =
		createColumn("city", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String> zip =
		createColumn("zip", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String>
		commerceRegionCode = createColumn(
			"commerceRegionCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String>
		countryTwoLettersISOCode = createColumn(
			"countryTwoLettersISOCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, Double> latitude =
		createColumn(
			"latitude", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, Double> longitude =
		createColumn(
			"longitude", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, String> type =
		createColumn("type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceInventoryWarehouseTable, Integer> status =
		createColumn(
			"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private CommerceInventoryWarehouseTable() {
		super("CIWarehouse", CommerceInventoryWarehouseTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1640728522