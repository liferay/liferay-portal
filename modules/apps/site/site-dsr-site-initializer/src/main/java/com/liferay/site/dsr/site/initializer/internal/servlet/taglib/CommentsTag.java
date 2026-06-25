/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.servlet.taglib;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectEntryServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.dsr.site.initializer.internal.servlet.ServletContextUtil;
import com.liferay.site.dsr.site.initializer.util.DSRRoomUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author Stefano Motta
 */
public class CommentsTag extends IncludeTag {

	public long getGroupId() {
		return _groupId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_groupId = 0;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-site-dsr-site-initializer:comments:readOnly",
			Boolean.FALSE);
		httpServletRequest.setAttribute(
			"liferay-site-dsr-site-initializer:comments:roomId", 0L);

		if (_groupId == 0) {
			return;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(_groupId);

		if (group == null) {
			return;
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", group.getCompanyId());

		if ((objectDefinition == null) ||
			!Objects.equals(
				group.getClassName(), objectDefinition.getClassName())) {

			return;
		}

		String addCommentURL = "";
		String deleteCommentURL = "";
		String editCommentURL = "";

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		ObjectEntry objectEntry = ObjectEntryLocalServiceUtil.fetchObjectEntry(
			group.getClassPK());

		boolean readOnly = false;

		if ((objectEntry != null) &&
			DSRRoomUtil.isReadOnly(objectEntry, permissionChecker)) {

			readOnly = true;
		}

		try {
			ModelResourcePermission<ObjectEntry> modelResourcePermission =
				ObjectEntryServiceUtil.getModelResourcePermission(
					objectDefinition.getObjectDefinitionId());

			if (!readOnly &&
				modelResourcePermission.contains(
					permissionChecker, group.getClassPK(),
					ActionKeys.ADD_DISCUSSION)) {

				addCommentURL = StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/add_content_item_comment?className=",
					URLEncoder.encode(
						objectDefinition.getClassName(),
						StandardCharsets.UTF_8),
					"&classPK=", group.getClassPK());
			}

			if (!readOnly &&
				modelResourcePermission.contains(
					permissionChecker, group.getClassPK(),
					ActionKeys.DELETE_DISCUSSION)) {

				deleteCommentURL = StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/delete_content_item_comment");
			}

			if (!readOnly &&
				modelResourcePermission.contains(
					permissionChecker, group.getClassPK(),
					ActionKeys.UPDATE_DISCUSSION)) {

				editCommentURL = StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item_comment?className=",
					URLEncoder.encode(
						objectDefinition.getClassName(),
						StandardCharsets.UTF_8),
					"&classPK=", group.getClassPK());
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		httpServletRequest.setAttribute(
			"liferay-site-dsr-site-initializer:comments:addCommentURL",
			addCommentURL);
		httpServletRequest.setAttribute(
			"liferay-site-dsr-site-initializer:comments:deleteCommentURL",
			deleteCommentURL);
		httpServletRequest.setAttribute(
			"liferay-site-dsr-site-initializer:comments:editCommentURL",
			editCommentURL);

		EditorConfiguration contentItemCommentEditorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				StringPool.BLANK, "contentItemCommentEditor", StringPool.BLANK,
				Collections.emptyMap(), themeDisplay,
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest));

		Map<String, Object> data =
			contentItemCommentEditorConfiguration.getData();

		httpServletRequest.setAttribute(
			"liferay-site-dsr-site-initializer:comments:editorConfig",
			data.get("editorConfig"));

		httpServletRequest.setAttribute(
			"liferay-site-dsr-site-initializer:comments:getCommentsURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/get_asset_comments?className=",
				URLEncoder.encode(
					objectDefinition.getClassName(), StandardCharsets.UTF_8),
				"&classPK=", group.getClassPK()));
		httpServletRequest.setAttribute(
			"liferay-site-dsr-site-initializer:comments:readOnly", readOnly);
		httpServletRequest.setAttribute(
			"liferay-site-dsr-site-initializer:comments:roomId",
			group.getClassPK());
	}

	private static final String _PAGE = "/comments/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(CommentsTag.class);

	private long _groupId;

}