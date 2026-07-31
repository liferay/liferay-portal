/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.web.internal.constants.DesignLibraryWebKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Mario Leandro
 * @author Thiago Buarque
 */
public abstract class BaseDesignLibraryDisplayContext {

	public BaseDesignLibraryDisplayContext(
		HttpServletRequest httpServletRequest) {

		this.httpServletRequest = httpServletRequest;

		depotEntry = (DepotEntry)httpServletRequest.getAttribute(
			DesignLibraryWebKeys.DESIGN_LIBRARY_ENTRY);
		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	protected Map<String, Object> buildEmptyState(
		String descriptionKey, String image, String titleKey) {

		return HashMapBuilder.<String, Object>put(
			"description", LanguageUtil.get(httpServletRequest, descriptionKey)
		).put(
			"image", image
		).put(
			"title", LanguageUtil.get(httpServletRequest, titleKey)
		).build();
	}

	protected String getAssetLibraryURL(Group group, String path) {
		return StringBundler.concat(
			"/o/headless-asset-library/v1.0/asset-libraries/",
			group.getExternalReferenceCode(), path);
	}

	protected Group getGroup() throws PortalException {
		return depotEntry.getGroup();
	}

	protected String getViewResourcesURL(
		LiferayPortletResponse liferayPortletResponse) {

		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/design_library/view_resources_design_library"
		).setParameter(
			"designLibraryEntryId", depotEntry.getDepotEntryId()
		).buildString();
	}

	protected boolean hasAssignMembersPermission(Group group)
		throws PortalException {

		return GroupPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), group.getGroupId(),
			ActionKeys.ASSIGN_MEMBERS);
	}

	protected boolean hasDepotEntryPermission(Group group, String actionId) {
		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		return permissionChecker.hasPermission(
			group, DepotEntry.class.getName(), group.getClassPK(), actionId);
	}

	protected final DepotEntry depotEntry;
	protected final HttpServletRequest httpServletRequest;
	protected final ThemeDisplay themeDisplay;

}