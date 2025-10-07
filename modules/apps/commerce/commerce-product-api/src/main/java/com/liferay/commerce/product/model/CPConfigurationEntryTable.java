/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.math.BigDecimal;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CPConfigurationEntry&quot; database table.
 *
 * @author Marco Leo
 * @see CPConfigurationEntry
 * @generated
 */
public class CPConfigurationEntryTable
	extends BaseTable<CPConfigurationEntryTable> {

	public static final CPConfigurationEntryTable INSTANCE =
		new CPConfigurationEntryTable();

	public final Column<CPConfigurationEntryTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CPConfigurationEntryTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CPConfigurationEntryTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Long>
		CPConfigurationEntryId = createColumn(
			"CPConfigurationEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CPConfigurationEntryTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Long> classNameId =
		createColumn(
			"classNameId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Long> classPK = createColumn(
		"classPK", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Long> CPConfigurationListId =
		createColumn(
			"CPConfigurationListId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Long> CPTaxCategoryId =
		createColumn(
			"CPTaxCategoryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, String>
		allowedOrderQuantities = createColumn(
			"allowedOrderQuantities", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Boolean> backOrders =
		createColumn(
			"backOrders", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Long>
		commerceAvailabilityEstimateId = createColumn(
			"commerceAvailabilityEstimateId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, String>
		CPDefinitionInventoryEngine = createColumn(
			"CPDefinitionInventoryEngine", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Double> depth = createColumn(
		"depth", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Boolean>
		displayAvailability = createColumn(
			"displayAvailability", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Boolean>
		displayStockQuantity = createColumn(
			"displayStockQuantity", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Boolean> freeShipping =
		createColumn(
			"freeShipping", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Double> height =
		createColumn("height", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, String> lowStockActivity =
		createColumn(
			"lowStockActivity", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, BigDecimal>
		maxOrderQuantity = createColumn(
			"maxOrderQuantity", BigDecimal.class, Types.DECIMAL,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, BigDecimal>
		minOrderQuantity = createColumn(
			"minOrderQuantity", BigDecimal.class, Types.DECIMAL,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, BigDecimal>
		minStockQuantity = createColumn(
			"minStockQuantity", BigDecimal.class, Types.DECIMAL,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, BigDecimal>
		multipleOrderQuantity = createColumn(
			"multipleOrderQuantity", BigDecimal.class, Types.DECIMAL,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Boolean> purchasable =
		createColumn(
			"purchasable", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Boolean> shippable =
		createColumn(
			"shippable", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Double> shippingExtraPrice =
		createColumn(
			"shippingExtraPrice", Double.class, Types.DOUBLE,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Boolean> shipSeparately =
		createColumn(
			"shipSeparately", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Boolean> taxExempt =
		createColumn(
			"taxExempt", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Double> weight =
		createColumn("weight", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPConfigurationEntryTable, Double> width = createColumn(
		"width", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);

	private CPConfigurationEntryTable() {
		super("CPConfigurationEntry", CPConfigurationEntryTable::new);
	}

}