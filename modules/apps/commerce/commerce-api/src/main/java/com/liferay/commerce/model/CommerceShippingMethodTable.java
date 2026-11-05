/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CommerceShippingMethod&quot; database table.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingMethod
 * @generated
 */
public class CommerceShippingMethodTable
	extends BaseTable<CommerceShippingMethodTable> {

	public static final CommerceShippingMethodTable INSTANCE =
		new CommerceShippingMethodTable();

	public final Column<CommerceShippingMethodTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CommerceShippingMethodTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, Long>
		commerceShippingMethodId = createColumn(
			"commerceShippingMethodId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CommerceShippingMethodTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, String> name =
		createColumn("name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, String> description =
		createColumn(
			"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, Boolean> active =
		createColumn(
			"active_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, String> engineKey =
		createColumn(
			"engineKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, Long> imageId =
		createColumn("imageId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, Double> priority =
		createColumn(
			"priority", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, String> trackingURL =
		createColumn(
			"trackingURL", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, Clob> typeSettings =
		createColumn(
			"typeSettings", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<CommerceShippingMethodTable, Integer> status =
		createColumn(
			"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private CommerceShippingMethodTable() {
		super("CommerceShippingMethod", CommerceShippingMethodTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1706486773