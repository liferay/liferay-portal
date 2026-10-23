/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class OrganizationFinderUtil {

	public static int countO_ByKeywords(
		long companyId, long parentOrganizationId,
		String parentOrganizationIdComparator, String keywords, String type,
		Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params) {

		return getFinder().countO_ByKeywords(
			companyId, parentOrganizationId, parentOrganizationIdComparator,
			keywords, type, regionId, countryId, params);
	}

	public static int countO_ByC_PO_N_T_S_C_Z_R_C(
		long companyId, long parentOrganizationId,
		String parentOrganizationIdComparator, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator) {

		return getFinder().countO_ByC_PO_N_T_S_C_Z_R_C(
			companyId, parentOrganizationId, parentOrganizationIdComparator,
			name, type, street, city, zip, regionId, countryId, params,
			andOperator);
	}

	public static int countO_ByC_PO_N_T_S_C_Z_R_C(
		long companyId, long parentOrganizationId,
		String parentOrganizationIdComparator, String[] names, String type,
		String[] streets, String[] cities, String[] zips, Long regionId,
		Long countryId, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator) {

		return getFinder().countO_ByC_PO_N_T_S_C_Z_R_C(
			companyId, parentOrganizationId, parentOrganizationIdComparator,
			names, type, streets, cities, zips, regionId, countryId, params,
			andOperator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
		findO_ByKeywords(
			long companyId, long parentOrganizationId,
			String parentOrganizationIdComparator, String keywords, String type,
			Long regionId, Long countryId,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Organization>
					orderByComparator) {

		return getFinder().findO_ByKeywords(
			companyId, parentOrganizationId, parentOrganizationIdComparator,
			keywords, type, regionId, countryId, params, start, end,
			orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
		findO_ByNoAssets() {

		return getFinder().findO_ByNoAssets();
	}

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
		findO_ByC_PO_N_T_S_C_Z_R_C(
			long companyId, long parentOrganizationId,
			String parentOrganizationIdComparator, String name, String type,
			String street, String city, String zip, Long regionId,
			Long countryId, java.util.LinkedHashMap<String, Object> params,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Organization>
					orderByComparator) {

		return getFinder().findO_ByC_PO_N_T_S_C_Z_R_C(
			companyId, parentOrganizationId, parentOrganizationIdComparator,
			name, type, street, city, zip, regionId, countryId, params,
			andOperator, start, end, orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Organization>
		findO_ByC_PO_N_T_S_C_Z_R_C(
			long companyId, long parentOrganizationId,
			String parentOrganizationIdComparator, String[] names, String type,
			String[] streets, String[] cities, String[] zips, Long regionId,
			Long countryId, java.util.LinkedHashMap<String, Object> params,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Organization>
					orderByComparator) {

		return getFinder().findO_ByC_PO_N_T_S_C_Z_R_C(
			companyId, parentOrganizationId, parentOrganizationIdComparator,
			names, type, streets, cities, zips, regionId, countryId, params,
			andOperator, start, end, orderByComparator);
	}

	public static OrganizationFinder getFinder() {
		if (_finder == null) {
			_finder = (OrganizationFinder)PortalBeanLocatorUtil.locate(
				OrganizationFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(OrganizationFinder finder) {
		_finder = finder;
	}

	private static OrganizationFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:912236717