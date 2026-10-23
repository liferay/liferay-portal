/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface OrganizationFinder {

	public int countO_ByKeywords(
		long companyId, long parentOrganizationId,
		String parentOrganizationIdComparator, String keywords, String type,
		Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params);

	public int countO_ByC_PO_N_T_S_C_Z_R_C(
		long companyId, long parentOrganizationId,
		String parentOrganizationIdComparator, String name, String type,
		String street, String city, String zip, Long regionId, Long countryId,
		java.util.LinkedHashMap<String, Object> params, boolean andOperator);

	public int countO_ByC_PO_N_T_S_C_Z_R_C(
		long companyId, long parentOrganizationId,
		String parentOrganizationIdComparator, String[] names, String type,
		String[] streets, String[] cities, String[] zips, Long regionId,
		Long countryId, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator);

	public java.util.List<com.liferay.portal.kernel.model.Organization>
		findO_ByKeywords(
			long companyId, long parentOrganizationId,
			String parentOrganizationIdComparator, String keywords, String type,
			Long regionId, Long countryId,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Organization>
					orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Organization>
		findO_ByNoAssets();

	public java.util.List<com.liferay.portal.kernel.model.Organization>
		findO_ByC_PO_N_T_S_C_Z_R_C(
			long companyId, long parentOrganizationId,
			String parentOrganizationIdComparator, String name, String type,
			String street, String city, String zip, Long regionId,
			Long countryId, java.util.LinkedHashMap<String, Object> params,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Organization>
					orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Organization>
		findO_ByC_PO_N_T_S_C_Z_R_C(
			long companyId, long parentOrganizationId,
			String parentOrganizationIdComparator, String[] names, String type,
			String[] streets, String[] cities, String[] zips, Long regionId,
			Long countryId, java.util.LinkedHashMap<String, Object> params,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Organization>
					orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1922651618