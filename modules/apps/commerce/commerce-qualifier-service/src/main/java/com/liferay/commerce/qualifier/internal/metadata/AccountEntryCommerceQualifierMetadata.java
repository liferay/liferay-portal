/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryTable;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.qualifier.configuration.AccountEntryCommerceQualifierConfiguration;
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
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Riccardo Alberti
 */
@Component(service = CommerceQualifierMetadata.class)
public class AccountEntryCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<AccountEntry> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return AccountEntryCommerceQualifierConfiguration.class;
	}

	@Override
	public String getKey() {
		return "account";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return AccountEntryTable.INSTANCE.name;
	}

	@Override
	public Class<AccountEntry> getModelClass() {
		return AccountEntry.class;
	}

	@Override
	public String getModelClassName() {
		return AccountEntry.class.getName();
	}

	@Override
	public ModelResourcePermission<AccountEntry> getModelResourcePermission() {
		return _accountEntryModelResourcePermission;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _accountEntryLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return AccountEntryTable.INSTANCE.accountEntryId;
	}

	@Override
	public Table getTable() {
		return AccountEntryTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			AccountEntryTable.INSTANCE.accountEntryId.descending()
		};
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

}