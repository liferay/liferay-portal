/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;DepotEntryGroupRel&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryGroupRel
 * @generated
 */
public class DepotEntryGroupRelTable
	extends BaseTable<DepotEntryGroupRelTable> {

	public static final DepotEntryGroupRelTable INSTANCE =
		new DepotEntryGroupRelTable();

	public final Column<DepotEntryGroupRelTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<DepotEntryGroupRelTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<DepotEntryGroupRelTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Long> depotEntryGroupRelId =
		createColumn(
			"depotEntryGroupRelId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<DepotEntryGroupRelTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Boolean>
		ddmStructuresAvailable = createColumn(
			"ddmStructuresAvailable", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Long> depotEntryId =
		createColumn(
			"depotEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Boolean> searchable =
		createColumn(
			"searchable", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Long> toGroupId = createColumn(
		"toGroupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Integer> type = createColumn(
		"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<DepotEntryGroupRelTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);

	private DepotEntryGroupRelTable() {
		super("DepotEntryGroupRel", DepotEntryGroupRelTable::new);
	}

}