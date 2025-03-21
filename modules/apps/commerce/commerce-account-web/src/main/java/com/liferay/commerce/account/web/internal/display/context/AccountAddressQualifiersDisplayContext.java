/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.web.internal.display.context;

import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Danny Situ
 */
public class AccountAddressQualifiersDisplayContext {

	public AccountAddressQualifiersDisplayContext(
		AddressLocalService addressLocalService,
		CommerceChannelRelService commerceChannelRelService,
		HttpServletRequest httpServletRequest) {

		_addressLocalService = addressLocalService;
		_commerceChannelRelService = commerceChannelRelService;
		_httpServletRequest = httpServletRequest;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);
	}

	public Address getAccountAddress() throws PortalException {
		if (_address != null) {
			return _address;
		}

		long addressId = ParamUtil.getLong(
			_cpRequestHelper.getRenderRequest(), "accountEntryAddressId");

		if (addressId > 0) {
			_address = _addressLocalService.getAddress(addressId);
		}

		return _address;
	}

	public List<FDSActionDropdownItem>
			getAccountAddressChannelFDSActionDropdownItems()
		throws PortalException {

		return getFDSActionTemplates();
	}

	public String getAccountAddressChannelsAPIURL() throws PortalException {
		return "/o/headless-commerce-admin-channel/v1.0/account-addresses/" +
			getAccountAddressId() +
				"/account-address-channels?nestedFields=channel";
	}

	public long getAccountAddressId() throws PortalException {
		Address address = getAccountAddress();

		if (address == null) {
			return 0;
		}

		return address.getAddressId();
	}

	public String getActiveChannelEligibility() throws PortalException {
		long commerceChannelRelsCount =
			_commerceChannelRelService.getCommerceChannelRelsCount(
				Address.class.getName(), getAccountAddressId());

		if (commerceChannelRelsCount > 0) {
			return "channels";
		}

		return "all";
	}

	protected List<FDSActionDropdownItem> getFDSActionTemplates() {
		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			null, "trash", "remove",
			LanguageUtil.get(_httpServletRequest, "remove"), "delete", "delete",
			"headless");

		fdsActionDropdownItems.add(fdsActionDropdownItem);

		return fdsActionDropdownItems;
	}

	private Address _address;
	private final AddressLocalService _addressLocalService;
	private final CommerceChannelRelService _commerceChannelRelService;
	private final CPRequestHelper _cpRequestHelper;
	private final HttpServletRequest _httpServletRequest;

}