/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;

import jakarta.servlet.ServletContext;

/**
 * @author Bruno Farache
 * @author Shuyang Zhou
 */
public class PortletClassInvoker {

	public static Object invoke(
			String portletId, MethodKey methodKey, Object... arguments)
		throws Exception {

		portletId = _getRootPortletId(portletId);

		ClassLoader portletClassLoader = PortalClassLoaderUtil.getClassLoader();

		PortletBag portletBag = PortletBagPool.get(portletId);

		if (portletBag != null) {
			ServletContext servletContext = portletBag.getServletContext();

			portletClassLoader = servletContext.getClassLoader();
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				portletClassLoader)) {

			MethodHandler methodHandler = new MethodHandler(
				methodKey, arguments);

			return methodHandler.invoke();
		}
	}

	/**
	 * Copied from
	 * <code>com.liferay.portal.kernel.model.PortletConstants</code>.
	 */
	private static String _getRootPortletId(String portletId) {
		int pos = portletId.indexOf(_INSTANCE_SEPARATOR);

		if (pos == -1) {
			return portletId;
		}

		return portletId.substring(0, pos);
	}

	private static final String _INSTANCE_SEPARATOR = "_INSTANCE_";

}