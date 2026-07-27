/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;OSBFaro_FaroDataSourceUsage&quot; database table.
 *
 * @author Matthew Kong
 * @see FaroDataSourceUsage
 * @generated
 */
public class FaroDataSourceUsageTable
	extends BaseTable<FaroDataSourceUsageTable> {

	public static final FaroDataSourceUsageTable INSTANCE =
		new FaroDataSourceUsageTable();

	public final Column<FaroDataSourceUsageTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<FaroDataSourceUsageTable, Long> faroDataSourceUsageId =
		createColumn(
			"faroDataSourceUsageId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<FaroDataSourceUsageTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroDataSourceUsageTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroDataSourceUsageTable, Long> createTime =
		createColumn(
			"createTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroDataSourceUsageTable, Long> modifiedTime =
		createColumn(
			"modifiedTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroDataSourceUsageTable, Long> billableEventsCount =
		createColumn(
			"billableEventsCount", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<FaroDataSourceUsageTable, Long> dataSourceId =
		createColumn(
			"dataSourceId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroDataSourceUsageTable, String> dataSourceName =
		createColumn(
			"dataSourceName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroDataSourceUsageTable, String> dataSourceStatus =
		createColumn(
			"dataSourceStatus", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<FaroDataSourceUsageTable, Long> faroProjectId =
		createColumn(
			"faroProjectId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroDataSourceUsageTable, Long> knownIndividualsCount =
		createColumn(
			"knownIndividualsCount", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<FaroDataSourceUsageTable, Long> usageTime =
		createColumn(
			"usageTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);

	private FaroDataSourceUsageTable() {
		super("OSBFaro_FaroDataSourceUsage", FaroDataSourceUsageTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:605503934