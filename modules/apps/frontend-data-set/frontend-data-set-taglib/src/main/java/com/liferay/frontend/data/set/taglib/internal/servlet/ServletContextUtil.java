/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.taglib.internal.servlet;

import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Chema Balsas
 * @author Marko Cikos
 */
public class ServletContextUtil {

	public static FDSSerializer getFDSSerializer() {
		return _fdsSerializerSnapshot.get();
	}

	public static String getFDSSettingsNamespace(
		HttpServletRequest httpServletRequest, String id) {

		StringBundler sb = new StringBundler(6);

		sb.append("FDS£");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		Portal portal = _portalSnapshot.get();

		sb.append(portal.getPortletNamespace(portletDisplay.getId()));

		sb.append(StringPool.POUND);
		sb.append(themeDisplay.getPlid());
		sb.append(StringPool.POUND);
		sb.append(id);

		return sb.toString();
	}

	public static ServletContext getServletContext() {
		return _servletContextSnapshot.get();
	}

	private static final Snapshot<FDSSerializer> _fdsSerializerSnapshot =
		new Snapshot<>(
			ServletContextUtil.class, FDSSerializer.class,
			"(frontend.data.set.serializer.type=" + FDSSerializer.TYPE_SYSTEM +
				")");
	private static final Snapshot<Portal> _portalSnapshot = new Snapshot<>(
		ServletContextUtil.class, Portal.class);
	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			ServletContextUtil.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.frontend.data.set.taglib)");

}