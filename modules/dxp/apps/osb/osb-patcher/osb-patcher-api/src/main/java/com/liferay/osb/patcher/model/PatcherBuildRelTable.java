/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;OSBPatcher_PatcherBuildRel&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildRel
 * @generated
 */
public class PatcherBuildRelTable extends BaseTable<PatcherBuildRelTable> {

	public static final PatcherBuildRelTable INSTANCE =
		new PatcherBuildRelTable();

	public final Column<PatcherBuildRelTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<PatcherBuildRelTable, Long> patcherBuildRelId =
		createColumn(
			"patcherBuildRelId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherBuildRelTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildRelTable, Long> childPatcherBuildId =
		createColumn(
			"childPatcherBuildId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildRelTable, Long> parentPatcherBuildId =
		createColumn(
			"parentPatcherBuildId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);

	private PatcherBuildRelTable() {
		super("OSBPatcher_PatcherBuildRel", PatcherBuildRelTable::new);
	}

}