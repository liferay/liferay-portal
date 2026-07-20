/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.model.impl;

import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.account.configuration.manager.AccountEntryAddressSubtypeConfigurationManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.list.type.manager.ListTypeEntryManagerUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CountryLocalServiceUtil;
import com.liferay.portal.kernel.service.ListTypeLocalServiceUtil;
import com.liferay.portal.kernel.service.PhoneLocalServiceUtil;
import com.liferay.portal.kernel.service.RegionLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author Andrea Di Giorgi
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Deprecated
public class CommerceAddressImpl extends CommerceAddressBaseImpl {

	public static CommerceAddress fromAddress(Address address) {
		if (address == null) {
			return null;
		}

		CommerceAddress commerceAddress = new CommerceAddressImpl();

		Map<String, BiConsumer<CommerceAddress, Object>>
			attributeSetterBiConsumers =
				commerceAddress.getAttributeSetterBiConsumers();

		Map<String, Object> modelAttributes = address.getModelAttributes();

		for (Map.Entry<String, Object> entry : modelAttributes.entrySet()) {
			BiConsumer<CommerceAddress, Object>
				commerceAddressObjectBiConsumer =
					attributeSetterBiConsumers.get(entry.getKey());

			if (commerceAddressObjectBiConsumer != null) {
				commerceAddressObjectBiConsumer.accept(
					commerceAddress, entry.getValue());
			}
		}

		commerceAddress.setCommerceAddressId(address.getAddressId());

		List<Phone> phones = PhoneLocalServiceUtil.getPhones(
			address.getCompanyId(), Address.class.getName(),
			address.getAddressId());

		if (!phones.isEmpty()) {
			Phone phone = phones.get(0);

			commerceAddress.setPhoneNumber(phone.getNumber());
		}

		commerceAddress.setDefaultBilling(
			toCommerceAccountDefaultBilling(address));
		commerceAddress.setDefaultShipping(
			toCommerceAccountDefaultShipping(address));
		commerceAddress.setType(toCommerceAddressType(address));

		return commerceAddress;
	}

	public static boolean isAccountEntryAddress(Address address) {
		return Objects.equals(
			AccountEntry.class.getName(), address.getClassName());
	}

	public static long toAddressTypeId(int commerceAddressType)
		throws PortalException {

		if (CommerceAddressConstants.ADDRESS_TYPE_BILLING ==
				commerceAddressType) {

			return _getAddressTypeId(
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING);
		}
		else if (CommerceAddressConstants.ADDRESS_TYPE_SHIPPING ==
					commerceAddressType) {

			return _getAddressTypeId(
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING);
		}

		return _getAddressTypeId(
			AccountListTypeConstants.
				ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING);
	}

	public static boolean toCommerceAccountDefaultBilling(Address address) {
		if (isAccountEntryAddress(address)) {
			AccountEntry accountEntry =
				AccountEntryLocalServiceUtil.fetchAccountEntry(
					address.getClassPK());

			if (accountEntry != null) {
				Address defaultBillingAddress =
					accountEntry.getDefaultBillingAddress();

				if ((defaultBillingAddress != null) &&
					(defaultBillingAddress.getAddressId() ==
						address.getAddressId())) {

					return true;
				}
			}
		}

		return false;
	}

	public static boolean toCommerceAccountDefaultShipping(Address address) {
		if (isAccountEntryAddress(address)) {
			AccountEntry accountEntry =
				AccountEntryLocalServiceUtil.fetchAccountEntry(
					address.getClassPK());

			if (accountEntry != null) {
				Address defaultShippingAddress =
					accountEntry.getDefaultShippingAddress();

				if ((defaultShippingAddress != null) &&
					(defaultShippingAddress.getAddressId() ==
						address.getAddressId())) {

					return true;
				}
			}
		}

		return false;
	}

	public static int toCommerceAddressType(Address address) {
		ListType listType = address.getListType();

		String listTypeName = listType.getName();

		if (Objects.equals(
				AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING,
				listTypeName)) {

			return CommerceAddressConstants.ADDRESS_TYPE_BILLING;
		}
		else if (Objects.equals(
					AccountListTypeConstants.
						ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING,
					listTypeName)) {

			return CommerceAddressConstants.ADDRESS_TYPE_SHIPPING;
		}
		else if (Objects.equals(
					AccountListTypeConstants.
						ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING,
					listTypeName)) {

			return CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING;
		}

		return CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING;
	}

	@Override
	public Country fetchCountry() {
		return CountryLocalServiceUtil.fetchCountry(getCountryId());
	}

	@Override
	public Country getCountry() throws PortalException {
		return CountryLocalServiceUtil.getCountry(getCountryId());
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return ExpandoBridgeFactoryUtil.getExpandoBridge(
			getCompanyId(), Address.class.getName(), getPrimaryKey());
	}

	@Override
	public Region getRegion() throws PortalException {
		long regionId = getRegionId();

		if (regionId > 0) {
			return RegionLocalServiceUtil.getRegion(regionId);
		}

		return null;
	}

	@Override
	public String getSubtype(Locale locale) {
		if (Validator.isNull(getSubtype())) {
			return StringPool.BLANK;
		}

		String externalReferenceCode =
			AccountEntryAddressSubtypeConfigurationManagerUtil.
				getAddressSubtypeListTypeDefinitionExternalReferenceCode(
					getCompanyId(),
					CommerceAddressConstants.getAddressTypeLabel(getType()));

		if (Validator.isNull(externalReferenceCode)) {
			return getSubtype();
		}

		ListTypeEntry listTypeEntry =
			ListTypeEntryLocalServiceUtil.fetchListTypeEntry(
				ListTypeEntryManagerUtil.
					getListTypeEntryIdByListTypeDefinitionExternalReferenceCode(
						externalReferenceCode, getCompanyId(), getSubtype()));

		if (listTypeEntry == null) {
			return getSubtype();
		}

		return listTypeEntry.getName(locale);
	}

	@Override
	public boolean isGeolocated() {
		if ((getLatitude() == 0) && (getLongitude() == 0)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isSameAddress(CommerceAddress commerceAddress) {
		if (Objects.equals(getCity(), commerceAddress.getCity()) &&
			(getCountryId() == commerceAddress.getCountryId()) &&
			(getLatitude() == commerceAddress.getLatitude()) &&
			(getLongitude() == commerceAddress.getLongitude()) &&
			Objects.equals(getName(), commerceAddress.getName()) &&
			Objects.equals(
				getPhoneNumber(), commerceAddress.getPhoneNumber()) &&
			(getRegionId() == commerceAddress.getRegionId()) &&
			Objects.equals(getStreet1(), commerceAddress.getStreet1()) &&
			Objects.equals(getStreet2(), commerceAddress.getStreet2()) &&
			Objects.equals(getStreet3(), commerceAddress.getStreet3()) &&
			(getType() == commerceAddress.getType()) &&
			Objects.equals(getZip(), commerceAddress.getZip())) {

			return true;
		}

		return false;
	}

	private static long _getAddressTypeId(String name) throws PortalException {
		ListType listType = ListTypeLocalServiceUtil.getListType(
			CompanyThreadLocal.getCompanyId(), name,
			AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS);

		return listType.getListTypeId();
	}

}