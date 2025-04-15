/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.internal.resource.v1_0;

import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.exception.NoSuchAddressException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.headless.commerce.admin.account.dto.v1_0.Account;
import com.liferay.headless.commerce.admin.account.dto.v1_0.AccountAddress;
import com.liferay.headless.commerce.admin.account.resource.v1_0.AccountAddressResource;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.Response;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/account-address.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = AccountAddressResource.class
)
@Deprecated
public class AccountAddressResourceImpl extends BaseAccountAddressResourceImpl {

	@Override
	public Response deleteAccountAddress(Long id) throws Exception {
		_commerceAddressService.deleteCommerceAddress(id);

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Response deleteAccountAddressByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceAddress commerceAddress =
			_commerceAddressService.fetchCommerceAddressByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceAddress == null) {
			throw new NoSuchAddressException(
				"Unable to find account address with external reference code " +
					externalReferenceCode);
		}

		_commerceAddressService.deleteCommerceAddress(
			commerceAddress.getCommerceAddressId());

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public AccountAddress getAccountAddress(Long id) throws Exception {
		return _toAccountAddress(
			_commerceAddressService.getCommerceAddress(id));
	}

	@Override
	public AccountAddress getAccountAddressByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceAddress commerceAddress =
			_commerceAddressService.fetchCommerceAddressByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceAddress == null) {
			throw new NoSuchAddressException(
				"Unable to find account address with external reference code " +
					externalReferenceCode);
		}

		return _toAccountAddress(commerceAddress);
	}

	@Override
	public Page<AccountAddress>
			getAccountByExternalReferenceCodeAccountAddressesPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (accountEntry == null) {
			throw new NoSuchEntryException(
				"Unable to find account with external reference code " +
					externalReferenceCode);
		}

		return _getAccountAddressesPage(accountEntry, pagination);
	}

	@NestedField(parentClass = Account.class, value = "accountAddresses")
	@Override
	public Page<AccountAddress> getAccountIdAccountAddressesPage(
			Long id, Pagination pagination)
		throws Exception {

		return _getAccountAddressesPage(
			_accountEntryLocalService.getAccountEntry(id), pagination);
	}

	@Override
	public AccountAddress patchAccountAddress(
			Long id, AccountAddress accountAddress)
		throws Exception {

		CommerceAddress commerceAddress =
			_commerceAddressService.getCommerceAddress(id);

		Country country = _countryService.getCountryByA2(
			commerceAddress.getCompanyId(), accountAddress.getCountryISOCode());

		commerceAddress = _commerceAddressService.updateCommerceAddress(
			GetterUtil.getString(
				accountAddress.getExternalReferenceCode(),
				commerceAddress.getExternalReferenceCode()),
			commerceAddress.getCommerceAddressId(),
			GetterUtil.getLong(
				_getCountryId(country), commerceAddress.getCountryId()),
			GetterUtil.getLong(
				_getRegionId(country, accountAddress),
				commerceAddress.getRegionId()),
			GetterUtil.getString(
				accountAddress.getCity(), commerceAddress.getCity()),
			GetterUtil.getString(
				accountAddress.getDescription(),
				commerceAddress.getDescription()),
			GetterUtil.getString(
				accountAddress.getName(), commerceAddress.getName()),
			GetterUtil.getString(
				accountAddress.getPhoneNumber(),
				commerceAddress.getPhoneNumber()),
			GetterUtil.getString(
				accountAddress.getStreet1(), commerceAddress.getStreet1()),
			GetterUtil.getString(
				accountAddress.getStreet2(), commerceAddress.getStreet2()),
			GetterUtil.getString(
				accountAddress.getStreet3(), commerceAddress.getStreet3()),
			commerceAddress.getSubtype(),
			GetterUtil.getInteger(
				accountAddress.getType(), commerceAddress.getType()),
			GetterUtil.getString(
				accountAddress.getZip(), commerceAddress.getZip()),
			_serviceContextHelper.getServiceContext());

		_updateDefaultBillingShippingAddressId(
			commerceAddress.getClassPK(), accountAddress, commerceAddress);

		return _toAccountAddress(commerceAddress);
	}

	@Override
	public Response patchAccountAddressByExternalReferenceCode(
			String externalReferenceCode, AccountAddress accountAddress)
		throws Exception {

		CommerceAddress commerceAddress =
			_commerceAddressService.fetchCommerceAddressByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceAddress == null) {
			throw new NoSuchAddressException(
				"Unable to find account address with external reference code " +
					externalReferenceCode);
		}

		Country country = _countryService.getCountryByA2(
			commerceAddress.getCompanyId(), accountAddress.getCountryISOCode());

		_commerceAddressService.updateCommerceAddress(
			GetterUtil.getString(
				accountAddress.getExternalReferenceCode(),
				commerceAddress.getExternalReferenceCode()),
			commerceAddress.getCommerceAddressId(),
			GetterUtil.getLong(
				_getCountryId(country), commerceAddress.getCountryId()),
			GetterUtil.getLong(
				_getRegionId(country, accountAddress),
				commerceAddress.getRegionId()),
			GetterUtil.getString(
				accountAddress.getCity(), commerceAddress.getCity()),
			GetterUtil.getString(
				accountAddress.getDescription(),
				commerceAddress.getDescription()),
			GetterUtil.getString(
				accountAddress.getName(), commerceAddress.getName()),
			GetterUtil.getString(
				accountAddress.getPhoneNumber(),
				commerceAddress.getPhoneNumber()),
			GetterUtil.getString(
				accountAddress.getStreet1(), commerceAddress.getStreet1()),
			GetterUtil.getString(
				accountAddress.getStreet2(), commerceAddress.getStreet2()),
			GetterUtil.getString(
				accountAddress.getStreet3(), commerceAddress.getStreet3()),
			commerceAddress.getSubtype(),
			GetterUtil.getInteger(
				accountAddress.getType(), commerceAddress.getType()),
			GetterUtil.getString(
				accountAddress.getZip(), commerceAddress.getZip()),
			_serviceContextHelper.getServiceContext());

		_updateDefaultBillingShippingAddressId(
			commerceAddress.getClassPK(), accountAddress, commerceAddress);

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public AccountAddress postAccountByExternalReferenceCodeAccountAddress(
			String externalReferenceCode, AccountAddress accountAddress)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (accountEntry == null) {
			throw new NoSuchEntryException(
				"Unable to find account with external reference code " +
					externalReferenceCode);
		}

		CommerceAddress commerceAddress = null;

		if (accountAddress.getId() != null) {
			commerceAddress = _commerceAddressService.fetchCommerceAddress(
				accountAddress.getId());
		}
		else if (accountAddress.getExternalReferenceCode() != null) {
			commerceAddress =
				_commerceAddressService.
					fetchCommerceAddressByExternalReferenceCode(
						accountAddress.getExternalReferenceCode(),
						contextCompany.getCompanyId());
		}

		if (commerceAddress != null) {
			Country country = _countryService.getCountryByA2(
				commerceAddress.getCompanyId(),
				accountAddress.getCountryISOCode());

			return _toAccountAddress(
				_commerceAddressService.updateCommerceAddress(
					GetterUtil.getString(
						accountAddress.getExternalReferenceCode(),
						commerceAddress.getExternalReferenceCode()),
					commerceAddress.getCommerceAddressId(),
					GetterUtil.getLong(
						_getCountryId(country), commerceAddress.getCountryId()),
					GetterUtil.getLong(
						_getRegionId(country, accountAddress),
						commerceAddress.getRegionId()),
					GetterUtil.getString(accountAddress.getCity(), null),
					GetterUtil.getString(accountAddress.getDescription(), null),
					GetterUtil.getString(accountAddress.getName(), null),
					GetterUtil.getString(accountAddress.getPhoneNumber(), null),
					GetterUtil.getString(accountAddress.getStreet1(), null),
					GetterUtil.getString(accountAddress.getStreet2(), null),
					GetterUtil.getString(accountAddress.getStreet3(), null),
					StringPool.BLANK,
					GetterUtil.getInteger(
						accountAddress.getType(), commerceAddress.getType()),
					GetterUtil.getString(accountAddress.getZip(), null),
					_serviceContextHelper.getServiceContext()));
		}

		return _addAccountAddress(accountEntry, accountAddress);
	}

	@Override
	public AccountAddress postAccountIdAccountAddress(
			Long id, AccountAddress accountAddress)
		throws Exception {

		return _addAccountAddress(
			_accountEntryLocalService.getAccountEntry(id), accountAddress);
	}

	@Override
	public AccountAddress putAccountAddress(
			Long id, AccountAddress accountAddress)
		throws Exception {

		CommerceAddress commerceAddress =
			_commerceAddressService.getCommerceAddress(id);

		Country country = _countryService.getCountryByA2(
			commerceAddress.getCompanyId(), accountAddress.getCountryISOCode());

		commerceAddress = _commerceAddressService.updateCommerceAddress(
			GetterUtil.getString(
				accountAddress.getExternalReferenceCode(),
				commerceAddress.getExternalReferenceCode()),
			commerceAddress.getCommerceAddressId(),
			GetterUtil.getLong(
				_getCountryId(country), commerceAddress.getCountryId()),
			GetterUtil.getLong(
				_getRegionId(country, accountAddress),
				commerceAddress.getRegionId()),
			GetterUtil.getString(accountAddress.getCity()),
			GetterUtil.getString(accountAddress.getDescription()),
			GetterUtil.getString(accountAddress.getName()),
			GetterUtil.getString(accountAddress.getPhoneNumber()),
			GetterUtil.getString(accountAddress.getStreet1()),
			GetterUtil.getString(accountAddress.getStreet2()),
			GetterUtil.getString(accountAddress.getStreet3()), StringPool.BLANK,
			GetterUtil.getInteger(accountAddress.getType()),
			GetterUtil.getString(accountAddress.getZip()),
			_serviceContextHelper.getServiceContext());

		return _toAccountAddress(commerceAddress);
	}

	private AccountAddress _addAccountAddress(
			AccountEntry accountEntry, AccountAddress accountAddress)
		throws Exception {

		Country country = _countryService.getCountryByA2(
			accountEntry.getCompanyId(), accountAddress.getCountryISOCode());

		CommerceAddress commerceAddress =
			_commerceAddressService.addCommerceAddress(
				accountAddress.getExternalReferenceCode(),
				AccountEntry.class.getName(), accountEntry.getAccountEntryId(),
				country.getCountryId(), _getRegionId(country, accountAddress),
				accountAddress.getCity(), accountAddress.getDescription(),
				accountAddress.getName(), accountAddress.getPhoneNumber(),
				accountAddress.getStreet1(), accountAddress.getStreet2(),
				accountAddress.getStreet3(), StringPool.BLANK,
				GetterUtil.getInteger(
					accountAddress.getType(),
					CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING),
				accountAddress.getZip(),
				_serviceContextHelper.getServiceContext());

		_updateDefaultBillingShippingAddressId(
			accountEntry.getAccountEntryId(), accountAddress, commerceAddress);

		return _accountAddressDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceAddress.getCommerceAddressId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	private Page<AccountAddress> _getAccountAddressesPage(
			AccountEntry accountEntry, Pagination pagination)
		throws Exception {

		List<CommerceAddress> commerceAddresses =
			_commerceAddressService.getCommerceAddresses(
				AccountEntry.class.getName(), accountEntry.getAccountEntryId(),
				pagination.getStartPosition(), pagination.getEndPosition(),
				null);

		int totalCount = _commerceAddressService.getCommerceAddressesCount(
			AccountEntry.class.getName(), accountEntry.getAccountEntryId());

		return Page.of(
			_toAccountAddresses(commerceAddresses), pagination, totalCount);
	}

	private long _getCountryId(Country country) {
		if (country == null) {
			return 0;
		}

		return country.getCountryId();
	}

	private long _getRegionId(Country country, AccountAddress accountAddress)
		throws Exception {

		if (Validator.isNull(accountAddress.getRegionISOCode()) ||
			(country == null)) {

			return 0;
		}

		Region region = _regionLocalService.getRegion(
			country.getCountryId(), accountAddress.getRegionISOCode());

		return region.getRegionId();
	}

	private AccountAddress _toAccountAddress(CommerceAddress commerceAddress)
		throws Exception {

		return _accountAddressDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				commerceAddress.getCommerceAddressId(),
				contextAcceptLanguage.getPreferredLocale()));
	}

	private List<AccountAddress> _toAccountAddresses(
			List<CommerceAddress> commerceAddresses)
		throws Exception {

		return transform(
			commerceAddresses,
			commerceAddress -> _accountAddressDTOConverter.toDTO(
				new DefaultDTOConverterContext(
					commerceAddress.getCommerceAddressId(),
					contextAcceptLanguage.getPreferredLocale())));
	}

	private void _updateDefaultBillingShippingAddressId(
			long accountEntryId, AccountAddress accountAddress,
			CommerceAddress commerceAddress)
		throws Exception {

		if (Boolean.TRUE.equals(accountAddress.getDefaultBilling())) {
			_accountEntryLocalService.updateDefaultBillingAddressId(
				accountEntryId, commerceAddress.getCommerceAddressId());

			commerceAddress.setDefaultBilling(true);
		}

		if (Boolean.TRUE.equals(accountAddress.getDefaultShipping())) {
			_accountEntryLocalService.updateDefaultShippingAddressId(
				accountEntryId, commerceAddress.getCommerceAddressId());

			commerceAddress.setDefaultShipping(true);
		}
	}

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.account.internal.dto.v1_0.converter.AccountAddressDTOConverter)"
	)
	private DTOConverter<CommerceAddress, AccountAddress>
		_accountAddressDTOConverter;

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CountryLocalService _countryService;

	@Reference
	private RegionLocalService _regionLocalService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}