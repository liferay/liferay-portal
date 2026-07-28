/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Blob;
import java.sql.Types;

/**
 * The table class for the &quot;ERCVersionedEntryVersion&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see ERCVersionedEntryVersion
 * @generated
 */
public class ERCVersionedEntryVersionTable
	extends BaseTable<ERCVersionedEntryVersionTable> {

	public static final ERCVersionedEntryVersionTable INSTANCE =
		new ERCVersionedEntryVersionTable();

	public final Column<ERCVersionedEntryVersionTable, Long>
		ercVersionedEntryVersionId = createColumn(
			"ercVersionedEntryVersionId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<ERCVersionedEntryVersionTable, Integer> version =
		createColumn(
			"version", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryVersionTable, String> uuid =
		createColumn("uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryVersionTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryVersionTable, Long>
		ercVersionedEntryId = createColumn(
			"ercVersionedEntryId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryVersionTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryVersionTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryVersionTable, Blob> blob =
		createColumn("blob_", Blob.class, Types.BLOB, Column.FLAG_DEFAULT);

	private ERCVersionedEntryVersionTable() {
		super("ERCVersionedEntryVersion", ERCVersionedEntryVersionTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-71320565