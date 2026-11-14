/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceCurrencyTable;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.qualifier.configuration.CommerceCurrencyCommerceQualifierConfiguration;
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
public class CommerceCurrencyCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<CommerceCurrency> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return CommerceCurrencyCommerceQualifierConfiguration.class;
	}

	@Override
	public String getKey() {
		return "currency";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return CommerceCurrencyTable.INSTANCE.name;
	}

	@Override
	public Class<CommerceCurrency> getModelClass() {
		return CommerceCurrency.class;
	}

	@Override
	public String getModelClassName() {
		return CommerceCurrency.class.getName();
	}

	@Override
	public ModelResourcePermission<CommerceCurrency>
		getModelResourcePermission() {

		return null;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _commerceCurrencyLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommerceCurrencyTable.INSTANCE.commerceCurrencyId;
	}

	@Override
	public Table getTable() {
		return CommerceCurrencyTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			CommerceCurrencyTable.INSTANCE.priority.descending()
		};
	}

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

}