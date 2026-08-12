/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CTCollection&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see CTCollection
 * @generated
 */
public class CTCollectionTable extends BaseTable<CTCollectionTable> {

	public static final CTCollectionTable INSTANCE = new CTCollectionTable();

	public final Column<CTCollectionTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CTCollectionTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Long> ctCollectionId = createColumn(
		"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CTCollectionTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Long> ctRemoteId = createColumn(
		"ctRemoteId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Long> schemaVersionId = createColumn(
		"schemaVersionId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, String> description = createColumn(
		"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Long> onDemandUserId = createColumn(
		"onDemandUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Boolean> shareable = createColumn(
		"shareable", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Long> statusByUserId = createColumn(
		"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTCollectionTable, Date> statusDate = createColumn(
		"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private CTCollectionTable() {
		super("CTCollection", CTCollectionTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1472358053