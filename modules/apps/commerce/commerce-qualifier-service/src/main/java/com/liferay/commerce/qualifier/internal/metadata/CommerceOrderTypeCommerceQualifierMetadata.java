/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeTable;
import com.liferay.commerce.qualifier.configuration.CommerceOrderTypeCommerceQualifierConfiguration;
import com.liferay.commerce.qualifier.metadata.BaseCommerceQualifierMetadata;
import com.liferay.commerce.qualifier.metadata.CommerceQualifierMetadata;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
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
public class CommerceOrderTypeCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<CommerceOrderType> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return CommerceOrderTypeCommerceQualifierConfiguration.class;
	}

	@Override
	public String getKey() {
		return "order-type";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return CommerceOrderTypeTable.INSTANCE.name;
	}

	@Override
	public Class<CommerceOrderType> getModelClass() {
		return CommerceOrderType.class;
	}

	@Override
	public String getModelClassName() {
		return CommerceOrderType.class.getName();
	}

	@Override
	public ModelResourcePermission<CommerceOrderType>
		getModelResourcePermission() {

		return _commerceOrderTypeModelResourcePermission;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _commerceOrderTypeLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId;
	}

	@Override
	public Table getTable() {
		return CommerceOrderTypeTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.descending()
		};
	}

	@Reference
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrderType)"
	)
	private ModelResourcePermission<CommerceOrderType>
		_commerceOrderTypeModelResourcePermission;

}