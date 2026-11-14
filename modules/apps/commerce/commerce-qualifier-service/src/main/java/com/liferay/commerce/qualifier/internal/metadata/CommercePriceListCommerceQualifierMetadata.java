/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListTable;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.qualifier.configuration.CommercePriceListCommerceQualifierConfiguration;
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
public class CommercePriceListCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<CommercePriceList> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return CommercePriceListCommerceQualifierConfiguration.class;
	}

	@Override
	public String getKey() {
		return "price-list";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return CommercePriceListTable.INSTANCE.name;
	}

	@Override
	public Class<CommercePriceList> getModelClass() {
		return CommercePriceList.class;
	}

	@Override
	public String getModelClassName() {
		return CommercePriceList.class.getName();
	}

	@Override
	public ModelResourcePermission<CommercePriceList>
		getModelResourcePermission() {

		return _commercePriceListModelResourcePermission;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _commercePriceListLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommercePriceListTable.INSTANCE.commercePriceListId;
	}

	@Override
	public Table getTable() {
		return CommercePriceListTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			CommercePriceListTable.INSTANCE.catalogBasePriceList.ascending(),
			CommercePriceListTable.INSTANCE.priority.descending()
		};
	}

	@Reference
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.price.list.model.CommercePriceList)"
	)
	private ModelResourcePermission<CommercePriceList>
		_commercePriceListModelResourcePermission;

}