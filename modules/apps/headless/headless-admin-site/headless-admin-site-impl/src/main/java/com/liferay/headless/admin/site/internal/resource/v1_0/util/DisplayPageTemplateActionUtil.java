/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.internal.resource.v1_0.DisplayPageTemplateResourceImpl;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.util.ActionUtil;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

/**
 * @author Javier Moral
 */
public class DisplayPageTemplateActionUtil {

	public static Map<String, Map<String, String>> getDesignLibraryActions(
		Object contextScopeChecker, String designLibraryExternalReferenceCode,
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		ModelResourcePermission<LayoutPageTemplateEntry>
			modelResourcePermission,
		UriInfo uriInfo) {

		Map<String, String> templateParameterMap = HashMapBuilder.put(
			"designLibraryExternalReferenceCode",
			designLibraryExternalReferenceCode
		).put(
			"displayPageTemplateExternalReferenceCode",
			layoutPageTemplateEntry.getExternalReferenceCode()
		).build();

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			_addAction(
				ActionKeys.DELETE, contextScopeChecker, layoutPageTemplateEntry,
				"deleteDesignLibraryDisplayPageTemplate",
				modelResourcePermission, templateParameterMap, uriInfo)
		).put(
			"get",
			_addAction(
				ActionKeys.VIEW, contextScopeChecker, layoutPageTemplateEntry,
				"getDesignLibraryDisplayPageTemplate", modelResourcePermission,
				templateParameterMap, uriInfo)
		).put(
			"permissions",
			_addAction(
				ActionKeys.PERMISSIONS, contextScopeChecker,
				layoutPageTemplateEntry,
				"getDesignLibraryDisplayPageTemplatePermissionsPage",
				modelResourcePermission, templateParameterMap, uriInfo)
		).build();
	}

	private static Map<String, String> _addAction(
		String actionName, Object contextScopeChecker,
		LayoutPageTemplateEntry layoutPageTemplateEntry, String methodName,
		ModelResourcePermission<LayoutPageTemplateEntry>
			modelResourcePermission,
		Map<String, String> templateParameterMap, UriInfo uriInfo) {

		return ActionUtil.addAction(
			actionName, DisplayPageTemplateResourceImpl.class,
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), methodName,
			contextScopeChecker, modelResourcePermission, templateParameterMap,
			uriInfo);
	}

}