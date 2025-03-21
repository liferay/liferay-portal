/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;

import jakarta.portlet.PortletRequest;

/**
 * @author Alejandro Tardín
 * @author Roberto Díaz
 */
public interface AssetDisplayPageEntryFormProcessor {

	public void process(
			String className, long classPK, PortletRequest portletRequest)
		throws PortalException;

	public void process(
			String className, long classPK, ServiceContext serviceContext)
		throws PortalException;

}