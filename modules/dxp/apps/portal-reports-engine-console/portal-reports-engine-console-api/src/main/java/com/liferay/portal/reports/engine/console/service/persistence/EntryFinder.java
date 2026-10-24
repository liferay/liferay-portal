/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface EntryFinder {

	public int filterCountByG_CD_N_SN(
		long groupId, String definitionName, String userName,
		java.util.Date createDateGT, java.util.Date createDateLT,
		boolean andOperator);

	public java.util.List<com.liferay.portal.reports.engine.console.model.Entry>
		filterFindByG_CD_N_SN(
			long groupId, String definitionName, String userName,
			java.util.Date createDateGT, java.util.Date createDateLT,
			boolean andOperator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.reports.engine.console.model.Entry>
					orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:-706698729