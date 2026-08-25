/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.math.BigDecimal;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CPDefinitionOptionValueRel&quot; database table.
 *
 * @author Marco Leo
 * @see CPDefinitionOptionValueRel
 * @generated
 */
public class CPDefinitionOptionValueRelTable
	extends BaseTable<CPDefinitionOptionValueRelTable> {

	public static final CPDefinitionOptionValueRelTable INSTANCE =
		new CPDefinitionOptionValueRelTable();

	public final Column<CPDefinitionOptionValueRelTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CPDefinitionOptionValueRelTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CPDefinitionOptionValueRelTable, String> uuid =
		createColumn("uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, Long>
		CPDefinitionOptionValueRelId = createColumn(
			"CPDefinitionOptionValueRelId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CPDefinitionOptionValueRelTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, Long>
		CPDefinitionOptionRelId = createColumn(
			"CPDefinitionOptionRelId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, String>
		CPInstanceUuid = createColumn(
			"CPInstanceUuid", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, Long> CProductId =
		createColumn(
			"CProductId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, String> key =
		createColumn("key_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, String> name =
		createColumn("name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, Boolean> preselected =
		createColumn(
			"preselected", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, BigDecimal> price =
		createColumn(
			"price", BigDecimal.class, Types.DECIMAL, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, Double> priority =
		createColumn(
			"priority", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, BigDecimal> quantity =
		createColumn(
			"quantity", BigDecimal.class, Types.DECIMAL, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionValueRelTable, String>
		unitOfMeasureKey = createColumn(
			"unitOfMeasureKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);

	private CPDefinitionOptionValueRelTable() {
		super(
			"CPDefinitionOptionValueRel", CPDefinitionOptionValueRelTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1740820427