/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.image.uploader.web.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.kernel.file.uploads.UserFileUploadsSettings;

import jakarta.portlet.PortletRequest;

/**
 * @author Peter Fellwock
 */
public class UploadImageUtil {

	public static final String TEMP_IMAGE_FILE_NAME = "tempImageFileName";

	public static final String TEMP_IMAGE_FOLDER_NAME =
		UploadImageUtil.class.getName();

	public static long getMaxFileSize(PortletRequest portletRequest) {
		String currentLogoURL = portletRequest.getParameter("currentLogoURL");

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (StringUtil.startsWith(
				currentLogoURL,
				themeDisplay.getPathImage() + "/organization_logo") ||
			StringUtil.startsWith(
				currentLogoURL,
				themeDisplay.getPathImage() + "/user_female_portrait") ||
			StringUtil.startsWith(
				currentLogoURL,
				themeDisplay.getPathImage() + "/user_male_portrait") ||
			StringUtil.startsWith(
				currentLogoURL,
				themeDisplay.getPathImage() + "/user_portrait")) {

			UserFileUploadsSettings userFileUploadsSettings =
				_userFileUploadSettingsSnapshot.get();

			return userFileUploadsSettings.getImageMaxSize();
		}

		return UploadServletRequestConfigurationProviderUtil.getMaxSize();
	}

	public static FileEntry getTempImageFileEntry(PortletRequest portletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return TempFileEntryUtil.getTempFileEntry(
			themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
			TEMP_IMAGE_FOLDER_NAME,
			ParamUtil.getString(portletRequest, TEMP_IMAGE_FILE_NAME));
	}

	private static final Snapshot<UserFileUploadsSettings>
		_userFileUploadSettingsSnapshot = new Snapshot<>(
			UploadImageUtil.class, UserFileUploadsSettings.class);

}