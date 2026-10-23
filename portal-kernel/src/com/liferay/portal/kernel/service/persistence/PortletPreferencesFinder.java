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
public interface PortletPreferencesFinder {

	public long countByO_O_P(
		long ownerId, int ownerType, String portletId,
		boolean excludeDefaultPreferences);

	public long countByO_O_P_P_P(
		long ownerId, int ownerType, long plid, String portletId,
		boolean excludeDefaultPreferences);

	public java.util.List<com.liferay.portal.kernel.model.PortletPreferences>
		findByPortletId(String portletId);

	public java.util.List<com.liferay.portal.kernel.model.PortletPreferences>
		findByC_G_O_O_P_P(
			long companyId, long groupId, long ownerId, int ownerType,
			String portletId, boolean privateLayout);

}
// LIFERAY-SERVICE-BUILDER-HASH:872737585