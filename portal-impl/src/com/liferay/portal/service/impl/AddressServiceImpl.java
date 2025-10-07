/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.service.base.AddressServiceBaseImpl;
import com.liferay.portal.service.permission.CommonPermissionUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class AddressServiceImpl extends AddressServiceBaseImpl {

	@Override
	public Address addAddress(
			String externalReferenceCode, String className, long classPK,
			long countryId, long listTypeId, long regionId, String city,
			String description, boolean mailing, String name, boolean primary,
			String street1, String street2, String street3, String subtype,
			String zip, String phoneNumber, ServiceContext serviceContext)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				className, "com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			permissionChecker, className, classPK, actionId);

		return addressLocalService.addAddress(
			externalReferenceCode, permissionChecker.getUserId(), className,
			classPK, countryId, listTypeId, regionId, city, description,
			mailing, name, primary, street1, street2, street3, subtype, zip,
			phoneNumber, serviceContext);
	}

	@Override
	public void deleteAddress(long addressId) throws PortalException {
		Address address = addressPersistence.findByPrimaryKey(addressId);

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				address.getClassName(),
				"com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), address.getClassNameId(),
			address.getClassPK(), actionId);

		addressLocalService.deleteAddress(address);
	}

	@Override
	public Address fetchAddressByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Address address =
			addressLocalService.fetchAddressByExternalReferenceCode(
				externalReferenceCode, companyId);

		if (address != null) {
			CommonPermissionUtil.check(
				getPermissionChecker(), address.getClassNameId(),
				address.getClassPK(), ActionKeys.VIEW);
		}

		return address;
	}

	@Override
	public Address getAddress(long addressId) throws PortalException {
		Address address = addressPersistence.findByPrimaryKey(addressId);

		CommonPermissionUtil.check(
			getPermissionChecker(), address.getClassNameId(),
			address.getClassPK(), ActionKeys.VIEW);

		return address;
	}

	@Override
	public List<Address> getAddresses(String className, long classPK)
		throws PortalException {

		CommonPermissionUtil.check(
			getPermissionChecker(), className, classPK, ActionKeys.VIEW);

		User user = getUser();

		return addressLocalService.getAddresses(
			user.getCompanyId(), className, classPK);
	}

	@Override
	public List<Address> getListTypeAddresses(
			String className, long classPK, long[] listTypeIds)
		throws PortalException {

		CommonPermissionUtil.check(
			getPermissionChecker(), className, classPK, ActionKeys.VIEW);

		User user = getUser();

		return addressLocalService.getListTypeAddresses(
			user.getCompanyId(), className, classPK, listTypeIds);
	}

	@Override
	public Address getOrAddEmptyAddress(
			String externalReferenceCode, String className, long classPK)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		Address address = fetchAddressByExternalReferenceCode(
			externalReferenceCode, permissionChecker.getCompanyId());

		if (address != null) {
			return address;
		}

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				className, "com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			permissionChecker, className, classPK, actionId);

		return addressLocalService.getOrAddEmptyAddress(
			externalReferenceCode, permissionChecker.getCompanyId(),
			permissionChecker.getUserId(), className, classPK);
	}

	@Override
	public Address updateAddress(
			String externalReferenceCode, long addressId, long countryId,
			long listTypeId, long regionId, String city, String description,
			boolean mailing, String name, boolean primary, String street1,
			String street2, String street3, String subtype, String zip,
			String phoneNumber)
		throws PortalException {

		Address address = addressPersistence.findByPrimaryKey(addressId);

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				address.getClassName(),
				"com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), address.getClassNameId(),
			address.getClassPK(), actionId);

		return addressLocalService.updateAddress(
			externalReferenceCode, addressId, countryId, listTypeId, regionId,
			city, description, mailing, name, primary, street1, street2,
			street3, subtype, zip, phoneNumber);
	}

	@Override
	public Address updateExternalReferenceCode(
			Address address, String externalReferenceCode)
		throws PortalException {

		String actionId = ActionKeys.UPDATE;

		if (Objects.equals(
				address.getClassName(),
				"com.liferay.account.model.AccountEntry")) {

			actionId = "MANAGE_ADDRESSES";
		}

		CommonPermissionUtil.check(
			getPermissionChecker(), address.getClassNameId(),
			address.getClassPK(), actionId);

		return addressLocalService.updateExternalReferenceCode(
			address, externalReferenceCode);
	}

	@Override
	public Address updateExternalReferenceCode(
			long addressId, String externalReferenceCode)
		throws PortalException {

		return updateExternalReferenceCode(
			addressPersistence.findByPrimaryKey(addressId),
			externalReferenceCode);
	}

}