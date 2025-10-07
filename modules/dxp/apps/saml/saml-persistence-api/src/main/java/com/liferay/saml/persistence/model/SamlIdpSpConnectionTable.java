/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;SamlIdpSpConnection&quot; database table.
 *
 * @author Mika Koivisto
 * @see SamlIdpSpConnection
 * @generated
 */
public class SamlIdpSpConnectionTable
	extends BaseTable<SamlIdpSpConnectionTable> {

	public static final SamlIdpSpConnectionTable INSTANCE =
		new SamlIdpSpConnectionTable();

	public final Column<SamlIdpSpConnectionTable, Long> samlIdpSpConnectionId =
		createColumn(
			"samlIdpSpConnectionId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<SamlIdpSpConnectionTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, String> samlSpEntityId =
		createColumn(
			"samlSpEntityId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, Integer> assertionLifetime =
		createColumn(
			"assertionLifetime", Integer.class, Types.INTEGER,
			Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, String> attributeNames =
		createColumn(
			"attributeNames", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, Boolean> attributesEnabled =
		createColumn(
			"attributesEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, Boolean>
		attributesNamespaceEnabled = createColumn(
			"attributesNamespaceEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, Boolean> enabled =
		createColumn(
			"enabled", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, Boolean> encryptionForced =
		createColumn(
			"encryptionForced", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, Date> metadataUpdatedDate =
		createColumn(
			"metadataUpdatedDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, String> metadataUrl =
		createColumn(
			"metadataUrl", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, Clob> metadataXml =
		createColumn(
			"metadataXml", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, String> nameIdAttribute =
		createColumn(
			"nameIdAttribute", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SamlIdpSpConnectionTable, String> nameIdFormat =
		createColumn(
			"nameIdFormat", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private SamlIdpSpConnectionTable() {
		super("SamlIdpSpConnection", SamlIdpSpConnectionTable::new);
	}

}