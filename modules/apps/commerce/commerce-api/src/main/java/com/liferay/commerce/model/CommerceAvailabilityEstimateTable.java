/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CommerceAvailabilityEstimate&quot; database table.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAvailabilityEstimate
 * @generated
 */
public class CommerceAvailabilityEstimateTable
	extends BaseTable<CommerceAvailabilityEstimateTable> {

	public static final CommerceAvailabilityEstimateTable INSTANCE =
		new CommerceAvailabilityEstimateTable();

	public final Column<CommerceAvailabilityEstimateTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CommerceAvailabilityEstimateTable, String> uuid =
		createColumn("uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceAvailabilityEstimateTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceAvailabilityEstimateTable, Long>
		commerceAvailabilityEstimateId = createColumn(
			"commerceAvailabilityEstimateId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CommerceAvailabilityEstimateTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceAvailabilityEstimateTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceAvailabilityEstimateTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceAvailabilityEstimateTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceAvailabilityEstimateTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceAvailabilityEstimateTable, String> title =
		createColumn("title", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceAvailabilityEstimateTable, Double> priority =
		createColumn(
			"priority", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CommerceAvailabilityEstimateTable, Date>
		lastPublishDate = createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<CommerceAvailabilityEstimateTable, Integer> status =
		createColumn(
			"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private CommerceAvailabilityEstimateTable() {
		super(
			"CommerceAvailabilityEstimate",
			CommerceAvailabilityEstimateTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1452020066