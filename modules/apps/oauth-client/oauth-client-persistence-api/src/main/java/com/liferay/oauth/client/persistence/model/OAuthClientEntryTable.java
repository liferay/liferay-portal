/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OAuthClientEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientEntry
 * @generated
 */
public class OAuthClientEntryTable extends BaseTable<OAuthClientEntryTable> {

	public static final OAuthClientEntryTable INSTANCE =
		new OAuthClientEntryTable();

	public final Column<OAuthClientEntryTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<OAuthClientEntryTable, Long> oAuthClientEntryId =
		createColumn(
			"oAuthClientEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<OAuthClientEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, String>
		authRequestParametersJSON = createColumn(
			"authRequestParametersJSON", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, String> authServerWellKnownURI =
		createColumn(
			"authServerWellKnownURI", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, String> clientId = createColumn(
		"clientId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, Clob> customClaimsJSON =
		createColumn(
			"customClaimsJSON", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, Clob> infoJSON = createColumn(
		"infoJSON", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, Long> metadataCacheTime =
		createColumn(
			"metadataCacheTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, String> oidcUserInfoMapperJSON =
		createColumn(
			"oidcUserInfoMapperJSON", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<OAuthClientEntryTable, String>
		tokenRequestParametersJSON = createColumn(
			"tokenRequestParametersJSON", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);

	private OAuthClientEntryTable() {
		super("OAuthClientEntry", OAuthClientEntryTable::new);
	}

}