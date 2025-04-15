/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public interface LayoutClone {

	public String get(HttpServletRequest httpServletRequest, long plid);

	public void update(
		HttpServletRequest httpServletRequest, long plid, String typeSettings);

}