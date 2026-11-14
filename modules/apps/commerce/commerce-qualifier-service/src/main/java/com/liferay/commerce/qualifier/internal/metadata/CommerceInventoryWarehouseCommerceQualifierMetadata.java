/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseTable;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.qualifier.configuration.CommerceInventoryWarehouseCommerceQualifierConfiguration;
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
public class CommerceInventoryWarehouseCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<CommerceInventoryWarehouse> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return CommerceInventoryWarehouseCommerceQualifierConfiguration.class;
	}

	@Override
	public String getKey() {
		return "inventory-warehouse";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return CommerceInventoryWarehouseTable.INSTANCE.name;
	}

	@Override
	public Class<CommerceInventoryWarehouse> getModelClass() {
		return CommerceInventoryWarehouse.class;
	}

	@Override
	public String getModelClassName() {
		return CommerceInventoryWarehouse.class.getName();
	}

	@Override
	public ModelResourcePermission<CommerceInventoryWarehouse>
		getModelResourcePermission() {

		return _commerceInventoryWarehouseModelResourcePermission;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _commerceInventoryWarehouseLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommerceInventoryWarehouseTable.INSTANCE.
			commerceInventoryWarehouseId;
	}

	@Override
	public Table getTable() {
		return CommerceInventoryWarehouseTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			CommerceInventoryWarehouseTable.INSTANCE.
				commerceInventoryWarehouseId.descending()
		};
	}

	@Reference
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouse)"
	)
	private ModelResourcePermission<CommerceInventoryWarehouse>
		_commerceInventoryWarehouseModelResourcePermission;

}