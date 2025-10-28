/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OpenIdConnectUser&quot; database table.
 *
 * @author Arthur Chan
 * @see OpenIdConnectUser
 * @generated
 */
public class OpenIdConnectUserTable extends BaseTable<OpenIdConnectUserTable> {

	public static final OpenIdConnectUserTable INSTANCE =
		new OpenIdConnectUserTable();

	public final Column<OpenIdConnectUserTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<OpenIdConnectUserTable, Long> openIdConnectUserId =
		createColumn(
			"openIdConnectUserId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<OpenIdConnectUserTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectUserTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectUserTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectUserTable, String> issuer = createColumn(
		"issuer", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OpenIdConnectUserTable, String> subject = createColumn(
		"subject", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private OpenIdConnectUserTable() {
		super("OpenIdConnectUser", OpenIdConnectUserTable::new);
	}

}