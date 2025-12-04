/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OpenIdConnectSession&quot; database table.
 *
 * @author Arthur Chan
 * @see OpenIdConnectSession
 * @generated
 */
public class OpenIdConnectSessionTable
	extends BaseTable<OpenIdConnectSessionTable> {

	public static final OpenIdConnectSessionTable INSTANCE =
		new OpenIdConnectSessionTable();

	public final Column<OpenIdConnectSessionTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<OpenIdConnectSessionTable, Long>
		openIdConnectSessionId = createColumn(
			"openIdConnectSessionId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<OpenIdConnectSessionTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectSessionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectSessionTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectSessionTable, Clob> accessToken =
		createColumn(
			"accessToken", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectSessionTable, Date>
		accessTokenExpirationDate = createColumn(
			"accessTokenExpirationDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectSessionTable, String>
		authServerWellKnownURI = createColumn(
			"authServerWellKnownURI", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectSessionTable, String> clientId =
		createColumn(
			"clientId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectSessionTable, Clob> idToken = createColumn(
		"idToken", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectSessionTable, String> issuer =
		createColumn(
			"issuer", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectSessionTable, String> refreshToken =
		createColumn(
			"refreshToken", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectSessionTable, String> sessionId =
		createColumn(
			"sessionId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private OpenIdConnectSessionTable() {
		super("OpenIdConnectSession", OpenIdConnectSessionTable::new);
	}

}