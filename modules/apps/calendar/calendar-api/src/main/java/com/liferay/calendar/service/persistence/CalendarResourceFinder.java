/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Eduardo Lundgren
 * @generated
 */
@ProviderType
public interface CalendarResourceFinder {

	public int countByKeywords(
		long companyId, long[] groupIds, long[] classNameIds, String keywords,
		boolean active);

	public int countByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String[] codes,
		String[] names, String[] descriptions, boolean active,
		boolean andOperator);

	public int filterCountByKeywords(
		long companyId, long[] groupIds, long[] classNameIds, String keywords,
		boolean active);

	public int filterCountByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String code,
		String name, String description, boolean active, boolean andOperator);

	public int filterCountByC_G_C_C_N_D_A(
		long companyId, long[] groupIds, long[] classNameIds, String[] codes,
		String[] names, String[] descriptions, boolean active,
		boolean andOperator);

	public java.util.List<com.liferay.calendar.model.CalendarResource>
		filterFindByKeywords(
			long companyId, long[] groupIds, long[] classNameIds,
			String keywords, boolean active, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.calendar.model.CalendarResource>
					orderByComparator);

	public java.util.List<com.liferay.calendar.model.CalendarResource>
		filterFindByC_G_C_C_N_D_A(
			long companyId, long[] groupIds, long[] classNameIds, String code,
			String name, String description, boolean active,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.calendar.model.CalendarResource>
					orderByComparator);

	public java.util.List<com.liferay.calendar.model.CalendarResource>
		filterFindByC_G_C_C_N_D_A(
			long companyId, long[] groupIds, long[] classNameIds,
			String[] codes, String[] names, String[] descriptions,
			boolean active, boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.calendar.model.CalendarResource>
					orderByComparator);

	public java.util.List<com.liferay.calendar.model.CalendarResource>
		findByKeywords(
			long companyId, long[] groupIds, long[] classNameIds,
			String keywords, boolean active, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.calendar.model.CalendarResource>
					orderByComparator);

	public java.util.List<com.liferay.calendar.model.CalendarResource>
		findByC_G_C_C_N_D_A(
			long companyId, long[] groupIds, long[] classNameIds,
			String[] codes, String[] names, String[] descriptions,
			boolean active, boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.calendar.model.CalendarResource>
					orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:1661961123