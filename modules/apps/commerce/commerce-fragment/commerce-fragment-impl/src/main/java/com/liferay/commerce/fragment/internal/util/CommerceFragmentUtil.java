/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceFragmentUtil {

	public static String getCurrentAccountPostURL(
		HttpServletRequest httpServletRequest) {

		return StringBundler.concat(
			PortalUtil.getPortalURL(httpServletRequest),
			PortalUtil.getPathContext(), "/o/commerce-ui/set-current-account");
	}

}