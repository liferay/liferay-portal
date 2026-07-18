/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.CountryPermissionUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.base.RegionServiceBaseImpl;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class RegionServiceImpl extends RegionServiceBaseImpl {

	@Override
	public Region addRegion(
			String externalReferenceCode, long countryId, boolean active,
			String name, double position, String regionCode,
			ServiceContext serviceContext)
		throws PortalException {

		CountryPermissionUtil.check(
			getPermissionChecker(), countryId, ActionKeys.UPDATE);

		return regionLocalService.addRegion(
			externalReferenceCode, countryId, active, name, position,
			regionCode, serviceContext);
	}

	@Override
	public void deleteRegion(long regionId) throws PortalException {
		Region region = regionPersistence.findByPrimaryKey(regionId);

		CountryPermissionUtil.check(
			getPermissionChecker(), region.getCountryId(), ActionKeys.UPDATE);

		regionLocalService.deleteRegion(regionId);
	}

	@Override
	public Region fetchRegion(long regionId) {
		return regionPersistence.fetchByPrimaryKey(regionId);
	}

	@Override
	public Region fetchRegion(long countryId, String regionCode) {
		return regionPersistence.fetchByC_R(countryId, regionCode);
	}

	@Override
	public Region fetchRegionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Region region = regionPersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (region != null) {
			CountryPermissionUtil.check(
				getPermissionChecker(), region.getCountryId(), ActionKeys.VIEW);
		}

		return region;
	}

	@Override
	public Region getRegion(long regionId) throws PortalException {
		return regionPersistence.findByPrimaryKey(regionId);
	}

	@Override
	public Region getRegion(long countryId, String regionCode)
		throws PortalException {

		return regionPersistence.findByC_R(countryId, regionCode);
	}

	@Override
	public Region getRegionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		Region region = regionPersistence.findByERC_C(
			externalReferenceCode, companyId);

		CountryPermissionUtil.check(
			getPermissionChecker(), region.getCountryId(), ActionKeys.VIEW);

		return region;
	}

	@Override
	public List<Region> getRegions() {
		return regionPersistence.findAll();
	}

	@Override
	public List<Region> getRegions(boolean active) {
		return regionPersistence.findByActive(active);
	}

	@Override
	public List<Region> getRegions(long countryId) {
		return regionPersistence.findByCountryId(
			countryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@AccessControlled(guestAccessEnabled = true)
	@Override
	public List<Region> getRegions(long countryId, boolean active) {
		return regionPersistence.findByC_A(
			countryId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<Region> getRegions(
		long countryId, boolean active, int start, int end,
		OrderByComparator<Region> orderByComparator) {

		return regionPersistence.findByC_A(
			countryId, active, start, end, orderByComparator);
	}

	@Override
	public List<Region> getRegions(
		long countryId, int start, int end,
		OrderByComparator<Region> orderByComparator) {

		return regionPersistence.findByCountryId(
			countryId, start, end, orderByComparator);
	}

	@Override
	public List<Region> getRegions(long companyId, String a2, boolean active)
		throws PortalException {

		return regionLocalService.getRegions(companyId, a2, active);
	}

	@Override
	public int getRegionsCount(long countryId) {
		return regionPersistence.countByCountryId(countryId);
	}

	@Override
	public int getRegionsCount(long countryId, boolean active) {
		return regionPersistence.countByC_A(countryId, active);
	}

	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	@Override
	public BaseModelSearchResult<Region> searchRegions(
			long companyId, Boolean active, String keywords,
			LinkedHashMap<String, Object> params, int start, int end,
			OrderByComparator<Region> orderByComparator)
		throws PortalException {

		return regionLocalService.searchRegions(
			companyId, active, keywords, params, start, end, orderByComparator);
	}

	@Override
	public Region updateActive(long regionId, boolean active)
		throws PortalException {

		Region region = regionPersistence.findByPrimaryKey(regionId);

		CountryPermissionUtil.check(
			getPermissionChecker(), region.getCountryId(), ActionKeys.UPDATE);

		return regionLocalService.updateActive(regionId, active);
	}

	@Override
	public Region updateRegion(
			String externalReferenceCode, long regionId, boolean active,
			String name, double position, String regionCode)
		throws PortalException {

		Region region = regionPersistence.findByPrimaryKey(regionId);

		CountryPermissionUtil.check(
			getPermissionChecker(), region.getCountryId(), ActionKeys.UPDATE);

		return regionLocalService.updateRegion(
			externalReferenceCode, regionId, active, name, position,
			regionCode);
	}

}