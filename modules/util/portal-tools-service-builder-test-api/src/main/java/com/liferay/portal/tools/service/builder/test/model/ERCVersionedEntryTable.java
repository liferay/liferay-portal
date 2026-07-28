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
 * The table class for the &quot;ERCVersionedEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see ERCVersionedEntry
 * @generated
 */
public class ERCVersionedEntryTable extends BaseTable<ERCVersionedEntryTable> {

	public static final ERCVersionedEntryTable INSTANCE =
		new ERCVersionedEntryTable();

	public final Column<ERCVersionedEntryTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<ERCVersionedEntryTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryTable, Long> headId = createColumn(
		"headId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryTable, Boolean> head = createColumn(
		"head", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryTable, Long> ercVersionedEntryId =
		createColumn(
			"ercVersionedEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<ERCVersionedEntryTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ERCVersionedEntryTable, Blob> blob = createColumn(
		"blob_", Blob.class, Types.BLOB, Column.FLAG_DEFAULT);

	private ERCVersionedEntryTable() {
		super("ERCVersionedEntry", ERCVersionedEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1767433638