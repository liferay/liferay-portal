/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.taglib.aui.AUIUtil;
import com.liferay.user.associated.data.display.UADDisplay;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Samuel Trong Tran
 */
public class UADHierarchyChecker extends EmptyOnClickRowChecker {

	public UADHierarchyChecker(
		PortletResponse portletResponse, UADDisplay<?>[] uadDisplays) {

		super(portletResponse);

		_uadDisplays = uadDisplays;
	}

	@Override
	protected String getRowCheckBox(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String name, String value, String checkBoxRowIds,
		String checkBoxAllRowIds, String checkBoxPostOnClick) {

		for (UADDisplay<?> uadDisplay : _uadDisplays) {
			try {
				long primaryKey = GetterUtil.getLong(value);

				uadDisplay.get(primaryKey);

				name += AUIUtil.normalizeId(uadDisplay.getTypeKey());

				return super.getRowCheckBox(
					httpServletRequest, checked, disabled, name, value,
					checkBoxRowIds, checkBoxAllRowIds, checkBoxPostOnClick);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UADHierarchyChecker.class);

	private final UADDisplay<?>[] _uadDisplays;

}