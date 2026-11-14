/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryTable;
import com.liferay.commerce.order.rule.service.COREntryLocalService;
import com.liferay.commerce.qualifier.configuration.COREntryCommerceQualifierConfiguration;
import com.liferay.commerce.qualifier.metadata.BaseCommerceQualifierMetadata;
import com.liferay.commerce.qualifier.metadata.CommerceQualifierMetadata;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
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
public class CommerceOrderRuleCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<COREntry> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return COREntryCommerceQualifierConfiguration.class;
	}

	@Override
	public String getKey() {
		return "order-rule";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return COREntryTable.INSTANCE.name;
	}

	@Override
	public Class<COREntry> getModelClass() {
		return COREntry.class;
	}

	@Override
	public String getModelClassName() {
		return COREntry.class.getName();
	}

	@Override
	public ModelResourcePermission<COREntry> getModelResourcePermission() {
		return _commerceTermEntryModelResourcePermission;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _corEntryLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return COREntryTable.INSTANCE.COREntryId;
	}

	@Override
	public Table getTable() {
		return COREntryTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			COREntryTable.INSTANCE.COREntryId.descending()
		};
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.order.rule.model.COREntry)"
	)
	private ModelResourcePermission<COREntry>
		_commerceTermEntryModelResourcePermission;

	@Reference
	private COREntryLocalService _corEntryLocalService;

}