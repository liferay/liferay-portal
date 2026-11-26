/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.service;

import com.liferay.account.configuration.AccountEntryAddressSubtypeConfigurationUtil;
import com.liferay.account.model.AccountEntry;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManagerUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.portal.kernel.exception.AddressSubtypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalServiceWrapper;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = ServiceWrapper.class)
public class AccountAddressLocalServiceWrapper
	extends AddressLocalServiceWrapper {

	@Override
	public Address addAddress(
			String externalReferenceCode, long userId, String className,
			long classPK, long countryId, long listTypeId, long regionId,
			String city, String description, boolean mailing, String name,
			boolean primary, String street1, String street2, String street3,
			String subtype, String zip, String phoneNumber,
			ServiceContext serviceContext)
		throws PortalException {

		if (!EmptyModelManagerUtil.isEmptyModel()) {
			User user = _userLocalService.getUser(userId);

			_validateSubtype(
				_classNameLocalService.getClassNameId(className),
				user.getCompanyId(), listTypeId, subtype);
		}

		return super.addAddress(
			externalReferenceCode, userId, className, classPK, countryId,
			listTypeId, regionId, city, description, mailing, name, primary,
			street1, street2, street3, subtype, zip, phoneNumber,
			serviceContext);
	}

	@Override
	public Address updateAddress(
			String externalReferenceCode, long addressId, long countryId,
			long listTypeId, long regionId, String city, String description,
			boolean mailing, String name, boolean primary, String street1,
			String street2, String street3, String subtype, String zip,
			String phoneNumber)
		throws PortalException {

		Address address = getAddress(addressId);

		_validateSubtype(
			address.getClassNameId(), address.getCompanyId(), listTypeId,
			subtype);

		return super.updateAddress(
			externalReferenceCode, addressId, countryId, listTypeId, regionId,
			city, description, mailing, name, primary, street1, street2,
			street3, subtype, zip, phoneNumber);
	}

	private void _validateSubtype(
			long classNameId, long companyId, long listTypeId, String subtype)
		throws PortalException {

		if (Validator.isNull(subtype) ||
			((classNameId != _classNameLocalService.getClassNameId(
				AccountEntry.class)) &&
			 (classNameId != _classNameLocalService.getClassNameId(
				 "com.liferay.commerce.model.CommerceOrder")))) {

			return;
		}

		ListType listType = _listTypeLocalService.getListType(listTypeId);

		String externalReferenceCode =
			AccountEntryAddressSubtypeConfigurationUtil.
				getAddressSubtypeListTypeDefinitionExternalReferenceCode(
					companyId, listType.getName());

		if (Validator.isNull(externalReferenceCode)) {
			throw new AddressSubtypeException();
		}

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (listTypeDefinition == null) {
			throw new AddressSubtypeException();
		}

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				listTypeDefinition.getListTypeDefinitionId(), subtype);

		if (listTypeEntry == null) {
			throw new AddressSubtypeException();
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference
	private UserLocalService _userLocalService;

}