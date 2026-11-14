/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.commerce.qualifier.configuration.CommerceTermEntryCommerceQualifierConfiguration;
import com.liferay.commerce.qualifier.metadata.BaseCommerceQualifierMetadata;
import com.liferay.commerce.qualifier.metadata.CommerceQualifierMetadata;
import com.liferay.commerce.term.constants.CommerceTermEntryConstants;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.model.CommerceTermEntryTable;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.PersistedModelLocalService;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(service = CommerceQualifierMetadata.class)
public class CommercePaymentTermCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<CommerceTermEntry> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return CommerceTermEntryCommerceQualifierConfiguration.class;
	}

	@Override
	public Predicate getFilterPredicate() {
		return CommerceTermEntryTable.INSTANCE.type.eq(
			CommerceTermEntryConstants.TYPE_PAYMENT_TERMS);
	}

	@Override
	public String getKey() {
		return "payment-term";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return CommerceTermEntryTable.INSTANCE.name;
	}

	@Override
	public Class<CommerceTermEntry> getModelClass() {
		return CommerceTermEntry.class;
	}

	@Override
	public String getModelClassName() {
		return CommerceTermEntry.class.getName();
	}

	@Override
	public ModelResourcePermission<CommerceTermEntry>
		getModelResourcePermission() {

		return _commerceTermEntryModelResourcePermission;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _commerceTermEntryLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommerceTermEntryTable.INSTANCE.commerceTermEntryId;
	}

	@Override
	public Table getTable() {
		return CommerceTermEntryTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			CommerceTermEntryTable.INSTANCE.commerceTermEntryId.descending()
		};
	}

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.term.model.CommerceTermEntry)"
	)
	private ModelResourcePermission<CommerceTermEntry>
		_commerceTermEntryModelResourcePermission;

}