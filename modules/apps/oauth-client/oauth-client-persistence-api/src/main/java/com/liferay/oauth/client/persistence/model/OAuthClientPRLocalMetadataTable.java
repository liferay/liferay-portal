/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OAuthClientPRLocalMetadata&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadata
 * @generated
 */
public class OAuthClientPRLocalMetadataTable
	extends BaseTable<OAuthClientPRLocalMetadataTable> {

	public static final OAuthClientPRLocalMetadataTable INSTANCE =
		new OAuthClientPRLocalMetadataTable();

	public final Column<OAuthClientPRLocalMetadataTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<OAuthClientPRLocalMetadataTable, String> uuid =
		createColumn("uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuthClientPRLocalMetadataTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<OAuthClientPRLocalMetadataTable, Long>
		oAuthClientPRLocalMetadataId = createColumn(
			"oAuthClientPRLocalMetadataId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<OAuthClientPRLocalMetadataTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OAuthClientPRLocalMetadataTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OAuthClientPRLocalMetadataTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuthClientPRLocalMetadataTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<OAuthClientPRLocalMetadataTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<OAuthClientPRLocalMetadataTable, Boolean>
		localWellKnownEnabled = createColumn(
			"localWellKnownEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<OAuthClientPRLocalMetadataTable, String>
		localWellKnownURI = createColumn(
			"localWellKnownURI", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<OAuthClientPRLocalMetadataTable, Clob> metadataJSON =
		createColumn(
			"metadataJSON", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<OAuthClientPRLocalMetadataTable, String> resource =
		createColumn(
			"resource", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private OAuthClientPRLocalMetadataTable() {
		super(
			"OAuthClientPRLocalMetadata", OAuthClientPRLocalMetadataTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1774357834