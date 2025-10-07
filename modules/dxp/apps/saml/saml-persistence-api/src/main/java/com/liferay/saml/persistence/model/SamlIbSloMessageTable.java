/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;SamlIbSloMessage&quot; database table.
 *
 * @author Mika Koivisto
 * @see SamlIbSloMessage
 * @generated
 */
public class SamlIbSloMessageTable extends BaseTable<SamlIbSloMessageTable> {

	public static final SamlIbSloMessageTable INSTANCE =
		new SamlIbSloMessageTable();

	public final Column<SamlIbSloMessageTable, Long> samlIbSloMessageId =
		createColumn(
			"samlIbSloMessageId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<SamlIbSloMessageTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SamlIbSloMessageTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SamlIbSloMessageTable, String> samlIdpEntityId =
		createColumn(
			"samlIdpEntityId", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SamlIbSloMessageTable, Clob> logoutRequestXml =
		createColumn(
			"logoutRequestXml", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<SamlIbSloMessageTable, String> samlIdpSessionIndex =
		createColumn(
			"samlIdpSessionIndex", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);

	private SamlIbSloMessageTable() {
		super("SamlIbSloMessage", SamlIbSloMessageTable::new);
	}

}