/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayPortletURLWrapper;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.internal.PortletResponseImpl;

import jakarta.portlet.PortletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletURLImplWrapper extends LiferayPortletURLWrapper {

	public PortletURLImplWrapper(
		PortletResponse portletResponse, long plid, String lifecycle) {

		super(_createLiferayPortletURL(portletResponse, lifecycle));

		setPlid(plid);
	}

	private static LiferayPortletURL _createLiferayPortletURL(
		PortletResponse portletResponse, String lifecycle) {

		LiferayPortletResponse liferayPortletResponse =
			LiferayPortletUtil.getLiferayPortletResponse(portletResponse);

		PortletResponseImpl portletResponseImpl =
			(PortletResponseImpl)liferayPortletResponse;

		return PortletURLFactoryUtil.create(
			portletResponseImpl.getPortletRequest(),
			portletResponseImpl.getPortlet(), null, lifecycle);
	}

}