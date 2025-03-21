/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portlet.PortletBagFactory;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;

import jakarta.servlet.ServletContext;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Neil Griffin
 */
public class PortletBagUtil {

	public static Portlet getPortletInstance(
			ServletContext servletContext,
			com.liferay.portal.kernel.model.Portlet portletModel,
			String rootPortletId)
		throws PortletException {

		PortletBag portletBag = PortletBagPool.get(rootPortletId);

		// Portlet bag should never be null unless the portlet has been
		// undeployed

		if (portletBag == null) {
			PortletBagFactory portletBagFactory = new PortletBagFactory();

			portletBagFactory.setClassLoader(
				PortalClassLoaderUtil.getClassLoader());
			portletBagFactory.setServletContext(servletContext);
			portletBagFactory.setWARFile(false);

			try {
				portletBag = portletBagFactory.create(portletModel);
			}
			catch (Exception exception) {
				throw new PortletException(exception);
			}
		}

		return portletBag.getPortletInstance();
	}

}