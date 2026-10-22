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
public interface UserGroupRoleFinder {

	public java.util.List<com.liferay.portal.kernel.model.UserGroupRole>
		findByGroupRoleType(long groupId, int roleType);

}
// LIFERAY-SERVICE-BUILDER-HASH:1433632602