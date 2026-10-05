/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.internal.resource.v1_0.MasterPageResourceImpl;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.util.ActionUtil;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

/**
 * @author Georgel Pop
 */
public class MasterPageActionUtil {

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
			"masterPageExternalReferenceCode",
			layoutPageTemplateEntry.getExternalReferenceCode()
		).build();

		return HashMapBuilder.<String, Map<String, String>>put(
			() -> {
				if (!layoutPageTemplateEntry.isDraft()) {
					return "copy";
				}

				return null;
			},
			() -> _addAction(
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY,
				contextScopeChecker, layoutPageTemplateEntry,
				"postDesignLibraryMasterPageCopy",
				LayoutPageTemplateConstants.RESOURCE_NAME, templateParameterMap,
				uriInfo)
		).put(
			() -> {
				if (!layoutPageTemplateEntry.isDraft()) {
					return "copyWithPermission";
				}

				return null;
			},
			() -> _addAction(
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY,
				contextScopeChecker, layoutPageTemplateEntry,
				"postDesignLibraryMasterPageCopyWithPermission",
				LayoutPageTemplateConstants.RESOURCE_NAME, templateParameterMap,
				uriInfo)
		).put(
			"delete",
			_addAction(
				ActionKeys.DELETE, contextScopeChecker, layoutPageTemplateEntry,
				"deleteDesignLibraryMasterPage", modelResourcePermission,
				templateParameterMap, uriInfo)
		).put(
			"get",
			_addAction(
				ActionKeys.VIEW, contextScopeChecker, layoutPageTemplateEntry,
				"getDesignLibraryMasterPage", modelResourcePermission,
				templateParameterMap, uriInfo)
		).put(
			() -> {
				if (layoutPageTemplateEntry.isApproved() &&
					!layoutPageTemplateEntry.isDefaultTemplate()) {

					return "markAsDefault";
				}

				return null;
			},
			() -> _addAction(
				ActionKeys.UPDATE, contextScopeChecker, layoutPageTemplateEntry,
				"postDesignLibraryMasterPageMarkAsDefault",
				modelResourcePermission, templateParameterMap, uriInfo)
		).put(
			"permissions",
			_addAction(
				ActionKeys.PERMISSIONS, contextScopeChecker,
				layoutPageTemplateEntry,
				"getDesignLibraryMasterPagePermissionsPage",
				modelResourcePermission, templateParameterMap, uriInfo)
		).put(
			() -> {
				if (layoutPageTemplateEntry.isDefaultTemplate()) {
					return "unmarkAsDefault";
				}

				return null;
			},
			() -> _addAction(
				ActionKeys.UPDATE, contextScopeChecker, layoutPageTemplateEntry,
				"postDesignLibraryMasterPageUnmarkAsDefault",
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
			actionName, MasterPageResourceImpl.class,
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), methodName,
			contextScopeChecker, modelResourcePermission, templateParameterMap,
			uriInfo);
	}

	private static Map<String, String> _addAction(
		String actionName, Object contextScopeChecker,
		LayoutPageTemplateEntry layoutPageTemplateEntry, String methodName,
		String resourceName, Map<String, String> templateParameterMap,
		UriInfo uriInfo) {

		return ActionUtil.addAction(
			actionName, MasterPageResourceImpl.class,
			layoutPageTemplateEntry.getGroupId(), methodName,
			contextScopeChecker, null, resourceName,
			layoutPageTemplateEntry.getGroupId(), templateParameterMap,
			uriInfo);
	}

}