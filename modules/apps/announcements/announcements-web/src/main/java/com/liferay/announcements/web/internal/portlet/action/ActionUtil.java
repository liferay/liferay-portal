/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.portlet.action;

import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.service.AnnouncementsEntryServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Raymond Augé
 */
public class ActionUtil {

	public static AnnouncementsEntry getEntry(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		long entryId = ParamUtil.getLong(httpServletRequest, "entryId");

		if (entryId > 0) {
			return AnnouncementsEntryServiceUtil.getEntry(entryId);
		}

		return null;
	}

	public static AnnouncementsEntry getEntry(PortletRequest portletRequest)
		throws PortalException {

		return getEntry(PortalUtil.getHttpServletRequest(portletRequest));
	}

}