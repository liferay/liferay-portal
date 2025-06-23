/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.live.users.jmx;

/**
 * @author Raymond Augé
 */
public interface LiveUsersMBean {

	public long getCompanyLiveUsersCount(String webId);

	public long getServerLiveUsersCount();

}