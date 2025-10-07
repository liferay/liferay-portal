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
 * The table class for the &quot;SamlPeerBinding&quot; database table.
 *
 * @author Mika Koivisto
 * @see SamlPeerBinding
 * @generated
 */
public class SamlPeerBindingTable extends BaseTable<SamlPeerBindingTable> {

	public static final SamlPeerBindingTable INSTANCE =
		new SamlPeerBindingTable();

	public final Column<SamlPeerBindingTable, Long> samlPeerBindingId =
		createColumn(
			"samlPeerBindingId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<SamlPeerBindingTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SamlPeerBindingTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SamlPeerBindingTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SamlPeerBindingTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SamlPeerBindingTable, String> samlPeerEntityId =
		createColumn(
			"samlPeerEntityId", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SamlPeerBindingTable, Boolean> deleted = createColumn(
		"deleted", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<SamlPeerBindingTable, String> samlNameIdFormat =
		createColumn(
			"samlNameIdFormat", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SamlPeerBindingTable, String> samlNameIdNameQualifier =
		createColumn(
			"samlNameIdNameQualifier", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SamlPeerBindingTable, String>
		samlNameIdSpNameQualifier = createColumn(
			"samlNameIdSpNameQualifier", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SamlPeerBindingTable, String> samlNameIdSpProvidedId =
		createColumn(
			"samlNameIdSpProvidedId", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SamlPeerBindingTable, String> samlNameIdValue =
		createColumn(
			"samlNameIdValue", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);

	private SamlPeerBindingTable() {
		super("SamlPeerBinding", SamlPeerBindingTable::new);
	}

}