/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OAuthClientASLocalMetadata&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientASLocalMetadata
 * @generated
 */
public class OAuthClientASLocalMetadataTable
	extends BaseTable<OAuthClientASLocalMetadataTable> {

	public static final OAuthClientASLocalMetadataTable INSTANCE =
		new OAuthClientASLocalMetadataTable();

	public final Column<OAuthClientASLocalMetadataTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<OAuthClientASLocalMetadataTable, Long>
		oAuthClientASLocalMetadataId = createColumn(
			"oAuthClientASLocalMetadataId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<OAuthClientASLocalMetadataTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, String>
		allowedGrantTypes = createColumn(
			"allowedGrantTypes", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, String> allowedScopes =
		createColumn(
			"allowedScopes", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, String> issuer =
		createColumn(
			"issuer", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, Boolean>
		localWellKnownEnabled = createColumn(
			"localWellKnownEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, String>
		localWellKnownURIOAS = createColumn(
			"localWellKnownURIOAS", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, String>
		localWellKnownURIOIC = createColumn(
			"localWellKnownURIOIC", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, String>
		metadataJSONAS = createColumn(
			"metadataJSONAS", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<OAuthClientASLocalMetadataTable, String>
		metadataJSONOIC = createColumn(
			"metadataJSONOIC", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);

	private OAuthClientASLocalMetadataTable() {
		super(
			"OAuthClientASLocalMetadata", OAuthClientASLocalMetadataTable::new);
	}

}