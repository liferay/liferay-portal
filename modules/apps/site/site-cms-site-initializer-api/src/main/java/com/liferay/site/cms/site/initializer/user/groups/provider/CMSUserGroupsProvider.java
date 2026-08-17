/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.user.groups.provider;

import com.liferay.portal.kernel.model.UserGroup;

import java.util.List;

/**
 * @author Larissa Ribeiro
 */
public interface CMSUserGroupsProvider {

	public List<UserGroup> getUserGroups(String keywords, int start, int end);

	public int getUserGroupsCount(String keywords);

}