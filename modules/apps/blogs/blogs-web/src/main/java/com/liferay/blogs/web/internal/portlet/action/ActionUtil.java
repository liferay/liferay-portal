/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.portlet.action;

import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryServiceUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class ActionUtil {

	public static BlogsEntry getEntry(PortletRequest portletRequest)
		throws Exception {

		BlogsEntry entry = null;

		long entryId = ParamUtil.getLong(portletRequest, "entryId");
		String urlTitle = ParamUtil.getString(portletRequest, "urlTitle");

		if (entryId > 0) {
			entry = BlogsEntryServiceUtil.getEntry(entryId);
		}
		else if (Validator.isNotNull(urlTitle) &&
				 SessionErrors.isEmpty(portletRequest)) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			try {
				entry = BlogsEntryServiceUtil.getEntry(
					themeDisplay.getScopeGroupId(), urlTitle);
			}
			catch (NoSuchEntryException noSuchEntryException) {
				if (urlTitle.indexOf(CharPool.UNDERLINE) != -1) {

					// Check another URL title for backwards compatibility. See
					// LEP-5733.

					urlTitle = StringUtil.replace(
						urlTitle, CharPool.UNDERLINE, CharPool.DASH);

					entry = BlogsEntryServiceUtil.getEntry(
						themeDisplay.getScopeGroupId(), urlTitle);
				}
				else {
					throw noSuchEntryException;
				}
			}
		}

		if ((entry != null) && entry.isInTrash()) {
			throw new NoSuchEntryException("{entryId=" + entryId + "}");
		}

		return entry;
	}

}