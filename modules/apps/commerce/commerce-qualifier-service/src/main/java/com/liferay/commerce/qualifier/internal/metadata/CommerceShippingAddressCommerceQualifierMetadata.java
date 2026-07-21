/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.commerce.qualifier.configuration.AddressCommerceQualifierConfiguration;
import com.liferay.commerce.qualifier.metadata.BaseCommerceQualifierMetadata;
import com.liferay.commerce.qualifier.metadata.CommerceQualifierMetadata;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.AddressTable;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	configurationPid = "com.liferay.commerce.qualifier.configuration.AddressCommerceQualifierConfiguration",
	service = CommerceQualifierMetadata.class
)
public class CommerceShippingAddressCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<Address> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return AddressCommerceQualifierConfiguration.class;
	}

	@Override
	public Predicate getFilterPredicate() {
		return AddressTable.INSTANCE.listTypeId.in(
			_getListTypeIds(
				AccountListTypeConstants.
					ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING));
	}

	@Override
	public String getKey() {
		return "shipping-address";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return AddressTable.INSTANCE.name;
	}

	@Override
	public Class<Address> getModelClass() {
		return Address.class;
	}

	@Override
	public String getModelClassName() {
		return Address.class.getName();
	}

	@Override
	public ModelResourcePermission<Address> getModelResourcePermission() {
		return null;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _addressLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return AddressTable.INSTANCE.addressId;
	}

	@Override
	public Table getTable() {
		return AddressTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			AddressTable.INSTANCE.addressId.descending()
		};
	}

	private Long[] _getListTypeIds(String... names) {
		List<Long> listTypeIds = new ArrayList<>();

		for (String name : names) {
			ListType listType = _listTypeLocalService.fetchListType(
				CompanyThreadLocal.getCompanyId(), name,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);

			if (listType != null) {
				listTypeIds.add(listType.getListTypeId());
			}
		}

		return listTypeIds.toArray(new Long[0]);
	}

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

}