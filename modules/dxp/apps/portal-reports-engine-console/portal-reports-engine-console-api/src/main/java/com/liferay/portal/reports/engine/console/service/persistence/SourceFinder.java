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
public interface SourceFinder {

	public int filterCountByG_N_DU(
		long groupId, String name, String driverUrl, boolean andOperator);

	public java.util.List
		<com.liferay.portal.reports.engine.console.model.Source>
			filterFindByG_N_DU(
				long groupId, String name, String driverUrl,
				boolean andOperator, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.reports.engine.console.model.Source>
						orderByComparator);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1886789310