/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.commerce.qualifier.configuration.CommerceShippingFixedOptionCommerceQualifierConfiguration;
import com.liferay.commerce.qualifier.metadata.BaseCommerceQualifierMetadata;
import com.liferay.commerce.qualifier.metadata.CommerceQualifierMetadata;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionTable;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
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
public class CommerceShippingFixedOptionCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<CommerceShippingFixedOption> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return CommerceShippingFixedOptionCommerceQualifierConfiguration.class;
	}

	@Override
	public String getKey() {
		return "shipping-fixed-option";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return CommerceShippingFixedOptionTable.INSTANCE.name;
	}

	@Override
	public Class<CommerceShippingFixedOption> getModelClass() {
		return CommerceShippingFixedOption.class;
	}

	@Override
	public String getModelClassName() {
		return CommerceShippingFixedOption.class.getName();
	}

	@Override
	public ModelResourcePermission<CommerceShippingFixedOption>
		getModelResourcePermission() {

		return null;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _commerceShippingFixedOptionLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommerceShippingFixedOptionTable.INSTANCE.
			commerceShippingFixedOptionId;
	}

	@Override
	public Table getTable() {
		return CommerceShippingFixedOptionTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			CommerceShippingFixedOptionTable.INSTANCE.priority.descending()
		};
	}

	@Reference
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

}