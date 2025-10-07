/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;SamlSpMessage&quot; database table.
 *
 * @author Mika Koivisto
 * @see SamlSpMessage
 * @generated
 */
public class SamlSpMessageTable extends BaseTable<SamlSpMessageTable> {

	public static final SamlSpMessageTable INSTANCE = new SamlSpMessageTable();

	public final Column<SamlSpMessageTable, Long> samlSpMessageId =
		createColumn(
			"samlSpMessageId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<SamlSpMessageTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SamlSpMessageTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SamlSpMessageTable, String> samlIdpEntityId =
		createColumn(
			"samlIdpEntityId", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SamlSpMessageTable, Date> expirationDate = createColumn(
		"expirationDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SamlSpMessageTable, String> samlIdpResponseKey =
		createColumn(
			"samlIdpResponseKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);

	private SamlSpMessageTable() {
		super("SamlSpMessage", SamlSpMessageTable::new);
	}

}