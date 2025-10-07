/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;OSBFaro_FaroProjectUsage&quot; database table.
 *
 * @author Matthew Kong
 * @see FaroProjectUsage
 * @generated
 */
public class FaroProjectUsageTable extends BaseTable<FaroProjectUsageTable> {

	public static final FaroProjectUsageTable INSTANCE =
		new FaroProjectUsageTable();

	public final Column<FaroProjectUsageTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<FaroProjectUsageTable, Long> faroProjectUsageId =
		createColumn(
			"faroProjectUsageId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<FaroProjectUsageTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectUsageTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectUsageTable, Long> createTime = createColumn(
		"createTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectUsageTable, Long> modifiedTime =
		createColumn(
			"modifiedTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectUsageTable, Long> faroProjectId =
		createColumn(
			"faroProjectId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectUsageTable, Long> knownIndividualsCount =
		createColumn(
			"knownIndividualsCount", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<FaroProjectUsageTable, String> monthDateKey =
		createColumn(
			"monthDateKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectUsageTable, Long> pageViewsCount =
		createColumn(
			"pageViewsCount", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectUsageTable, Long> usageTime = createColumn(
		"usageTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);

	private FaroProjectUsageTable() {
		super("OSBFaro_FaroProjectUsage", FaroProjectUsageTable::new);
	}

}