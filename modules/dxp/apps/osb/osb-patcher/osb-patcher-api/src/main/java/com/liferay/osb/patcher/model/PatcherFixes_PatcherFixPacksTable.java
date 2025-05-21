/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;PatcherFixes_PatcherFixPacks&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFix
 * @see PatcherFixPack
 * @generated
 */
public class PatcherFixes_PatcherFixPacksTable
	extends BaseTable<PatcherFixes_PatcherFixPacksTable> {

	public static final PatcherFixes_PatcherFixPacksTable INSTANCE =
		new PatcherFixes_PatcherFixPacksTable();

	public final Column<PatcherFixes_PatcherFixPacksTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherFixes_PatcherFixPacksTable, Long> patcherFixId =
		createColumn(
			"patcherFixId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherFixes_PatcherFixPacksTable, Long>
		patcherFixPackId = createColumn(
			"patcherFixPackId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);

	private PatcherFixes_PatcherFixPacksTable() {
		super(
			"PatcherFixes_PatcherFixPacks",
			PatcherFixes_PatcherFixPacksTable::new);
	}

}