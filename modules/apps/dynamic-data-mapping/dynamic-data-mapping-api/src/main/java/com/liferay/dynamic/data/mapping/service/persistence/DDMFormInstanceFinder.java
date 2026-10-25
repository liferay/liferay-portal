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
public interface DDMFormInstanceFinder {

	public int filterCountByKeywords(
		long companyId, long groupId, String keywords);

	public int filterCountByKeywords(
		long companyId, long groupId, String keywords, int status);

	public int filterCountByC_G(long companyId, long groupId);

	public int filterCountByC_G_N_D(
		long companyId, long groupId, String[] names, String[] descriptions,
		boolean andOperator);

	public java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMFormInstance>
			filterFindByKeywords(
				long companyId, long groupId, String keywords, int status,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.dynamic.data.mapping.model.DDMFormInstance>
						orderByComparator);

	public java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMFormInstance>
			filterFindByKeywords(
				long companyId, long groupId, String keywords, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.dynamic.data.mapping.model.DDMFormInstance>
						orderByComparator);

	public java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMFormInstance>
			filterFindByC_G(long companyId, long groupId, int start, int end);

	public java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMFormInstance>
			filterFindByC_G_N_D(
				long companyId, long groupId, String[] names,
				String[] descriptions, boolean andOperator, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.dynamic.data.mapping.model.DDMFormInstance>
						orderByComparator);

	public java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMFormInstance>
			filterFindByC_G_N_D_S(
				long companyId, long groupId, String[] names,
				String[] descriptions, int status, boolean andOperator,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.dynamic.data.mapping.model.DDMFormInstance>
						orderByComparator);

	public java.util.List
		<com.liferay.dynamic.data.mapping.model.DDMFormInstance> findByKeywords(
			long companyId, long groupId, String keywords, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.mapping.model.DDMFormInstance>
					orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:-536828885