/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.portlet.action.util;

import com.liferay.document.library.opener.onedrive.web.internal.DLOpenerOneDriveFileReference;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceURL;

/**
 * @author Cristina González
 */
public class OneDriveURLUtil {

	public static String getBackgroundTaskStatusURL(
		DLOpenerOneDriveFileReference dlOpenerOneDriveFileReference,
		Portal portal, PortletRequest portletRequest,
		PortletURLFactory portletURLFactory) {

		ResourceURL resourceURL = portletURLFactory.create(
			portletRequest, portal.getPortletId(portletRequest),
			PortletRequest.RESOURCE_PHASE);

		resourceURL.setParameter(
			"backgroundTaskId",
			String.valueOf(
				dlOpenerOneDriveFileReference.getBackgroundTaskId()));
		resourceURL.setParameter(
			"fileEntryId",
			String.valueOf(dlOpenerOneDriveFileReference.getFileEntryId()));
		resourceURL.setResourceID(
			"/document_library/one_drive_background_task_status");

		return resourceURL.toString();
	}

}