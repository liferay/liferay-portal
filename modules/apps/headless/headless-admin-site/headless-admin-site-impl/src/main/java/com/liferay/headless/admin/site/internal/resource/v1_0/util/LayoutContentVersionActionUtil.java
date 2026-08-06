/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.internal.resource.v1_0.PageSpecificationVersionResourceImpl;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.util.ActionUtil;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutContentVersionActionUtil {

	public static Map<String, Map<String, String>> getActions(
		Object contextScopeChecker, boolean deletable,
		LayoutContentVersion layoutContentVersion,
		ModelResourcePermission<Layout> layoutModelResourcePermission,
		String siteExternalReferenceCode, String sitePageExternalReferenceCode,
		UriInfo uriInfo) {

		Map<String, String> templateParameterMap = HashMapBuilder.put(
			"pageSpecificationVersionExternalReferenceCode",
			layoutContentVersion.getExternalReferenceCode()
		).put(
			"siteExternalReferenceCode", siteExternalReferenceCode
		).put(
			"sitePageExternalReferenceCode", sitePageExternalReferenceCode
		).build();

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			() -> {
				if (!deletable) {
					return null;
				}

				return _addAction(
					contextScopeChecker, layoutContentVersion,
					layoutModelResourcePermission,
					"deleteSiteSitePagePageSpecificationVersion",
					templateParameterMap, uriInfo);
			}
		).put(
			"get",
			_addAction(
				contextScopeChecker, layoutContentVersion,
				layoutModelResourcePermission,
				"getSiteSitePagePageSpecificationVersion", templateParameterMap,
				uriInfo)
		).build();
	}

	private static Map<String, String> _addAction(
		Object contextScopeChecker, LayoutContentVersion layoutContentVersion,
		ModelResourcePermission<Layout> layoutModelResourcePermission,
		String methodName, Map<String, String> templateParameterMap,
		UriInfo uriInfo) {

		return ActionUtil.addAction(
			ActionKeys.UPDATE, PageSpecificationVersionResourceImpl.class,
			layoutContentVersion.getPlid(), methodName, contextScopeChecker,
			layoutModelResourcePermission, templateParameterMap, uriInfo);
	}

}