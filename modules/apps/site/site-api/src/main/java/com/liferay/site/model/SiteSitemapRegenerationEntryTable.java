/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;SiteSitemapRegenerationEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see SiteSitemapRegenerationEntry
 * @generated
 */
public class SiteSitemapRegenerationEntryTable
	extends BaseTable<SiteSitemapRegenerationEntryTable> {

	public static final SiteSitemapRegenerationEntryTable INSTANCE =
		new SiteSitemapRegenerationEntryTable();

	public final Column<SiteSitemapRegenerationEntryTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<SiteSitemapRegenerationEntryTable, Long>
		siteSitemapRegenerationEntryId = createColumn(
			"siteSitemapRegenerationEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<SiteSitemapRegenerationEntryTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SiteSitemapRegenerationEntryTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SiteSitemapRegenerationEntryTable, String>
		assetTypeKey = createColumn(
			"assetTypeKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private SiteSitemapRegenerationEntryTable() {
		super(
			"SiteSitemapRegenerationEntry",
			SiteSitemapRegenerationEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1133725277