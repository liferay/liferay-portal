/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.search;

import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBCategoryLocalServiceUtil;
import com.liferay.message.boards.service.MBThreadLocalServiceUtil;
import com.liferay.message.boards.web.internal.security.permission.MBCategoryPermission;
import com.liferay.message.boards.web.internal.security.permission.MBMessagePermission;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Sergio González
 */
public class EntriesChecker extends EmptyOnClickRowChecker {

	public EntriesChecker(
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

		MBThread thread = null;

		long entryId = GetterUtil.getLong(primaryKey);

		MBCategory category = MBCategoryLocalServiceUtil.fetchMBCategory(
			entryId);

		if (category == null) {
			thread = MBThreadLocalServiceUtil.fetchThread(entryId);
		}

		if ((category == null) && (thread == null)) {
			return StringPool.BLANK;
		}

		boolean showInput = false;

		String name = null;

		if (category != null) {
			name = MBCategory.class.getSimpleName();

			try {
				if (MBCategoryPermission.contains(
						_permissionChecker, category, ActionKeys.DELETE)) {

					showInput = true;
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}
		else {
			name = MBThread.class.getSimpleName();

			try {
				if (MBCategoryPermission.contains(
						_permissionChecker, thread.getGroupId(),
						thread.getCategoryId(), ActionKeys.LOCK_THREAD) ||
					MBMessagePermission.contains(
						_permissionChecker, thread.getRootMessageId(),
						ActionKeys.DELETE)) {

					showInput = true;
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		if (!showInput) {
			return StringPool.BLANK;
		}

		String checkBoxRowIds = _getEntryRowIds();

		return getRowCheckBox(
			httpServletRequest, checked, disabled,
			_liferayPortletResponse.getNamespace() + RowChecker.ROW_IDS + name,
			primaryKey, checkBoxRowIds, "'#" + getAllRowIds() + "'",
			StringPool.BLANK);
	}

	private String _getEntryRowIds() {
		return StringBundler.concat(
			"['", _liferayPortletResponse.getNamespace(), RowChecker.ROW_IDS,
			MBCategory.class.getSimpleName(), "', '",
			_liferayPortletResponse.getNamespace(), RowChecker.ROW_IDS,
			MBThread.class.getSimpleName(), "']");
	}

	private static final Log _log = LogFactoryUtil.getLog(EntriesChecker.class);

	private final LiferayPortletResponse _liferayPortletResponse;
	private final PermissionChecker _permissionChecker;

}