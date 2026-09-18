/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CommerceDiscountRule&quot; database table.
 *
 * @author Marco Leo
 * @see CommerceDiscountRule
 * @generated
 */
public class CommerceDiscountRuleTable
	extends BaseTable<CommerceDiscountRuleTable> {

	public static final CommerceDiscountRuleTable INSTANCE =
		new CommerceDiscountRuleTable();

	public final Column<CommerceDiscountRuleTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CommerceDiscountRuleTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceDiscountRuleTable, Long>
		commerceDiscountRuleId = createColumn(
			"commerceDiscountRuleId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CommerceDiscountRuleTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceDiscountRuleTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceDiscountRuleTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceDiscountRuleTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceDiscountRuleTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceDiscountRuleTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceDiscountRuleTable, Long> commerceDiscountId =
		createColumn(
			"commerceDiscountId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CommerceDiscountRuleTable, String> type = createColumn(
		"type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceDiscountRuleTable, Clob> typeSettings =
		createColumn(
			"typeSettings", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);

	private CommerceDiscountRuleTable() {
		super("CommerceDiscountRule", CommerceDiscountRuleTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:183382669