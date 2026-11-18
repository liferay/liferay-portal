/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.notifications.internal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.sharing.model.SharingEntry;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Akhash Ramprakash
 */
public class SharingNotificationURLUtil {

	public static String getURL(
			HttpServletRequest httpServletRequest, SharingEntry sharingEntry)
		throws PortalException {

		if (httpServletRequest == null) {
			return null;
		}

		PortletURL portletURL = PortletProviderUtil.getPortletURL(
			httpServletRequest, SharingEntry.class.getName(),
			PortletProvider.Action.PREVIEW);

		if (portletURL == null) {
			return null;
		}

		return PortletURLBuilder.create(
			portletURL
		).setParameter(
			"classNameId", sharingEntry.getClassNameId()
		).setParameter(
			"classPK", sharingEntry.getClassPK()
		).setParameter(
			"sharingEntryId", sharingEntry.getSharingEntryId()
		).buildString();
	}

	public static String getURL(
			PortletRequest portletRequest, SharingEntry sharingEntry)
		throws PortalException {

		if (portletRequest == null) {
			return null;
		}

		return getURL(
			PortalUtil.getHttpServletRequest(portletRequest), sharingEntry);
	}

}