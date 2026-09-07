/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.internal.resource.v1_0.PageTemplateSetResourceImpl;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.util.ActionUtil;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

/**
 * @author Georgel Pop
 */
public class PageTemplateSetActionUtil {

	public static Map<String, Map<String, String>> getDesignLibraryActions(
		Object contextScopeChecker, String designLibraryExternalReferenceCode,
		LayoutPageTemplateCollection layoutPageTemplateCollection,
		ModelResourcePermission<LayoutPageTemplateCollection>
			modelResourcePermission,
		UriInfo uriInfo) {

		Map<String, String> templateParameterMap = HashMapBuilder.put(
			"designLibraryExternalReferenceCode",
			designLibraryExternalReferenceCode
		).put(
			"pageTemplateSetExternalReferenceCode",
			layoutPageTemplateCollection.getExternalReferenceCode()
		).build();

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			_addAction(
				ActionKeys.DELETE, contextScopeChecker,
				layoutPageTemplateCollection,
				"deleteDesignLibraryPageTemplateSet", modelResourcePermission,
				templateParameterMap, uriInfo)
		).put(
			"get",
			_addAction(
				ActionKeys.VIEW, contextScopeChecker,
				layoutPageTemplateCollection, "getDesignLibraryPageTemplateSet",
				modelResourcePermission, templateParameterMap, uriInfo)
		).build();
	}

	private static Map<String, String> _addAction(
		String actionName, Object contextScopeChecker,
		LayoutPageTemplateCollection layoutPageTemplateCollection,
		String methodName,
		ModelResourcePermission<LayoutPageTemplateCollection>
			modelResourcePermission,
		Map<String, String> templateParameterMap, UriInfo uriInfo) {

		return ActionUtil.addAction(
			actionName, PageTemplateSetResourceImpl.class,
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId(),
			methodName, contextScopeChecker, modelResourcePermission,
			templateParameterMap, uriInfo);
	}

}