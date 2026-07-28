/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;CompoundPKEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see CompoundPKEntry
 * @generated
 */
public class CompoundPKEntryTable extends BaseTable<CompoundPKEntryTable> {

	public static final CompoundPKEntryTable INSTANCE =
		new CompoundPKEntryTable();

	public final Column<CompoundPKEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CompoundPKEntryTable, Long> classNameId = createColumn(
		"classNameId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CompoundPKEntryTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private CompoundPKEntryTable() {
		super("CompoundPKEntry", CompoundPKEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:835221958