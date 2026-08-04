/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CPDefinition&quot; database table.
 *
 * @author Marco Leo
 * @see CPDefinition
 * @generated
 */
public class CPDefinitionTable extends BaseTable<CPDefinitionTable> {

	public static final CPDefinitionTable INSTANCE = new CPDefinitionTable();

	public final Column<CPDefinitionTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CPDefinitionTable, Long> ctCollectionId = createColumn(
		"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CPDefinitionTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, String> defaultLanguageId =
		createColumn(
			"defaultLanguageId", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Long> CPDefinitionId = createColumn(
		"CPDefinitionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CPDefinitionTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, String>
		CProductExternalReferenceCode = createColumn(
			"CProductExternalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Long> CProductId = createColumn(
		"CProductId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Long> CPTaxCategoryId = createColumn(
		"CPTaxCategoryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> accountGroupFilterEnabled =
		createColumn(
			"accountGroupFilterEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> availableIndividually =
		createColumn(
			"availableIndividually", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> channelFilterEnabled =
		createColumn(
			"channelFilterEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, String> DDMStructureKey =
		createColumn(
			"DDMStructureKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Long> deliveryMaxSubscriptionCycles =
		createColumn(
			"deliveryMaxSubscriptionCycles", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean>
		deliverySubscriptionEnabled = createColumn(
			"deliverySubscriptionEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Integer> deliverySubscriptionLength =
		createColumn(
			"deliverySubscriptionLength", Integer.class, Types.INTEGER,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, String> deliverySubscriptionType =
		createColumn(
			"deliverySubscriptionType", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, String>
		deliverySubscriptionTypeSettings = createColumn(
			"deliverySubTypeSettings", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Double> depth = createColumn(
		"depth", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Date> displayDate = createColumn(
		"displayDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Date> expirationDate = createColumn(
		"expirationDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> freeShipping = createColumn(
		"freeShipping", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Double> height = createColumn(
		"height", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> ignoreSKUCombinations =
		createColumn(
			"ignoreSKUCombinations", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Long> maxSubscriptionCycles =
		createColumn(
			"maxSubscriptionCycles", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, String> productTypeName =
		createColumn(
			"productTypeName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> published = createColumn(
		"published", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> shipSeparately =
		createColumn(
			"shipSeparately", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> shippable = createColumn(
		"shippable", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Double> shippingExtraPrice =
		createColumn(
			"shippingExtraPrice", Double.class, Types.DOUBLE,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> subscriptionEnabled =
		createColumn(
			"subscriptionEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Integer> subscriptionLength =
		createColumn(
			"subscriptionLength", Integer.class, Types.INTEGER,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, String> subscriptionType =
		createColumn(
			"subscriptionType", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Clob> subscriptionTypeSettings =
		createColumn(
			"subscriptionTypeSettings", Clob.class, Types.CLOB,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> taxExempt = createColumn(
		"taxExempt", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Boolean> telcoOrElectronics =
		createColumn(
			"telcoOrElectronics", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Integer> version = createColumn(
		"version", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Double> weight = createColumn(
		"weight", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Double> width = createColumn(
		"width", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Date> lastPublishDate = createColumn(
		"lastPublishDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Long> statusByUserId = createColumn(
		"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, String> statusByUserName =
		createColumn(
			"statusByUserName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionTable, Date> statusDate = createColumn(
		"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private CPDefinitionTable() {
		super("CPDefinition", CPDefinitionTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1285525045