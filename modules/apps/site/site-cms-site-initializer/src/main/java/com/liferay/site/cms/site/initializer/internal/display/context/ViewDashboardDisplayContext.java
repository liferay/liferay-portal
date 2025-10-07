/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Map;

/**
 * @author Adriano Interaminense
 */
public class ViewDashboardDisplayContext {

	public ViewDashboardDisplayContext(
		GroupLocalService groupLocalService, ThemeDisplay themeDisplay) {

		_groupLocalService = groupLocalService;
		_themeDisplay = themeDisplay;
	}

	public Map<String, Object> getConstants() {
		return HashMapBuilder.<String, Object>put(
			"cmsGroupId",
			() -> {
				try {
					Group group = _groupLocalService.getGroup(
						_themeDisplay.getCompanyId(), GroupConstants.CMS);

					return group.getGroupId();
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}

				return null;
			}
		).put(
			"ercContentStructures",
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES
		).put(
			"ercFileTypes",
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
		).build();
	}

	public Map<String, Object> getReactData() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"constants", getConstants()
		).put(
			"dashboard",
			PortalUtil.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayoutByFriendlyURL(
					_themeDisplay.getScopeGroupId(), false, "/dashboard"),
				_themeDisplay)
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewDashboardDisplayContext.class);

	private final GroupLocalService _groupLocalService;
	private final ThemeDisplay _themeDisplay;

}