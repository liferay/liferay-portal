/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelTable;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.qualifier.configuration.CommercePaymentMethodGroupRelCommerceQualifierConfiguration;
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
public class CommercePaymentMethodGroupRelCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<CommercePaymentMethodGroupRel> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return CommercePaymentMethodGroupRelCommerceQualifierConfiguration.
			class;
	}

	@Override
	public String getKey() {
		return "payment-method-group-rel";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return CommercePaymentMethodGroupRelTable.INSTANCE.name;
	}

	@Override
	public Class<CommercePaymentMethodGroupRel> getModelClass() {
		return CommercePaymentMethodGroupRel.class;
	}

	@Override
	public String getModelClassName() {
		return CommercePaymentMethodGroupRel.class.getName();
	}

	@Override
	public ModelResourcePermission<CommercePaymentMethodGroupRel>
		getModelResourcePermission() {

		return null;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _commercePaymentMethodGroupRelLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommercePaymentMethodGroupRelTable.INSTANCE.
			commercePaymentMethodGroupRelId;
	}

	@Override
	public Table getTable() {
		return CommercePaymentMethodGroupRelTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			CommercePaymentMethodGroupRelTable.INSTANCE.priority.descending()
		};
	}

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

}