/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OSBPatcher_PatcherTicketHint&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherTicketHint
 * @generated
 */
public class PatcherTicketHintTable extends BaseTable<PatcherTicketHintTable> {

	public static final PatcherTicketHintTable INSTANCE =
		new PatcherTicketHintTable();

	public final Column<PatcherTicketHintTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<PatcherTicketHintTable, Long> patcherTicketHintId =
		createColumn(
			"patcherTicketHintId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<PatcherTicketHintTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherTicketHintTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherTicketHintTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherTicketHintTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherTicketHintTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherTicketHintTable, Long> patcherProductVersionId =
		createColumn(
			"patcherProductVersionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<PatcherTicketHintTable, String> script = createColumn(
		"script", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private PatcherTicketHintTable() {
		super("OSBPatcher_PatcherTicketHint", PatcherTicketHintTable::new);
	}

}