/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;CTSParent&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see CTSParent
 * @generated
 */
public class CTSParentTable extends BaseTable<CTSParentTable> {

	public static final CTSParentTable INSTANCE = new CTSParentTable();

	public final Column<CTSParentTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CTSParentTable, Long> ctCollectionId = createColumn(
		"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CTSParentTable, Long> ctsParentId = createColumn(
		"ctsParentId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CTSParentTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTSParentTable, Long> ctsGrandParentId = createColumn(
		"ctsGrandParentId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTSParentTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private CTSParentTable() {
		super("CTSParent", CTSParentTable::new);
	}

}