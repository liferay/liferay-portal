/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.CommentUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Mikel Lorza
 */
public class ViewAssetDisplayContext {

	public ViewAssetDisplayContext(
			HttpServletRequest httpServletRequest,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryService objectEntryService)
		throws PortalException {

		_httpServletRequest = httpServletRequest;

		_objectEntry = objectEntryService.getObjectEntry(
			ParamUtil.getLong(httpServletRequest, "objectEntryId"));

		_objectDefinition = objectDefinitionLocalService.getObjectDefinition(
			_objectEntry.getObjectDefinitionId());

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getAdditionalProps() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"backURL",
			PortalUtil.escapeRedirect(
				ParamUtil.getString(_httpServletRequest, "backURL"))
		).put(
			"className", _objectDefinition.getClassName()
		).put(
			"commentsProps", CommentUtil.getCommentsProps(_httpServletRequest)
		).put(
			"contentViewURL",
			StringBundler.concat(
				_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/edit_content_item?p_l_mode=read&p_p_state=",
				LiferayWindowState.POP_UP, "&redirect=",
				_themeDisplay.getURLCurrent(), "&objectEntryId=",
				_objectEntry.getObjectEntryId())
		).put(
			"getObjectEntryURL",
			StringBundler.concat(
				"/o", _objectDefinition.getRESTContextPath(), "/scopes/",
				_objectEntry.getGroupId(), "/by-external-reference-code/",
				_objectEntry.getExternalReferenceCode(),
				"?nestedFields=file.metadata,file.previewURL,file.thumbnailURL")
		).put(
			"hasCommentPermission",
			() -> {
				ModelResourcePermission<ObjectEntry> modelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(
							_objectDefinition.getClassName());

				return modelResourcePermission.contains(
					_themeDisplay.getPermissionChecker(), _objectEntry,
					ActionKeys.ADD_DISCUSSION);
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntry _objectEntry;
	private final ThemeDisplay _themeDisplay;

}