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
public interface DDMTemplateFinder {

	public int filterCountByG_SC_S(
		long groupId, long structureClassNameId, int status);

	public int filterCountByC_G_C_C_R_T_M_S(
		long companyId, long[] groupIds, long[] classNameIds, long[] classPKs,
		long resourceClassNameId, String type, String mode, int status);

	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMTemplate>
		filterFindByG_SC_S(
			long groupId, long structureClassNameId, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.mapping.model.DDMTemplate>
					orderByComparator);

	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMTemplate>
		filterFindByC_G_C_C_R_T_M_S(
			long companyId, long[] groupIds, long classNameId, long classPK,
			long resourceClassNameId, String type, String mode, int status,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.mapping.model.DDMTemplate>
					orderByComparator);

	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMTemplate>
		filterFindByC_G_C_C_R_T_M_S(
			long companyId, long[] groupIds, long[] classNameIds,
			long[] classPKs, long resourceClassNameId, String type, String mode,
			int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.mapping.model.DDMTemplate>
					orderByComparator);

	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMTemplate>
		findByG_SC_S(
			long groupId, long structureClassNameId, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.mapping.model.DDMTemplate>
					orderByComparator);

	public java.util.List<com.liferay.dynamic.data.mapping.model.DDMTemplate>
		findByC_G_C_C_R_T_M_S(
			long companyId, long[] groupIds, long[] classNameIds,
			long[] classPKs, long resourceClassNameId, String type, String mode,
			int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.dynamic.data.mapping.model.DDMTemplate>
					orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:461799657