/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OAuth2Authorization&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2Authorization
 * @generated
 */
public class OAuth2AuthorizationTable
	extends BaseTable<OAuth2AuthorizationTable> {

	public static final OAuth2AuthorizationTable INSTANCE =
		new OAuth2AuthorizationTable();

	public final Column<OAuth2AuthorizationTable, Long> oAuth2AuthorizationId =
		createColumn(
			"oAuth2AuthorizationId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<OAuth2AuthorizationTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Long> oAuth2ApplicationId =
		createColumn(
			"oAuth2ApplicationId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Long>
		oAuth2ApplicationScopeAliasesId = createColumn(
			"oA2AScopeAliasesId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Clob> accessTokenContent =
		createColumn(
			"accessTokenContent", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Long> accessTokenContentHash =
		createColumn(
			"accessTokenContentHash", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Date> accessTokenCreateDate =
		createColumn(
			"accessTokenCreateDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Date>
		accessTokenExpirationDate = createColumn(
			"accessTokenExpirationDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Clob> audiences =
		createColumn("audiences", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, String> remoteHostInfo =
		createColumn(
			"remoteHostInfo", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, String> remoteIPInfo =
		createColumn(
			"remoteIPInfo", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Clob> refreshTokenContent =
		createColumn(
			"refreshTokenContent", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Long>
		refreshTokenContentHash = createColumn(
			"refreshTokenContentHash", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Date> refreshTokenCreateDate =
		createColumn(
			"refreshTokenCreateDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, Date>
		refreshTokenExpirationDate = createColumn(
			"refreshTokenExpirationDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<OAuth2AuthorizationTable, String>
		rememberDeviceContent = createColumn(
			"rememberDeviceContent", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);

	private OAuth2AuthorizationTable() {
		super("OAuth2Authorization", OAuth2AuthorizationTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-48770570