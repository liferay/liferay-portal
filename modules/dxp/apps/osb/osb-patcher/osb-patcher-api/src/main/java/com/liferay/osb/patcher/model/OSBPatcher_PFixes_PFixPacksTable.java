/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;OSBPatcher_PFixes_PFixPacks&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFix
 * @see PatcherFixPack
 * @generated
 */
public class OSBPatcher_PFixes_PFixPacksTable
	extends BaseTable<OSBPatcher_PFixes_PFixPacksTable> {

	public static final OSBPatcher_PFixes_PFixPacksTable INSTANCE =
		new OSBPatcher_PFixes_PFixPacksTable();

	public final Column<OSBPatcher_PFixes_PFixPacksTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<OSBPatcher_PFixes_PFixPacksTable, Long> patcherFixId =
		createColumn(
			"patcherFixId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<OSBPatcher_PFixes_PFixPacksTable, Long>
		patcherFixPackId = createColumn(
			"patcherFixPackId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);

	private OSBPatcher_PFixes_PFixPacksTable() {
		super(
			"OSBPatcher_PFixes_PFixPacks",
			OSBPatcher_PFixes_PFixPacksTable::new);
	}

}