/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.DAOParamUtil;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.exception.NoSuchRegionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.CountryServiceUtil;
import com.liferay.portal.kernel.service.RegionServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Hugo Huijser
 */
public class OrganizationSearchTerms extends OrganizationDisplayTerms {

	public OrganizationSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		city = DAOParamUtil.getString(portletRequest, CITY);
		countryId = ParamUtil.getLong(portletRequest, COUNTRY_ID);
		name = DAOParamUtil.getString(portletRequest, NAME);
		parentOrganizationId = ParamUtil.getLong(
			portletRequest, PARENT_ORGANIZATION_ID);
		regionId = ParamUtil.getLong(portletRequest, REGION_ID);
		street = DAOParamUtil.getString(portletRequest, STREET);
		type = DAOParamUtil.getString(portletRequest, TYPE);
		zip = DAOParamUtil.getString(portletRequest, ZIP);
	}

	public Long getCountryIdObj() {
		if (countryId == 0) {
			return null;
		}

		return Long.valueOf(countryId);
	}

	public String getCountryName() throws PortalException {
		String countryName = null;

		if (countryId != 0) {
			try {
				Country country = CountryServiceUtil.getCountry(countryId);

				countryName = StringUtil.toLowerCase(country.getName());

				countryName = StringUtil.quote(countryName, StringPool.QUOTE);
			}
			catch (NoSuchCountryException noSuchCountryException) {
				if (_log.isWarnEnabled()) {
					_log.warn(noSuchCountryException);
				}
			}
		}

		return countryName;
	}

	public Long getRegionIdObj() {
		if (regionId == 0) {
			return null;
		}

		return Long.valueOf(regionId);
	}

	public String getRegionName() throws PortalException {
		String regionName = null;

		if (regionId != 0) {
			try {
				Region region = RegionServiceUtil.getRegion(regionId);

				regionName = StringUtil.toLowerCase(region.getName());

				regionName = StringUtil.quote(regionName, StringPool.QUOTE);
			}
			catch (NoSuchRegionException noSuchRegionException) {
				if (_log.isWarnEnabled()) {
					_log.warn(noSuchRegionException);
				}
			}
		}

		return regionName;
	}

	public boolean hasSearchTerms() {
		if (isAdvancedSearch()) {
			if (Validator.isNotNull(city) || (countryId > 0) ||
				Validator.isNotNull(name) || (regionId > 0) ||
				Validator.isNotNull(street) || Validator.isNotNull(type) ||
				Validator.isNotNull(zip)) {

				return true;
			}
		}
		else {
			if (Validator.isNotNull(keywords)) {
				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationSearchTerms.class);

}