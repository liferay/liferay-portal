/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.display.context;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.portlet.shared.search.NullPortletURL;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.Map;

/**
 * @author André de Oliveira
 */
public class PortletURLFactoryImpl implements PortletURLFactory {

	public PortletURLFactoryImpl(
		PortletRequest portletRequest, MimeResponse mimeResponse) {

		_portletRequest = portletRequest;
	}

	@Override
	public PortletURL getPortletURL() throws PortletException {
		return new NullPortletURL() {

			@Override
			public void setParameter(String name, String value) {
				String portalURL = PortalUtil.getPortalURL(
					PortalUtil.getHttpServletRequest(_portletRequest));
				String currentURL = (String)_portletRequest.getAttribute(
					WebKeys.CURRENT_URL);

				_url = portalURL.concat(currentURL);

				Map<String, String[]> parameterMap =
					HttpComponentsUtil.getParameterMap(_url);

				String[] values = parameterMap.get(name);

				if (!ArrayUtil.contains(values, value)) {
					_url = HttpComponentsUtil.addParameter(_url, name, value);
				}
			}

			@Override
			public String toString() {
				return _url;
			}

			private String _url;

		};
	}

	private final PortletRequest _portletRequest;

}