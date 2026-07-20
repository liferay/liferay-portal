/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.instance.lifecycle;

import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.service.ListTypeLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class AddAccountEntryAddressListTypesPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!_hasListType(
				company.getCompanyId(),
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS)) {

			_listTypeLocalService.addListType(
				company.getCompanyId(),
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);
		}

		if (!_hasListType(
				company.getCompanyId(),
				AccountListTypeConstants.
					ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS)) {

			_listTypeLocalService.addListType(
				company.getCompanyId(),
				AccountListTypeConstants.
					ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);
		}

		if (!_hasListType(
				company.getCompanyId(),
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS)) {

			_listTypeLocalService.addListType(
				company.getCompanyId(),
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);
		}

		if (!_hasListType(
				company.getCompanyId(),
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS)) {

			_listTypeLocalService.addListType(
				company.getCompanyId(),
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);
		}
	}

	private boolean _hasListType(long companyId, String name, String type) {
		ListType listType = _listTypeLocalService.fetchListType(
			companyId, name, type);

		if (listType != null) {
			return true;
		}

		return false;
	}

	@Reference
	private ListTypeLocalService _listTypeLocalService;

}