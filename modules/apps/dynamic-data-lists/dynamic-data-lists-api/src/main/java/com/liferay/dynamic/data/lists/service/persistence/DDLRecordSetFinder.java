/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface DDLRecordSetFinder {

	public int countByKeywords(
		long companyId, long groupId, String keywords, int scope);

	public int filterCountByKeywords(
		long companyId, long groupId, String keywords, int scope);

	public int filterCountByC_G_N_D_S(
		long companyId, long groupId, String name, String description,
		int scope, boolean andOperator);

	public java.util.List<com.liferay.dynamic.data.lists.model.DDLRecordSet>
		filterFindByKeywords(
			long companyId, long groupId, String keywords, int scope, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.lists.model.DDLRecordSet>
					orderByComparator);

	public java.util.List<com.liferay.dynamic.data.lists.model.DDLRecordSet>
		filterFindByC_G_N_D_S(
			long companyId, long groupId, String name, String description,
			int scope, boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.lists.model.DDLRecordSet>
					orderByComparator);

	public java.util.List<com.liferay.dynamic.data.lists.model.DDLRecordSet>
		filterFindByC_G_N_D_S(
			long companyId, long groupId, String[] names, String[] descriptions,
			int scope, boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.lists.model.DDLRecordSet>
					orderByComparator);

	public java.util.List<com.liferay.dynamic.data.lists.model.DDLRecordSet>
		findByKeywords(
			long companyId, long groupId, String keywords, int scope, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.lists.model.DDLRecordSet>
					orderByComparator);

	public java.util.List<com.liferay.dynamic.data.lists.model.DDLRecordSet>
		findByC_G_N_D_S(
			long companyId, long groupId, String[] names, String[] descriptions,
			int scope, boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.lists.model.DDLRecordSet>
					orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1109253404