/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v8_5_0;

import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Pei-Jung Lan
 */
public class CommerceAddressTypeUpgradeProcess extends UpgradeProcess {

	public CommerceAddressTypeUpgradeProcess(
		CompanyLocalService companyLocalService,
		ListTypeLocalService listTypeLocalService) {

		_companyLocalService = companyLocalService;
		_listTypeLocalService = listTypeLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				_setAddressListType(
					companyId,
					_getListTypeId(
						companyId,
						AccountListTypeConstants.
							ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING),
					14000);
				_setAddressListType(
					companyId,
					_getListTypeId(
						companyId,
						AccountListTypeConstants.
							ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING),
					14002);
				_setAddressListType(
					companyId,
					_getListTypeId(
						companyId,
						AccountListTypeConstants.
							ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING),
					14001);
			});
	}

	private long _getListTypeId(long companyId, String name) {
		ListType listType = _listTypeLocalService.fetchListType(
			companyId, name, AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);

		if (listType == null) {
			listType = _listTypeLocalService.addListType(
				companyId, name,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);
		}

		return listType.getListTypeId();
	}

	private void _setAddressListType(
			long companyId, long newListTypeId, long oldListTypeId)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"update Address set listTypeId = ", newListTypeId,
				" where companyId = ", companyId, " and listTypeId = ",
				oldListTypeId));
	}

	private final CompanyLocalService _companyLocalService;
	private final ListTypeLocalService _listTypeLocalService;

}