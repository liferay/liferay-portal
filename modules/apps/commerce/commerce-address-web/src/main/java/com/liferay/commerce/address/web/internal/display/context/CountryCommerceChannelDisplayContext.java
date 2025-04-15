/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.address.web.internal.display.context;

import com.liferay.commerce.address.web.internal.display.context.helper.CommerceAddressRequestHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CountryCommerceChannelDisplayContext {

	public CountryCommerceChannelDisplayContext(
		CommerceChannelRelService commerceChannelRelService,
		CommerceChannelService commerceChannelService,
		CountryService countryService, HttpServletRequest httpServletRequest) {

		_commerceChannelRelService = commerceChannelRelService;
		_commerceChannelService = commerceChannelService;
		_countryService = countryService;
		_httpServletRequest = httpServletRequest;

		_commerceAddressRequestHelper = new CommerceAddressRequestHelper(
			httpServletRequest);
	}

	public long[] getCommerceChannelRelCommerceChannelIds()
		throws PortalException {

		return TransformUtil.transformToLongArray(
			_commerceChannelRelService.getCommerceChannelRels(
				Country.class.getName(), getCountryId(), null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			CommerceChannelRel::getCommerceChannelId);
	}

	public List<CommerceChannel> getCommerceChannels() throws PortalException {
		return _commerceChannelService.getCommerceChannels(
			_commerceAddressRequestHelper.getCompanyId());
	}

	public Country getCountry() throws PortalException {
		if (_country != null) {
			return _country;
		}

		long countryId = ParamUtil.getLong(_httpServletRequest, "countryId");

		if (countryId > 0) {
			_country = _countryService.getCountry(countryId);
		}

		return _country;
	}

	public long getCountryId() throws PortalException {
		Country country = getCountry();

		if (country == null) {
			return 0;
		}

		return country.getCountryId();
	}

	private final CommerceAddressRequestHelper _commerceAddressRequestHelper;
	private final CommerceChannelRelService _commerceChannelRelService;
	private final CommerceChannelService _commerceChannelService;
	private Country _country;
	private final CountryService _countryService;
	private final HttpServletRequest _httpServletRequest;

}