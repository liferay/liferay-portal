/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.search;

import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.service.AnnouncementsEntryLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.announcements.service.permission.AnnouncementsEntryPermission;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Roberto Díaz
 */
public class AnnouncementsEntryChecker extends EmptyOnClickRowChecker {

	public AnnouncementsEntryChecker(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(liferayPortletResponse);

		_liferayPortletResponse = liferayPortletResponse;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_permissionChecker = themeDisplay.getPermissionChecker();
	}

	@Override
	public String getAllRowsCheckBox() {
		return null;
	}

	@Override
	public String getAllRowsCheckBox(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public String getRowCheckBox(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String primaryKey) {

		long entryId = GetterUtil.getLong(primaryKey);

		AnnouncementsEntry entry = null;

		try {
			entry = AnnouncementsEntryLocalServiceUtil.getEntry(entryId);
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return StringPool.BLANK;
		}

		boolean showInput = false;

		try {
			if (AnnouncementsEntryPermission.contains(
					_permissionChecker, entry, ActionKeys.DELETE)) {

				showInput = true;

				if (!showInput) {
					return StringPool.BLANK;
				}
			}
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return StringPool.BLANK;
		}

		String checkBoxRowIds = StringBundler.concat(
			"['", _liferayPortletResponse.getNamespace(), RowChecker.ROW_IDS,
			AnnouncementsEntry.class.getSimpleName(), "']");

		String checkBoxAllRowIds = "'#" + getAllRowIds() + "'";

		return getRowCheckBox(
			httpServletRequest, checked, disabled,
			StringBundler.concat(
				_liferayPortletResponse.getNamespace(), RowChecker.ROW_IDS,
				AnnouncementsEntry.class.getSimpleName()),
			primaryKey, checkBoxRowIds, checkBoxAllRowIds, StringPool.BLANK);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnnouncementsEntryChecker.class);

	private final LiferayPortletResponse _liferayPortletResponse;
	private final PermissionChecker _permissionChecker;

}