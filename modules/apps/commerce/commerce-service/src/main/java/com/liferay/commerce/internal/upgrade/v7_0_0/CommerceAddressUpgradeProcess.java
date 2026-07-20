/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v7_0_0;

import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Date;

/**
 * @author Drew Brokke
 */
public class CommerceAddressUpgradeProcess extends UpgradeProcess {

	public CommerceAddressUpgradeProcess(
		AddressLocalService addressLocalService,
		AccountEntryLocalService accountEntryLocalService,
		CompanyLocalService companyLocalService,
		ListTypeLocalService listTypeLocalService,
		PhoneLocalService phoneLocalService,
		UserLocalService userLocalService) {

		_addressLocalService = addressLocalService;
		_accountEntryLocalService = accountEntryLocalService;
		_companyLocalService = companyLocalService;
		_listTypeLocalService = listTypeLocalService;
		_phoneLocalService = phoneLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				_setAddressListType(
					companyId,
					AccountListTypeConstants.
						ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING);
				_setAddressListType(
					companyId,
					AccountListTypeConstants.
						ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING);
				_setAddressListType(
					companyId,
					AccountListTypeConstants.
						ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING);
			});

		processConcurrently(
			"select * from CommerceAddress order by commerceAddressId",
			resultSet -> new Object[] {
				resultSet.getLong("commerceAddressId"),
				resultSet.getString("externalReferenceCode"),
				resultSet.getLong("companyId"), resultSet.getLong("userId"),
				resultSet.getString("userName"),
				resultSet.getTimestamp("createDate"),
				resultSet.getTimestamp("modifiedDate"),
				resultSet.getLong("classNameId"), resultSet.getLong("classPK"),
				resultSet.getLong("countryId"), resultSet.getInt("type_"),
				resultSet.getLong("regionId"), resultSet.getString("city"),
				resultSet.getString("description"),
				resultSet.getDouble("latitude"),
				resultSet.getDouble("longitude"), resultSet.getString("name"),
				resultSet.getString("street1"), resultSet.getString("street2"),
				resultSet.getString("street3"), resultSet.getString("zip"),
				resultSet.getString("phoneNumber")
			},
			values -> {
				Address address = _addressLocalService.createAddress(
					(Long)values[0]);

				address.setExternalReferenceCode((String)values[1]);

				long companyId = (Long)values[2];
				long userId = (Long)values[3];

				address.setCompanyId(companyId);

				if (userId == 0) {
					address.setUserId(
						_userLocalService.getGuestUserId(companyId));
				}
				else {
					address.setUserId(userId);
				}

				address.setUserName((String)values[4]);
				address.setCreateDate((Date)values[5]);
				address.setModifiedDate((Date)values[6]);
				address.setClassNameId((Long)values[7]);
				address.setClassPK((Long)values[8]);
				address.setCountryId((Long)values[9]);
				address.setListTypeId(
					_getListTypeId((Integer)values[10], companyId));
				address.setRegionId((Long)values[11]);
				address.setCity((String)values[12]);
				address.setDescription((String)values[13]);
				address.setLatitude((Double)values[14]);
				address.setLongitude((Double)values[15]);
				address.setName((String)values[16]);
				address.setStreet1((String)values[17]);
				address.setStreet2((String)values[18]);
				address.setStreet3((String)values[19]);
				address.setZip((String)values[20]);

				address = _addressLocalService.addAddress(address);

				_setPhoneNumber(address, (String)values[21]);
			},
			null);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select CommerceAddress.commerceAddressId, ",
						"CommerceAddress.classPK, CommerceAddress.",
						"defaultBilling, CommerceAddress.defaultShipping from ",
						"CommerceAddress inner join ClassName_ on ",
						"CommerceAddress.classNameId = ClassName_.classNameId ",
						"where (CommerceAddress.defaultBilling = [$TRUE$] or ",
						"CommerceAddress.defaultShipping = [$TRUE$]) and ",
						"(ClassName_.value = ? or ClassName_.value = ?)")))) {

			preparedStatement.setString(1, AccountEntry.class.getName());
			preparedStatement.setString(
				2, "com.liferay.commerce.account.model.CommerceAccount");

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long addressId = resultSet.getLong("commerceAddressId");
					long classPK = resultSet.getLong("classPK");

					_setDefaultBilling(
						addressId, classPK,
						resultSet.getBoolean("defaultBilling"));
					_setDefaultShipping(
						addressId, classPK,
						resultSet.getBoolean("defaultShipping"));
				}
			}
		}
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropTables("CommerceAddress")
		};
	}

	private long _getAddressListTypeId(long companyId, String name)
		throws PortalException {

		ListType listType = _listTypeLocalService.getListType(
			companyId, name, AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);

		return listType.getListTypeId();
	}

	private long _getListTypeId(int commerceAddressType, long companyId)
		throws PortalException {

		String name = null;

		if (CommerceAddressConstants.ADDRESS_TYPE_BILLING ==
				commerceAddressType) {

			name = AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING;
		}
		else if (CommerceAddressConstants.ADDRESS_TYPE_SHIPPING ==
					commerceAddressType) {

			name = AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING;
		}
		else {
			name =
				AccountListTypeConstants.
					ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING;
		}

		return _getAddressListTypeId(companyId, name);
	}

	private void _setAddressListType(long companyId, String name) {
		ListType listType = _listTypeLocalService.fetchListType(
			companyId, name, AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);

		if (listType == null) {
			_listTypeLocalService.addListType(
				companyId, name,
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);
		}
	}

	private void _setDefaultBilling(
		long addressId, long classPK, boolean defaultBilling) {

		if (defaultBilling) {
			try {
				_accountEntryLocalService.updateDefaultBillingAddressId(
					classPK, addressId);
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}
	}

	private void _setDefaultShipping(
		long addressId, long classPK, boolean defaultShipping) {

		if (defaultShipping) {
			try {
				_accountEntryLocalService.updateDefaultShippingAddressId(
					classPK, addressId);
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}
	}

	private void _setPhoneNumber(Address address, String phoneNumber) {
		if (phoneNumber == null) {
			return;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(address.getUserId());

		try {
			_phoneLocalService.addPhone(
				null, serviceContext.getUserId(), Address.class.getName(),
				address.getAddressId(), phoneNumber, null,
				_listTypeLocalService.getListTypeId(
					address.getCompanyId(), "phone-number",
					ListTypeConstants.ADDRESS_PHONE),
				false, serviceContext);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceAddressUpgradeProcess.class);

	private final AccountEntryLocalService _accountEntryLocalService;
	private final AddressLocalService _addressLocalService;
	private final CompanyLocalService _companyLocalService;
	private final ListTypeLocalService _listTypeLocalService;
	private final PhoneLocalService _phoneLocalService;
	private final UserLocalService _userLocalService;

}