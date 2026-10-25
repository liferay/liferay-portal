/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface DDMDataProviderInstanceFinder {

	public java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance>
			filterByC_G(long companyId, long[] groupIds, int start, int end);

	public java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance>
			filterByKeywords(
				long companyId, long[] groupIds, String keywords, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.dynamic.data.mapping.model.
						DDMDataProviderInstance> orderByComparator);

	public int filterCountByKeywords(
		long companyId, long[] groupIds, String keywords);

	public int filterCountByC_G(long companyId, long[] groupIds);

	public int filterCountByC_G_N_D(
		long companyId, long[] groupIds, String name, String description,
		boolean andOperator);

	public java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance>
			filterFindByC_G_N_D(
				long companyId, long[] groupIds, String name,
				String description, boolean andOperator, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.dynamic.data.mapping.model.
						DDMDataProviderInstance> orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:1973428710