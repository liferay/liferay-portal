/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;AudiencesEntryGroupRel&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryGroupRel
 * @generated
 */
public class AudiencesEntryGroupRelTable
	extends BaseTable<AudiencesEntryGroupRelTable> {

	public static final AudiencesEntryGroupRelTable INSTANCE =
		new AudiencesEntryGroupRelTable();

	public final Column<AudiencesEntryGroupRelTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<AudiencesEntryGroupRelTable, Long>
		audiencesEntryGroupRelId = createColumn(
			"audiencesEntryGroupRelId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<AudiencesEntryGroupRelTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<AudiencesEntryGroupRelTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<AudiencesEntryGroupRelTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<AudiencesEntryGroupRelTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<AudiencesEntryGroupRelTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<AudiencesEntryGroupRelTable, String> audienceEntryERC =
		createColumn(
			"audienceEntryERC", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<AudiencesEntryGroupRelTable, String> groupERC =
		createColumn(
			"groupERC", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private AudiencesEntryGroupRelTable() {
		super("AudiencesEntryGroupRel", AudiencesEntryGroupRelTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2131265715