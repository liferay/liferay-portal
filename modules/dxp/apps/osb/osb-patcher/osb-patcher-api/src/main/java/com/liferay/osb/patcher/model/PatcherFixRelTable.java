/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;OSBPatcher_PatcherFixRel&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixRel
 * @generated
 */
public class PatcherFixRelTable extends BaseTable<PatcherFixRelTable> {

	public static final PatcherFixRelTable INSTANCE = new PatcherFixRelTable();

	public final Column<PatcherFixRelTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<PatcherFixRelTable, Long> patcherFixRelId =
		createColumn(
			"patcherFixRelId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherFixRelTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherFixRelTable, Long> childPatcherFixId =
		createColumn(
			"childPatcherFixId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherFixRelTable, Long> parentPatcherFixId =
		createColumn(
			"parentPatcherFixId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);

	private PatcherFixRelTable() {
		super("OSBPatcher_PatcherFixRel", PatcherFixRelTable::new);
	}

}