/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CommerceChannel&quot; database table.
 *
 * @author Marco Leo
 * @see CommerceChannel
 * @generated
 */
public class CommerceChannelTable extends BaseTable<CommerceChannelTable> {

	public static final CommerceChannelTable INSTANCE =
		new CommerceChannelTable();

	public final Column<CommerceChannelTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CommerceChannelTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CommerceChannelTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, Long> commerceChannelId =
		createColumn(
			"commerceChannelId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CommerceChannelTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, Long> accountEntryId =
		createColumn(
			"accountEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, Long> siteGroupId = createColumn(
		"siteGroupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, String> type = createColumn(
		"type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, String> typeSettings =
		createColumn(
			"typeSettings", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, String> commerceCurrencyCode =
		createColumn(
			"commerceCurrencyCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, String> priceDisplayType =
		createColumn(
			"priceDisplayType", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, Boolean> discountsTargetNetPrice =
		createColumn(
			"discountsTargetNetPrice", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CommerceChannelTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private CommerceChannelTable() {
		super("CommerceChannel", CommerceChannelTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1081342436