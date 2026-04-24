/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;PR_ProductionReadinessIgnore&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see ProductionReadinessIgnore
 * @generated
 */
public class ProductionReadinessIgnoreTable
	extends BaseTable<ProductionReadinessIgnoreTable> {

	public static final ProductionReadinessIgnoreTable INSTANCE =
		new ProductionReadinessIgnoreTable();

	public final Column<ProductionReadinessIgnoreTable, Long>
		productionReadinessIgnoreId = createColumn(
			"productionReadinessIgnoreId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<ProductionReadinessIgnoreTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ProductionReadinessIgnoreTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ProductionReadinessIgnoreTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ProductionReadinessIgnoreTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ProductionReadinessIgnoreTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ProductionReadinessIgnoreTable, String> ruleKey =
		createColumn(
			"ruleKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ProductionReadinessIgnoreTable, String> reason =
		createColumn(
			"reason", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private ProductionReadinessIgnoreTable() {
		super(
			"PR_ProductionReadinessIgnore",
			ProductionReadinessIgnoreTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:269543430