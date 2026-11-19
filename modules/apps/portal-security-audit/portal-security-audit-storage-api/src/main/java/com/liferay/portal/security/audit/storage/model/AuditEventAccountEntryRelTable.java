/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;Audit_EventAccountEntryRel&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see AuditEventAccountEntryRel
 * @generated
 */
public class AuditEventAccountEntryRelTable
	extends BaseTable<AuditEventAccountEntryRelTable> {

	public static final AuditEventAccountEntryRelTable INSTANCE =
		new AuditEventAccountEntryRelTable();

	public final Column<AuditEventAccountEntryRelTable, Long>
		auditEventAccountEntryRelId = createColumn(
			"auditEventAccountEntryRelId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<AuditEventAccountEntryRelTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<AuditEventAccountEntryRelTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<AuditEventAccountEntryRelTable, Long> accountEntryId =
		createColumn(
			"accountEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<AuditEventAccountEntryRelTable, Long> auditEventId =
		createColumn(
			"auditEventId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);

	private AuditEventAccountEntryRelTable() {
		super(
			"Audit_EventAccountEntryRel", AuditEventAccountEntryRelTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-642551531