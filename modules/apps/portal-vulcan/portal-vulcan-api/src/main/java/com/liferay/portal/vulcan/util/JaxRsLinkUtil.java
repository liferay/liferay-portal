/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.petra.string.StringPool;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

/**
 * @author Brian Wing Shun Chan
 */
public class JaxRsLinkUtil {

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static String getJaxRsLink(
		Class<?> clazz, String methodName, UriInfo uriInfo, Object... values) {

		String basePath = UriInfoUtil.getBasePath(uriInfo);

		if (basePath.endsWith(StringPool.FORWARD_SLASH)) {
			basePath = basePath.substring(0, basePath.length() - 1);
		}

		URI resourceURI = UriBuilder.fromResource(
			clazz
		).build();

		URI methodURI = UriBuilder.fromMethod(
			clazz, methodName
		).build(
			values
		);

		return basePath + resourceURI.toString() + methodURI.toString();
	}

	public static String getJaxRsLink(
		String applicationPath, Class<?> clazz, String methodName,
		UriInfo uriInfo, Object... values) {

		return UriInfoUtil.getBaseUriBuilder(
			applicationPath, uriInfo
		).path(
			clazz
		).path(
			clazz, methodName
		).build(
			values, false
		).toString();
	}

}