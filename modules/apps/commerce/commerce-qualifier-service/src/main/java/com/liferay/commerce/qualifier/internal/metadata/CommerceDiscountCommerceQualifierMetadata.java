/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountTable;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.qualifier.configuration.CommerceDiscountCommerceQualifierConfiguration;
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
public class CommerceDiscountCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<CommerceDiscount> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return CommerceDiscountCommerceQualifierConfiguration.class;
	}

	@Override
	public String getKey() {
		return "discount";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return CommerceDiscountTable.INSTANCE.title;
	}

	@Override
	public Class<CommerceDiscount> getModelClass() {
		return CommerceDiscount.class;
	}

	@Override
	public String getModelClassName() {
		return CommerceDiscount.class.getName();
	}

	@Override
	public ModelResourcePermission<CommerceDiscount>
		getModelResourcePermission() {

		return _commerceDiscountModelResourcePermission;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _commerceDiscountLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommerceDiscountTable.INSTANCE.commerceDiscountId;
	}

	@Override
	public Table getTable() {
		return CommerceDiscountTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			CommerceDiscountTable.INSTANCE.commerceDiscountId.descending()
		};
	}

	@Reference
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.discount.model.CommerceDiscount)"
	)
	private ModelResourcePermission<CommerceDiscount>
		_commerceDiscountModelResourcePermission;

}