/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.display.context;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.processor.ImageProcessorUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class RepositoryEntryBrowserDisplayContext {

	public RepositoryEntryBrowserDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public String getGroupCssIcon(long groupId) throws PortalException {
		Group group = _getGroup(groupId);

		return group.getIconCssClass();
	}

	public String getGroupLabel(long groupId, Locale locale)
		throws PortalException {

		Group group = _getGroup(groupId);

		return group.getDescriptiveName(locale);
	}

	public String getType(FileVersion fileVersion) {
		if (fileVersion == null) {
			return StringPool.BLANK;
		}

		if (ArrayUtil.contains(
				PropsValues.DL_FILE_ENTRY_PREVIEW_AUDIO_MIME_TYPES,
				fileVersion.getMimeType())) {

			return "audio";
		}

		if (ImageProcessorUtil.isSupported(fileVersion.getMimeType())) {
			return "image";
		}

		if (ArrayUtil.contains(
				PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_MIME_TYPES,
				fileVersion.getMimeType()) ||
			Objects.equals(
				ContentTypes.
					APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML,
				fileVersion.getMimeType())) {

			return "video";
		}

		return StringPool.BLANK;
	}

	public boolean hasGuestViewPermission(FileEntry fileEntry)
		throws PortalException {

		if (_guestRole == null) {
			_guestRole = RoleLocalServiceUtil.getRole(
				fileEntry.getCompanyId(), RoleConstants.GUEST);
		}

		return ResourcePermissionLocalServiceUtil.hasResourcePermission(
			fileEntry.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fileEntry.getFileEntryId()), _guestRole.getRoleId(),
			ActionKeys.VIEW);
	}

	public boolean isPreviewable(FileVersion fileVersion) {
		if (fileVersion == null) {
			return false;
		}

		if (ArrayUtil.contains(
				PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_MIME_TYPES,
				fileVersion.getMimeType()) ||
			ImageProcessorUtil.isImageSupported(fileVersion.getMimeType()) ||
			Objects.equals(
				ContentTypes.
					APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML,
				fileVersion.getMimeType())) {

			return true;
		}

		return false;
	}

	public boolean isSearchEverywhere() {
		if (_searchEverywhere != null) {
			return _searchEverywhere;
		}

		if (Objects.equals(
				ParamUtil.getString(_httpServletRequest, "scope"),
				"everywhere")) {

			_searchEverywhere = true;
		}
		else {
			_searchEverywhere = false;
		}

		return _searchEverywhere;
	}

	private Group _getGroup(long groupId) throws PortalException {
		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (group.isCompany()) {
			return group;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		GroupPermissionUtil.check(
			themeDisplay.getPermissionChecker(), group, ActionKeys.VIEW);

		return group;
	}

	private Role _guestRole;
	private final HttpServletRequest _httpServletRequest;
	private Boolean _searchEverywhere;

}