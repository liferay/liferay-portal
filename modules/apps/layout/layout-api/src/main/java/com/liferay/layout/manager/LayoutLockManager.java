/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.manager;

import com.liferay.layout.model.LockedLayout;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

/**
 * @author Lourdes Fernández Besada
 */
public interface LayoutLockManager {

	public void getLock(ActionRequest actionRequest) throws PortalException;

	public void getLock(Layout layout, long userId) throws PortalException;

	public List<LockedLayout> getLockedLayouts(
		long companyId, long groupId, Locale locale);

	public String getLockedLayoutURL(ActionRequest actionRequest);

	public String getLockedLayoutURL(HttpServletRequest httpServletRequest);

	public String getUnlockDraftLayoutURL(
			LiferayPortletResponse liferayPortletResponse,
			PortletURLBuilder.UnsafeSupplier<Object, Exception>
				redirectUnsafeSupplier)
		throws Exception;

	public void unlock(Layout layout, long userId);

	public void unlockLayouts(long companyId, long autosaveMinutes)
		throws PortalException;

	public void unlockLayoutsByUserId(long companyId, long userId)
		throws PortalException;

}