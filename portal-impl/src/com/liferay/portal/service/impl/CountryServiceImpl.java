/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.CountryPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.base.CountryServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class CountryServiceImpl extends CountryServiceBaseImpl {

	@Override
	public Country addCountry(
			String externalReferenceCode, String a2, String a3, boolean active,
			boolean billingAllowed, String idd, String name, String number,
			double position, boolean shippingAllowed, boolean subjectToVAT,
			boolean zipRequired, ServiceContext serviceContext)
		throws PortalException {

		PortalPermissionUtil.check(
			getPermissionChecker(), ActionKeys.ADD_COUNTRY);

		return countryLocalService.addCountry(
			externalReferenceCode, a2, a3, active, billingAllowed, idd, name,
			number, position, shippingAllowed, subjectToVAT, zipRequired,
			serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country addCountry(
			String name, String a2, String a3, String number, String idd,
			boolean active)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		PermissionChecker permissionChecker = getPermissionChecker();

		serviceContext.setCompanyId(permissionChecker.getCompanyId());
		serviceContext.setUserId(permissionChecker.getUserId());

		return addCountry(
			null, a2, a3, active, true, idd, name, number, 0, true, false, true,
			serviceContext);
	}

	@Override
	public void deleteCountry(long countryId) throws PortalException {
		CountryPermissionUtil.check(
			getPermissionChecker(), countryId, ActionKeys.DELETE);

		countryLocalService.deleteCountry(countryId);
	}

	@Override
	public Country fetchCountry(long countryId) {
		return countryPersistence.fetchByPrimaryKey(countryId);
	}

	@Override
	public Country fetchCountryByA2(long companyId, String a2) {
		return countryPersistence.fetchByC_A2(companyId, a2);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country fetchCountryByA2(String a2) {
		return fetchCountryByA2(PortalInstancePool.getDefaultCompanyId(), a2);
	}

	@Override
	public Country fetchCountryByA3(long companyId, String a3) {
		return countryPersistence.fetchByC_A3(companyId, a3);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country fetchCountryByA3(String a3) {
		return fetchCountryByA3(PortalInstancePool.getDefaultCompanyId(), a3);
	}

	@Override
	public Country fetchCountryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Country country = countryPersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (country != null) {
			CountryPermissionUtil.check(
				getPermissionChecker(), country.getCountryId(),
				ActionKeys.VIEW);
		}

		return country;
	}

	@Override
	public List<Country> getCompanyCountries(long companyId) {
		return countryPersistence.findByCompanyId(companyId);
	}

	@AccessControlled(guestAccessEnabled = true)
	@Override
	public List<Country> getCompanyCountries(long companyId, boolean active) {
		return countryPersistence.findByC_Active(companyId, active);
	}

	@Override
	public List<Country> getCompanyCountries(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return countryPersistence.findByC_Active(
			companyId, active, start, end, orderByComparator);
	}

	@Override
	public List<Country> getCompanyCountries(
		long companyId, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return countryPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCompanyCountriesCount(long companyId) {
		return countryPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getCompanyCountriesCount(long companyId, boolean active) {
		return countryPersistence.countByC_Active(companyId, active);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public List<Country> getCountries() {
		return getCompanyCountries(PortalInstancePool.getDefaultCompanyId());
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@AccessControlled(guestAccessEnabled = true)
	@Deprecated
	@Override
	public List<Country> getCountries(boolean active) {
		return getCompanyCountries(
			PortalInstancePool.getDefaultCompanyId(), active);
	}

	@Override
	public Country getCountry(long countryId) throws PortalException {
		return countryPersistence.findByPrimaryKey(countryId);
	}

	@Override
	public Country getCountryByA2(long companyId, String a2)
		throws PortalException {

		return countryPersistence.findByC_A2(companyId, a2);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country getCountryByA2(String a2) throws PortalException {
		return getCountryByA2(PortalInstancePool.getDefaultCompanyId(), a2);
	}

	@Override
	public Country getCountryByA3(long companyId, String a3)
		throws PortalException {

		return countryPersistence.findByC_A3(companyId, a3);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country getCountryByA3(String a3) throws PortalException {
		return getCountryByA3(PortalInstancePool.getDefaultCompanyId(), a3);
	}

	@Override
	public Country getCountryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Country country = countryPersistence.findByERC_C(
			externalReferenceCode, companyId);

		CountryPermissionUtil.check(
			getPermissionChecker(), country.getCountryId(), ActionKeys.VIEW);

		return country;
	}

	@Override
	public Country getCountryByName(long companyId, String name)
		throws PortalException {

		return countryPersistence.findByC_Name(companyId, name);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x)
	 */
	@Deprecated
	@Override
	public Country getCountryByName(String name) throws PortalException {
		return getCountryByName(PortalInstancePool.getDefaultCompanyId(), name);
	}

	@Override
	public Country getCountryByNumber(long companyId, String number)
		throws PortalException {

		return countryPersistence.findByC_Number(companyId, number);
	}

	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	@Override
	public BaseModelSearchResult<Country> searchCountries(
			long companyId, Boolean active, String keywords, int start, int end,
			OrderByComparator<Country> orderByComparator)
		throws PortalException {

		return countryLocalService.searchCountries(
			companyId, active, keywords, start, end, orderByComparator);
	}

	@Override
	public Country updateActive(long countryId, boolean active)
		throws PortalException {

		CountryPermissionUtil.check(
			getPermissionChecker(), countryId, ActionKeys.UPDATE);

		return countryLocalService.updateActive(countryId, active);
	}

	@Override
	public Country updateCountry(
			String externalReferenceCode, long countryId, String a2, String a3,
			boolean active, boolean billingAllowed, String idd, String name,
			String number, double position, boolean shippingAllowed,
			boolean subjectToVAT)
		throws PortalException {

		CountryPermissionUtil.check(
			getPermissionChecker(), countryId, ActionKeys.UPDATE);

		return countryLocalService.updateCountry(
			externalReferenceCode, countryId, a2, a3, active, billingAllowed,
			idd, name, number, position, shippingAllowed, subjectToVAT);
	}

	@Override
	public Country updateGroupFilterEnabled(
			long countryId, boolean groupFilterEnabled)
		throws PortalException {

		CountryPermissionUtil.check(
			getPermissionChecker(), countryId, ActionKeys.UPDATE);

		return countryLocalService.updateGroupFilterEnabled(
			countryId, groupFilterEnabled);
	}

}