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
public interface LayoutFinder {

	public java.util.List<com.liferay.portal.kernel.model.Layout>
		findByScopeGroup(long groupId);

	public java.util.List<com.liferay.portal.kernel.model.Layout>
		findByScopeGroup(long groupId, boolean privateLayout);

	public java.util.List<com.liferay.portal.kernel.model.LayoutReference>
		findByC_P_P(
			long companyId, String portletId, String preferencesKey,
			String preferencesValue);

}
// LIFERAY-SERVICE-BUILDER-HASH:750608168