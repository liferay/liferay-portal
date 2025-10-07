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
 * The table class for the &quot;SamlSpIdpConnection&quot; database table.
 *
 * @author Mika Koivisto
 * @see SamlSpIdpConnection
 * @generated
 */
public class SamlSpIdpConnectionTable
	extends BaseTable<SamlSpIdpConnectionTable> {

	public static final SamlSpIdpConnectionTable INSTANCE =
		new SamlSpIdpConnectionTable();

	public final Column<SamlSpIdpConnectionTable, Long> samlSpIdpConnectionId =
		createColumn(
			"samlSpIdpConnectionId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<SamlSpIdpConnectionTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, String> samlIdpEntityId =
		createColumn(
			"samlIdpEntityId", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Boolean>
		assertionSignatureRequired = createColumn(
			"assertionSignatureRequired", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Long> clockSkew =
		createColumn(
			"clockSkew", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Boolean> enabled =
		createColumn(
			"enabled", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Boolean> forceAuthn =
		createColumn(
			"forceAuthn", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Boolean> ldapImportEnabled =
		createColumn(
			"ldapImportEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Date> metadataUpdatedDate =
		createColumn(
			"metadataUpdatedDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, String> metadataUrl =
		createColumn(
			"metadataUrl", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Clob> metadataXml =
		createColumn(
			"metadataXml", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, String> nameIdFormat =
		createColumn(
			"nameIdFormat", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Boolean> signAuthnRequest =
		createColumn(
			"signAuthnRequest", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, Boolean>
		unknownUsersAreStrangers = createColumn(
			"unknownUsersAreStrangers", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, String>
		userAttributeMappings = createColumn(
			"userAttributeMappings", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SamlSpIdpConnectionTable, String>
		userIdentifierExpression = createColumn(
			"userIdentifierExpression", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);

	private SamlSpIdpConnectionTable() {
		super("SamlSpIdpConnection", SamlSpIdpConnectionTable::new);
	}

}