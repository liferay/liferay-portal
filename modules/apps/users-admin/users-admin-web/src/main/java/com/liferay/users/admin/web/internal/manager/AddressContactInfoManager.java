/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.manager;

import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.AddressService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;

import java.util.List;

/**
 * @author Samuel Trong Tran
 */
public class AddressContactInfoManager extends BaseContactInfoManager<Address> {

	public AddressContactInfoManager(
		AddressLocalService addressLocalService, AddressService addressService,
		String className, long classPK) {

		_addressLocalService = addressLocalService;
		_addressService = addressService;
		_className = className;
		_classPK = classPK;
	}

	@Override
	protected Address construct(ActionRequest actionRequest) throws Exception {
		String street1 = ParamUtil.getString(actionRequest, "addressStreet1");
		String street2 = ParamUtil.getString(actionRequest, "addressStreet2");
		String street3 = ParamUtil.getString(actionRequest, "addressStreet3");
		String city = ParamUtil.getString(actionRequest, "addressCity");
		String zip = ParamUtil.getString(actionRequest, "addressZip");
		long countryId = ParamUtil.getLong(actionRequest, "addressCountryId");

		if (Validator.isNull(street1) && Validator.isNull(street2) &&
			Validator.isNull(street3) && Validator.isNull(city) &&
			Validator.isNull(zip) && (countryId == 0)) {

			return null;
		}

		long addressId = ParamUtil.getLong(actionRequest, "primaryKey");

		Address address = _addressLocalService.createAddress(addressId);

		address.setCountryId(countryId);
		address.setListTypeId(
			ParamUtil.getLong(actionRequest, "addressListTypeId"));
		address.setRegionId(
			ParamUtil.getLong(actionRequest, "addressRegionId"));
		address.setCity(city);
		address.setMailing(
			ParamUtil.getBoolean(actionRequest, "addressMailing"));
		address.setPrimary(
			ParamUtil.getBoolean(actionRequest, "addressPrimary"));
		address.setStreet1(street1);
		address.setStreet2(street2);
		address.setStreet3(street3);
		address.setZip(zip);

		return address;
	}

	@Override
	protected Address doAdd(Address address) throws Exception {
		return _addressService.addAddress(
			address.getExternalReferenceCode(), _className, _classPK,
			address.getCountryId(), address.getListTypeId(),
			address.getRegionId(), address.getCity(), null, address.isMailing(),
			null, address.isPrimary(), address.getStreet1(),
			address.getStreet2(), address.getStreet3(), address.getSubtype(),
			address.getZip(), null, new ServiceContext());
	}

	@Override
	protected void doDelete(long addressId) throws Exception {
		_addressService.deleteAddress(addressId);
	}

	@Override
	protected void doUpdate(Address address) throws Exception {
		_addressService.updateAddress(
			address.getExternalReferenceCode(), address.getAddressId(),
			address.getCountryId(), address.getListTypeId(),
			address.getRegionId(), address.getCity(), address.getDescription(),
			address.isMailing(), address.getName(), address.isPrimary(),
			address.getStreet1(), address.getStreet2(), address.getStreet3(),
			address.getSubtype(), address.getZip(), address.getPhoneNumber());
	}

	@Override
	protected Address get(long addressId) throws Exception {
		return _addressService.getAddress(addressId);
	}

	@Override
	protected List<Address> getAll() throws Exception {
		return _addressService.getAddresses(_className, _classPK);
	}

	@Override
	protected long getPrimaryKey(Address address) {
		return address.getAddressId();
	}

	@Override
	protected boolean isPrimary(Address address) {
		return address.isPrimary();
	}

	@Override
	protected void setPrimary(Address address, boolean primary) {
		address.setPrimary(primary);
	}

	private final AddressLocalService _addressLocalService;
	private final AddressService _addressService;
	private final String _className;
	private final long _classPK;

}