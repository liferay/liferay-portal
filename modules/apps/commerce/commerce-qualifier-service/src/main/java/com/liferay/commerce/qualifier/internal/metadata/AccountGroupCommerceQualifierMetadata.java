/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupTable;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.qualifier.configuration.AccountGroupCommerceQualifierConfiguration;
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
public class AccountGroupCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<AccountGroup> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return AccountGroupCommerceQualifierConfiguration.class;
	}

	@Override
	public String getKey() {
		return "account-group";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return AccountGroupTable.INSTANCE.name;
	}

	@Override
	public Class<AccountGroup> getModelClass() {
		return AccountGroup.class;
	}

	@Override
	public String getModelClassName() {
		return AccountGroup.class.getName();
	}

	@Override
	public ModelResourcePermission<AccountGroup> getModelResourcePermission() {
		return _accountGroupModelResourcePermission;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _accountGroupLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return AccountGroupTable.INSTANCE.accountGroupId;
	}

	@Override
	public Table getTable() {
		return AccountGroupTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			AccountGroupTable.INSTANCE.accountGroupId.descending()
		};
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.account.model.AccountGroup)"
	)
	private ModelResourcePermission<AccountGroup>
		_accountGroupModelResourcePermission;

}